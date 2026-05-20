package vallegrande.edu.pe.paymentService.repository;

import vallegrande.edu.pe.paymentService.model.Finanzas;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FinanzasRepository extends JpaRepository<Finanzas, Long> {
    List<Finanzas> findByTenantIdOrderByCreatedAtDesc(Integer tenantId);
}
