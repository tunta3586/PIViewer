package space.personal.repository;

import jakarta.persistence.EntityManager;

import org.springframework.stereotype.Repository;

import space.personal.domain.Member;

@Repository
public class MemberRepository {
    
    private final EntityManager em;
    public MemberRepository(EntityManager em) {
        this.em = em;
    }

    public void save(Member member) {
        em.persist(member);
    }

    public Member findOne(Long id) {
        return em.find(Member.class, id);
    }
}