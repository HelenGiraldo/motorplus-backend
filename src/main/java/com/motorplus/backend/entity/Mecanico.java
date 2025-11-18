package com.motorplus.backend.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Mecanico {
    private Long idMecanico;
    private String nombres;
    private String apellidos;
    private String documento;
    private BigDecimal tarifaPorHora;
    private String telefono;
    private String correo;
    private String direccion;
    private List<TelefonoMecanico> telefonos;
    private List<CorreoMecanico> correos;
    private List<Especialidad> especialidades; // NUEVO: lista de especialidades
}