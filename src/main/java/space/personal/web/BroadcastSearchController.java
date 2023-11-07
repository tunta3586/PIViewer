package space.personal.web;


import java.util.ArrayList;
import java.util.Optional;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import jakarta.servlet.http.HttpServletRequest;
import space.personal.SessionManager;
import space.personal.domain.Follower;
import space.personal.domain.LiveConfig;
import space.personal.domain.Member;
import space.personal.domain.youtube.YoutubeIsLive;
import space.personal.repository.LiveConfigRepository;
import space.personal.service.MemberService;

@Controller
@CrossOrigin(origins = "${cors.origins}", allowCredentials = "true")
public class BroadcastSearchController {
    public final MemberService memberService;
    public final SessionManager sessionManager;
    public final LiveConfigRepository liveConfigRepository;

    public BroadcastSearchController(MemberService memberService, SessionManager sessionManager, LiveConfigRepository liveConfigRepository) {
        this.memberService = memberService;
        this.sessionManager = sessionManager;
        this.liveConfigRepository = liveConfigRepository;
    }

    // 2023-11-06 수정 예정
    // 해당 함수는 follow 목록을 본 다음에 해당 함수를 
    @GetMapping("/isLive")
    @ResponseBody
    public ArrayList<YoutubeIsLive> checkLive(Model model, @RequestParam("userId") String userId, HttpServletRequest request) throws InterruptedException{
        return memberService.checkFollowerIsLive(userId);
        // return new ArrayList<YoutubeIsLive>();
    }

    // 기능에 대해서 다시 생각
    @GetMapping("/follow")
    @ResponseBody
    public void follow(Model model, 
        @RequestParam("followName") String followName, 
        @RequestParam("customUrl") String customUrl,
        @RequestParam("twitchChannelId") String twitchChannelId,
        HttpServletRequest request
    ){
        Member member = (Member)sessionManager.getSession(request);
        LiveConfig liveConfig = Optional.ofNullable(liveConfigRepository.findByCustomUrl(customUrl)).orElse(null);

        // liveConfig 가 존재하지 않은 경우에는 동작하지 않는다.
        // 해당의 경우에는 에러를 보내거나 하는것이 옳을것으로 보인다.
        // 따로 안내를 해야할 것 같다. => Refresh 함수 같은것이 필요로 할것으로 보인다.
        if(member != null && liveConfig!= null){
            member = memberService.findUser(member.getUsername());
            if(memberService.checkFollow(member, customUrl)){
                Follower follower = new Follower();
                follower.setName(followName);
                follower.setLiveConfig(liveConfig);
                follower.setTwitchChannelId(twitchChannelId);
                follower.setMember(member);

                memberService.follow(member, follower);
            }else{
                // 중복이 있는경우 이므로 조치를 해야한다.
            }
        }
    }

    // 생각해보면 크게 문제는 안될것 같다.
    // 기능은 있으면 편하고 없으면 불편한 딱 그정도
    @GetMapping("/unFollow")
    @ResponseBody
    public void unFollow(Model model, 
        @RequestParam("userId") String userId, 
        @RequestParam("youtubeChannelId") String youtubeChannelId,
        HttpServletRequest request
    ){
        if(sessionManager.getSession(request) != null){
            Member member = memberService.findUser(userId);
            memberService.unFollow(member, youtubeChannelId);
        }
    }

    // 2023-11-06 수정 예정
    // @GetMapping("/searchLiveConfig")
    // @ResponseBody
    // public LiveConfig searchLiveConfig(Model model,
    //     @RequestParam("userId") String userId, 
    //     @RequestParam("youtubeChannelId") String youtubeChannelId,
    //     HttpServletRequest request
    // ){
    //     if(sessionManager.getSession(request) != null){
    //         LiveConfig liveConfig = new LiveConfig();
    //         Follower follower = memberService.findFollower(memberService.findUser(userId), youtubeChannelId);
    //         liveConfig.setYoutubeLiveVideoId(memberService.youtubeLiveVideoIdSearch(youtubeChannelId));
    //         liveConfig.setTwitchChannelId(follower.getTwitchChannelId());
    //         return liveConfig;
    //     }
    //     return null;
    // }
}
