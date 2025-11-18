package com.motorplus.backend.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CorreoProveedor {
    private Integer idCorreo;
    private String email;
    private String tipo;
    private Boolean esPrincipal;
    private Integer idProveedor;
}