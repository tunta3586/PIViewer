package space.personal.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import space.personal.domain.Member;
import space.personal.domain.SignupRequest;
import space.personal.service.MemberService;

@Controller
public class HomeController {
    
    public final MemberService memberService;
    public HomeController(MemberService memberService){
        this.memberService = memberService;
    }

    @PostMapping("/signup")
    @ResponseBody
    public String signUp(@RequestBody SignupRequest request){
        Member member = new Member();
        member.setUsername(request.getUsername());
        member.setPassword(request.getPassword());
        
        // 중복되는 username이 있는 경우를 체크해야함
        // service에 추가해야함

        // 최종 문제가 없는경우 회원가입
        memberService.join(member);

        return "";
    }
}
