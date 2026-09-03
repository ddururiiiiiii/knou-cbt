package knou.cbt.domain.statistics.exception;

public class AttemptNotFoundException extends RuntimeException {

    public AttemptNotFoundException(Long attemptId) {
        super("응시 기록을 찾을 수 없습니다: " + attemptId);
    }
}
