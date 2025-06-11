package repositorios;

import entidades.test_entidad;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface test_repositorio extends JpaRepository<test_entidad, Long> {

}