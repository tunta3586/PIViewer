package space.personal.domain.twitch;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class TwitchSearchResult {
    private List<data> data;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class data {
        private String broadcaster_login;
        private String display_name;
        private String is_live;
        private String thumbnail_url;
    }
}
