package knou.cbt.domain.member.mapper;

import knou.cbt.domain.member.model.Member;
import knou.cbt.domain.member.model.OAuthProvider;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface MemberMapper {

    Member findByProviderAndProviderId(@Param("provider") OAuthProvider provider,
                                        @Param("providerId") String providerId);

    Member findById(@Param("id") Long id);

    void insert(Member member);

    void updateLastLoginAt(@Param("id") Long id);

    void updateProfile(@Param("id") Long id,
                        @Param("nickname") String nickname,
                        @Param("departmentId") Long departmentId);

    void deleteById(@Param("id") Long id);

    List<Member> findPage(@Param("keyword") String keyword,
                           @Param("limit") int limit,
                           @Param("offset") int offset);

    long countAll(@Param("keyword") String keyword);
}
