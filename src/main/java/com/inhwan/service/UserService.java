package com.inhwan.service;

import java.util.Collections;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.inhwan.dto.UserDto;
import com.inhwan.entity.Authority;
import com.inhwan.entity.User;
import com.inhwan.exception.DuplicateMemberException;
import com.inhwan.exception.NotFoundMemberException;
import com.inhwan.repository.UserRepository;
import com.inhwan.util.SecurityUtil;


@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    // 회원가입을 수행하는 메서드
    public UserDto signup(UserDto userDto) {
    	
    	// 기존에 존재한 회원인지 체크
        if (userRepository.findOneWithAuthoritiesByUsername(userDto.getUsername()).orElse(null) != null) {
            throw new DuplicateMemberException("이미 가입되어 있는 유저입니다.");
        }
        
        // 아닐 경우 권한 정보 넣기
        Authority authority = Authority.builder()
                .authorityName("ROLE_USER")
                .build();
        
        // 유저 정보 저장
        User user = User.builder()
                .username(userDto.getUsername())
                .password(passwordEncoder.encode(userDto.getPassword()))
                .nickname(userDto.getNickname())
                .authorities(Collections.singleton(authority))
                .activated(true)
                .build();

        return UserDto.from(userRepository.save(user));
    }
    
    //////////////////////////////////////////////////
    /* 권한 검증 부분 */
    
    @Transactional(readOnly = true)
	// username을 기준으로 User 이름과 권한을 반환
    public UserDto getUserWithAuthorities(String username) {
        return UserDto.from(userRepository.findOneWithAuthoritiesByUsername(username).orElse(null));
    }

    @Transactional(readOnly = true)
	//현재 SecurityContext에 저장된 User 이름과 권한을 반환.
    public UserDto getMyUserWithAuthorities() {
        return UserDto.from(
                SecurityUtil.getCurrentUsername()
                        .flatMap(userRepository::findOneWithAuthoritiesByUsername)
                        .orElseThrow(() -> new NotFoundMemberException("Member not found"))
        );
    }
}
