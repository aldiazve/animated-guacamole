
import java.io.IOException;
import java.io.PushbackInputStream;

public class Main {

    public static void main(String[] args) throws IOException {
        Lexer l = new Lexer();
        l.Tokenize(new PushbackInputStream(System.in));

    }
}
