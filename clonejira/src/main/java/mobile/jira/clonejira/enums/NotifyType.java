package mobile.jira.clonejira.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum NotifyType {
    ADD_PROJECT("add_project"),
    SYSTEM("system"),
    OTHER("other"),
    ASSIGN_TASK("asssign_task"),
    UPCOMING_TASK("upcoming_task"),
    DUE_TASK("due_task"),
    ISSUE("issue");

    private final String value;
    NotifyType(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }
}
