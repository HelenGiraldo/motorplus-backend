package com.motorplus.backend.entity;

import java.time.LocalDateTime;

public class Especialidad {
    private Long idEspecialidad;
    private String nombre;
    private String descripcion;
    private Integer nivel;
    private LocalDateTime createdAt;

    // Constructores
    public Especialidad() {}

    public Especialidad(Long idEspecialidad, String nombre, String descripcion, Integer nivel) {
        this.idEspecialidad = idEspecialidad;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.nivel = nivel;
        this.createdAt = LocalDateTime.now();
    }

    // Getters y Setters
    public Long getIdEspecialidad() {
        return idEspecialidad;
    }

    public void setIdEspecialidad(Long idEspecialidad) {
        this.idEspecialidad = idEspecialidad;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Integer getNivel() {
        return nivel;
    }

    public void setNivel(Integer nivel) {
        this.nivel = nivel;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return nombre;
    }
}