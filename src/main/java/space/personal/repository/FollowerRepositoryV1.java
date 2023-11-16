package space.personal.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import space.personal.domain.Follower;
import space.personal.domain.Member;

public class FollowerRepositoryV1 implements FollowerRepository{

    @PersistenceContext
    EntityManager em;

    @Override
    public Follower saveFollower(Follower follower) {
        em.persist(follower);
        return follower;
    }

    @Override
    public Follower deletFollower(Follower follower) {
        em.remove(follower);
        return follower;
    }

    @Override
    public Follower findFollower(Member member, String customUrl) {
        Follower follower = em.createQuery("SELECT e FROM Follower e WHERE e.custom_url = :customUrl AND e.member.id = :memberId", Follower.class).
        setParameter("customUrl", customUrl).
        setParameter("memberId", member.getId()).
        getSingleResult();
        return follower;
    }
}
