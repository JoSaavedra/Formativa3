package com.example.Formativa3.controlador;


import java.util.List;
import java.util.Optional;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.Formativa3.entidades.libros;
import com.example.Formativa3.servicio.servicioLibros;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;


@RestController
@RequestMapping(path = "api/v1/libros")
@Tag(name = "Libros", description = "Gestión de libros de la biblioteca")
public class controladorLibros {


    @Autowired
    private final servicioLibros servicioLibros;

    public controladorLibros(servicioLibros servicioLibros) {
        this.servicioLibros = servicioLibros;
    }

    @Operation(summary = "Obtener todos los valores de los libros", description = "Retorna una lista de todos los libros disponibles")
    @GetMapping
    public List<libros> getAll(){
        return servicioLibros.listarLibros();
    }

    @Operation(summary = "Obtener libro por ID", description = "Retorna un libro específico basado en su ID")

    @GetMapping("/{id_libro}")
    public ResponseEntity<EntityModel<libros>> getBId(@PathVariable("id_libro") Long id_libro){
        Optional<libros> libro = servicioLibros.listarLibros(id_libro);

        if (libro.isPresent()) {
            EntityModel<libros> libroConLinks = EntityModel.of(libro.get())
                    .add(linkTo(methodOn(controladorLibros.class).getBId(id_libro)).withSelfRel())
                    .add(linkTo(methodOn(controladorLibros.class).getAll()).withRel("todos-libros"));

            return ResponseEntity.ok(libroConLinks);
        }

        return ResponseEntity.notFound().build();
    }
    @PostMapping
    public void guardarActualizar(@RequestBody libros libros){
        servicioLibros.guardarOActualizar(libros);
    }
    @DeleteMapping("/{id_libro}")
    public void delete(@PathVariable("id_libro") Long id_libro){
        servicioLibros.borrar(id_libro);
    }
}


