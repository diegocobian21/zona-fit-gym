package dc.zona_fit.modelo;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;

//la implementacion es con hibernate, jpa jakarta persistence
//en entidades JPA se evita @Data/@EqualsAndHashCode: comparar por todos los campos (y el id mutable) da problemas con Hibernate
@Entity
@Getter
@Setter
@NoArgsConstructor //genera el constructor vacio (requerido por JPA)
@AllArgsConstructor //constructor con todos los argumentos para crear objetos con los atributos
@ToString
public class Cliente {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id; //no es un dato primitivo su valor es null, es un tipo object
    private String nombre;
    private String apellido;
    private Integer membresia;
}
