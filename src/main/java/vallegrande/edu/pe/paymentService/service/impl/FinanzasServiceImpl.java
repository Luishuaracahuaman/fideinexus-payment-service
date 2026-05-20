package vallegrande.edu.pe.paymentService.service.impl;

import vallegrande.edu.pe.paymentService.model.Finanzas;
import vallegrande.edu.pe.paymentService.repository.FinanzasRepository;
import vallegrande.edu.pe.paymentService.service.FinanzasService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FinanzasServiceImpl implements FinanzasService {

    @Autowired
    private FinanzasRepository finanzasRepository;

    @Override
    public List<Finanzas> listarPorTenant(Integer tenantId) {
        return finanzasRepository.findByTenantIdOrderByCreatedAtDesc(tenantId);
    }

    @Override
    public Finanzas guardar(Finanzas finanzas) {
        if (finanzas.getEstado() == null || finanzas.getEstado().isEmpty()) {
            finanzas.setEstado("I");
        }
        return finanzasRepository.save(finanzas);
    }

    @Override
    public Finanzas cambiarEstado(Long id, String estado) {
        Finanzas finanzas = finanzasRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Finanzas no encontrada con id: " + id));
        finanzas.setEstado(estado);
        return finanzasRepository.save(finanzas);
    }
}
