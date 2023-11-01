package space.personal.domain.youtube;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class YoutubeIsLive {
    private String title;
    private boolean isLive;
}
