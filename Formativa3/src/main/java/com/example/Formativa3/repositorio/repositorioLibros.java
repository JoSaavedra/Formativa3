package com.example.Formativa3.repositorio;

import com.example.Formativa3.entidades.libros;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface repositorioLibros extends JpaRepository<libros, Long> {
}
