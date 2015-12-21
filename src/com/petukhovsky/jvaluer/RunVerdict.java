package com.petukhovsky.jvaluer;

/**
 * Created by Arthur on 12/18/2015.
 */
public enum RunVerdict {
    SUCCESS("Success"),
    CRASHED("Crashed"),
    RUNTIME_ERROR("Runtime error"),
    TIME_LIMIT_EXCEEDED("Time limit exceeded"),
    MEMORY_LIMIT_EXCEEDED("Memory limit exceeded"),
    IDLENESS_LIMIT_EXCEEDED("Idleness limit exceeded"),
    SECURITY_VIOLATION("Security violation"),
    FAIL("Fail");

    private String text;
    RunVerdict(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }
}
