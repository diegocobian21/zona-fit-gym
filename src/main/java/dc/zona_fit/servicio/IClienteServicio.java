package dc.zona_fit.servicio;

import dc.zona_fit.modelo.Cliente;

import java.util.List;

//los metodos de una interfaz ya son public por defecto
public interface IClienteServicio {
    List<Cliente> listarClientes();

    Cliente buscarClientePorId(Integer idCliente);

    void guardarCliente(Cliente cliente);

    void eliminarCliente(Cliente cliente);

    void eliminarClientePorId(Integer idCliente);
}
