package com.motorplus.backend.entity;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class OrdenRepuesto {
    private Long idOrden;
    private Long idRepuesto;
    private Integer cantidad;
    private BigDecimal precioUnitario;
    private BigDecimal subtotal;
    private String nombreRepuesto;
    private String descripcionRepuesto;
    private Integer stockDisponible;

    // Método para identificar único (usando la clave compuesta)
    public String getIdentificadorUnico() {
        return idOrden + "-" + idRepuesto;
    }
}