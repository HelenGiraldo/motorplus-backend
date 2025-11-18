package com.motorplus.backend.entity;

import java.util.List;

public enum TipoServicio {
    PREVENTIVO(1L, "Preventivo"),        // ← Cambiado a PREVENTIVO
    CORRECTIVO(2L, "Correctivo"),        // ← Agregado
    ESPECIALIZADO(3L, "Especializado"),  // ← Agregado
    MECANICA(4L, "Mecánica"),
    ELECTRICIDAD(5L, "Electricidad"),
    PINTURA(6L, "Pintura"),
    DIAGNOSTICO(7L, "Diagnóstico");

    private final Long id;
    private final String nombre;

    TipoServicio(Long id, String nombre) {
        this.id = id;
        this.nombre = nombre;
    }

    public Long getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    // Método para obtener el enum desde el ID de la base de datos
    public static TipoServicio fromId(Long id) {
        for (TipoServicio tipo : values()) {
            if (tipo.getId().equals(id)) {
                return tipo;
            }
        }
        throw new IllegalArgumentException("TipoServicio no encontrado para id: " + id);
    }

    // Método para obtener el enum desde el nombre
    public static TipoServicio fromNombre(String nombre) {
        for (TipoServicio tipo : values()) {
            if (tipo.getNombre().equalsIgnoreCase(nombre)) {
                return tipo;
            }
        }
        throw new IllegalArgumentException("TipoServicio no encontrado para nombre: " + nombre);
    }

    // Método para obtener todos los tipos como lista para la UI
    public static List<TipoServicio> getAll() {
        return List.of(values());
    }
}