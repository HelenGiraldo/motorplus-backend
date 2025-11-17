package com.motorplus.backend.service;

import com.motorplus.backend.dao.DetalleOrdenServicioDAO;
import com.motorplus.backend.entity.DetalleOrdenServicio;

import java.util.List;

public class DetalleOrdenServicioService {

    private DetalleOrdenServicioDAO detalleOrdenServicioDAO;

    public DetalleOrdenServicioService() {
        this.detalleOrdenServicioDAO = new DetalleOrdenServicioDAO();
    }

    public List<DetalleOrdenServicio> obtenerServiciosPorOrden(Long ordenId) {
        return detalleOrdenServicioDAO.findByOrdenId(ordenId);
    }

    public boolean agregarServicioAOrden(DetalleOrdenServicio detalle) {
        // Validar que no esté duplicado
        boolean existe = detalleOrdenServicioDAO.existeServicioEnOrden(
                detalle.getIdOrden(), detalle.getIdServicio()
        );

        if (existe) {
            throw new RuntimeException("El servicio ya está agregado a esta orden");
        }

        return detalleOrdenServicioDAO.agregarServicioAOrden(detalle);
    }

    public boolean eliminarServicioDeOrden(Long ordenId, Long servicioId) {
        return detalleOrdenServicioDAO.eliminarServicioDeOrden(ordenId, servicioId);
    }

    public boolean validarServicioEnOrden(Long ordenId, Long servicioId) {
        return detalleOrdenServicioDAO.existeServicioEnOrden(ordenId, servicioId);
    }
}