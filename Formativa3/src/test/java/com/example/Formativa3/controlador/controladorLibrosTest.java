package com.example.Formativa3.controlador;

import com.example.Formativa3.entidades.libros;
import com.example.Formativa3.servicio.servicioLibros;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Arrays;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controladorLibros.class)
public class controladorLibrosTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private servicioLibros servicioLibros;

    @Autowired
    private ObjectMapper objectMapper;

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
    void testGetAll() throws Exception {
        when(servicioLibros.listarLibros()).thenReturn(Arrays.asList(libro1, libro2));

        mockMvc.perform(get("/api/v1/libros")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.librosList").exists())
                .andExpect(jsonPath("$._embedded.librosList[0].id_libro").value(1))
                .andExpect(jsonPath("$._embedded.librosList[1].id_libro").value(2))
                .andExpect(jsonPath("$._links.self.href").exists());
    }

    @Test
    void testGetBId_Found() throws Exception {
        when(servicioLibros.listarLibros(1L)).thenReturn(Optional.of(libro1));

        mockMvc.perform(get("/api/v1/libros/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id_libro").value(1))
                .andExpect(jsonPath("$.titulo").value("Don Quijote"))
                .andExpect(jsonPath("$._links.self.href").exists())
                .andExpect(jsonPath("$._links.todos-libros.href").exists());
    }

    @Test
    void testGetBId_NotFound() throws Exception {
        when(servicioLibros.listarLibros(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/v1/libros/99")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void testGuardarActualizar() throws Exception {
        when(servicioLibros.guardarOActualizar(any(libros.class))).thenReturn(libro1);

        mockMvc.perform(post("/api/v1/libros")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(libro1))
                        .accept(MediaTypes.HAL_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id_libro").value(1))
                .andExpect(jsonPath("$._links.self.href").exists())
                .andExpect(jsonPath("$._links.todos-libros.href").exists());
    }

    @Test
    void testDelete() throws Exception {
        doNothing().when(servicioLibros).borrar(anyLong());

        mockMvc.perform(delete("/api/v1/libros/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(servicioLibros, times(1)).borrar(1L);
    }
}
