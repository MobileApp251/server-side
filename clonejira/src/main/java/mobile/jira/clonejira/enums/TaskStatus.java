package mobile.jira.clonejira.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum TaskStatus {
    OPEN("open"),
    PROGRESS("progress"),
    DONE("done"),
    REOPEN("reopen"),
    CLOSE("close");
    
    private final String value;
    
    TaskStatus(String value) {
        this.value = value;
    }
    
    @JsonValue
    public String getValue() {
        return value;
    }
}
