package knou.cbt.domain.member.dto;

import knou.cbt.domain.member.model.Member;
import knou.cbt.domain.member.model.OAuthProvider;

import java.time.LocalDateTime;

public record MemberResponse(
        Long id,
        OAuthProvider provider,
        String email,
        String nickname,
        Long departmentId,
        LocalDateTime lastLoginAt,
        LocalDateTime createdAt
) {
    public static MemberResponse from(Member member) {
        return new MemberResponse(
                member.getId(),
                member.getProvider(),
                member.getEmail(),
                member.getNickname(),
                member.getDepartmentId(),
                member.getLastLoginAt(),
                member.getCreatedAt()
        );
    }
}
