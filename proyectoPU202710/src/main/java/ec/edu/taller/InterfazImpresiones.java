package ec.edu.taller;

import java.util.List;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;


public class InterfazImpresiones extends JFrame {
    private static final int CAPACIDAD = 10;


    private JPanel panelPrincipal;
    private JTabbedPane tabbedPane;
    private JTextField txtCodigo;
    private JComboBox<String> cmbSolicitante;
    private JTextField txtDocumento;
    private JSpinner spnPrioridad;
    private JButton btnRegistrar;
    private JButton btnLimpiar;
    private JLabel lblCodigo;
    private JLabel lblSolicitante;
    private JLabel lblDocumento;
    private JLabel lblPrioridad;
    private JLabel lblEstado;
    private JTextArea txtListado;
    private JLabel lblPendientes;
    private JLabel lblDisponibles;
    private JLabel lblCapacidad;
    private JButton btnProcesar;
    private JButton btnConsultar;
    private JButton btnListar;


    private final GestorImpresiones gestor = new GestorImpresiones(CAPACIDAD);

    public InterfazImpresiones() {
        super("Centro de impresión universitario");
        setContentPane(panelPrincipal);
        setDefaultCloseOperation(EXIT_ON_CLOSE);


        for (String s : new String[] {"ESTUDIANTE", "DOCENTE", "ADMINISTRATIVO"}) {
            cmbSolicitante.addItem(s);
        }
        spnPrioridad.setModel(new SpinnerNumberModel(1, 1, 10, 1));
        txtListado.setRows(6);

        btnRegistrar.addActionListener(e -> registrar());
        btnLimpiar.addActionListener(e -> limpiarFormulario());
        btnProcesar.addActionListener(e -> procesar());
        btnConsultar.addActionListener(e -> consultar());
        btnListar.addActionListener(e -> listar());

        cargarDatosQuemados();
        refrescarListado();
        actualizarContadores();

        pack();
        setLocationRelativeTo(null);
    }


    private void cargarDatosQuemados() {
        gestor.registrarTrabajo(new TrabajoImpresion("IMP-001", "ESTUDIANTE", "Informe.pdf", 5));
        gestor.registrarTrabajo(new TrabajoImpresion("IMP-002", "DOCENTE", "Examen.pdf", 1));
        gestor.registrarTrabajo(new TrabajoImpresion("IMP-003", "ADMINISTRATIVO", "Acta.pdf", 3));
        gestor.registrarTrabajo(new TrabajoImpresion("IMP-004", "ESTUDIANTE", "Tesis.pdf", 4));
        gestor.registrarTrabajo(new TrabajoImpresion("IMP-005", "DOCENTE", "Silabo.pdf", 2));
        gestor.registrarTrabajo(new TrabajoImpresion("IMP-006", "ADMINISTRATIVO", "Oficio.pdf", 6));
        gestor.registrarTrabajo(new TrabajoImpresion("IMP-007", "ESTUDIANTE", "Tarea.pdf", 7));
        gestor.registrarTrabajo(new TrabajoImpresion("IMP-008", "DOCENTE", "Rubrica.pdf", 3));
    }


    private void registrar() {
        String codigo = txtCodigo.getText();
        String documento = txtDocumento.getText().trim();
        if (codigo.trim().isEmpty()) {
            aviso("El código no puede estar vacío.");
            return;
        }
        if (documento.isEmpty()) {
            aviso("Ingrese el nombre del documento.");
            return;
        }
        TrabajoImpresion trabajo = new TrabajoImpresion(codigo,
                (String) cmbSolicitante.getSelectedItem(), documento,
                (Integer) spnPrioridad.getValue());

        if (gestor.registrarTrabajo(trabajo)) {
            JOptionPane.showMessageDialog(this, "Trabajo registrado: " + trabajo.getCodigo(),
                    "Registro exitoso", JOptionPane.INFORMATION_MESSAGE);
            limpiarFormulario();
            refrescarListado();
        } else if (gestor.existeTrabajo(codigo)) {
            aviso("Rechazado: ya existe un trabajo pendiente con el código " + codigo + ".");
        } else if (gestor.consultarEspaciosDisponibles() <= 0) {
            aviso("Rechazado: la cola está llena (capacidad " + CAPACIDAD + ").");
        } else {
            aviso("Rechazado: el trabajo no es válido.");
        }
        actualizarContadores();
    }

    private void limpiarFormulario() {
        txtCodigo.setText("");
        txtDocumento.setText("");
        cmbSolicitante.setSelectedIndex(0);
        spnPrioridad.setValue(1);
        txtCodigo.requestFocusInWindow();
    }

    private void consultar() {
        TrabajoImpresion siguiente = gestor.consultarSiguienteTrabajo();
        if (siguiente == null) {
            limpiarDetalle();
            aviso("La cola está vacía: no hay trabajos pendientes.");
            return;
        }
        mostrarDetalle(siguiente);
    }

    private void procesar() {
        TrabajoImpresion procesado = gestor.procesarSiguienteTrabajo();
        if (procesado == null) {
            limpiarDetalle();
            aviso("La cola está vacía: no hay trabajos para procesar.");
            return;
        }
        mostrarDetalle(procesado); // el estado aparece como PROCESADO
        refrescarListado();
        actualizarContadores();
    }

    private void listar() {
        if (gestor.contarTrabajosPendientes() == 0) {
            txtListado.setText("");
            aviso("No hay trabajos pendientes.");
            return;
        }
        refrescarListado();
    }


    private void refrescarListado() {
        List<TrabajoImpresion> lista = gestor.listarTrabajos();
        StringBuilder sb = new StringBuilder();
        for (TrabajoImpresion t : lista) {
            sb.append(t).append('\n');
        }
        txtListado.setText(sb.toString());
        txtListado.setCaretPosition(0);
    }

    private void mostrarDetalle(TrabajoImpresion t) {
        lblCodigo.setText(t.getCodigo());
        lblSolicitante.setText(t.getSolicitante());
        lblDocumento.setText(t.getDocumento());
        lblPrioridad.setText(String.valueOf(t.getPrioridad()));
        lblEstado.setText(t.getEstado());
    }

    private void limpiarDetalle() {
        lblCodigo.setText("-");
        lblSolicitante.setText("-");
        lblDocumento.setText("-");
        lblPrioridad.setText("-");
        lblEstado.setText("-");
    }

    private void actualizarContadores() {
        lblPendientes.setText("PENDIENTES " + gestor.contarTrabajosPendientes());
        lblDisponibles.setText("DISPONIBLES " + gestor.consultarEspaciosDisponibles());
        lblCapacidad.setText("CAPACIDAD " + CAPACIDAD);
    }

    private void aviso(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Aviso", JOptionPane.WARNING_MESSAGE);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new InterfazImpresiones().setVisible(true));
    }
}
