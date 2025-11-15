package com.motorplus.backend.entity;

import java.io.Serializable;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class OrdenRepuestoId implements Serializable {
    private Long ordenTrabajo;
    private Long repuesto;
}