package com.finanzas.backend_finanzas.dto;

import java.util.List;


public record SearchEmailResponse(
        Integer id,
        String email,
        Boolean activo,
        List<Integer> roleIds

) {}