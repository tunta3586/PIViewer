package space.personal.repository;

import java.util.List;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import space.personal.domain.Follower;
import space.personal.domain.Member;

public class MemberRepositoryV1 implements MemberRepository{

    @PersistenceContext
    EntityManager em;

    @Override
    public Member save(Member member) {
        em.persist(member);
        return member;
    }

    @Override
    public Member findMember(String userid) {
        Member member = (Member) em.createQuery("SELECT e FROM Member e WHERE e.username = :username", 
        Member.class).setParameter("username", userid).getSingleResult();
        return member;
    }

    @Override
    public List<Follower> findAllFollower(Long id) {
        Member member = em.find(Member.class, id);
        return member.getFollowers();
    }
}
