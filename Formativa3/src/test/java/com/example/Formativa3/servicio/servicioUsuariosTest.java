package com.example.Formativa3.servicio;

import com.example.Formativa3.entidades.usuarios;
import com.example.Formativa3.repositorio.repositorioUsuarios;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class servicioUsuariosTest {

    @Mock
    private repositorioUsuarios repositorioUsuarios;

    @InjectMocks
    private servicioUsuarios servicioUsuarios;

    private usuarios usuario1;
    private usuarios usuario2;

    @BeforeEach
    void setUp() {
        usuario1 = new usuarios();
        usuario1.setId_usuario(1L);
        usuario1.setPrimerNombre("Ana");
        usuario1.setPrimerApellido("García");
        usuario1.setEmail("ana@ejemplo.com");

        usuario2 = new usuarios();
        usuario2.setId_usuario(2L);
        usuario2.setPrimerNombre("Pedro");
        usuario2.setPrimerApellido("Martínez");
        usuario2.setEmail("pedro@ejemplo.com");
    }

    @Test
    void testListarUsuarios() {
        when(repositorioUsuarios.findAll()).thenReturn(Arrays.asList(usuario1, usuario2));

        List<usuarios> resultado = servicioUsuarios.listarUsuarios();

        assertEquals(2, resultado.size());
        assertEquals("Ana", resultado.get(0).getPrimerNombre());
        assertEquals("Pedro", resultado.get(1).getPrimerNombre());
        verify(repositorioUsuarios, times(1)).findAll();
    }

    @Test
    void testListarUsuarioPorId_Encontrado() {
        when(repositorioUsuarios.findById(1L)).thenReturn(Optional.of(usuario1));

        Optional<usuarios> resultado = servicioUsuarios.listarUsuarios(1L);

        assertTrue(resultado.isPresent());
        assertEquals("Ana", resultado.get().getPrimerNombre());
        verify(repositorioUsuarios, times(1)).findById(1L);
    }

    @Test
    void testListarUsuarioPorId_NoEncontrado() {
        when(repositorioUsuarios.findById(99L)).thenReturn(Optional.empty());

        Optional<usuarios> resultado = servicioUsuarios.listarUsuarios(99L);

        assertFalse(resultado.isPresent());
        verify(repositorioUsuarios, times(1)).findById(99L);
    }

    @Test
    void testGuardarOActualizar() {
        when(repositorioUsuarios.save(any(usuarios.class))).thenReturn(usuario1);

        usuarios usuarioGuardado = servicioUsuarios.guardarOActualizar(usuario1);

        assertEquals("Ana", usuarioGuardado.getPrimerNombre());
        verify(repositorioUsuarios, times(1)).save(usuario1);
    }

    @Test
    void testBorrar() {
        doNothing().when(repositorioUsuarios).deleteById(anyLong());

        servicioUsuarios.borrar(1L);

        verify(repositorioUsuarios, times(1)).deleteById(1L);
    }

    // Eliminar o comentar los métodos de prueba que no existen en la implementación
    // como buscarPorNombre, buscarPorApellido, buscarPorEmail
    // ya que no están implementados en servicioUsuarios
}