package servicios;

import entidades.test_entidad;
import repositorios.test_repositorio;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class test_servicio {

    private final test_repositorio testRepositorio;

    public List<test_entidad> obtenerTodos() {
        return testRepositorio.findAll();
    }

    public test_entidad guardar(String nombre) {
        test_entidad nueva = new test_entidad(nombre);
        return testRepositorio.save(nueva);
    }

    public long contar() {
        return testRepositorio.count();
    }
}