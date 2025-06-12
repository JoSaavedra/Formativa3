package com.example.Formativa3.entidades;

import lombok.Data;
import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import java.util.*;

@Data
@Entity
@Table(name = "tabla_usuarios")
public class usuarios {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id_usuario;

    private String primerNombre;

    private String primerApellido;

    @Column(name = "correo_electronico", unique = true, nullable = false)
    private String email;

    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference("usuario-prestamos")
    private List<prestamos> listaPrestamos = new ArrayList<>();
}
