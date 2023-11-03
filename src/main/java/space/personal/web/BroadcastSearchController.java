package space.personal.web;

import java.util.ArrayList;

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
import space.personal.domain.SearchResult;
import space.personal.domain.youtube.YoutubeIsLive;
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

    @GetMapping("/youtubeSearch")
    @ResponseBody
    public SearchResult searchChannel(Model model, @RequestParam("search") String query, HttpServletRequest request) {
        if(sessionManager.getSession(request)!= null){
            SearchResult searchResult = new SearchResult();
            searchResult.setTwitchSearchResult(memberService.twitchSearchChannel(query));
            searchResult.setYoutubeSearchResult(memberService.youtubeSearchChannel(query));
            return searchResult;
        }
        return new SearchResult();
    }

    @GetMapping("/isLive")
    @ResponseBody
    public ArrayList<YoutubeIsLive> checkLive(Model model, @RequestParam("userId") String userId, HttpServletRequest request){
        if(sessionManager.getSession(request) != null){
            return memberService.checkFollowerIsLive(userId);
        }
        return null;
    }

    @GetMapping("/follow")
    @ResponseBody
    public void follow(Model model, 
        @RequestParam("userId") String userId, 
        @RequestParam("followName") String followName, 
        @RequestParam("youtubeChannelId") String youtubeChannelId,
        @RequestParam("twitchChannelId") String twitchChannelId,
        HttpServletRequest request
    ){
        if(sessionManager.getSession(request) != null){
            Member member = memberService.findUser(userId);

            if(memberService.checkFollow(member, youtubeChannelId)){
                Follower follower = new Follower();
                follower.setName(followName);
                follower.setYoutubeChannelId(youtubeChannelId);
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

    @GetMapping("/searchLiveConfig")
    @ResponseBody
    public LiveConfig searchLiveConfig(Model model,
        @RequestParam("userId") String userId, 
        @RequestParam("youtubeChannelId") String youtubeChannelId,
        HttpServletRequest request
    ){
        if(sessionManager.getSession(request) != null){
            LiveConfig liveConfig = new LiveConfig();
            Follower follower = memberService.findFollower(memberService.findUser(userId), youtubeChannelId);
            liveConfig.setYoutubeLiveVideoId(memberService.youtubeLiveVideoIdSearch(youtubeChannelId));
            liveConfig.setTwitchChannelId(follower.getTwitchChannelId());
            return liveConfig;
        }
        return null;
    }
}
