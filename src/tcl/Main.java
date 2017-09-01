package tcl;

import java.io.IOException;
import java.io.PushbackInputStream;

/**
 *
 * @author romain
 */
public class Main {

    enum Estado {
        INICIAL, NO, CADENA, IDENTIFICADOR
    };

    public static Estado next(char c, Estado s) {
        switch (s) {
            case INICIAL:
                if (c == '"') {
                    return Estado.CADENA;
                }
                if (c >= 'a' && c <= 'z' || c >= 'A' && c <= 'Z') {
                    return Estado.IDENTIFICADOR;
                }
                if (c == '\n' || c == '\r' || c == '\t'|| c == ' ') {
                    return Estado.NO;
                }
                System.err.println("Caracter desconocido ('" + c+"')...");
                System.exit(1);
                return Estado.NO;
            case CADENA:
                if (c == '"') {
                    return Estado.NO;
                }
                return Estado.CADENA;
            case IDENTIFICADOR:
                if (c >= 'a' && c <= 'z' || c >= 'A' && c <= 'Z' || c >= '0' && c <= '9') {
                    return Estado.IDENTIFICADOR;
                }
                return Estado.NO;
            default:
                System.err.println("Estado desconocido...");
                System.exit(1);
                return Estado.NO;
        }
    }

    public static void main(String[] args) throws IOException {
        Estado actual = Estado.INICIAL, siguiente;
        String lexema = "";
        PushbackInputStream in = new PushbackInputStream(System.in);
        Boolean completo = false;
        while (!completo) {
            int i = in.read();
            if(i == -1){
                completo = true;
                i = (int)' ';
            }
            
            char caracter = (char) i;
            lexema += caracter;
            siguiente = next(caracter, actual);
            if (siguiente == Estado.NO) {
                switch (actual) {
                    case INICIAL:
                        break;
                    case CADENA:
                        System.out.println("Cadena: " + lexema.substring(1, lexema.length() - 1));
                        break;
                    case IDENTIFICADOR:
                        System.out.println("Id:" + lexema.substring(0, lexema.length()-1));
                        in.unread((int) caracter);
                        break;
                }
                lexema = "";
                siguiente = Estado.INICIAL;
            }
            actual = siguiente;
        }

    }

}
