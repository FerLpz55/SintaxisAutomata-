package fragmento.compilador;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableModel;

public final class VentanaCompilador extends JFrame {
    private final JTextArea editor = new JTextArea();
    private final DefaultTableModel modeloTokens = new DefaultTableModel(
            new Object[] { "Tipo", "Lexema", "Linea", "Columna" }, 0);
    private final JLabel estado = new JLabel("Listo");

    public VentanaCompilador() {
        super("Compilador - Lenguajes y Automatas II");
        configurarVentana();
        configurarEditor();
        agregarComponentes();
    }

    private void configurarVentana() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(950, 620));
        setLocationRelativeTo(null);
    }

    private void configurarEditor() {
        editor.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 15));
        editor.setText(String.join(System.lineSeparator(),
                "long contador = 0;",
                "double total = 12.5;",
                "while (contador < 10) {",
                "    write total;",
                "    contador++;",
                "    if (contador == 5) {",
                "        continue;",
                "    }",
                "}",
                "read contador;"));
    }

    private void agregarComponentes() {
        JPanel barra = new JPanel(new FlowLayout(FlowLayout.LEFT));
        barra.add(new JButton(new AccionAbrir()));
        barra.add(new JButton(new AccionGuardar()));
        barra.add(new JButton(new AccionAnalizar()));

        JTable tablaTokens = new JTable(modeloTokens);
        tablaTokens.setFillsViewportHeight(true);

        JSplitPane division = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                new JScrollPane(editor), new JScrollPane(tablaTokens));
        division.setResizeWeight(0.62);

        estado.setHorizontalAlignment(SwingConstants.LEFT);
        estado.setBorder(javax.swing.BorderFactory.createEmptyBorder(6, 10, 6, 10));

        add(barra, BorderLayout.NORTH);
        add(division, BorderLayout.CENTER);
        add(estado, BorderLayout.SOUTH);
    }

    private void analizar() {
        modeloTokens.setRowCount(0);
        try {
            ResultadoCompilacion resultado = new Compilador().compilar(editor.getText());
            for (Token token : resultado.getTokens()) {
                if (token.getTipoToken() != TokenType.EOF) {
                    modeloTokens.addRow(new Object[] {
                            token.getTipoToken(), token.getLexema(), token.getLinea(), token.getColumna()
                    });
                }
            }
            estado.setText(resultado.getMensaje() + ". Tokens: " + modeloTokens.getRowCount());
        } catch (CompilacionException ex) {
            estado.setText(ex.getMessage());
        }
    }

    private void abrir() {
        JFileChooser selector = new JFileChooser();
        if (selector.showOpenDialog(this) != JFileChooser.APPROVE_OPTION) {
            return;
        }
        try {
            Path ruta = selector.getSelectedFile().toPath();
            editor.setText(Files.readString(ruta, StandardCharsets.UTF_8));
            estado.setText("Archivo abierto: " + ruta.getFileName());
        } catch (IOException ex) {
            mostrarError("No se pudo abrir el archivo", ex);
        }
    }

    private void guardar() {
        JFileChooser selector = new JFileChooser();
        if (selector.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) {
            return;
        }
        try {
            Path ruta = selector.getSelectedFile().toPath();
            Files.writeString(ruta, editor.getText(), StandardCharsets.UTF_8);
            estado.setText("Archivo guardado: " + ruta.getFileName());
        } catch (IOException ex) {
            mostrarError("No se pudo guardar el archivo", ex);
        }
    }

    private void mostrarError(String titulo, Exception ex) {
        JOptionPane.showMessageDialog(this, ex.getMessage(), titulo, JOptionPane.ERROR_MESSAGE);
    }

    private final class AccionAbrir extends javax.swing.AbstractAction {
        private AccionAbrir() {
            super("Abrir");
        }

        @Override
        public void actionPerformed(ActionEvent evento) {
            abrir();
        }
    }

    private final class AccionGuardar extends javax.swing.AbstractAction {
        private AccionGuardar() {
            super("Guardar");
        }

        @Override
        public void actionPerformed(ActionEvent evento) {
            guardar();
        }
    }

    private final class AccionAnalizar extends javax.swing.AbstractAction {
        private AccionAnalizar() {
            super("Analizar");
        }

        @Override
        public void actionPerformed(ActionEvent evento) {
            analizar();
        }
    }
}
