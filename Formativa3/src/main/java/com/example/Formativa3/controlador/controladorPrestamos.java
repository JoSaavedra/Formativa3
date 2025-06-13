package com.example.Formativa3.controlador;

import com.example.Formativa3.entidades.prestamos;
import com.example.Formativa3.servicio.servicioPrestamos;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/api/v1/prestamos")
public class controladorPrestamos {

    @Autowired
    private servicioPrestamos servicioPrestamos;

    @GetMapping
    public CollectionModel<EntityModel<prestamos>> obtenerTodos() {
        List<EntityModel<prestamos>> prestamosConLinks = servicioPrestamos.listarPrestamos().stream()
                .map(prestamo -> EntityModel.of(prestamo,
                        linkTo(methodOn(controladorPrestamos.class).obtenerPorId(prestamo.getId_prestamo())).withSelfRel(),
                        linkTo(methodOn(controladorPrestamos.class).obtenerTodos()).withRel("todos-prestamos")
                ))
                .collect(Collectors.toList());

        return CollectionModel.of(prestamosConLinks,
                linkTo(methodOn(controladorPrestamos.class).obtenerTodos()).withSelfRel());
    }

    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<prestamos>> obtenerPorId(@PathVariable Long id) {
        Optional<prestamos> prestamo = servicioPrestamos.listarPrestamo(id);
        if (prestamo.isPresent()) {
            EntityModel<prestamos> prestamoConLinks = EntityModel.of(prestamo.get(),
                    linkTo(methodOn(controladorPrestamos.class).obtenerPorId(id)).withSelfRel(),
                    linkTo(methodOn(controladorPrestamos.class).obtenerTodos()).withRel("todos-prestamos"));
            return ResponseEntity.ok(prestamoConLinks);
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/usuarios/{id_usuario}")
    public CollectionModel<EntityModel<prestamos>> obtenerPorUsuario(@PathVariable Long id_usuario) {
        List<EntityModel<prestamos>> prestamosConLinks = servicioPrestamos.listarPrestamosPorUsuario(id_usuario).stream()
                .map(prestamo -> EntityModel.of(prestamo,
                        linkTo(methodOn(controladorPrestamos.class).obtenerPorId(prestamo.getId_prestamo())).withSelfRel(),
                        linkTo(methodOn(controladorPrestamos.class).obtenerPorUsuario(id_usuario)).withRel("prestamos-usuario"),
                        linkTo(methodOn(controladorPrestamos.class).obtenerTodos()).withRel("todos-prestamos")
                ))
                .collect(Collectors.toList());

        return CollectionModel.of(prestamosConLinks,
                linkTo(methodOn(controladorPrestamos.class).obtenerPorUsuario(id_usuario)).withSelfRel(),
                linkTo(methodOn(controladorPrestamos.class).obtenerTodos()).withRel("todos-prestamos"));
    }

    @GetMapping("/libros/{id_libro}")
    public CollectionModel<EntityModel<prestamos>> obtenerPorLibro(@PathVariable Long id_libro) {
        List<EntityModel<prestamos>> prestamosConLinks = servicioPrestamos.listarPrestamosPorLibro(id_libro).stream()
                .map(prestamo -> EntityModel.of(prestamo,
                        linkTo(methodOn(controladorPrestamos.class).obtenerPorId(prestamo.getId_prestamo())).withSelfRel(),
                        linkTo(methodOn(controladorPrestamos.class).obtenerPorLibro(id_libro)).withRel("prestamos-libro"),
                        linkTo(methodOn(controladorPrestamos.class).obtenerTodos()).withRel("todos-prestamos")
                ))
                .collect(Collectors.toList());

        return CollectionModel.of(prestamosConLinks,
                linkTo(methodOn(controladorPrestamos.class).obtenerPorLibro(id_libro)).withSelfRel(),
                linkTo(methodOn(controladorPrestamos.class).obtenerTodos()).withRel("todos-prestamos"));
    }

    @PostMapping("/prestar")
    public ResponseEntity<?> crearPrestamo(@RequestParam Long id_usuario,
                                           @RequestParam Long id_libro,
                                           @RequestParam(defaultValue = "14") int diasPrestamo) {
        try {
            prestamos nuevoPrestamo = servicioPrestamos.crearPrestamo(id_usuario, id_libro, diasPrestamo);
            EntityModel<prestamos> prestamoConLinks = EntityModel.of(nuevoPrestamo,
                    linkTo(methodOn(controladorPrestamos.class).obtenerPorId(nuevoPrestamo.getId_prestamo())).withSelfRel(),
                    linkTo(methodOn(controladorPrestamos.class).obtenerTodos()).withRel("todos-prestamos"));
            return new ResponseEntity<>(prestamoConLinks, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            Map<String, String> respuesta = new HashMap<>();
            respuesta.put("error", e.getMessage());
            return new ResponseEntity<>(respuesta, HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/{id}/devolver")
    public ResponseEntity<?> devolverLibro(@PathVariable Long id) {
        try {
            prestamos prestamo = servicioPrestamos.devolverLibro(id);
            EntityModel<prestamos> prestamoConLinks = EntityModel.of(prestamo,
                    linkTo(methodOn(controladorPrestamos.class).obtenerPorId(id)).withSelfRel(),
                    linkTo(methodOn(controladorPrestamos.class).obtenerTodos()).withRel("todos-prestamos"));
            return ResponseEntity.ok(prestamoConLinks);
        } catch (RuntimeException e) {
            Map<String, String> respuesta = new HashMap<>();
            respuesta.put("error", e.getMessage());
            return new ResponseEntity<>(respuesta, HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/actualizar-vencidos")
    public ResponseEntity<?> actualizarVencidos() {
        servicioPrestamos.actualizarPrestamosSobreDiaDeVencimiento();
        Map<String, String> respuesta = new HashMap<>();
        respuesta.put("mensaje", "Préstamos vencidos actualizados correctamente");
        return ResponseEntity.ok(respuesta);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarPrestamo(@PathVariable Long id) {
        try {
            servicioPrestamos.borrar(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            Map<String, String> respuesta = new HashMap<>();
            respuesta.put("error", "No se pudo eliminar el préstamo");
            return new ResponseEntity<>(respuesta, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}