package com.motorplus.backend.entity;

import java.io.Serializable;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode // Requerido para llaves compuestas
public class OrdenServicioId implements Serializable {
    private Long ordenTrabajo; // Debe coincidir con el nombre del campo en OrdenServicio
    private Long servicio;     // Debe coincidir con el nombre del campo en OrdenServicio
}