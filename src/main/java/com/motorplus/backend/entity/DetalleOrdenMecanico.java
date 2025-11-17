package com.motorplus.backend.entity;

import java.time.LocalDateTime;

public class DetalleOrdenMecanico {
    private Long idOrden;
    private Long idMecanico;
    private Long idServicio;
    private Long idRol;
    private Integer horasTrabajadas;
    private Double manoDeObra;

    // Datos adicionales para mostrar en la UI
    private String nombreMecanico;
    private String nombreServicio;
    private String nombreRol;

    public DetalleOrdenMecanico() {}

    public DetalleOrdenMecanico(Long idOrden, Long idMecanico, Long idServicio, Long idRol,
                                Integer horasTrabajadas, Double manoDeObra) {
        this.idOrden = idOrden;
        this.idMecanico = idMecanico;
        this.idServicio = idServicio;
        this.idRol = idRol;
        this.horasTrabajadas = horasTrabajadas;
        this.manoDeObra = manoDeObra;
    }

    // Getters y Setters
    public Long getIdOrden() { return idOrden; }
    public void setIdOrden(Long idOrden) { this.idOrden = idOrden; }

    public Long getIdMecanico() { return idMecanico; }
    public void setIdMecanico(Long idMecanico) { this.idMecanico = idMecanico; }

    public Long getIdServicio() { return idServicio; }
    public void setIdServicio(Long idServicio) { this.idServicio = idServicio; }

    public Long getIdRol() { return idRol; }
    public void setIdRol(Long idRol) { this.idRol = idRol; }

    public Integer getHorasTrabajadas() { return horasTrabajadas; }
    public void setHorasTrabajadas(Integer horasTrabajadas) { this.horasTrabajadas = horasTrabajadas; }

    public Double getManoDeObra() { return manoDeObra; }
    public void setManoDeObra(Double manoDeObra) { this.manoDeObra = manoDeObra; }

    public String getNombreMecanico() { return nombreMecanico; }
    public void setNombreMecanico(String nombreMecanico) { this.nombreMecanico = nombreMecanico; }

    public String getNombreServicio() { return nombreServicio; }
    public void setNombreServicio(String nombreServicio) { this.nombreServicio = nombreServicio; }

    public String getNombreRol() { return nombreRol; }
    public void setNombreRol(String nombreRol) { this.nombreRol = nombreRol; }
}