package com.motorplus.backend.entity;

public class DetalleOrdenServicio {
    private Long idOrden;
    private Long idServicio;
    private String descripcionTrabajo;

    // Datos para la UI
    private String nombreServicio;
    private String descripcionServicio;
    private String nombreTipoServicio;
    private Double precioBase;

    // Constructores, getters y setters...
    public DetalleOrdenServicio() {}

    public DetalleOrdenServicio(Long idOrden, Long idServicio, String descripcionTrabajo) {
        this.idOrden = idOrden;
        this.idServicio = idServicio;
        this.descripcionTrabajo = descripcionTrabajo;
    }

    // Getters y setters (generarlos o usar Lombok)
    public Long getIdOrden() { return idOrden; }
    public void setIdOrden(Long idOrden) { this.idOrden = idOrden; }

    public Long getIdServicio() { return idServicio; }
    public void setIdServicio(Long idServicio) { this.idServicio = idServicio; }

    public String getDescripcionTrabajo() { return descripcionTrabajo; }
    public void setDescripcionTrabajo(String descripcionTrabajo) { this.descripcionTrabajo = descripcionTrabajo; }

    public String getNombreServicio() { return nombreServicio; }
    public void setNombreServicio(String nombreServicio) { this.nombreServicio = nombreServicio; }

    public String getDescripcionServicio() { return descripcionServicio; }
    public void setDescripcionServicio(String descripcionServicio) { this.descripcionServicio = descripcionServicio; }

    public String getNombreTipoServicio() { return nombreTipoServicio; }
    public void setNombreTipoServicio(String nombreTipoServicio) { this.nombreTipoServicio = nombreTipoServicio; }

    public Double getPrecioBase() { return precioBase; }
    public void setPrecioBase(Double precioBase) { this.precioBase = precioBase; }
}