package com.motorplus.backend.service;

import com.motorplus.backend.dao.FacturaDAO;
import com.motorplus.backend.entity.Factura;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class FacturaService {

    private final FacturaDAO facturaDAO = new FacturaDAO();

    public List<Factura> getAll() {
        return facturaDAO.findAll();
    }

    public Factura getById(Long id) {
        return facturaDAO.findById(id);
    }

    public Factura create(Factura factura) {
        return facturaDAO.save(factura);
    }

    public boolean updateEstado(Long id, String estado) {
        return facturaDAO.updateStatus(id, estado);
    }
}


