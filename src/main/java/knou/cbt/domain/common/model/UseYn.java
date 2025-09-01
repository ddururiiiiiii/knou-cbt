package knou.cbt.domain.common.model;

public enum UseYn {
    Y("사용"),
    N("미사용");

    private final String description;

    UseYn(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}