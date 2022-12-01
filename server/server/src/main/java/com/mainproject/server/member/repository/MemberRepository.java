package com.mainproject.server.member.repository;

import com.mainproject.server.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {

    Optional<Member> findByEmail(String email);
    List<Member> findByNameContaining(String name);
    boolean existsByName(String name);
    boolean existsByEmail(String email);

}
