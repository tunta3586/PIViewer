package space.personal.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import space.personal.domain.Follower;
import space.personal.domain.LiveConfig;
import space.personal.domain.Member;
import space.personal.domain.youtube.YoutubeChannelList;
import space.personal.domain.youtube.YoutubeSearchResult;
import space.personal.domain.youtube.YoutubeSearchResult.Items;
import space.personal.repository.FollowerRepository;
import space.personal.repository.LiveConfigRepository;
import space.personal.repository.MemberRepository;

@Service
@Transactional
public class MemberServiceV1 implements MemberService{
    @Value("${youtube.api.key}")
    private String youtubeApiKey;
    @Value("${twitch.api.client.id}")
    private String twitchClientId;
    @Value("${twitch.api.client.acces_token}")
    private String twitchStringToken;
    
    private MemberRepository memberRepository;
    private FollowerRepository followerRepository;
    private LiveConfigRepository liveConfigRepository;
    private SearchService searchService;

    public MemberServiceV1(MemberRepository memberRepository, FollowerRepository followerRepository, LiveConfigRepository liveConfigRepository, SearchService searchService) {
        this.memberRepository = memberRepository;
        this.followerRepository = followerRepository;
        this.liveConfigRepository = liveConfigRepository;
        this.searchService = searchService;
    }

    /**
     * @param member
     */
    @Override
    public void join(Member member){
        YoutubeSearchResult youtubeSearchResult;
        List<Follower> followerList = new ArrayList<>();
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
                    Follower follower = new Follower();

                    LiveConfig liveConfig = Optional.ofNullable(liveConfigRepository.findByCustomUrl(result.getSnippet().getCustomUrl())).orElse(null);
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
                    }
                    follower.setLiveConfig(liveConfig);
                    follower.setMember(member);
                    follower.setTwitch_channel_id("");
                    followerList.add(follower);
                }
                nextPage = Optional.ofNullable(youtubeSearchResult.getNextPageToken()).orElse("");
            }
        }while(!nextPage.equals(""));
        member.setFollowers(followerList);

        memberRepository.save(member);
    }

    /**
    * @param member
     * @param follower
     * @return 
     */
    @Override
    public boolean checkFollow(Member member, String customUrl){
        for (Follower follower : member.getFollowers()) {
                if (follower.getLiveConfig().getCustom_url().equals(customUrl)) {
                    return false;
            }
        }
        return true;
    }

    /**
     * @param member
     * @param follower
     * @return
     */
    @Override
    public void follow(Member member, Follower follower){
        member.getFollowers().add(follower);
        memberRepository.save(member);
    }

    /**
     * @param member
     * @param follower
     * @return
     */
    @Override
    public void setTwitchChannelId(Follower follower, String twitchChannelId) {
        follower.setTwitch_channel_id(twitchChannelId);
        followerRepository.saveFollower(follower);
    }

    /**
     * @param member
     * @param follower
     */
    @Override
    public void unFollow(Member member, String follower){
        followerRepository.deletFollower(followerRepository.findFollower(member, follower));
    }

    /**
     * @param id
     * @return
     */
    @Override
    public Member findUser(String username){
        Member member = memberRepository.findMember(username);
        return member;
    }

    /**
     * @param member
     * @param youtubeChannelId
     * @return
     */
    @Override
    public Follower findFollower(Member member, String youtubeChannelId){
        return followerRepository.findFollower(member, youtubeChannelId);
    }

    @Override
    public LiveConfig searchChannel(String customUrl){
        return Optional.ofNullable(liveConfigRepository.findByCustomUrl(customUrl)).orElse(null);
    }
}
