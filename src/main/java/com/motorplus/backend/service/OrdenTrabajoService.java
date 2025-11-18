package com.motorplus.backend.service;

import com.motorplus.backend.dao.OrdenTrabajoDAO;
import org.springframework.stereotype.Service;

@Service
public class OrdenTrabajoService {

    private OrdenTrabajoDAO ordenTrabajoDAO;

    public OrdenTrabajoService() {
        this.ordenTrabajoDAO = new OrdenTrabajoDAO();
    }

    public boolean facturarOrden(Long ordenId, Double subtotal, Double iva, Double total, String estado) {
        return ordenTrabajoDAO.facturarOrden(ordenId, subtotal, iva, total, estado);
    }


}