package knou.cbt.domain.exam.model;

public enum ExamType {

    ATTENDANCE("출석대체시험"),
    FINAL("기말시험"),
    SEASONAL("계절학기시험");

    private final String description;

    ExamType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
