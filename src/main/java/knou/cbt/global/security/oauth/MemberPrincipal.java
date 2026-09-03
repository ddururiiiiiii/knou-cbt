package knou.cbt.global.security.oauth;

import knou.cbt.domain.member.model.Member;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.List;
import java.util.Map;

@Getter
public class MemberPrincipal implements OAuth2User {

    private final Long memberId;
    private final String nickname;
    private final Map<String, Object> attributes;

    public MemberPrincipal(Member member, Map<String, Object> attributes) {
        this.memberId = member.getId();
        this.nickname = member.getNickname();
        this.attributes = attributes;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_USER"));
    }

    @Override
    public String getName() {
        return String.valueOf(memberId);
    }
}
