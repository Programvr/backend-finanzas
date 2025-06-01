package com.finanzas.backend_finanzas.dto;

import java.util.List;

public class ChangeRoleRequest {
    private List<Integer> roleIds; // IDs de los roles a asignar

    // Getters y Setters
    public List<Integer> getRoleIds() {
        return roleIds;
    }

    public void setRoleIds(List<Integer> roleIds) {
        this.roleIds = roleIds;
    }
}
