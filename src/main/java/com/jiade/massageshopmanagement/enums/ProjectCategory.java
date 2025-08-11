package com.jiade.massageshopmanagement.enums;

public enum ProjectCategory {
    理疗项目,
    足疗项目,
    修治项目,
    其他项目;

    public static boolean isValid(String category) {
        for (ProjectCategory c : ProjectCategory.values()) {
            if (c.name().equals(category) || c.toString().equals(category)) {
                return true;
            }
        }
        return false;
    }
}
