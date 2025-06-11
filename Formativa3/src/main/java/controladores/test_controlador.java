package controladores;

import entidades.test_entidad;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import servicios.test_servicio;

import java.util.List;

@RestController
@RequestMapping("/test")
@RequiredArgsConstructor
public class test_controlador {

    private final test_servicio testServicio;

    // GET /test - Obtener todos los nombres
    @GetMapping
    public ResponseEntity<List<test_entidad>> obtenerTodos() {
        List<test_entidad> nombres = testServicio.obtenerTodos();
        return ResponseEntity.ok(nombres);
    }

    // POST /test - Crear nuevo nombre
    @PostMapping
    public ResponseEntity<test_entidad> crear(@RequestParam String nombre) {
        test_entidad guardado = testServicio.guardar(nombre);
        return ResponseEntity.ok(guardado);
    }

    // GET /test/conexion - Probar conexión a BD
    @GetMapping("/conexion")
    public ResponseEntity<String> probarConexion() {
        try {
            long total = testServicio.contar();
            return ResponseEntity.ok("✅ Conexión exitosa. Total registros: " + total);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("❌ Error: " + e.getMessage());
        }
    }
}