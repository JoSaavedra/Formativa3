package com.example.Formativa3.controlador;

import com.example.Formativa3.entidades.usuarios;
import com.example.Formativa3.servicio.servicioUsuarios;
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

@WebMvcTest(controladorUsuarios.class)
public class controladorUsuariosTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private servicioUsuarios servicioUsuarios;

    @Autowired
    private ObjectMapper objectMapper;

    private usuarios usuario1;
    private usuarios usuario2;

    @BeforeEach
    void setUp() {
        usuario1 = new usuarios();
        usuario1.setId_usuario(1L);
        usuario1.setNombre("Ana");
        usuario1.setApellido("García");
        usuario1.setEmail("ana@ejemplo.com");

        usuario2 = new usuarios();
        usuario2.setId_usuario(2L);
        usuario2.setNombre("Pedro");
        usuario2.setApellido("Martínez");
        usuario2.setEmail("pedro@ejemplo.com");
    }

    @Test
    void testGetAll() throws Exception {
        when(servicioUsuarios.listarUsuarios()).thenReturn(Arrays.asList(usuario1, usuario2));

        mockMvc.perform(get("/api/v1/usuarios")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.usuariosList").exists())
                .andExpect(jsonPath("$._embedded.usuariosList[0].id_usuario").value(1))
                .andExpect(jsonPath("$._embedded.usuariosList[1].id_usuario").value(2))
                .andExpect(jsonPath("$._links.self.href").exists());
    }

    @Test
    void testGetBId_Found() throws Exception {
        when(servicioUsuarios.listarUsuarios(1L)).thenReturn(Optional.of(usuario1));

        mockMvc.perform(get("/api/v1/usuarios/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id_usuario").value(1))
                .andExpect(jsonPath("$.nombre").value("Ana"))
                .andExpect(jsonPath("$._links.self.href").exists())
                .andExpect(jsonPath("$._links.todos-usuarios.href").exists());
    }

    @Test
    void testGetBId_NotFound() throws Exception {
        when(servicioUsuarios.listarUsuarios(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/v1/usuarios/99")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void testGuardarActualizar() throws Exception {
        when(servicioUsuarios.guardarOActualizar(any(usuarios.class))).thenReturn(usuario1);

        mockMvc.perform(post("/api/v1/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(usuario1))
                        .accept(MediaTypes.HAL_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id_usuario").value(1))
                .andExpect(jsonPath("$._links.self.href").exists())
                .andExpect(jsonPath("$._links.todos-usuarios.href").exists());
    }

    @Test
    void testDelete() throws Exception {
        doNothing().when(servicioUsuarios).borrar(anyLong());

        mockMvc.perform(delete("/api/v1/usuarios/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(servicioUsuarios, times(1)).borrar(1L);
    }
}