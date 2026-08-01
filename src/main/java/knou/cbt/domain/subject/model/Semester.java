package knou.cbt.domain.subject.model;

public enum Semester {
    FIRST("1학기"),
    SECOND("2학기");

    private final String description;

    Semester(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
