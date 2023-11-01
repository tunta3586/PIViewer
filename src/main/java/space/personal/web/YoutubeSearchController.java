package space.personal.web;

import java.util.ArrayList;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import space.personal.domain.Follower;
import space.personal.domain.Member;
import space.personal.domain.youtube.SearchResult;
import space.personal.domain.youtube.YoutubeIsLive;
import space.personal.service.MemberService;

@Controller
public class YoutubeSearchController {
    private MemberService memberService;

    public YoutubeSearchController(MemberService memberService) {
        this.memberService = memberService;
    }

    @GetMapping("/youtubeSearch")
    @ResponseBody
    public SearchResult searchChannel(Model model, @RequestParam("search") String query) {
        return memberService.youtubeSearchChannel(query);
    }

    @GetMapping("/isLive")
    @ResponseBody
    public ArrayList<YoutubeIsLive> checkLive(Model model, @RequestParam("userId") String userId){
        ArrayList<YoutubeIsLive> youtubeIsLives = memberService.checkFollowerIsLive(userId);
        return youtubeIsLives;
    }

    @GetMapping("/follow")
    @ResponseBody
    public void follow(Model model, 
        @RequestParam("userId") String userId, 
        @RequestParam("followName") String followName, 
        @RequestParam("youtubeChannelId") String youtubeChannelId,
        @RequestParam("twitchChannelId") String twitchChannelId
    ){
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

    @GetMapping("/unFollow")
    @ResponseBody
    public void unFollow(Model model, 
        @RequestParam("userId") String userId, 
        @RequestParam("youtubeChannelId") String youtubeChannelId){

        Member member = memberService.findUser(userId);
        memberService.unFollow(member, youtubeChannelId);
    }
}
