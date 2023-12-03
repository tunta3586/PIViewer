package space.personal.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import space.personal.domain.Follower;
import space.personal.domain.LiveConfig;
import space.personal.domain.Member;
import space.personal.domain.SearchFollower;
import space.personal.domain.search.YoutubeChannelList;
import space.personal.domain.search.YoutubeSearchResult;
import space.personal.domain.search.YoutubeSearchResult.Items;
import space.personal.mappers.FollowerMapper;
import space.personal.mappers.LiveConfigMapper;
import space.personal.mappers.MemberMapper;

public class MemberServiceV2 implements MemberService{
    private final FollowerMapper followerMapper;
    private final LiveConfigMapper liveConfigMapper;
    private final MemberMapper memberMapper;
    private final SearchService searchService;

    public MemberServiceV2(FollowerMapper followerMapper, LiveConfigMapper liveConfigMapper, MemberMapper memberMapper, SearchService searchService) {
        this.followerMapper = followerMapper;
        this.liveConfigMapper = liveConfigMapper;
        this.memberMapper = memberMapper;
        this.searchService = searchService;
    }

    @Override
    public void join(Member member) {
        memberMapper.save(member);
        YoutubeSearchResult youtubeSearchResult;
        Long memberId = memberMapper.findMember(member.getUsername()).getId();
        String searchURLs = "";
        String nextPage = "";
        do{ 
            youtubeSearchResult = searchService.getYoutubeSubscribeList(member.getYoutubeChannelId(), "50", nextPage);
            searchURLs = "";
            for (Items result : youtubeSearchResult.getItems()) {
                if(result.getContentDetails().getSubscription() != null)
                    searchURLs += "&id=" + result.getContentDetails().getSubscription().getResourceId().getChannelId();
            }
            if(!searchURLs.equals("")){
                YoutubeChannelList youtubeChannelList = searchService.getChannelUrlList(searchURLs);
                for (YoutubeChannelList.Items result : youtubeChannelList.getItems()) {
                    LiveConfig liveConfig = Optional.ofNullable(liveConfigMapper.findByCustomUrl(result.getSnippet().getCustomUrl())).orElse(null);
                    if(liveConfig == null){
                        liveConfig = new LiveConfig();
                        liveConfig.setName(result.getSnippet().getTitle());
                        String description = result.getSnippet().getDescription();
                        if (description != null && description.length() > 100) {
                            liveConfig.setDescription(description.substring(0, 100));
                        } else {
                            liveConfig.setDescription(description);
                        }
                        liveConfig.setCustom_url(result.getSnippet().getCustomUrl());
                        liveConfig.setThumbnails_url(result.getSnippet().getThumbnails().getMedium().getUrl());
                        liveConfig.setVideo_id("");
                        liveConfig.setIs_live("");
                        liveConfigMapper.save(liveConfig);
                    }
                    followerMapper.save(memberId, liveConfig.getCustom_url());
                }
                nextPage = Optional.ofNullable(youtubeSearchResult.getNextPageToken()).orElse("");
            }
        }while(!nextPage.equals(""));
    }

    @Override
    public boolean checkFollow(Member member, String customUrl) {
        for (Follower follower : member.getFollowers()) {
            if (follower.getLiveConfig().getCustom_url().equals(customUrl)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void follow(Member member, Follower follower) {
        followerMapper.save(member.getId(), follower.getLiveConfig().getCustom_url());
    }

    @Override
    public void setTwitchChannelId(Follower follower, String twitchChannelId) {
        followerMapper.updateTwitchChannelId(twitchChannelId, follower.getId(), follower.getLiveConfig().getCustom_url());
    }

    @Override
    public void unFollow(Member member, String follower) {
        followerMapper.delete(member, follower);
    }

    @Override
    public Member findUser(String username) {
        Member member = Optional.ofNullable(memberMapper.findMember(username)).orElse(null);
        if(member == null){
            return null;
        }
        List<SearchFollower> urlList = followerMapper.findAllFollowerByMemberId(member.getId());
        List<Follower> followers = setFollowers(member, urlList);
        member.setFollowers(followers);

        return member;
    }

    @Override
    public Follower findFollower(Member member, String youtubeChannelId) {
        return followerMapper.findFollower(member, youtubeChannelId);
    }

    @Override
    public LiveConfig searchChannel(String customUrl) {
        return liveConfigMapper.findByCustomUrl(customUrl);
    }

    public List<Follower> setFollowers(Member member, List<SearchFollower> urlList) {
        List<Follower> followers = new ArrayList<Follower>();
        for(SearchFollower url : urlList) {
            Follower follower = new Follower();
            follower.setId(member.getId());
            follower.setTwitch_channel_id(url.getTwitch_channel_id());
            follower.setLiveConfig(liveConfigMapper.findByCustomUrl(url.getCustom_url()));
            followers.add(follower);
        }
        return followers;
    }
}