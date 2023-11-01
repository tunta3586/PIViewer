package space.personal;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import space.personal.repository.FollowerRepository;
import space.personal.repository.FollowerRepositoryV1;
import space.personal.repository.MemberRepository;
import space.personal.repository.MemberRepositoryV1;

@Configuration
public class SpringConfig {
    @Bean
    public MemberRepository memberRepository(){
        return new MemberRepositoryV1();
    }
    @Bean
    public FollowerRepository followerRepository(){
        return new FollowerRepositoryV1();
    }
}
