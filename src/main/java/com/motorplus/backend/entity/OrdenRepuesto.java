package com.motorplus.backend.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrdenRepuesto {
    private Long idOrdenRepuesto;
    private Long idOrden;
    private Long idRepuesto;
    private Integer cantidad;
    private BigDecimal precioUnitario;
    private BigDecimal subtotal;

    // Campos adicionales para visualización (no están en BD)
    private String nombreRepuesto;
    private String descripcionRepuesto;
}