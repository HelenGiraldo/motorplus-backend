package com.motorplus.backend.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DetalleOrdenMecanico {
    private Long idOrden;
    private Long idMecanico;
    private Long idServicio;
    private Long idRol;
    private Integer horasTrabajadas;
    private Double manoDeObra;
    private Double tarifaPorHora;
    private String nombreMecanico;
    private String nombreServicio;
    private String nombreRol;
}