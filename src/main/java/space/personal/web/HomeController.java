package space.personal.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import space.personal.domain.Member;
import space.personal.service.MemberService;

@Controller
public class HomeController {
    
    public final MemberService memberService;
    public HomeController(MemberService memberService){
        this.memberService = memberService;
    }

    @GetMapping("/signup")
    public boolean signUp(Model model){
        Member member = new Member();
        member.setUsername("test2");
        member.setPassword("test2");
        memberService.join(member);

        return true;
    }
}
