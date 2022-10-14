package com.hcmute.hotel.security.config;

import com.hcmute.hotel.common.UserPermission;
import com.hcmute.hotel.handler.OAuth2Handler;
import com.hcmute.hotel.repository.HttpCookieOAuth2AuthorizationRequestRepository;
import com.hcmute.hotel.security.JWT.AuthEntryPointJwt;
import com.hcmute.hotel.security.Service.AppUserDetailService;
import com.hcmute.hotel.security.filter.AuthTokenFilter;
import com.hcmute.hotel.service.CustomOauth2Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class AppSecurityConfig  {
    @Bean
    public AuthTokenFilter authenticationJwtTokenFilter() {
        return new AuthTokenFilter();
    }
    @Autowired
    private AuthEntryPointJwt unauthorizedHandler;
    @Autowired
    AppUserDetailService userDetailsService;
    @Autowired
    CustomOauth2Service customOauth2Service;
    @Autowired
    OAuth2Handler oAuth2Handler;
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authentication) throws Exception {
        return authentication.getAuthenticationManager();
    }
    @Bean
    public HttpCookieOAuth2AuthorizationRequestRepository cookieAuthorizationRequestRepository() {
        return new HttpCookieOAuth2AuthorizationRequestRepository();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .cors()
                .and()
                .csrf().disable()
                .httpBasic()
                .disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
                .exceptionHandling().authenticationEntryPoint(unauthorizedHandler);
                ;
        ;
        http
                .authorizeRequests()
                .antMatchers(HttpMethod.GET,"/api/user/**").hasAnyAuthority(UserPermission.USER_READ.getPermission())
                .antMatchers("/api/admin/**").hasAnyAuthority(UserPermission.ADMIN_READ.getPermission(),UserPermission.ADMIN_WRITE.getPermission())
                .antMatchers("/**").permitAll()
                .and()
                .oauth2Login()
                .authorizationEndpoint()
                .baseUri("/oauth2/authorization")
                .authorizationRequestRepository(cookieAuthorizationRequestRepository())
                .and()
                .userInfoEndpoint()
                .userService(customOauth2Service)
                .and()
                .successHandler(oAuth2Handler)



        ;


        http.addFilterBefore(authenticationJwtTokenFilter(), UsernamePasswordAuthenticationFilter.class);
        return http.build();

    }

}
