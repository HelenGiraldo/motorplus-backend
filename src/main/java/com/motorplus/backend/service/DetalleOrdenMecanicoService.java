package com.motorplus.backend.service;

import com.motorplus.backend.dao.DetalleOrdenMecanicoDAO;
import com.motorplus.backend.entity.DetalleOrdenMecanico;

import java.util.List;

public class DetalleOrdenMecanicoService {

    private DetalleOrdenMecanicoDAO detalleOrdenMecanicoDAO;

    public DetalleOrdenMecanicoService() {
        this.detalleOrdenMecanicoDAO = new DetalleOrdenMecanicoDAO();
    }

    public List<DetalleOrdenMecanico> obtenerMecanicosPorOrden(Long ordenId) {
        return detalleOrdenMecanicoDAO.findByOrdenId(ordenId);
    }

    public boolean asignarMecanicoAOrden(DetalleOrdenMecanico detalle) {
        return detalleOrdenMecanicoDAO.asignarMecanico(detalle);
    }

    public boolean eliminarAsignacionMecanico(Long ordenId, Long mecanicoId, Long servicioId) {
        return detalleOrdenMecanicoDAO.eliminarAsignacion(ordenId, mecanicoId, servicioId);
    }

    public boolean actualizarHorasTrabajadas(Long ordenId, Long mecanicoId, Long servicioId, Integer horas, Double manoObra) {
        return detalleOrdenMecanicoDAO.actualizarHorasYManoObra(ordenId, mecanicoId, servicioId, horas, manoObra);
    }
}