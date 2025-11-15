package com.motorplus.backend.entity;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data @NoArgsConstructor @AllArgsConstructor
public class Factura {
    private Long idFactura;
    private LocalDateTime fechaEmision;
    private String estadoPago;
    private BigDecimal costoManoObra;
    private BigDecimal costoRepuestos;
    private BigDecimal impuestos;
    private BigDecimal valorTotal;
    private Long idOrdenTrabajo;
}