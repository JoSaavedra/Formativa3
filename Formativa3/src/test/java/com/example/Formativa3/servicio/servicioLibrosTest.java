package com.example.Formativa3.servicio;

import com.example.Formativa3.entidades.libros;
import com.example.Formativa3.repositorio.repositorioLibros;
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
public class servicioLibrosTest {

    @Mock
    private repositorioLibros repositorioLibros;

    @InjectMocks
    private servicioLibros servicioLibros;

    private libros libro1;
    private libros libro2;

    @BeforeEach
    void setUp() {
        libro1 = new libros();
        libro1.setId_libro(1L);
        libro1.setNombreLibro("Don Quijote");
        libro1.setAutor("Miguel de Cervantes");
        libro1.setDisponible(true);

        libro2 = new libros();
        libro2.setId_libro(2L);
        libro2.setNombreLibro("Cien años de soledad");
        libro2.setAutor("Gabriel García Márquez");
        libro2.setDisponible(true);
    }


    @Test
    void testListarLibros() {
        when(repositorioLibros.findAll()).thenReturn(Arrays.asList(libro1, libro2));

        List<libros> resultado = servicioLibros.listarLibros();

        assertEquals(2, resultado.size());
        assertEquals("Don Quijote", resultado.get(0).getNombreLibro());
        assertEquals("Cien años de soledad", resultado.get(1).getNombreLibro());
        verify(repositorioLibros, times(1)).findAll();
    }

    @Test
    void testListarLibroPorId_Encontrado() {
        when(repositorioLibros.findById(1L)).thenReturn(Optional.of(libro1));

        Optional<libros> resultado = servicioLibros.listarLibros(1L);

        assertTrue(resultado.isPresent());
        assertEquals("Don Quijote", resultado.get().getNombreLibro());
        verify(repositorioLibros, times(1)).findById(1L);
    }

    @Test
    void testListarLibroPorId_NoEncontrado() {
        when(repositorioLibros.findById(99L)).thenReturn(Optional.empty());

        Optional<libros> resultado = servicioLibros.listarLibros(99L);

        assertFalse(resultado.isPresent());
        verify(repositorioLibros, times(1)).findById(99L);
    }

    @Test
    void testGuardarOActualizar() {
        when(repositorioLibros.save(any(libros.class))).thenReturn(libro1);

        libros libroGuardado = servicioLibros.guardarOActualizar(libro1);

        assertEquals("Don Quijote", libroGuardado.getNombreLibro());
        verify(repositorioLibros, times(1)).save(libro1);
    }

    @Test
    void testBorrar() {
        doNothing().when(repositorioLibros).deleteById(anyLong());

        servicioLibros.borrar(1L);

        verify(repositorioLibros, times(1)).deleteById(1L);
    }
}