package space.personal;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import space.personal.mappers.FollowerMapper;
import space.personal.mappers.LiveConfigMapper;
import space.personal.mappers.MemberMapper;
import space.personal.repository.FollowerRepository;
import space.personal.repository.FollowerRepositoryV1;
import space.personal.repository.MemberRepository;
import space.personal.repository.MemberRepositoryV1;
import space.personal.service.CheckYoutubeLiveStreamService;
import space.personal.service.CheckYoutubeLiveStreamServiceV2;
import space.personal.service.MemberService;
import space.personal.service.MemberServiceV2;
import space.personal.service.SearchService;
import space.personal.service.SearchServiceV1;

@Configuration
public class SpringConfig {
    private final FollowerMapper followerMapper;
    private final LiveConfigMapper liveConfigMapper;
    private final MemberMapper memberMapper;
    
    public SpringConfig(FollowerMapper followerMapper, LiveConfigMapper liveConfigMapper, MemberMapper memberMapper){
        this.followerMapper = followerMapper;
        this.liveConfigMapper = liveConfigMapper;
        this.memberMapper = memberMapper;
    }

    @Bean
    public MemberRepository memberRepository(){
        return new MemberRepositoryV1();
    }
    @Bean
    public FollowerRepository followerRepository(){
        return new FollowerRepositoryV1();
    }
    @Bean
    public MemberService memberService(){
        return new MemberServiceV2(
            followerMapper,
            liveConfigMapper,
            memberMapper,
            searchService());
    }

    @Bean
    public CheckYoutubeLiveStreamService checkYoutubeLiveStreamService(){
        return new CheckYoutubeLiveStreamServiceV2(liveConfigMapper);
    }
    
    @Bean
    public SearchService searchService(){
        return new SearchServiceV1();
    }
}
