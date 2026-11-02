package dc.zona_fit.gui;

import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.icons.FlatSearchIcon;
import com.formdev.flatlaf.util.ColorFunctions;
import dc.zona_fit.modelo.Cliente;
import dc.zona_fit.servicio.IClienteServicio;
import org.springframework.stereotype.Component;

import javax.swing.*;
import javax.swing.border.MatteBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.regex.Pattern;

@Component
public class ZonaFitForma extends JFrame {
    //indices de las columnas de la tabla, evitamos "numeros magicos" repartidos por el codigo
    private static final int COL_ID = 0;
    private static final int COL_NOMBRE = 1;
    private static final int COL_APELLIDO = 2;
    private static final int COL_MEMBRESIA = 3;

    //paleta de la aplicacion
    private static final Color COLOR_ACENTO = new Color(0xF5B83D);
    private static final Color COLOR_PRIMARIO = new Color(0x2E7D32);
    private static final Color COLOR_PELIGRO = new Color(0xC62828);

    //componentes enlazados con ZonaFitForma.form
    private JPanel panelPrincipal;
    private JPanel encabezadoPanel;
    private JLabel tituloLabel;
    private JLabel subtituloLabel;
    private JLabel totalClientesLabel;
    private JPanel formularioPanel;
    private JLabel formularioTituloLabel;
    private JTextField nombreTexto;
    private JTextField apellidoTexto;
    private JTextField membresiaTexto;
    private JButton guardarButton;
    private JButton limpiarButton;
    private JPanel tablaPanel;
    private JLabel listadoTituloLabel;
    private JTextField buscarTexto;
    private JScrollPane tablaScroll;
    private JTable clientesTabla;
    private JLabel ayudaLabel;
    private JButton eliminarButton;

    private final IClienteServicio clienteServicio; //dependemos de la interfaz, no de la implementacion
    private DefaultTableModel tablaModeloClientes;
    private TableRowSorter<DefaultTableModel> ordenador;
    private Integer idCliente;

    //con un solo constructor Spring inyecta la dependencia sin necesidad de @Autowired
    public ZonaFitForma(IClienteServicio clienteServicio) {
        this.clienteServicio = clienteServicio;
        iniciarForma();
        aplicarEstilos();
        configurarEventos();
        //el listado se carga aqui y no en createUIComponents, porque ahi el servicio aun no esta asignado
        listarClientes();
        actualizarModoFormulario();
    }

    private void iniciarForma() {
        setTitle("Zona Fit Gym - Gestión de clientes");
        setContentPane(panelPrincipal);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 650);
        setMinimumSize(new Dimension(820, 520));
        setLocationRelativeTo(null); //centrar ventana
    }

    private void createUIComponents() {
        //filas alternadas en la tabla, se lee al crear el JTable
        UIManager.put("Table.alternateRowColor", ColorFunctions.lighten(UIManager.getColor("Table.background"), 0.03f));
        //evitamos la edicion de las celdas y declaramos el tipo de cada columna para ordenar bien los numeros
        this.tablaModeloClientes = new DefaultTableModel(0, 4) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }

            @Override
            public Class<?> getColumnClass(int columna) {
                return columna == COL_ID || columna == COL_MEMBRESIA ? Integer.class : String.class;
            }
        };
        String[] cabeceros = {"Id", "Nombre", "Apellido", "Membresía"};
        this.tablaModeloClientes.setColumnIdentifiers(cabeceros);
        this.clientesTabla = new JTable(tablaModeloClientes);
        //restringimos la seleccion de la tabla a un solo registro
        this.clientesTabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        //permite ordenar por columna (clic en el cabecero) y filtrar con el buscador
        this.ordenador = new TableRowSorter<>(tablaModeloClientes);
        this.clientesTabla.setRowSorter(ordenador);
    }

    private void aplicarEstilos() {
        //encabezado
        encabezadoPanel.setBorder(new MatteBorder(0, 0, 1, 0, UIManager.getColor("Separator.foreground")));
        tituloLabel.setFont(tituloLabel.getFont().deriveFont(Font.BOLD, 26f));
        tituloLabel.setForeground(COLOR_ACENTO);
        subtituloLabel.putClientProperty(FlatClientProperties.STYLE, "foreground: $Label.disabledForeground");
        totalClientesLabel.setFont(totalClientesLabel.getFont().deriveFont(Font.BOLD, 14f));

        //tarjetas con esquinas redondeadas y fondo un poco mas claro que la ventana
        var estiloTarjeta = "arc: 16; background: lighten($Panel.background,4%)";
        formularioPanel.putClientProperty(FlatClientProperties.STYLE, estiloTarjeta);
        tablaPanel.putClientProperty(FlatClientProperties.STYLE, estiloTarjeta);
        formularioTituloLabel.setFont(formularioTituloLabel.getFont().deriveFont(Font.BOLD, 16f));
        listadoTituloLabel.setFont(listadoTituloLabel.getFont().deriveFont(Font.BOLD, 16f));
        ayudaLabel.putClientProperty(FlatClientProperties.STYLE, "foreground: $Label.disabledForeground");

        //campos de texto un poco mas altos para que sean comodos de usar
        for (var campo : new JTextField[]{nombreTexto, apellidoTexto, membresiaTexto, buscarTexto})
            campo.setMargin(new Insets(5, 8, 5, 8));
        nombreTexto.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Ej. Ana");
        apellidoTexto.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Ej. López");
        membresiaTexto.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Número de membresía");
        //el campo de membresia solo acepta digitos
        ((AbstractDocument) membresiaTexto.getDocument()).setDocumentFilter(new SoloDigitosFilter());
        buscarTexto.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Buscar cliente...");
        buscarTexto.putClientProperty(FlatClientProperties.TEXT_FIELD_LEADING_ICON, new FlatSearchIcon());
        buscarTexto.putClientProperty(FlatClientProperties.TEXT_FIELD_SHOW_CLEAR_BUTTON, true);

        //botones: primario (verde), peligro (rojo) y secundario (por defecto)
        estiloBoton(guardarButton, COLOR_PRIMARIO);
        estiloBoton(eliminarButton, COLOR_PELIGRO);
        limpiarButton.setMargin(new Insets(6, 16, 6, 16));
        guardarButton.setToolTipText("Guardar (Enter)");
        limpiarButton.setToolTipText("Limpiar formulario (Esc)");

        //tabla
        clientesTabla.setRowHeight(30);
        clientesTabla.setShowVerticalLines(false);
        clientesTabla.setShowHorizontalLines(false);
        clientesTabla.setIntercellSpacing(new Dimension(0, 0));
        clientesTabla.setFillsViewportHeight(true);
        var cabecero = clientesTabla.getTableHeader();
        cabecero.setReorderingAllowed(false);
        cabecero.setFont(cabecero.getFont().deriveFont(Font.BOLD));
        cabecero.setPreferredSize(new Dimension(cabecero.getPreferredSize().width, 32));
        var centrado = new DefaultTableCellRenderer();
        centrado.setHorizontalAlignment(SwingConstants.CENTER);
        var columnas = clientesTabla.getColumnModel();
        columnas.getColumn(COL_ID).setCellRenderer(centrado);
        columnas.getColumn(COL_ID).setMaxWidth(70);
        columnas.getColumn(COL_MEMBRESIA).setCellRenderer(centrado);
        columnas.getColumn(COL_MEMBRESIA).setMaxWidth(130);
        columnas.getColumn(COL_MEMBRESIA).setPreferredWidth(110);
    }

    private void estiloBoton(JButton boton, Color fondo) {
        boton.putClientProperty(FlatClientProperties.STYLE,
                "background: " + hex(fondo) + "; foreground: #FFFFFF; borderWidth: 0; focusWidth: 0; font: bold");
        boton.setMargin(new Insets(6, 16, 6, 16));
    }

    private static String hex(Color color) {
        return String.format("#%06X", color.getRGB() & 0xFFFFFF);
    }

    private void configurarEventos() {
        guardarButton.addActionListener(e -> guardarCliente());
        limpiarButton.addActionListener(e -> limpiarFormulario());
        eliminarButton.addActionListener(e -> eliminarCliente());

        //la seleccion (con raton o teclado) carga el cliente en el formulario
        clientesTabla.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting())
                cargarClienteSeleccionado();
        });

        //filtrar la tabla mientras se escribe
        buscarTexto.getDocument().addDocumentListener(new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e) { filtrarClientes(); }
            @Override public void removeUpdate(DocumentEvent e) { filtrarClientes(); }
            @Override public void changedUpdate(DocumentEvent e) { filtrarClientes(); }
        });

        //Enter guarda y Esc limpia el formulario
        getRootPane().setDefaultButton(guardarButton);
        getRootPane().registerKeyboardAction(e -> limpiarFormulario(),
                KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_IN_FOCUSED_WINDOW);
    }

    private void listarClientes() {
        this.tablaModeloClientes.setRowCount(0);
        var clientes = this.clienteServicio.listarClientes();
        clientes.forEach(cliente -> {
            Object[] renglonCliente = {
                    cliente.getId(),
                    cliente.getNombre(),
                    cliente.getApellido(),
                    cliente.getMembresia()
            };
            this.tablaModeloClientes.addRow(renglonCliente);
        });
        var total = clientes.size();
        totalClientesLabel.setText(total + (total == 1 ? " cliente" : " clientes"));
    }

    private void filtrarClientes() {
        var texto = buscarTexto.getText().trim();
        //(?i) ignora mayusculas/minusculas y Pattern.quote evita que caracteres como "." o "(" rompan la busqueda
        ordenador.setRowFilter(texto.isEmpty() ? null : RowFilter.regexFilter("(?i)" + Pattern.quote(texto)));
    }

    private void guardarCliente() {
        //trim() evita que un campo con solo espacios pase la validacion
        var nombre = nombreTexto.getText().trim();
        var apellido = apellidoTexto.getText().trim();
        var membresiaStr = membresiaTexto.getText().trim();

        if (nombre.isEmpty()) {
            mostrarError("Proporciona un nombre", nombreTexto);
            return;
        }
        if (membresiaStr.isEmpty()) {
            mostrarError("Proporciona la membresía", membresiaTexto);
            return;
        }
        Integer membresia;
        try {
            membresia = Integer.parseInt(membresiaStr);
        } catch (NumberFormatException e) { //solo hay digitos, pero el numero puede ser demasiado grande
            mostrarError("La membresía no es un número válido", membresiaTexto);
            return;
        }

        var esNuevo = this.idCliente == null;
        var cliente = new Cliente(this.idCliente, nombre, apellido, membresia);
        try {
            this.clienteServicio.guardarCliente(cliente); //insertar o modificar segun el id
        } catch (RuntimeException e) {
            mostrarError("No se pudo guardar el cliente: " + e.getMessage(), null);
            return;
        }
        mostrarMensaje(esNuevo ? "Se agregó el nuevo cliente" : "Se actualizó el cliente");
        limpiarFormulario();
        listarClientes();
    }

    private void cargarClienteSeleccionado() {
        var renglon = renglonSeleccionado();
        eliminarButton.setEnabled(renglon != -1);
        if (renglon == -1) //-1 significa que no se selecciono ningun registro
            return;
        this.idCliente = (Integer) tablaModeloClientes.getValueAt(renglon, COL_ID);
        this.nombreTexto.setText(valorCelda(renglon, COL_NOMBRE));
        this.apellidoTexto.setText(valorCelda(renglon, COL_APELLIDO));
        this.membresiaTexto.setText(valorCelda(renglon, COL_MEMBRESIA));
        actualizarModoFormulario();
    }

    private void eliminarCliente() {
        var renglon = renglonSeleccionado();
        if (renglon == -1) {
            mostrarMensaje("Debe seleccionar un cliente a eliminar");
            return;
        }
        var id = (Integer) tablaModeloClientes.getValueAt(renglon, COL_ID);
        var nombreCompleto = (valorCelda(renglon, COL_NOMBRE) + " " + valorCelda(renglon, COL_APELLIDO)).trim();
        var respuesta = JOptionPane.showConfirmDialog(this,
                "¿Eliminar al cliente " + nombreCompleto + " (id " + id + ")?\nEsta acción no se puede deshacer.",
                "Confirmar eliminación", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (respuesta != JOptionPane.YES_OPTION)
            return;
        try {
            clienteServicio.eliminarClientePorId(id);
        } catch (RuntimeException e) {
            mostrarError("No se pudo eliminar el cliente: " + e.getMessage(), null);
            return;
        }
        mostrarMensaje("Cliente " + nombreCompleto + " eliminado");
        limpiarFormulario();
        listarClientes();
    }

    private void limpiarFormulario() {
        nombreTexto.setText("");
        apellidoTexto.setText("");
        membresiaTexto.setText("");
        //limpiamos el id del cliente seleccionado
        this.idCliente = null;
        //deseleccionamos el registro seleccionado de la tabla
        this.clientesTabla.getSelectionModel().clearSelection();
        actualizarModoFormulario();
        nombreTexto.requestFocusInWindow();
    }

    //el formulario indica si se esta agregando un cliente nuevo o editando uno existente
    private void actualizarModoFormulario() {
        var esNuevo = this.idCliente == null;
        formularioTituloLabel.setText(esNuevo ? "Nuevo cliente" : "Editar cliente #" + this.idCliente);
        guardarButton.setText(esNuevo ? "Agregar" : "Actualizar");
        eliminarButton.setEnabled(!esNuevo);
    }

    //con ordenamiento y filtro el indice visible no coincide con el del modelo, hay que convertirlo
    private int renglonSeleccionado() {
        var renglonVista = clientesTabla.getSelectedRow();
        return renglonVista == -1 ? -1 : clientesTabla.convertRowIndexToModel(renglonVista);
    }

    //una celda puede venir null (por ejemplo apellido vacio en la BD), evitamos el NullPointerException de toString()
    private String valorCelda(int renglon, int columna) {
        var valor = tablaModeloClientes.getValueAt(renglon, columna);
        return valor == null ? "" : valor.toString();
    }

    private void mostrarError(String mensaje, JTextField campo) {
        JOptionPane.showMessageDialog(this, mensaje, "Zona Fit", JOptionPane.WARNING_MESSAGE);
        if (campo != null) {
            campo.putClientProperty(FlatClientProperties.OUTLINE, FlatClientProperties.OUTLINE_ERROR);
            campo.requestFocusInWindow();
            //el borde rojo desaparece en cuanto el usuario vuelve a escribir
            campo.getDocument().addDocumentListener(new DocumentListener() {
                private void quitar() {
                    campo.putClientProperty(FlatClientProperties.OUTLINE, null);
                    campo.getDocument().removeDocumentListener(this);
                }
                @Override public void insertUpdate(DocumentEvent e) { quitar(); }
                @Override public void removeUpdate(DocumentEvent e) { quitar(); }
                @Override public void changedUpdate(DocumentEvent e) { quitar(); }
            });
        }
    }

    private void mostrarMensaje(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Zona Fit", JOptionPane.INFORMATION_MESSAGE);
    }

    //filtro de documento que descarta cualquier caracter que no sea digito
    private static class SoloDigitosFilter extends DocumentFilter {
        @Override
        public void insertString(FilterBypass fb, int offset, String texto, AttributeSet attr) throws BadLocationException {
            super.insertString(fb, offset, soloDigitos(texto), attr);
        }

        @Override
        public void replace(FilterBypass fb, int offset, int length, String texto, AttributeSet attrs) throws BadLocationException {
            super.replace(fb, offset, length, soloDigitos(texto), attrs);
        }

        private static String soloDigitos(String texto) {
            return texto == null ? null : texto.replaceAll("\\D", "");
        }
    }
}
