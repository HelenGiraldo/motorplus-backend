package com.motorplus.backend.entity;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data @NoArgsConstructor @AllArgsConstructor
public class Vehiculo {
    private Long idVehiculo;
    private String placa;
    private String marca;
    private String modelo;
    private Integer anio;
    private Long idCliente;
    private String color;
}