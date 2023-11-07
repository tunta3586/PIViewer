package space.personal.domain.youtube;

import lombok.Getter;
import lombok.Setter;
import space.personal.domain.LiveConfig;

@Setter
@Getter
public class SearchResult{
    private LiveConfig liveConfig;
    private boolean searchResult;
}