package knou.cbt.domain.examquestion.model;

public enum ImageLayout {
    GRID_2X2("2단 (2개씩 2줄)"),
    STACK_1X4("1단 (세로로 4줄)");

    private final String description;

    ImageLayout(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
