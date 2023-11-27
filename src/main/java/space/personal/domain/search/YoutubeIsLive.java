package space.personal.domain.search;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class YoutubeIsLive {
    private String title;
    
    // 나중에 Enum으로 다시 처리할 예정
    private String isLive;
}
