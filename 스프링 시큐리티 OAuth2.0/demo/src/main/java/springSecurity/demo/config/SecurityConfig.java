package springSecurity.demo.config;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import springSecurity.demo.config.oauth.PrincipalOauth2UserService;

@Configuration // IoC 빈(bean)을 등록
@EnableGlobalMethodSecurity(securedEnabled = true, prePostEnabled = true)  //secured 어노테이션 활성화. prePostEnabled preAuthorize 어노테이션 활성화
public class SecurityConfig {
    // 해당 메서드의 리턴되는 오브젝트를 ioC로 등록해준다.
    // 1. 코드받기(인증), 2. 엑세스토큰(권한), 3. 사용자프로필 정보를 가져와서
    // 4-1. 그 정보를 토대로 회원가입을 자동으로 진행시키기도하고
    // 4-2 (이메일, 전화번호, 이름, 아이디) 쇼핑몰 -> (집주소), 백화점몰 -> (vip등급, 일반등급 )
    @Autowired
    private PrincipalOauth2UserService principalOauth2UserService;

//    @Bean
//    public BCryptPasswordEncoder encodePwd(){
//        return new BCryptPasswordEncoder();
//    }
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf().disable();
        http.authorizeRequests()
                .antMatchers("/user/**").authenticated() // 인증만 되면 들어갈 수 있는 주소
                .antMatchers("/manager/**").access("hasRole('ROLE_ADMIN') or hasRole('ROLE_MANAGER')") //admin이랑 manager만 manager주소에 들어갈 수 있음
                .antMatchers("/admin/**").access("hasRole('ROLE_ADMIN')")
                .anyRequest().permitAll()   //위의 주소가 아니면 인증없이 다 들어갈 수 있다.
                .and()
                .formLogin()
                .loginPage("/loginForm")
                .loginProcessingUrl("/login") // login 주소가 호출이 되면 시큐리티가 낚아채서 대신 로그인을 진행해준다.
                .defaultSuccessUrl("/") // 로그인이 완성되면 메인페이지로
                .and()
                .oauth2Login()
                .loginPage("/loginForm") // 구글 로그인이 완료된 뒤의 후처리가 필요함. Tip. 코드X, (엑세스토큰 + 사용자프로필정보 O)
                .userInfoEndpoint()
                .userService(principalOauth2UserService);

        return http.build();
    }
}