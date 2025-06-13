package com.example.Formativa3.servicio;

import com.example.Formativa3.entidades.libros;
import com.example.Formativa3.entidades.prestamos;
import com.example.Formativa3.entidades.usuarios;
import com.example.Formativa3.repositorio.repositorioLibros;
import com.example.Formativa3.repositorio.repositorioPrestamos;
import com.example.Formativa3.repositorio.repositorioUsuarios;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class servicioPrestamosTest {

    @Mock
    private repositorioPrestamos repositorioPrestamos;

    @Mock
    private repositorioUsuarios repositorioUsuarios;

    @Mock
    private repositorioLibros repositorioLibros;

    @InjectMocks
    private servicioPrestamos servicioPrestamos;

    private usuarios usuario;
    private libros libro;
    private prestamos prestamo;

    @BeforeEach
    void setUp() {
        usuario = new usuarios();
        usuario.setId_usuario(1L);
        usuario.setNombre("Juan");
        usuario.setApellido("Pérez");
        usuario.setEmail("juan@ejemplo.com");

        libro = new libros();
        libro.setId_libro(1L);
        libro.setTitulo("El principito");
        libro.setAutor("Antoine de Saint-Exupéry");
        libro.setDisponible(true);

        prestamo = new prestamos();
        prestamo.setId_prestamo(1L);
        prestamo.setId_usuario(1L);
        prestamo.setId_libro(1L);
        prestamo.setFecha_prestamo(LocalDate.now());
        prestamo.setFecha_devolucion(LocalDate.now().plusDays(14));
        prestamo.setEstado("ACTIVO");
    }

    @Test
    void testListarPrestamos() {
        when(repositorioPrestamos.findAll()).thenReturn(Arrays.asList(prestamo));

        List<prestamos> resultado = servicioPrestamos.listarPrestamos();

        assertEquals(1, resultado.size());
        assertEquals(1L, resultado.get(0).getId_prestamo());
    }

    @Test
    void testListarPrestamo() {
        when(repositorioPrestamos.findById(1L)).thenReturn(Optional.of(prestamo));

        Optional<prestamos> resultado = servicioPrestamos.listarPrestamo(1L);

        assertTrue(resultado.isPresent());
        assertEquals(1L, resultado.get().getId_prestamo());
    }

    @Test
    void testListarPrestamosPorUsuario() {
        when(repositorioPrestamos.findByIdUsuario(1L)).thenReturn(Arrays.asList(prestamo));

        List<prestamos> resultado = servicioPrestamos.listarPrestamosPorUsuario(1L);

        assertEquals(1, resultado.size());
        assertEquals(1L, resultado.get(0).getId_usuario());
    }

    @Test
    void testListarPrestamosPorLibro() {
        when(repositorioPrestamos.findByIdLibro(1L)).thenReturn(Arrays.asList(prestamo));

        List<prestamos> resultado = servicioPrestamos.listarPrestamosPorLibro(1L);

        assertEquals(1, resultado.size());
        assertEquals(1L, resultado.get(0).getId_libro());
    }

    @Test
    void testCrearPrestamo_Exitoso() {
        when(repositorioUsuarios.findById(1L)).thenReturn(Optional.of(usuario));
        when(repositorioLibros.findById(1L)).thenReturn(Optional.of(libro));
        when(repositorioPrestamos.save(any(prestamos.class))).thenReturn(prestamo);

        prestamos resultado = servicioPrestamos.crearPrestamo(1L, 1L, 14);

        assertEquals(1L, resultado.getId_usuario());
        assertEquals(1L, resultado.getId_libro());
        assertEquals("ACTIVO", resultado.getEstado());
        assertEquals(LocalDate.now().plusDays(14), resultado.getFecha_devolucion());

        verify(repositorioLibros).save(any(libros.class)); // Verificar que se actualiza el libro
    }

    @Test
    void testCrearPrestamo_UsuarioNoExiste() {
        when(repositorioUsuarios.findById(99L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(RuntimeException.class, () -> {
            servicioPrestamos.crearPrestamo(99L, 1L, 14);
        });

        assertEquals("Usuario no encontrado", exception.getMessage());
    }

    @Test
    void testCrearPrestamo_LibroNoExiste() {
        when(repositorioUsuarios.findById(1L)).thenReturn(Optional.of(usuario));
        when(repositorioLibros.findById(99L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(RuntimeException.class, () -> {
            servicioPrestamos.crearPrestamo(1L, 99L, 14);
        });

        assertEquals("Libro no encontrado", exception.getMessage());
    }

    @Test
    void testCrearPrestamo_LibroNoDisponible() {
        libro.setDisponible(false);
        when(repositorioUsuarios.findById(1L)).thenReturn(Optional.of(usuario));
        when(repositorioLibros.findById(1L)).thenReturn(Optional.of(libro));

        Exception exception = assertThrows(RuntimeException.class, () -> {
            servicioPrestamos.crearPrestamo(1L, 1L, 14);
        });

        assertEquals("El libro no está disponible", exception.getMessage());
    }

    @Test
    void testDevolverLibro_Exitoso() {
        when(repositorioPrestamos.findById(1L)).thenReturn(Optional.of(prestamo));
        when(repositorioLibros.findById(1L)).thenReturn(Optional.of(libro));
        when(repositorioPrestamos.save(any(prestamos.class))).thenReturn(prestamo);

        prestamos resultado = servicioPrestamos.devolverLibro(1L);

        assertEquals("DEVUELTO", resultado.getEstado());
        verify(repositorioLibros).save(any(libros.class)); // Verificar que se actualiza el libro
    }

    @Test
    void testDevolverLibro_PrestamoNoEncontrado() {
        when(repositorioPrestamos.findById(99L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(RuntimeException.class, () -> {
            servicioPrestamos.devolverLibro(99L);
        });

        assertEquals("Préstamo no encontrado", exception.getMessage());
    }

    @Test
    void testDevolverLibro_LibroNoEncontrado() {
        when(repositorioPrestamos.findById(1L)).thenReturn(Optional.of(prestamo));
        when(repositorioLibros.findById(1L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(RuntimeException.class, () -> {
            servicioPrestamos.devolverLibro(1L);
        });

        assertEquals("Libro no encontrado", exception.getMessage());
    }

    @Test
    void testActualizarPrestamosSobreDiaDeVencimiento() {
        prestamo.setFecha_devolucion(LocalDate.now().minusDays(1)); // Préstamo vencido
        when(repositorioPrestamos.findByEstado("ACTIVO")).thenReturn(Arrays.asList(prestamo));

        servicioPrestamos.actualizarPrestamosSobreDiaDeVencimiento();

        verify(repositorioPrestamos).save(any(prestamos.class));
    }

    @Test
    void testBorrar() {
        doNothing().when(repositorioPrestamos).deleteById(anyLong());

        servicioPrestamos.borrar(1L);

        verify(repositorioPrestamos).deleteById(1L);
    }
}