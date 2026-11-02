package dc.zona_fit;

import dc.zona_fit.modelo.Cliente;
import dc.zona_fit.servicio.IClienteServicio;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.List;
import java.util.Scanner;


//@SpringBootApplication
public class ZonaFitApplication implements CommandLineRunner {

    @Autowired
    private IClienteServicio clienteServicio; //importamos la interface inyectando la dependencia a la capa de presentacion

    String nl = System.lineSeparator(); //caracter de separador de linea de manera generica para cualquier plataforma

    //concepto de loggin
    private static final Logger logger = LoggerFactory.getLogger(ZonaFitApplication.class); //indicamos la clase que manda informacion a consola

	public static void main(String[] args) {
        logger.info("Iniciando la aplicacion");
        //levantar la fabrica de spring
		SpringApplication.run(ZonaFitApplication.class, args);
        logger.info("aplicacion finalizada");
	}

    @Override
    public void run(String... args) throws Exception { //cuando ya estan todos los objetos listos de la fabrica de spring ahora si se puede usar
        zonaFitApp();
    }

    private void zonaFitApp(){
        var salir = false;
        var consola = new Scanner(System.in); //mientras la variable de salir sea verdader, se ejecuta el menu
        while (!salir){
            var opcion = mostrarMenu(consola);
            salir = ejecutarOpciones(consola,opcion);
            logger.info(nl); //ya no se usa print ya que es para aplicaciones mas basicas
        }
    }

    private int mostrarMenu(Scanner consola){
        logger.info("""
        \n**** APLICACION ZONA FIT GYM ****
        1. Listar Clientes
        2. Buscar Cliente
        3. Agregar Cliente
        4. Modificar cliente
        5. Eliminar cliente
        6. Salir
        Elige una opcion:\s""");
        return Integer.parseInt(consola.nextLine());

    }

    private boolean ejecutarOpciones(Scanner consola, int opcion){
        var salir = false;
        switch (opcion){
            case 1 -> {
                logger.info(nl + "--- Listado de Clientes ---" + nl);
                List<Cliente> clientes = clienteServicio.listarClientes();
                clientes.forEach(cliente -> logger.info(cliente.toString() + nl));
            }
            case 2 ->{
                logger.info(nl + "--- Buscar Cliente por Id --- " + nl);
                logger.info("Id cliente a buscar: ");
                var idCliente = Integer.parseInt(consola.nextLine());
                Cliente cliente = clienteServicio.buscarClientePorId(idCliente);
                if (cliente != null)
                    logger.info("Cliente encontrado: " + cliente + nl);
                else
                    logger.info("Cliente no encontrado: " + cliente + nl);
            }
            case 3 ->{
                logger.info("--- Agregar Cliente ---" + nl);
                logger.info("Nombre: ");
                var nombre = consola.nextLine();
                logger.info("Apellido: ");
                var apellido = consola.nextLine();
                logger.info("membresia: ");
                var membresia = Integer.parseInt(consola.nextLine());
                var cliente = new Cliente();
                cliente.setNombre(nombre);
                cliente.setApellido(apellido); //uso de loombok, inicializar valores del objeto
                cliente.setMembresia(membresia);
                clienteServicio.guardarCliente(cliente);
                logger.info("Cliente agregado: " + cliente + nl);
            }
            case 4 ->{
                logger.info("--- Modificar cliente ---" + nl);
                logger.info("Id Cliente: ");
                var idCliente = Integer.parseInt(consola.nextLine());
                Cliente cliente = clienteServicio.buscarClientePorId(idCliente);
                if (cliente != null){
                    logger.info("Nombre: ");
                    var nombre = consola.nextLine();
                    logger.info("Apellido: ");
                    var apellido = consola.nextLine();
                    logger.info("Membresia: ");
                    var membresia = Integer.parseInt(consola.nextLine());
                    cliente.setNombre(nombre);
                    cliente.setApellido(apellido);
                    cliente.setMembresia(membresia);
                    clienteServicio.guardarCliente(cliente);
                    logger.info("Cliente modificado: " + cliente + nl);
                }
                else
                    logger.info("Cliente no encontrado: " + cliente + nl);
            }
            case 5 ->{
                logger.info("--- Eliminar Cliente ----" + nl);
                logger.info("Id Cliente: ");
                var idCliente = Integer.parseInt(consola.nextLine());
                var cliente = clienteServicio.buscarClientePorId(idCliente);
                if (cliente != null){
                    clienteServicio.eliminarCliente(cliente);
                    logger.info("Cliente eliminado: " + cliente + nl);
                }
                else
                    logger.info("Cliente no encontrado: " + cliente + nl);
            }
            case 6 ->{
                logger.info("Hasta pronto!" + nl + nl);
                salir = true;
            }
            default -> logger.info("Opcion no reconocida: " + opcion + nl);
        }
        return salir;

    }
}
