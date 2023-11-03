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

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.transaction.Transactional;
import space.personal.domain.Follower;
import space.personal.domain.Member;
import space.personal.domain.twitch.TwitchSearchResult;
import space.personal.domain.youtube.YoutubeSearchResult;
import space.personal.domain.youtube.YoutubeIsLive;
import space.personal.repository.FollowerRepository;
import space.personal.repository.MemberRepository;

@Service
@Transactional
public class MemberService {
    @Value("${youtube.api.key}")
    private String youtubeApiKey;
    @Value("${twitch.api.client.id}")
    private String twitchClientId;
    @Value("${twitch.api.client.secret}")
    private String twitchClientSecret;
    @Value("${twitch.api.client.acces_token}")
    private String twitchStringToken;
    
    private MemberRepository memberRepository;
    private FollowerRepository followerRepository;

    public MemberService(MemberRepository memberRepository, FollowerRepository followerRepository){
        this.memberRepository = memberRepository;
        this.followerRepository = followerRepository;
    }

    /**
     * @param member
     */
    public void join(Member member){
        memberRepository.save(member);
    }

    /**
     * @param userId
     * @return
     */
    public ArrayList<YoutubeIsLive> checkFollowerIsLive(String userId){
        List<Follower> followers = memberRepository.findMember(userId).getFollowers();
        ArrayList<YoutubeIsLive> youtubeIsLive = new ArrayList<>();
        for(Follower follower: followers){
            YoutubeIsLive IsLive = new YoutubeIsLive();
            IsLive.setTitle(follower.getName());
            IsLive.setLive(isYoutubeLive(follower.getTwitchChannelId()));
            youtubeIsLive.add(IsLive);
        }
        return youtubeIsLive;
    }

    /**
    * @param member
     * @param follower
     * @return 
     */
    public boolean checkFollow(Member member, String youtubeChannelId){
        for (Follower follower : member.getFollowers()) {
            if (follower.getYoutubeChannelId().equals(youtubeChannelId)) {
               return true;
            }
        }
        return false;
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
     * @param channelId
     * @return
     */
    public boolean isYoutubeLive(String channelId) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            String url = "https://youtube.googleapis.com/youtube/v3/search?part=snippet&channelType=any&maxResults=25"
                    +"&channelId=" + channelId + "&eventType=live&type=video&key=" + youtubeApiKey;
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
                if(searchResult.getItems().get(0).getSnippet().getLiveBroadcastContent().equals("live"))
                    return true;
                else
                    return false;
            } else {
                System.out.println("HTTP 요청 실패: " + responseCode);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
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
     * @param query
     * @return
     */
    public TwitchSearchResult twitchSearchChannel(String query) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            String encodedQuery = URLEncoder.encode(query, "UTF-8");
            String url = "https://api.twitch.tv/helix/search/channels?query=" + encodedQuery; // JSON 데이터를 가져올 URL을 지정합니다.
            HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Authorization", "Bearer " + twitchStringToken);
            connection.setRequestProperty("Client-Id", twitchClientId);

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
                TwitchSearchResult TwitchSearchResult = objectMapper.readValue(jsonResponse, TwitchSearchResult.class);
                System.out.println(TwitchSearchResult);
                return TwitchSearchResult;
            } else {
                System.out.println("HTTP 요청 실패: " + responseCode);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new TwitchSearchResult();
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

    /**
     * @param channelId
     * @return
     */
    public String youtubeLiveVideoIdSearch(String channelId) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            String url = "https://youtube.googleapis.com/youtube/v3/search?part=snippet&channelType=any&maxResults=25"
                    +"&channelId=" + channelId + "&eventType=live&type=video&key=" + youtubeApiKey;
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
                return searchResult.getItems().get(0).getId().getVideoId();
            } else {
                System.out.println("HTTP 요청 실패: " + responseCode);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }
}
