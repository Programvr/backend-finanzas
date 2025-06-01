package com.finanzas.backend_finanzas.dto;

public record ChangePasswordRequest(
    Integer userId, 
    String currentPassword, 
    String newPassword
) {}