package fragmento.compilador;

import java.awt.GraphicsEnvironment;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import javax.swing.SwingUtilities;

public final class Main {
    private Main() {
    }

    public static void main(String[] args) throws Exception {
        if (args.length > 0) {
            ejecutarConsola(args[0]);
            return;
        }

        if (GraphicsEnvironment.isHeadless()) {
            System.out.println("Uso: java fragmento.compilador.Main archivo.lang");
            return;
        }

        SwingUtilities.invokeLater(() -> new VentanaCompilador().setVisible(true));
    }

    private static void ejecutarConsola(String ruta) throws Exception {
        String fuente = Files.readString(Path.of(ruta), StandardCharsets.UTF_8);
        ResultadoCompilacion resultado = new Compilador().compilar(fuente);
        resultado.getTokens().stream()
                .filter(token -> token.getTipoToken() != TokenType.EOF)
                .forEach(System.out::println);
        System.out.println(resultado.getMensaje());
    }
}
