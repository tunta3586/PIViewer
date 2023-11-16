package space.personal.web;

import java.util.ArrayList;
import java.util.List;

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
import space.personal.domain.TwitchSearchResult;
import space.personal.domain.youtube.SearchResult;
import space.personal.service.MemberService;
import space.personal.service.SearchService;

@Controller
@CrossOrigin(origins = "${cors.origins}", allowCredentials = "true")
public class BroadcastSearchController {
    public final MemberService memberService;
    public final SearchService searchService;
    public final SessionManager sessionManager;

    public BroadcastSearchController(MemberService memberService, SessionManager sessionManager, SearchService searchService) {
        this.memberService = memberService;
        this.sessionManager = sessionManager;
        this.searchService = searchService;
    }

    // 기능에 대해서 다시 생각
    @GetMapping("/follow")
    @ResponseBody
    public void follow(Model model, 
        @RequestParam("customUrl") String customUrl,
        HttpServletRequest request
    ){
        Member member = (Member)sessionManager.getSession(request);
        LiveConfig liveConfig = memberService.searchChannel(customUrl);
        // liveConfig 가 존재하지 않은 경우에는 동작하지 않는다.
        // 해당의 경우에는 에러를 보내거나 하는것이 옳을것으로 보인다.
        // 따로 안내를 해야할 것 같다. => Refresh 함수 같은것이 필요로 할것으로 보인다.
        if(member != null && liveConfig!= null){
            member = memberService.findUser(member.getUsername());
            if(memberService.checkFollow(member, customUrl)){
                Follower follower = new Follower();
                follower.setLiveConfig(liveConfig);
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

    // 생각해보면 크게 문제는 안될것 같다.
    // 기능은 있으면 편하고 없으면 불편한 딱 그정도
    @GetMapping("/searchChannel")
    @ResponseBody
    public SearchResult searchChannel(Model model, 
        @RequestParam("search") String customUrl, 
        HttpServletRequest request
    ){  
        SearchResult searchResult = new SearchResult();
        if(sessionManager.getSession(request) != null){
            LiveConfig liveConfig = memberService.searchChannel(customUrl);
            searchResult.setLiveConfig(liveConfig);
            if(liveConfig != null) searchResult.setSearchResult(true);
            else searchResult.setSearchResult(false);
            return searchResult;
        }
        searchResult.setSearchResult(false);
        return searchResult;
    }

    // 생각해보면 크게 문제는 안될것 같다.
    // 기능은 있으면 편하고 없으면 불편한 딱 그정도
    @GetMapping("/getFollowChannels")
    @ResponseBody
    public List<LiveConfig> getFollowChannels(Model model, 
        HttpServletRequest request
    ){  
        Member member = (Member)sessionManager.getSession(request);
        if(member != null){
            member = memberService.findUser(member.getUsername());
            List<Follower> followers = member.getFollowers();
            List<LiveConfig> configs = new ArrayList<>();
            for(Follower follower : followers){
                configs.add(follower.getLiveConfig());
            }
            return configs;
        }
        return new ArrayList<LiveConfig>();
    }

    @GetMapping("/getLiveStreamTwitchChannelId")
    @ResponseBody
    public String getLiveStreamTwitchChannelId(Model model, 
        @RequestParam("custumUrl") String customUrl, 
        HttpServletRequest request
    ){  
        Member member = (Member)sessionManager.getSession(request);
        if(member != null){
            member = memberService.findUser(member.getUsername());
            LiveConfig liveConfig = memberService.searchChannel(customUrl);
            List<Follower> followers = member.getFollowers();
            for(Follower follower : followers){
                if(follower.getLiveConfig().equals(liveConfig)){
                    return follower.getTwitch_channel_id();
                }
            }
        }
        return "";
    }

    @GetMapping("/setLiveStreamTwitchChannelId")
    @ResponseBody
    public boolean setLiveStreamTwitchChannelId(Model model, 
        @RequestParam("custumUrl") String customUrl, 
        @RequestParam("twitchChannelId") String twitchChannelId, 
        HttpServletRequest request
    ){  
        Member member = (Member)sessionManager.getSession(request);
        if(member != null){
            member = memberService.findUser(member.getUsername());
            LiveConfig liveConfig = memberService.searchChannel(customUrl);
            List<Follower> followers = member.getFollowers();
            for(Follower follower : followers){
                if(follower.getLiveConfig().equals(liveConfig)){
                    follower.setTwitch_channel_id(twitchChannelId);
                    memberService.setTwitchChannelId(follower, twitchChannelId);
                    return true;
                }
            }
        }
        return false;
    }

    @GetMapping("/twitchSearchChannel")
    @ResponseBody
    public TwitchSearchResult twitchSearchChannel(Model model, @RequestParam("search") String query, 
        HttpServletRequest request) {
        if(sessionManager.getSession(request)!= null){
            TwitchSearchResult twitchSearchResult = searchService.twitchSearchChannel(query);
            return twitchSearchResult;
        }
        return new TwitchSearchResult();
    }
}

