package space.personal.service;

import space.personal.domain.TwitchSearchResult;
import space.personal.domain.youtube.YoutubeChannelList;
import space.personal.domain.youtube.YoutubeSearchResult;

public interface SearchService {
    YoutubeSearchResult youtubeSearchChannel(String query);
    YoutubeSearchResult getYoutubeSubscribeList(String youtubeChannelId, String maxResults, String pageToken);
    YoutubeChannelList getChannelUrlList(String youtubeChannelIds);
    TwitchSearchResult twitchSearchChannel(String query);
}
