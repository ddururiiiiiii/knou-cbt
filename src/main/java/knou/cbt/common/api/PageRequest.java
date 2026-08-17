package knou.cbt.common.api;

// page/size는 컨트롤러에서 검증하지 않고 항상 pageOrDefault()/sizeOrDefault()로 보정해서 씀
// (잘못된 값이 들어와도 500 대신 조용히 기본값으로 처리하기 위함)
public record PageRequest(
        Integer page,
        Integer size
) {
    // 기본값 보정
    public int pageOrDefault() {
        return page == null || page < 1 ? 1 : page;
    }

    public int sizeOrDefault() {
        if (size == null || size < 1) {
            return 10;
        }
        return Math.min(size, 100);
    }

    public int offset() {
        return (pageOrDefault() - 1) * sizeOrDefault();
    }
}

