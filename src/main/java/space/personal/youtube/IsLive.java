package space.personal.youtube;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class IsLive {
    private String title;
    private boolean isLive;
}
