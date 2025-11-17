package com.motorplus.backend.entity;

import java.time.LocalDateTime;

public class Rol {
    private Long idRol;
    private String nombre;
    private String descripcion;
    private Integer nivel;
    private LocalDateTime createdAt;

    // Constructores
    public Rol() {}

    public Rol(Long idRol, String nombre, String descripcion, Integer nivel) {
        this.idRol = idRol;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.nivel = nivel;
        this.createdAt = LocalDateTime.now();
    }

    // Getters y Setters
    public Long getIdRol() {
        return idRol;
    }

    public void setIdRol(Long idRol) {
        this.idRol = idRol;
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