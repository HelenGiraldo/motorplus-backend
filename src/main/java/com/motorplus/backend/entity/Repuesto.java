package com.motorplus.backend.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Repuesto {
    private Long idRepuesto;
    private Long idProveedor;
    private String nombre;
    private String descripcion;
    private BigDecimal costoUnitario;
    private BigDecimal precioVenta;
    private Integer stockDisponible;
    private Integer stockMinimo;
}