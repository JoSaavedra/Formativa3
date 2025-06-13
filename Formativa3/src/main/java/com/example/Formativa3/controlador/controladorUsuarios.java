package com.example.Formativa3.controlador;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.Formativa3.entidades.usuarios;
import com.example.Formativa3.servicio.servicioUsuarios;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping(path = "api/v1/usuarios")
public class controladorUsuarios {

    @Autowired
    private final servicioUsuarios servicioUsuarios;

    public controladorUsuarios(servicioUsuarios servicioUsuarios) {
        this.servicioUsuarios = servicioUsuarios;
    }

    @GetMapping
    public CollectionModel<EntityModel<usuarios>> getAll() {
        List<EntityModel<usuarios>> usuariosConLinks = servicioUsuarios.listarUsuarios().stream()
                .map(usuario -> EntityModel.of(usuario,
                        linkTo(methodOn(controladorUsuarios.class).getBId(usuario.getId_usuario())).withSelfRel(),
                        linkTo(methodOn(controladorUsuarios.class).getAll()).withRel("todos-usuarios")
                ))
                .collect(Collectors.toList());

        return CollectionModel.of(usuariosConLinks,
                linkTo(methodOn(controladorUsuarios.class).getAll()).withSelfRel());
    }

    @GetMapping("/{id_usuario}")
    public ResponseEntity<EntityModel<usuarios>> getBId(@PathVariable("id_usuario") Long id_usuario) {
        Optional<usuarios> usuario = servicioUsuarios.listarUsuarios(id_usuario);

        if (usuario.isPresent()) {
            EntityModel<usuarios> usuarioConLinks = EntityModel.of(usuario.get(),
                    linkTo(methodOn(controladorUsuarios.class).getBId(id_usuario)).withSelfRel(),
                    linkTo(methodOn(controladorUsuarios.class).getAll()).withRel("todos-usuarios"));

            return ResponseEntity.ok(usuarioConLinks);
        }

        return ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<EntityModel<usuarios>> guardarActualizar(@RequestBody usuarios usuario) {
        servicioUsuarios.guardarOActualizar(usuario);

        EntityModel<usuarios> usuarioConLinks = EntityModel.of(usuario,
                linkTo(methodOn(controladorUsuarios.class).getBId(usuario.getId_usuario())).withSelfRel(),
                linkTo(methodOn(controladorUsuarios.class).getAll()).withRel("todos-usuarios"));

        return ResponseEntity.ok(usuarioConLinks);
    }

    @DeleteMapping("/{id_usuario}")
    public ResponseEntity<?> delete(@PathVariable("id_usuario") Long id_usuario) {
        servicioUsuarios.borrar(id_usuario);
        return ResponseEntity.noContent().build();
    }
}