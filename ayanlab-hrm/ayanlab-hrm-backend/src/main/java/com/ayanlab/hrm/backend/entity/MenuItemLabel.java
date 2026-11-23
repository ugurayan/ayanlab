package com.ayanlab.hrm.backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "MenuItemLabel")
public class MenuItemLabel {

    @Id
    @Column(length = 80)
    private String id;

    @Column(length = 64)
    private String menuItemCode;

    @Column(length = 8)
    private String localeCode;

    @Column(length = 256)
    private String label;

    public static String composeId(String menuItemCode, String localeCode) {
        return menuItemCode + ":" + localeCode;
    }
}