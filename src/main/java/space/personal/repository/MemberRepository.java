package space.personal.repository;

import java.util.List;

import org.springframework.stereotype.Repository;

import space.personal.domain.Follower;
import space.personal.domain.Member;

@Repository
public interface MemberRepository {
    Member save(Member member);
    Member findMember(String userid);
    List<Follower> findAllFollower(Long id);
}