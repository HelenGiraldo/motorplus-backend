package com.motorplus.backend.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Supervision {
    private Long idSupervision;
    private Long idMecanicoSupervisor;
    private Long idMecanicoSupervisado;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private String observaciones;
    private LocalDateTime createdAt;

    // Para mostrar nombres
    private String nombreSupervisor;
    private String nombreSupervisado;
}
