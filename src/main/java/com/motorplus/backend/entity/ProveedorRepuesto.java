package com.motorplus.backend.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProveedorRepuesto {
    private Long idProveedorRepuesto;
    private Integer idProveedor;
    private Long idRepuesto;
    private BigDecimal precioUnitario;
    private Integer stockDisponible;

    // Campos para mostrar en frontend
    private String nombreProveedor;
    private String nombreRepuesto;
}