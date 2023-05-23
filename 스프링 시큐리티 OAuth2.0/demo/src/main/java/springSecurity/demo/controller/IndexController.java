package springSecurity.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import springSecurity.demo.config.auth.PrincipalDetails;
import springSecurity.demo.model.User;
import springSecurity.demo.repository.UserRepository;

@Controller
public class IndexController {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    @GetMapping("/test/login")
    public @ResponseBody String testLogin(Authentication authentication, @AuthenticationPrincipal UserDetails userDetails){
        System.out.println("/test/login ================");

        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        System.out.println("principal.getUser() = " + principal.getUser());

        System.out.println("userDetails = " + userDetails.getUsername());
        return "세션 정보 확인하기";
    }
    @GetMapping("/test/oauth/login")
    public @ResponseBody String testOAuthLogin(Authentication authentication){
        System.out.println("/test/oauth/login ================");

        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        System.out.println("principal.getUser() = " + oAuth2User.getAttributes());

        return "OAuth 세션 정보 확인하기";
    }
    @GetMapping({"", "/"})
    public String index() {
//        return "index";
        return "index";
    }

    // OAuth 로그인을 해도 PrincipalDetails
    // 일반 로그인을 해도 PrincipalDetails
    @GetMapping("/user")
    public @ResponseBody String user(@AuthenticationPrincipal PrincipalDetails principalDetails) {
        System.out.println("principalDetails.getUser() = " + principalDetails.getUser());
        return "user";
    }

    @GetMapping("/admin")
    public @ResponseBody String admin() {
        return "admin";
    }

    @GetMapping("/manager")
    public @ResponseBody String manager() {
        return "manager";
    }

    @GetMapping("/loginForm")
    public String loginForm() {
        return "loginForm";
    }

    @GetMapping("/joinForm")
    public  String joinForm() {
        System.out.println("true = " + true);
        return "joinForm";
    }

    @PostMapping("/join")
    public String join(User user) {
        System.out.println("user = " + user);
        user.setRole("ROLE_USER");
        userRepository.save(user);
        String rawPassword = user.getPassword();
        String encPassword = bCryptPasswordEncoder.encode(rawPassword);
        user.setPassword(encPassword);
        userRepository.save(user);
        return "redirect:/loginForm";
    }

    @Secured("ROLE_ADMIN")  //ROLE이 ADMIN인 사람만 들어갈 수 있게
    @GetMapping("/info")
    public @ResponseBody String info(){
        return "개인정보";
    }

    @PreAuthorize("hasRole('ROLE_MANAGER') or hasRole('ROLE_ADMIN')")  //ROLE이 ADMIN인 사람만 들어갈 수 있게
    @GetMapping("/data")
    public @ResponseBody String data(){
        return "개인정보";
    }
}