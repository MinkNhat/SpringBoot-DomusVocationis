package vn.nmn.domusvocationis.util.constant;

import lombok.Getter;

@Getter
public enum SessionTimeEnum {
    MORNING("Sáng"),
    AFTERNOON("Trưa"),
    EVENING("Tối"),
    ALL_DAY("Cả ngày");

    private final String displayName;

    SessionTimeEnum(String displayName) {
        this.displayName = displayName;
    }
}
