
import java.util.HashMap;
import java.util.Map;

public class Token {

    public enum Type {
        IDENTIFIER, KEYWORD, STRING, INTEGER, DOUBLE, OPERATOR
    };
    Type type;
    int line;
    int column;
    String lexeme;

    public static final Map<String, String> operators = new HashMap<>();

    static {

        operators.put("{", "token_llave_izq");
        operators.put("}", "token_llave_der");
        operators.put("$", "token_dollar");
        operators.put(";", "token_pyc");
        operators.put("[", "token_cor_izq");
        operators.put("]", "token_cor_der");
        operators.put("(", "token_par_izq");
        operators.put(")", "token_par_der");
        operators.put("+", "token_mas");
        operators.put("-", "token_menos");
        operators.put("/", "token_div");
        operators.put("%", "token_mod");
        operators.put("<", "token_menor");
        operators.put("<=", "token_menor_igual");
        operators.put(">=", "token_mayor_igual");
        operators.put(">", "token_mayor");
        operators.put("!", "token_not");
        operators.put("!=", "token_diff_num");
        operators.put("*", "token_mul");
        operators.put("**", "token_pot");
        operators.put("&&", "token_and");
        operators.put("||", "token_or");
        operators.put("==", "token_igual_num");
        operators.put("ne", "token_diff_str");
        operators.put("eq", "token_igual_str");

    }

    public Token(Type type, String lexeme, int line, int column) {
        this.type = type;
        this.line = line;
        this.column = column;
        this.lexeme = lexeme;
    }

    @Override
    public String toString() {
        String t = "";
        switch (type) {
            case STRING:
                t = "token_string";
                break;
            case INTEGER:
                t = "token_integer";
                break;
            case DOUBLE:
                t = "token_double";
                break;
            case IDENTIFIER:
                t = "id";
                break;
            case OPERATOR:
                t = operators.get(lexeme);
                break;
            default:
                break;
        }
        if (!t.equals("")) {
            t += ",";
        }
        return "<" + t + lexeme + "," + line + "," + column + ">";
    }
}
