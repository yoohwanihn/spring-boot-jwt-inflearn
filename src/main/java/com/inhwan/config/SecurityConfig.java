package com.inhwan.config;

import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.filter.CorsFilter;

import com.inhwan.jwt.JwtAccessDeniedHandler;
import com.inhwan.jwt.JwtAuthenticationEntryPoint;
import com.inhwan.jwt.JwtSecurityConfig;
import com.inhwan.jwt.TokenProvider;

@EnableWebSecurity // 스프링 security 지원
@EnableMethodSecurity(prePostEnabled = true) // PreAuthorized 어노테이션을 메서드 단위로 사용하기 위해
@Configuration
public class SecurityConfig {

	private final TokenProvider tokenProvider;
	private final CorsFilter corsFilter;
	private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
	private final JwtAccessDeniedHandler jwtAccessDeniedHandler;

	public SecurityConfig(TokenProvider tokenProvider, 
						CorsFilter corsFilter,
						JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint, 
						JwtAccessDeniedHandler jwtAccessDeniedHandler) {
		this.tokenProvider = tokenProvider;
		this.corsFilter = corsFilter;
		this.jwtAuthenticationEntryPoint = jwtAuthenticationEntryPoint;
		this.jwtAccessDeniedHandler = jwtAccessDeniedHandler;
	}
	
	@Bean PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http
				// token을 사용하는 방식이기 때문에 csrf disable
	    		.csrf(csrf -> csrf.disable())
	    		
	    		// ExceptionHandler에 클래스들 추가
	    		.exceptionHandling(exceptionHandling -> exceptionHandling
	                    .accessDeniedHandler(jwtAccessDeniedHandler)
	                    .authenticationEntryPoint(jwtAuthenticationEntryPoint)
	            )
	
				.authorizeHttpRequests(authorizeHttpRequests -> authorizeHttpRequests
						.requestMatchers("/api/hello", "/api/authenticate", "/api/signup").permitAll()
						.requestMatchers(PathRequest.toH2Console()).permitAll().anyRequest().authenticated())
				
				// 세션을 사용하지 않기 때문에 STATELESS로 설정
	            .sessionManagement(sessionManagement ->
	                sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
	            )
				
				
				// enable h2-console
				.headers(headers -> headers.frameOptions(options -> options.sameOrigin()))
				
				// addFilterBefore 메서드로 등록했던 JwtSecurity 클래스도 적용
				.apply(new JwtSecurityConfig(tokenProvider));
		
		return http.build();
	}

}