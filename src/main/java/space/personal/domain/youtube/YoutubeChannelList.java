package space.personal.domain.youtube;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class YoutubeChannelList {
    private String nextPageToken;
    private List<Items> items;
    
    @JsonIgnoreProperties(ignoreUnknown = true) @Data
    public static class Items{
        private Snippet snippet;

        @JsonIgnoreProperties(ignoreUnknown = true) @Data
        public static class Snippet {
            private String title;
            private String customUrl;
            private String description;
            private String channelTitle;
            private Thumbnails thumbnails;
        }

        @JsonIgnoreProperties(ignoreUnknown = true) @Data
        public static class Thumbnails {
            private Image medium;
        }

        @JsonIgnoreProperties(ignoreUnknown = true) @Data
        public static class Image {
            private String url;
        }
    }
}
