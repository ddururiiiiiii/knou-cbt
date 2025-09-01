package knou.cbt.domain.subject.model;

public enum SubjectCategory {
    MAJOR("전공"),
    LIBERAL("교양"),
    ELECTIVE("일반선택");

    private final String description;

    SubjectCategory(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}

