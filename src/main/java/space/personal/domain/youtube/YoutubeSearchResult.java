package space.personal.domain.youtube;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class YoutubeSearchResult {
    private List<Items> items;
    
    @JsonIgnoreProperties(ignoreUnknown = true) @Data
    public static class Items{
        private Id id;
        private Snippet snippet;

        @JsonIgnoreProperties(ignoreUnknown = true) @Data
        public static class Id {
            private String kind;
            private String videoId;
        }

        @JsonIgnoreProperties(ignoreUnknown = true) @Data
        public static class Snippet {
            private String channelId;
            private String title;
            private String description;
            private Thumbnails thumbnails;
            private String liveBroadcastContent;
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
