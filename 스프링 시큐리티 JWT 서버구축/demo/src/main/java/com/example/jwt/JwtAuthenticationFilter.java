package com.example.jwt;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

// 스프링 시큐리티에서 UsernamePasswordAuthenticationFilter 가 있다.
// /login 요청해서 username, password 전송하면
// UsernamePasswordAuthenticationFilter 동작을 함
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
    private final AuthenticationManager authenticationManager;  //@RequiredArgsConstructor로 생성자를 만드는 방법과 똑같다.

    // /login 요청을 하면 로그인 시도를 위해서 실행되는 함수
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        System.out.println(" 하이 " );

        // 1. username, password 받아서
        // 2. 정상인지 로그인 시도를 해본다. authenticationManager 로 로그인 시도를 하면
        // 3. PrincipalDetailsService 가 호출 loadUserByUsername() 함수 실행됨.



        // 4. PrincipalDetails를 세션에 담고
        // 5. jwt토큰을 만들어서 응답해주면 된다.
        return super.attemptAuthentication(request, response);
    }
}
