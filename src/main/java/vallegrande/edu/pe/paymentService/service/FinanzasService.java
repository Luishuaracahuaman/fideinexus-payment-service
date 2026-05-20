package vallegrande.edu.pe.paymentService.service;

import vallegrande.edu.pe.paymentService.model.Finanzas;

import java.util.List;

public interface FinanzasService {
    List<Finanzas> listarPorTenant(Integer tenantId);
    Finanzas guardar(Finanzas finanzas);
    Finanzas cambiarEstado(Long id, String estado);
}
