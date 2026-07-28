package knou.cbt.domain.examquestion.model;

public enum OptionType {
    TEXT("텍스트"),
    IMAGE("이미지");

    private final String description;

    OptionType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
