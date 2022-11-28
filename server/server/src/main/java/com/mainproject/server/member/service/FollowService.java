package com.mainproject.server.member.service;

import com.mainproject.server.exception.BusinessException;
import com.mainproject.server.exception.ExceptionCode;
import com.mainproject.server.member.repository.FollowRepository;
import com.mainproject.server.member.entity.Follow;
import com.mainproject.server.member.entity.Member;
import com.mainproject.server.member.repository.MemberRepository;
import com.mainproject.server.playlist.entity.Playlist;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

@RequiredArgsConstructor
@Service
public class FollowService {
    private final FollowRepository followRepository;
    private final MemberRepository memberRepository;
    private final MemberService memberService;
    private final String KEY = "Ranking";
    @Value("${spring.redis.host}")
    private String redisHost;

    @Value("${spring.redis.port}")
    private int redisPort;

    @Resource(name = "redisTemplate")
    private ZSetOperations<String, String> zSetOperations;

    public void followMember(Long memberId, Long authMemberId) {

        Member member = verifyExistsMember(memberId);

        List<Playlist> membersPlaylist = member.getPlaylists();
        int Score = 0;

        for (Playlist pl : membersPlaylist) {
            int like = pl.getLikes().size();
            Score += like;
        }

        Long followCount = followRepository.findByMember(member)// 인플루언서를 follow한 목록 Follow entity
                .stream()
                .filter(f -> f.getFollowerId().equals(authMemberId)) // 그안에 내가 있는 경우
                .count(); // 0, 1

        if (followCount == 1) {
            Follow followMember = followRepository.findByMember(member)
                    .stream()
                    .filter(f -> f.getFollowerId().equals(authMemberId))
                    .findAny().get();

            followRepository.delete(followMember);
            zSetOperations.add(KEY, member.getEmail(), (double) (member.getFollows().size() + Score) - 1);
        } else {
            Follow followMember = new Follow();
            followMember.setFollowerId(authMemberId);
            followMember.setFollowingId(memberId);
            followMember.setMember(member); // follow 당하는 멤버를 저장

            followRepository.save(followMember);
            zSetOperations.add(KEY, member.getEmail(), (double) (member.getFollows().size() + Score) + 1);
        }
    }

    public Page<Member> getRankings() {

        Set<String> range = zSetOperations.reverseRange(KEY, 0, 7);
        System.out.println("range = " + range);
        List<String> rankList = new ArrayList<>(range);

        List<Member> memberList = new ArrayList<>();

        int index = 1;
        for (String email : rankList) {
            Member member = memberRepository.findByEmail(email).get();
            member.setRanking(index);
            memberRepository.save(member);
            memberList.add(member);
            index++;
        }

        Page<Member> memberPage = new PageImpl<>(memberList);
        return memberPage;

    }

    public Integer findRank(Member member) {

        Long ranking = Long.valueOf(0);

        // index가 0부터 시작하니까 +1
        if (zSetOperations.reverseRank(KEY, member.getEmail()) != null) {
            ranking = zSetOperations.reverseRank(KEY, member.getEmail()) + 1;
            if (ranking > 6) {
                return 0;
            }
        }
        return ranking.intValue();
    }

    public Boolean followState(Long memberId, Long authMemberId) {
        Member member = verifyExistsMember(memberId);

        Long followCount = followRepository.findByMember(member)
                .stream()
                .filter(f -> f.getFollowerId().equals(authMemberId))
                .count(); // 0, 1, []
        if (followCount == 1) {
            return true;
        }
        return false; // [], 0
    }

    public Page<Member> followingMembers(Long memberId) {

        List<Member> memberList = new ArrayList<>();

        // 해당 멤버가 행한 follow
        List<Follow> follwerList = followRepository.findByFollowerId(memberId);

        for (Follow follow : follwerList) {
            Member member = memberRepository.findById(follow.getFollowingId()).get();
            memberList.add(member);
        }

        Page<Member> memberPage = new PageImpl<>(memberList);
        return memberPage;
    }

    private Member verifyExistsMember(Long memberId) {

        return memberRepository.findById(memberId).orElseThrow(
                () -> {
                    throw new BusinessException(ExceptionCode.MEMBER_NOT_EXISTS);
                });
    }

    public List<Boolean> followStates(Long memberId, Long authMemberId) {

        List<Boolean> followStates = new ArrayList<>();

        // 해당 멤버가 행한 follow
        List<Follow> follwerList = followRepository.findByFollowerId(memberId);

        // Follow 여부를 List에 저장
        for (Follow follow : follwerList) {
            Member member = memberRepository.findById(follow.getFollowingId()).get();

            Long followCount = followRepository.findByMember(member)
                    .stream()
                    .filter(f -> f.getFollowerId().equals(authMemberId))
                    .count(); // 0, 1, []

            if (followCount == 1) { followStates.add(true);}
            else {followStates.add(false);}
        }
        return followStates;
    }
}
