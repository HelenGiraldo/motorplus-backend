package com.motorplus.backend.entity;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

@Data @NoArgsConstructor @AllArgsConstructor
public class OrdenTrabajo {
    private Long idOrdenTrabajo;
    private LocalDateTime fechaIngreso;
    private String diagnosticoInicial;
    private String estado;
    private Long idVehiculo;
}