package knou.cbt.common.api;

import java.util.List;

/**
 * 페이징 응답을 표준화하기 위한 DTO
 */
public record PageResponse<T>(
        List<T> content,   // 현재 페이지 데이터
        int currentPage,   // 현재 페이지 번호 (1부터)
        int size,          // 페이지 크기
        long totalElements,// 전체 데이터 개수
        int totalPages     // 전체 페이지 수
) {}