package space.personal.web;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.databind.ObjectMapper;

import space.personal.youtube.IsLive;
import space.personal.youtube.SearchResult;

@Controller
public class YoutubeSearchController {

    @Value("${youtube.api.key}")
    private String youtubeApiKey;

    @GetMapping("/youtubeSearch")
    @ResponseBody
    public SearchResult searchChannel(Model model, @RequestParam("search") String query) {
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
                SearchResult searchResult = objectMapper.readValue(jsonResponse, SearchResult.class);

                return searchResult;
            } else {
                System.out.println("HTTP 요청 실패: " + responseCode);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new SearchResult();
    }

    @GetMapping("/isLive")
    @ResponseBody
    public ArrayList<IsLive> checkLive(Model model, @RequestParam("userId") String userId){
        ArrayList<IsLive> followers = new ArrayList<>();

        return followers;
    }

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
                SearchResult searchResult = objectMapper.readValue(jsonResponse, SearchResult.class);
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
}
