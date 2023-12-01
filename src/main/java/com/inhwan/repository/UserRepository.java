package com.inhwan.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import com.inhwan.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {
	
	//EntityGraph 어노테이션은 query가 수행될 때 Lazy 조회가 아니라 Eager 조회로 authorities 정보를 같이 가져온다.
	@EntityGraph(attributePaths = "authorities")
	//유저 정보와 권한 정보를 같이 가져오는 메서드
	Optional<User> findOneWithAuthoritiesByUsername(String username);
}
