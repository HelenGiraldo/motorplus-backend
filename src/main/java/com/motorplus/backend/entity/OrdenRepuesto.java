package com.motorplus.backend.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "OrdenRepuesto")
@IdClass(OrdenRepuestoId.class)
public class OrdenRepuesto {

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idOrdenTrabajo")
    private OrdenTrabajo ordenTrabajo;

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idRepuesto")
    private Repuesto repuesto;

    @Column(name = "cantidad", nullable = false)
    private Integer cantidad;

    @Column(name = "costoAlMomento", nullable = false, precision = 10, scale = 2)
    private BigDecimal costoAlMomento;
}