package knou.cbt.common.api;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

public record PageRequest(
        @Min(1) Integer page,
        @Min(1) @Max(100) Integer size
) {
    // 기본값 보정
    public int pageOrDefault() {
        return page == null || page < 1 ? 1 : page;
    }

    public int sizeOrDefault() {
        return size == null || size < 1 ? 10 : size;
    }

    public int offset() {
        return (pageOrDefault() - 1) * sizeOrDefault();
    }
}

