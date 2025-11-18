package com.motorplus.backend.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DireccionMecanico {
    private Integer idDireccionMecanico;
    private String direccion;
    private String ciudad;
    private String provincia;
    private String codigoPostal;
    private Long idMecanico;

}