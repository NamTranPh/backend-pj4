package com.example.backend_pj4.infrastructure.databases.entities;

import com.example.backend_pj4.common.enums.RoleEnum;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = false)
public class RoleEnumConverter implements AttributeConverter<RoleEnum, String> {

    @Override
    public String convertToDatabaseColumn(RoleEnum roleEnum) {
        if (roleEnum == null) {
            return null;
        }
        return roleEnum.name();
    }

    @Override
    public RoleEnum convertToEntityAttribute(String dbData) {
        if (dbData == null) {
            return null;
        }
        return RoleEnum.valueOf(dbData);
    }
}
