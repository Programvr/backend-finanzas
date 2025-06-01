package com.finanzas.backend_finanzas.dto;

public record ApiResponse(boolean success, String message, Object data) {}
