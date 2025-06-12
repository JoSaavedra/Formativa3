package com.example.Formativa3.repositorio;

import com.example.Formativa3.entidades.usuarios;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface repositorioUsuarios extends JpaRepository<usuarios, Long> {
}
