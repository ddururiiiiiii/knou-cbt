package knou.cbt.domain.statistics.dto;

import knou.cbt.domain.member.model.OAuthProvider;

public record ProviderCountResponse(OAuthProvider provider, long count) {
}
