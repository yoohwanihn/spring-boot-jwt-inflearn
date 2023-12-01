package com.inhwan.jwt;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.GenericFilterBean;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;

/* Spring Request 앞단에 붙일 Custom Filter */
public class JwtFilter extends GenericFilterBean {
	
	private static final Logger logger = LoggerFactory.getLogger(JwtFilter.class);
	
	public static final String AUTHORIZATION_HEADER = "Authorization";
	
	private TokenProvider tokenProvider;
	
	public JwtFilter(TokenProvider tokenProvider) {
		this.tokenProvider = tokenProvider;
	}

	@Override
	// JWT 토큰의 인증 정보를 현재 실행중인 시큐리티 컨텍스트에 저장하기 위한 메서드
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		
		HttpServletRequest httpServletRequest = (HttpServletRequest) request;
		String jwt = resolveToken(httpServletRequest);	// request로부터 토큰을 받는다
		String requestURI = httpServletRequest.getRequestURI();
		
		if(StringUtils.hasText(jwt) && tokenProvider.validateToken(jwt)) {	// 토큰의 유효성 검사를 수행한다.
			Authentication authentication = tokenProvider.getAuthentication(jwt);	//토큰이 유효하다면 Authentication 객체를 받아 
			SecurityContextHolder.getContext().setAuthentication(authentication); 	//객체를 set해준다
			logger.debug("Security Context에 '{}' 인증 정보를 저장했습니다, uri: {}", authentication.getName(), requestURI);
		} else {
			logger.debug("유효한 JWT 토큰이 없습니다, Uri: {}", requestURI);
		}
		
		
	}
	
	// 필터링을 하기 위해서 필요한 토큰을 전달하는 메서드
	private String resolveToken(HttpServletRequest request) {
		String bearerToken = request.getHeader(AUTHORIZATION_HEADER);	//request 헤더에서 토큰정보 꺼내옴
		if(StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
			return bearerToken.substring(7);
		}
		
		return null;
	}
}
