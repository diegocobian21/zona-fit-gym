package dc.zona_fit.repositorio;

import dc.zona_fit.modelo.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;

//necesita el tipo de dato con la que se trabaja en este claso la clase Cliente y seguido la llave primaria que es tipo Integer

public interface ClienteRepositorio extends JpaRepository<Cliente,Integer> {

    //con esa interfaz se accede facilmente a los metodos para insertar, eliminar, modificar,leer informacion,son objetos genericos por eso
    //se proporciona el tipo y llave primaria, esto es la capa de datos
    //son parte de la fabrica de spring solamente extendiendo esta interfaz, se crean objetos del repositorio
}
