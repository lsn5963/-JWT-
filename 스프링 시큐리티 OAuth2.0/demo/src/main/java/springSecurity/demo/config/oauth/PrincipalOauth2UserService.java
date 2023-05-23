package springSecurity.demo.config.oauth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import springSecurity.demo.config.auth.PrincipalDetails;
import springSecurity.demo.config.oauth.provider.FacebookUserInfo;
import springSecurity.demo.config.oauth.provider.GoogleUserInfo;
import springSecurity.demo.config.oauth.provider.NaverUserInfo;
import springSecurity.demo.config.oauth.provider.OAuth2UserInfo;
import springSecurity.demo.model.User;
import springSecurity.demo.repository.UserRepository;

import java.util.Map;

@Service
public class PrincipalOauth2UserService extends DefaultOAuth2UserService {
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    @Autowired
    UserRepository userRepository;
    @Override
    //여기서 후처리 진행
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        System.out.println("userRequest = " + userRequest.getClientRegistration()); //registrationId로 어떤 OAuth로 로그인 했는지 확인가능.
        System.out.println("userRequest = " + userRequest.getAccessToken());
        // 구글로그인 버튼 클릭 -> 구글로그인창 -> 로그인을 완료 -> code를 리턴(OAuth-Client 라이브러리) -> Access Token요청
        // userRequest 정보 -> 회원프로필 받아야함(loadUser함수) -> 구글로부터 회원프로필 받아준다.

        System.out.println("userRequest = " + userRequest.getClientRegistration());

        OAuth2User oAuth2User = super.loadUser(userRequest);
        OAuth2UserInfo oAuth2UserInfo = null;
        if (userRequest.getClientRegistration().getRegistrationId().equals("google")){
            System.out.println("구글 로그인 요청");
            oAuth2UserInfo = new GoogleUserInfo(oAuth2User.getAttributes());
        }else if (userRequest.getClientRegistration().getRegistrationId().equals("facebook")){
            System.out.println("페이스북 로그인 요청");
            oAuth2UserInfo = new FacebookUserInfo(oAuth2User.getAttributes());
        }else if (userRequest.getClientRegistration().getRegistrationId().equals("naver")){
            System.out.println("네이버 로그인 요청");
            oAuth2UserInfo = new NaverUserInfo((Map)oAuth2User.getAttributes().get("response"));
        }else{
            System.out.println("흠");
        }
        String provider = oAuth2UserInfo.getProvider();
        // 회원가입을 강제로 진행해볼 예정
        String providerId = oAuth2UserInfo.getProviderId();
        String username = provider+"_"+providerId;  //google_10974~~~
        String password = bCryptPasswordEncoder.encode("겟인데어");
        String email = oAuth2UserInfo.getEmail();
        String role = "ROLE_USER";


        User userEntity = userRepository.findByUsername(username);

        if (userEntity == null){
            userEntity = User.builder()
                    .username(username)
                    .password(password)
                    .email(email)
                    .role(role)
                    .provider(provider)
                    .providerId(providerId)
                    .build();
            userRepository.save(userEntity);
        }

        return new PrincipalDetails(userEntity, oAuth2User.getAttributes());
    }
}