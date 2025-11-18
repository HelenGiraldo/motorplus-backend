package com.motorplus.backend.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DireccionProveedor {
    private Integer idDireccion;
    private String direccion;
    private String ciudad;
    private String departamento;
    private Boolean esPrincipal;
    private Integer idProveedor;
}