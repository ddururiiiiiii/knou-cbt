package knou.cbt.domain.member.service;

import knou.cbt.common.api.PageRequest;
import knou.cbt.common.api.PageResponse;
import knou.cbt.domain.member.dto.MemberResponse;
import knou.cbt.domain.member.model.Member;
import knou.cbt.domain.member.model.OAuthProvider;

public interface MemberService {

    Member findOrCreate(OAuthProvider provider, String providerId, String email, String nickname);

    MemberResponse get(Long id);

    void updateProfile(Long id, String nickname, Long departmentId);

    void withdraw(Long id);

    PageResponse<MemberResponse> listPage(String keyword, PageRequest pageRequest);
}
