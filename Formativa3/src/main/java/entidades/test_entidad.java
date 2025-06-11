package entidades;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import jakarta.persistence.*;

@Entity
@Table(name = "test_nombres")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class test_entidad {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String nombre;

    public test_entidad(String nombre) {
        this.nombre = nombre;
    }
}