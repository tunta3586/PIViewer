package space.personal.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.transaction.Transactional;
import space.personal.domain.Follower;
import space.personal.domain.LiveConfig;
import space.personal.domain.Member;
import space.personal.domain.youtube.YoutubeChannelList;
import space.personal.domain.youtube.YoutubeIsLive;
import space.personal.domain.youtube.YoutubeSearchResult;
import space.personal.domain.youtube.YoutubeSearchResult.Items;
import space.personal.repository.FollowerRepository;
import space.personal.repository.LiveConfigRepository;
import space.personal.repository.MemberRepository;

@Service
@Transactional
public class MemberService {
    @Value("${youtube.api.key}")
    private String youtubeApiKey;
    
    private MemberRepository memberRepository;
    private FollowerRepository followerRepository;
    private LiveConfigRepository liveConfigRepository;

    public MemberService(MemberRepository memberRepository, FollowerRepository followerRepository, LiveConfigRepository liveConfigRepository) {
        this.memberRepository = memberRepository;
        this.followerRepository = followerRepository;
        this.liveConfigRepository = liveConfigRepository;
    }

    /**
     * @param member
     */
    public void join(Member member){
        YoutubeSearchResult youtubeSearchResult;
        List<Follower> followerList = new ArrayList<>();
        String searchURLs = "";
        String nextPage = "";
        do{ 
            youtubeSearchResult = getYoutubeSubscribeList(member.getYoutubeChannelId(), "50", nextPage);
            searchURLs = "";
            for (Items result : youtubeSearchResult.getItems()) {
                if(result.getContentDetails().getSubscription() != null)
                    searchURLs += "&id=" + result.getContentDetails().getSubscription().getResourceId().getChannelId();
            }
            YoutubeChannelList youtubeChannelList = getChannelUrlList(searchURLs);
            for (YoutubeChannelList.Items result : youtubeChannelList.getItems()) {
                Follower follower = new Follower();
                follower.setName(result.getSnippet().getTitle());

                LiveConfig liveConfig = Optional.ofNullable(liveConfigRepository.findByCustomUrl(result.getSnippet().getCustomUrl())).orElse(null);
                if(liveConfig == null){
                    liveConfig = new LiveConfig();
                    liveConfig.setCustomUrl(result.getSnippet().getCustomUrl());
                    liveConfig.setThumbnailsUrl(result.getSnippet().getThumbnails().getMedium().getUrl());
                }
                follower.setLiveConfig(liveConfig);
                follower.setMember(member);
                followerList.add(follower);
            }
            nextPage = Optional.ofNullable(youtubeSearchResult.getNextPageToken()).orElse("");
        }while(!nextPage.equals(""));
        member.setFollowers(followerList);

        memberRepository.save(member);
    }

    // /**
    //  * @param userId
    //  * @return
    //  * @throws InterruptedException
    //  */
    public ArrayList<YoutubeIsLive> checkFollowerIsLive(String userId) throws InterruptedException{
        List<Follower> followers = memberRepository.findMember(userId).getFollowers();
        ArrayList<YoutubeIsLive> youtubeIsLive = new ArrayList<>();
        for(Follower follower: followers){
            YoutubeIsLive IsLive = new YoutubeIsLive();
            IsLive.setTitle(follower.getName());
            IsLive.setIsLive(follower.getLiveConfig().getIsLive());
            youtubeIsLive.add(IsLive);
        }
        return youtubeIsLive;
    }

    /**
    * @param member
     * @param follower
     * @return 
     */
    public boolean checkFollow(Member member, String customUrl){
        for (Follower follower : member.getFollowers()) {
                if (follower.getLiveConfig().getCustomUrl().equals(customUrl)) {
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
    public void follow(Member member, Follower follower){
        member.getFollowers().add(follower);
        memberRepository.save(member);
    }

    /**
     * @param member
     * @param follower
     */
    public void unFollow(Member member, String follower){
        followerRepository.deletFollower(followerRepository.findFollower(member, follower));
    }

    /**
     * @param query
     * @return
     */
    public YoutubeSearchResult youtubeSearchChannel(String query) {
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            String encodedQuery = URLEncoder.encode(query, "UTF-8");
            String url = "https://youtube.googleapis.com/youtube/v3/search?part=snippet&channelType=any&maxResults=25&q=" + encodedQuery + "&type=channel&key=" + youtubeApiKey; // JSON 데이터를 가져올 URL을 지정합니다.
            HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setRequestMethod("GET");

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8));
                String inputLine;
                StringBuffer response = new StringBuffer();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                String jsonResponse = response.toString();
                YoutubeSearchResult searchResult = objectMapper.readValue(jsonResponse, YoutubeSearchResult.class);

                return searchResult;
            } else {
                System.out.println("HTTP 요청 실패: " + responseCode);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new YoutubeSearchResult();
    }

    /**
     * @param id
     * @return
     */
    public Member findUser(String username){
        Member member = memberRepository.findMember(username);
        return member;
    }

    /**
     * @param member
     * @param youtubeChannelId
     * @return
     */
    public Follower findFollower(Member member, String youtubeChannelId){
        return followerRepository.findFollower(member, youtubeChannelId);
    }

    // 2023-11-06 추가
    /**
     * @param youtubeChannelId
     * @return
     */
    public YoutubeSearchResult getYoutubeSubscribeList(String youtubeChannelId, String maxResults, String pageToken) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            String url = "https://www.googleapis.com/youtube/v3/activities?part=snippet%2CcontentDetails"
                    +"&channelId=" + youtubeChannelId
                    +"&maxResults="+ maxResults
                    +"&pageToken="+ pageToken
                    + "&key=" + youtubeApiKey;
            HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setRequestMethod("GET");

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8));
                String inputLine;
                StringBuffer response = new StringBuffer();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                String jsonResponse = response.toString();
                YoutubeSearchResult searchResult = objectMapper.readValue(jsonResponse, YoutubeSearchResult.class);
                return searchResult;
            } else {
                System.out.println("HTTP 요청 실패: " + responseCode);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new YoutubeSearchResult();
    }

    // 2023-11-06 추가
    /**
     * @param youtubeChannelIds
     * @return
     */
    public YoutubeChannelList getChannelUrlList(String youtubeChannelIds) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            String url = "https://www.googleapis.com/youtube/v3/channels?part=snippet&maxResults=50"
                    + youtubeChannelIds + "&key=" + youtubeApiKey;;
            HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setRequestMethod("GET");

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8));
                String inputLine;
                StringBuffer response = new StringBuffer();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                String jsonResponse = response.toString();
                YoutubeChannelList searchResult = objectMapper.readValue(jsonResponse, YoutubeChannelList.class);
                return searchResult;
            } else {
                System.out.println("HTTP 요청 실패: " + responseCode);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new YoutubeChannelList();
    }
}
