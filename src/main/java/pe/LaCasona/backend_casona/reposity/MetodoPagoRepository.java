package pe.LaCasona.backend_casona.reposity;

import org.springframework.data.jpa.repository.JpaRepository;
import pe.LaCasona.backend_casona.models.DTO.MetodoPago;
import pe.LaCasona.backend_casona.models.Entity.MetodoPagoEntity;
import pe.LaCasona.backend_casona.models.Entity.UsuarioEntity;

import java.util.Optional;

public interface MetodoPagoRepository extends JpaRepository<MetodoPagoEntity, Integer> {
    MetodoPagoEntity findByNombre(MetodoPago metodoPago);
}
