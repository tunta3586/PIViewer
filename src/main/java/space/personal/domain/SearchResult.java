package space.personal.domain;

import lombok.Data;
import space.personal.domain.twitch.TwitchSearchResult;
import space.personal.domain.youtube.YoutubeSearchResult;

@Data
public class SearchResult {
    private TwitchSearchResult twitchSearchResult;
    private YoutubeSearchResult youtubeSearchResult;
}
