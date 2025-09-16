package com.example.backend_pj4.domain.enums;

public enum MembershipStatus {
    FREE("free", "Miễn phí"),
    PREMIUM("premium", "Premium"), 
    VIP("vip", "VIP");
    
    private final String value;
    private final String displayName;
    
    MembershipStatus(String value, String displayName) {
        this.value = value;
        this.displayName = displayName;
    }
    
    public String getValue() {
        return value;
    }
    
    public String getDisplayName() {
        return displayName;
    }
}