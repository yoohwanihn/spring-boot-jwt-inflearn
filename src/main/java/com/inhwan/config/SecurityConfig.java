package com.inhwan.config;

import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity	//스프링 security 지원
public class SecurityConfig {

	
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http

	        	.authorizeHttpRequests(authorizeHttpRequests -> authorizeHttpRequests
	                .requestMatchers("/api/hello", "/api/authenticate", "/api/signup").permitAll()
	                .requestMatchers(PathRequest.toH2Console()).permitAll()
	                .anyRequest().authenticated())
	        
			     // enable h2-console
		        .headers(headers ->
		            headers.frameOptions(options ->
		                options.sameOrigin()
		            )
		        )	;
        
        		
        return http.build();
    }
    
}