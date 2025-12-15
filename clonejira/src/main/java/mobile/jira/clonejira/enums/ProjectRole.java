package mobile.jira.clonejira.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum ProjectRole {
    MEMBER("member"),
    LEADER("leader");

    private final String value;

    ProjectRole(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue(){
        return value;
    }
}
