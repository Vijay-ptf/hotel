package com.hotel.admin_service.hotel;

public enum District {
    THIRUVANANTHAPURAM("Thiruvananthapuram"),
    KOLLAM("Kollam"),
    PATHANAMTHITTA("Pathanamthitta"),
    ALAPPUZHA("Alappuzha"),
    KOTTAYAM("Kottayam"),
    IDUKKI("Idukki"),
    ERNAKULAM("Ernakulam"),
    THRISSUR("Thrissur"),
    PALAKKAD("Palakkad"),
    MALAPPURAM("Malappuram"),
    KOZHIKODE("Kozhikode"),
    WAYANAD("Wayanad"),
    KANNUR("Kannur"),
    KASARAGOD("Kasaragod");

    private final String displayName;

    District(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}


