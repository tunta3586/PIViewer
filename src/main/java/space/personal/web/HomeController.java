package space.personal.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import space.personal.SessionManager;
import space.personal.domain.LoginRequest;
import space.personal.domain.Member;
import space.personal.domain.SignupRequest;
import space.personal.service.MemberService;

@Controller
@CrossOrigin(origins = "localhost:3000", allowCredentials = "true")
public class HomeController {
    
    public final MemberService memberService;
    public final SessionManager sessionManager;

    public HomeController(MemberService memberService , SessionManager sessionManager){
        this.memberService = memberService;
        this.sessionManager = sessionManager;
    }

    @PostMapping("/signup")
    @ResponseBody
    public String signUp(@RequestBody SignupRequest request){
        Member member = new Member();
        member.setUsername(request.getUsername());
        member.setPassword(request.getPassword());
        
        // 중복 체크
        if(memberService.findUser(member.getUsername()) != null){
            return "false";
        }
        memberService.join(member);
        return "true";
    }

    @PostMapping("/login")
    @ResponseBody
    public String login(@RequestBody LoginRequest request, HttpServletResponse response){
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername(request.getUsername());  //username
        loginRequest.setPassword(request.getPassword());  //password
        Member member;
        // send session cookie
        member = memberService.findUser(loginRequest.getUsername());

        if(member!= null && member.getPassword().equals(loginRequest.getPassword())){
            sessionManager.createSession(member, response);
            return "true";
        }
        return "false";
    }

    @GetMapping("/logout")
    @ResponseBody
    public void logout(HttpServletRequest request){
        sessionManager.expire(request);
    }

    @GetMapping("/checkLogin")
    @ResponseBody
    public boolean checkCookies(Model model, HttpServletRequest request){
        if(sessionManager.getSession(request) != null){
            return true;    
        }
        return false;
    }
}
