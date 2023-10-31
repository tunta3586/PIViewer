package space.personal.service;

import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import space.personal.domain.Member;
import space.personal.repository.MemberRepository;

@Service
public class MemberService {

    private final MemberRepository memberRepository;
    
    public MemberService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    @Transactional
    public void join(Member member) {
        memberRepository.save(member);
    }
}
