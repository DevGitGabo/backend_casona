package pe.LaCasona.backend_casona.reposity;

import org.springframework.data.jpa.repository.JpaRepository;
import pe.LaCasona.backend_casona.models.DTO.Producto;
import pe.LaCasona.backend_casona.models.Entity.ProductoEntity;

public interface ProductoRepository extends JpaRepository<ProductoEntity, Integer> {
    ProductoEntity findByProducto (Producto producto);
}
