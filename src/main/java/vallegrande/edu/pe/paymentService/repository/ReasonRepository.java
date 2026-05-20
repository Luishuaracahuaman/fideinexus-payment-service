package vallegrande.edu.pe.paymentService.repository;

import vallegrande.edu.pe.paymentService.model.Reason;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReasonRepository extends JpaRepository<Reason, Long> {
}
