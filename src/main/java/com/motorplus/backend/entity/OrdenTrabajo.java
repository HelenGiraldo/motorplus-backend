package com.motorplus.backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "OrdenTrabajo")
public class OrdenTrabajo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idOrdenTrabajo")
    private Long idOrdenTrabajo;

    @Column(name = "fechaIngreso", nullable = false)
    private LocalDateTime fechaIngreso = LocalDateTime.now();

    @Column(name = "diagnosticoInicial", nullable = false, columnDefinition = "TEXT")
    private String diagnosticoInicial;

    @Column(name = "estado", nullable = false, length = 45)
    private String estado = "En Progreso";

    // --- Relaciones ---
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idVehiculo", nullable = false)
    @JsonIgnoreProperties("ordenesTrabajo")
    private Vehiculo vehiculo;

    @OneToOne(mappedBy = "ordenTrabajo", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnoreProperties("ordenTrabajo")
    private Factura factura;

    @OneToMany(mappedBy = "ordenTrabajo", cascade = CascadeType.ALL)
    @JsonIgnoreProperties("ordenTrabajo")
    private Set<OrdenServicio> ordenServicios;

    @OneToMany(mappedBy = "ordenTrabajo", cascade = CascadeType.ALL)
    @JsonIgnoreProperties("ordenTrabajo")
    private Set<OrdenMecanico> ordenMecanicos;

    @OneToMany(mappedBy = "ordenTrabajo", cascade = CascadeType.ALL)
    @JsonIgnoreProperties("ordenTrabajo")
    private Set<OrdenRepuesto> ordenRepuestos;
}