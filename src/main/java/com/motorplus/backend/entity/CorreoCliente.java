package com.motorplus.backend.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CorreoCliente {
    private Integer idCorreo;
    private String email;
    private String tipo; // 'PERSONAL', 'CORPORATIVO'
    private Boolean esPrincipal;
    private Integer idCliente;
}