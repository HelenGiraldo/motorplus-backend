package com.motorplus.backend.entity;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.math.BigDecimal;

@Data @NoArgsConstructor @AllArgsConstructor
public class Servicio {
    private Long idServicio;
    private String nombre;
    private String descripcion;
    private BigDecimal precioBase;
}