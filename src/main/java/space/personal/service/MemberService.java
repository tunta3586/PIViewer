package space.personal.service;

import space.personal.domain.Follower;
import space.personal.domain.LiveConfig;
import space.personal.domain.Member;

public interface MemberService {
    void join(Member member);
    boolean checkFollow(Member member, String customUrl);
    void follow(Member member, Follower follower);
    void setTwitchChannelId(Follower follower, String channelId);
    void unFollow(Member member, String follower);
    Member findUser(String username);
    Follower findFollower(Member member, String youtubeChannelId);
    LiveConfig searchChannel(String customUrl);
}
