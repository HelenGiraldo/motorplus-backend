package com.motorplus.backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "Factura")
public class Factura {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idFactura")
    private Long idFactura;

    @Column(name = "fechaEmision", nullable = false)
    private LocalDateTime fechaEmision = LocalDateTime.now();

    @Column(name = "estadoPago", nullable = false, length = 20)
    private String estadoPago = "Pendiente";

    @Column(name = "costoManoObra", nullable = false, precision = 10, scale = 2)
    private BigDecimal costoManoObra;

    @Column(name = "costoRepuestos", nullable = false, precision = 10, scale = 2)
    private BigDecimal costoRepuestos;

    @Column(name = "impuestos", nullable = false, precision = 10, scale = 2)
    private BigDecimal impuestos;

    @Column(name = "valorTotal", nullable = false, precision = 10, scale = 2)
    private BigDecimal valorTotal;

    // --- Relación ---
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idOrdenTrabajo", nullable = false, unique = true)
    @JsonIgnoreProperties("factura")
    private OrdenTrabajo ordenTrabajo;
}