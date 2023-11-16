package space.personal.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import org.springframework.beans.factory.annotation.Value;

import com.fasterxml.jackson.databind.ObjectMapper;

import space.personal.domain.TwitchSearchResult;
import space.personal.domain.youtube.YoutubeChannelList;
import space.personal.domain.youtube.YoutubeSearchResult;

public class SearchServiceV1 implements SearchService {
    @Value("${youtube.api.key}")
    private String youtubeApiKey;
    @Value("${twitch.api.client.id}")
    private String twitchClientId;
    @Value("${twitch.api.client.acces_token}")
    private String twitchStringToken;

    @Override
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

    @Override
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

    @Override
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

    @Override
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
                return TwitchSearchResult;
            } else {
                System.out.println("HTTP 요청 실패: " + responseCode);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new TwitchSearchResult();
    }
}