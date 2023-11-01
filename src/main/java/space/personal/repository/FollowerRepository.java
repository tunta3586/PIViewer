package space.personal.repository;

import space.personal.domain.Follower;
import space.personal.domain.Member;

public interface FollowerRepository {
    Follower saveFollower(Follower follower);
    Follower deletFollower(Follower follower);
    Follower findFollower(Member member, String youtubeChannelId);
}
