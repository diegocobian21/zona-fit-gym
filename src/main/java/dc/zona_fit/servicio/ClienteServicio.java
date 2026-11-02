package dc.zona_fit.servicio;

import dc.zona_fit.modelo.Cliente;
import dc.zona_fit.repositorio.ClienteRepositorio;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ClienteServicio implements IClienteServicio {

    //inyeccion por constructor: el campo puede ser final y la clase es facil de probar sin Spring
    private final ClienteRepositorio clienteRepositorio;

    public ClienteServicio(ClienteRepositorio clienteRepositorio) {
        this.clienteRepositorio = clienteRepositorio;
    }

    @Override
    @Transactional(readOnly = true) //solo lectura, Hibernate puede optimizar la consulta
    public List<Cliente> listarClientes() {
        return clienteRepositorio.findAll(); //regresa todos los clientes de la tabla de clientes
    }

    @Override
    @Transactional(readOnly = true)
    public Cliente buscarClientePorId(Integer idCliente) {
        return clienteRepositorio.findById(idCliente).orElse(null); //regresa un valor tipo optional, si no lo encuentra regresa null
    }

    @Override
    @Transactional
    public void guardarCliente(Cliente cliente) {
        clienteRepositorio.save(cliente); //si el id es null se hace un insert, de lo contrario un update
    }

    @Override
    @Transactional
    public void eliminarCliente(Cliente cliente) {
        clienteRepositorio.delete(cliente);
    }

    @Override
    @Transactional
    public void eliminarClientePorId(Integer idCliente) {
        clienteRepositorio.deleteById(idCliente); //no hace falta construir un objeto Cliente solo para borrar
    }
}
