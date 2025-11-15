package com.motorplus.backend.entity;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data @NoArgsConstructor @AllArgsConstructor
public class Mecanico {
    private Long idMecanico;
    private String nombres;
    private String apellidos;
    private String documento;
    private String especialidad;
}