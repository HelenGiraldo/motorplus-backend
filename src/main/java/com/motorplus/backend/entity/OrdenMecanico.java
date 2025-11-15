package com.motorplus.backend.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "OrdenMecanico")
@IdClass(OrdenMecanicoId.class)
public class OrdenMecanico {

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idOrdenTrabajo")
    private OrdenTrabajo ordenTrabajo;

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idMecanico")
    private Mecanico mecanico;

    @Column(name = "rol", nullable = false, length = 100)
    private String rol;
}