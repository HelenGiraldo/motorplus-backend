package com.motorplus.backend.controller;

import com.motorplus.backend.entity.TipoServicio;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/tipos-servicio")
@CrossOrigin(origins = "*")
public class TipoServicioController {

    @GetMapping
    public List<Map<String, Object>> getAllTiposServicio() {
        List<Map<String, Object>> tiposList = new ArrayList<>();

        for (TipoServicio tipo : TipoServicio.values()) {
            Map<String, Object> tipoMap = new HashMap<>();
            tipoMap.put("id", tipo.getId());
            tipoMap.put("nombre", tipo.getNombre());
            tiposList.add(tipoMap);
        }

        return tiposList;
    }

    @GetMapping("/{id}")
    public Map<String, Object> getTipoServicioById(@PathVariable Long id) {
        TipoServicio tipo = TipoServicio.fromId(id);
        Map<String, Object> tipoMap = new HashMap<>();
        tipoMap.put("id", tipo.getId());
        tipoMap.put("nombre", tipo.getNombre());
        return tipoMap;
    }
}