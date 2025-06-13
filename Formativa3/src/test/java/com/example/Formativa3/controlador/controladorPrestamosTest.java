package com.example.Formativa3.controlador;

import com.example.Formativa3.entidades.prestamos;
import com.example.Formativa3.servicio.servicioPrestamos;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Optional;

import static com.example.Formativa3.entidades.prestamos.EstadoPrestamo.DEVUELTO;
import static com.example.Formativa3.entidades.prestamos.EstadoPrestamo.PRESTADO;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controladorPrestamos.class)
public class controladorPrestamosTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private servicioPrestamos servicioPrestamos;

    private prestamos prestamo1;
    private prestamos prestamo2;

    @BeforeEach
    void setUp() {
        prestamo1 = new prestamos();
        prestamo1.setId_prestamo(1L);
        prestamo1.setId_usuario(1L);
        prestamo1.setId_libro(1L);
        prestamo1.setFechaPrestamo(LocalDate.now());
        prestamo1.setFechaDevolucionReal(LocalDate.now().plusDays(14));
        prestamo1.setEstado(PRESTADO);

        prestamo2 = new prestamos();
        prestamo2.setId_prestamo(2L);
        prestamo2.setId_usuario(2L);
        prestamo2.setId_libro(2L);
        prestamo2.setFechaPrestamo(LocalDate.now().minusDays(5));
        prestamo2.setFechaDevolucionReal(LocalDate.now().plusDays(9));
        prestamo2.setEstado(PRESTADO);
    }

    @Test
    void testObtenerTodos() throws Exception {
        when(servicioPrestamos.listarPrestamos()).thenReturn(Arrays.asList(prestamo1, prestamo2));

        mockMvc.perform(get("/api/v1/prestamos")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.prestamosList").exists())
                .andExpect(jsonPath("$._embedded.prestamosList[0].id_prestamo").value(1))
                .andExpect(jsonPath("$._embedded.prestamosList[1].id_prestamo").value(2))
                .andExpect(jsonPath("$._links.self.href").exists());
    }

    @Test
    void testObtenerPorId_Found() throws Exception {
        when(servicioPrestamos.listarPrestamo(1L)).thenReturn(Optional.of(prestamo1));

        mockMvc.perform(get("/api/v1/prestamos/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id_prestamo").value(1))
                .andExpect(jsonPath("$._links.self.href").exists())
                .andExpect(jsonPath("$._links.todos-prestamos.href").exists());
    }

    @Test
    void testObtenerPorId_NotFound() throws Exception {
        when(servicioPrestamos.listarPrestamo(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/v1/prestamos/99")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void testObtenerPorUsuario() throws Exception {
        when(servicioPrestamos.listarPrestamosPorUsuario(1L)).thenReturn(Arrays.asList(prestamo1));

        mockMvc.perform(get("/api/v1/prestamos/usuarios/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.prestamosList").exists())
                .andExpect(jsonPath("$._embedded.prestamosList[0].id_usuario").value(1))
                .andExpect(jsonPath("$._links.self.href").exists())
                .andExpect(jsonPath("$._links.todos-prestamos.href").exists());
    }

    @Test
    void testObtenerPorLibro() throws Exception {
        when(servicioPrestamos.listarPrestamosPorLibro(1L)).thenReturn(Arrays.asList(prestamo1));

        mockMvc.perform(get("/api/v1/prestamos/libros/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.prestamosList").exists())
                .andExpect(jsonPath("$._embedded.prestamosList[0].id_libro").value(1))
                .andExpect(jsonPath("$._links.self.href").exists())
                .andExpect(jsonPath("$._links.todos-prestamos.href").exists());
    }

    @Test
    void testCrearPrestamo_Exitoso() throws Exception {
        when(servicioPrestamos.crearPrestamo(anyLong(), anyLong(), anyInt())).thenReturn(prestamo1);

        mockMvc.perform(post("/api/v1/prestamos/prestar")
                        .param("id_usuario", "1")
                        .param("id_libro", "1")
                        .param("diasPrestamo", "14")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id_prestamo").value(1))
                .andExpect(jsonPath("$._links.self.href").exists())
                .andExpect(jsonPath("$._links.todos-prestamos.href").exists());
    }

    @Test
    void testCrearPrestamo_Error() throws Exception {
        when(servicioPrestamos.crearPrestamo(anyLong(), anyLong(), anyInt()))
                .thenThrow(new RuntimeException("El libro no está disponible"));

        mockMvc.perform(post("/api/v1/prestamos/prestar")
                        .param("id_usuario", "1")
                        .param("id_libro", "1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("El libro no está disponible"));
    }

    @Test
    void testDevolverLibro_Exitoso() throws Exception {
        prestamo1.setEstado(DEVUELTO);
        when(servicioPrestamos.devolverLibro(1L)).thenReturn(prestamo1);

        mockMvc.perform(put("/api/v1/prestamos/1/devolver")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("DEVUELTO"))
                .andExpect(jsonPath("$._links.self.href").exists())
                .andExpect(jsonPath("$._links.todos-prestamos.href").exists());
    }

    @Test
    void testDevolverLibro_Error() throws Exception {
        when(servicioPrestamos.devolverLibro(99L))
                .thenThrow(new RuntimeException("Préstamo no encontrado"));

        mockMvc.perform(put("/api/v1/prestamos/99/devolver")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Préstamo no encontrado"));
    }

    @Test
    void testActualizarVencidos() throws Exception {
        doNothing().when(servicioPrestamos).actualizarPrestamosSobreDiaDeVencimiento();

        mockMvc.perform(put("/api/v1/prestamos/actualizar-vencidos")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.mensaje").value("Préstamos vencidos actualizados correctamente"));
    }

    @Test
    void testEliminarPrestamo() throws Exception {
        doNothing().when(servicioPrestamos).borrar(anyLong());

        mockMvc.perform(delete("/api/v1/prestamos/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(servicioPrestamos, times(1)).borrar(1L);
    }
}