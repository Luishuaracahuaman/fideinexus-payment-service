package vallegrande.edu.pe.paymentService.rest;

import vallegrande.edu.pe.paymentService.model.Finanzas;
import vallegrande.edu.pe.paymentService.service.FinanzasService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/finanzas")
@CrossOrigin(origins = "*")
public class FinanzasRest {

    @Autowired
    private FinanzasService finanzasService;

    @GetMapping("/tenant/{tenantId}")
    public List<Finanzas> listarPorTenant(@PathVariable Integer tenantId) {
        return finanzasService.listarPorTenant(tenantId);
    }

    @PostMapping("/guardar")
    public Finanzas guardar(@RequestBody Finanzas finanzas) {
        return finanzasService.guardar(finanzas);
    }

    @PutMapping("/estado/{id}")
    public Finanzas cambiarEstado(@PathVariable Long id, @RequestParam String estado) {
        return finanzasService.cambiarEstado(id, estado);
    }
}
