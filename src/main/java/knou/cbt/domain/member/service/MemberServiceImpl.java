package knou.cbt.domain.member.service;

import knou.cbt.common.api.PageRequest;
import knou.cbt.common.api.PageResponse;
import knou.cbt.domain.member.dto.MemberResponse;
import knou.cbt.domain.member.mapper.MemberMapper;
import knou.cbt.domain.member.model.Member;
import knou.cbt.domain.member.model.OAuthProvider;
import knou.cbt.domain.statistics.service.StatisticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {

    private final MemberMapper memberMapper;
    private final StatisticsService statisticsService;

    @Override
    @Transactional
    public Member findOrCreate(OAuthProvider provider, String providerId, String email, String nickname) {
        Member member = memberMapper.findByProviderAndProviderId(provider, providerId);
        if (member == null) {
            member = Member.create(provider, providerId, email, nickname);
            memberMapper.insert(member);
        }
        memberMapper.updateLastLoginAt(member.getId());
        return memberMapper.findById(member.getId());
    }

    @Override
    public MemberResponse get(Long id) {
        return MemberResponse.from(memberMapper.findById(id));
    }

    @Override
    public void updateProfile(Long id, String nickname, Long departmentId) {
        memberMapper.updateProfile(id, nickname, departmentId);
    }

    @Override
    @Transactional
    public void withdraw(Long id) {
        // 응시 이력은 통계 집계용으로 남기되 개인 식별만 지운다 (비식별화)
        statisticsService.anonymizeMemberAttempts(id);
        memberMapper.deleteById(id);
    }

    @Override
    public PageResponse<MemberResponse> listPage(String keyword, PageRequest pageRequest) {
        long totalElements = memberMapper.countAll(keyword);
        List<MemberResponse> content = memberMapper.findPage(
                        keyword, pageRequest.sizeOrDefault(), pageRequest.offset())
                .stream()
                .map(MemberResponse::from)
                .toList();
        int totalPages = (int) Math.ceil((double) totalElements / pageRequest.sizeOrDefault());

        return new PageResponse<>(content, pageRequest.pageOrDefault(), pageRequest.sizeOrDefault(),
                totalElements, totalPages);
    }
}
