package com.ayanlab.hrm.backend.enums;

public enum HrmShell {
    EMPLOYER("employer"),
    WORKER("worker"),
    ADMIN("admin");

    private final String code;

    HrmShell(String code) {
        this.code = code;
    }

    public String code() {
        return code;
    }
}
