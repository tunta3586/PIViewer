package space.personal.mappers;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import space.personal.domain.Follower;
import space.personal.domain.Member;
import space.personal.domain.SearchFollower;

@Mapper
public interface FollowerMapper {
    void save(@Param("memberId") Long memberId, @Param("customUrl") String customUrl);
    void delete(Member member, @Param("customUrl") String customUrl);
    void updateTwitchChannelId(@Param("twitch_channel_id") String twitch_channel_id, @Param("member_id") Long member_id, @Param("custom_url") String customUrl);
    Follower findFollower(Member member, @Param("customUrl") String customUrl);
    List<SearchFollower> findAllFollowerByMemberId(@Param("memberId") Long memberId);
}
