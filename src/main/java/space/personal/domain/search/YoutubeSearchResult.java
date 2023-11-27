package space.personal.domain.search;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class YoutubeSearchResult {
    private List<Items> items;
    private String nextPageToken;
    private PageInfo pageInfo;
    
    @JsonIgnoreProperties(ignoreUnknown = true) @Data
    public static class Items{
        private Snippet snippet;
        private ContentDetails contentDetails;

        @JsonIgnoreProperties(ignoreUnknown = true) @Data
        public static class Snippet {
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

        @JsonIgnoreProperties(ignoreUnknown = true) @Data
        public static class ContentDetails {
            private Subscription subscription;
        }

        @JsonIgnoreProperties(ignoreUnknown = true) @Data
        public static class Subscription {
            private ResourceId resourceId;
        }

        @JsonIgnoreProperties(ignoreUnknown = true) @Data
        public static class ResourceId {
            private String channelId;
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true) @Data
    public static class PageInfo {
        private int totalResults;
    }
}
