package mobile.jira.clonejira.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum TaskPriority {
    HIGH("high"),
    MEDIUM("medium"),
    LOW("low");

    private final String value;

    TaskPriority(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }
}
