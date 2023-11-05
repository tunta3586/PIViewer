package space.personal.web;


import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import jakarta.servlet.http.HttpServletRequest;
import space.personal.SessionManager;
import space.personal.domain.Follower;
import space.personal.domain.Member;
import space.personal.service.MemberService;

@Controller
@CrossOrigin(origins = "${cors.origins}", allowCredentials = "true")
public class BroadcastSearchController {
    public final MemberService memberService;
    public final SessionManager sessionManager;

    public BroadcastSearchController(MemberService memberService, SessionManager sessionManager) {
        this.memberService = memberService;
        this.sessionManager = sessionManager;
    }

    // 2023-11-06 삭제
    // @GetMapping("/youtubeSearch")
    // @ResponseBody
    // public SearchResult searchChannel(Model model, @RequestParam("search") String query, HttpServletRequest request) {
    //     if(sessionManager.getSession(request)!= null){
    //         SearchResult searchResult = new SearchResult();
    //         searchResult.setTwitchSearchResult(memberService.twitchSearchChannel(query));
    //         searchResult.setYoutubeSearchResult(memberService.youtubeSearchChannel(query));
    //         return searchResult;
    //     }
    //     return new SearchResult();
    // }

    // 2023-11-06 수정 예정
    // @GetMapping("/isLive")
    // @ResponseBody
    // public ArrayList<YoutubeIsLive> checkLive(Model model, @RequestParam("userId") String userId, HttpServletRequest request){
    //     if(sessionManager.getSession(request) != null){
    //         return memberService.checkFollowerIsLive(userId);
    //     }
    //     return null;
    // }

    @GetMapping("/follow")
    @ResponseBody
    public void follow(Model model, 
        @RequestParam("followName") String followName, 
        @RequestParam("customUrl") String customUrl,
        @RequestParam("twitchChannelId") String twitchChannelId,
        HttpServletRequest request
    ){
        Member member = (Member)sessionManager.getSession(request);
        if(member != null){
            member = memberService.findUser(member.getUsername());
            if(memberService.checkFollow(member, customUrl)){
                Follower follower = new Follower();
                follower.setName(followName);
                follower.setCustomUrl(customUrl);
                follower.setTwitchChannelId(twitchChannelId);
                follower.setMember(member);

                memberService.follow(member, follower);
            }else{
                // 중복이 있는경우 이므로 조치를 해야한다.
            }
        }
    }

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
