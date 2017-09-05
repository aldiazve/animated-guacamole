import java.io.*;
import java.util.*;

public class Lexer {

	// Lista de estados del autómata.
	enum State {
    INITIAL,
    WHITESPACE,
    LINE_BREACK,
    STRING,
    IDENTIFIER,
    COMMENT,
    SPECIAL_CHARACTER,
    LEXICALE_ERROR,
  };
  // Lista de los posibles tokens a imprimir
  enum PrinterType {
  	STRING,
  	SPECIAL_CHARACTER,
  	INTEGER,
  	DOUBLE,
  	IDENTIFIER,
  	RESERVED_WORDS
  }
  // Lista de caracteres especiales.
  public static Map<String, String> specialCaracters = new HashMap<String, String>();

  public static void initSpecialCaractersMap(){
  	specialCaracters.put("{", "token_llave_izq");
  	specialCaracters.put("}", "token_llave_der");
  	specialCaracters.put("$", "token_dollar");
  	specialCaracters.put(";", "token_pyc");
  	specialCaracters.put("[", "token_cor_izq");
  	specialCaracters.put("]", "token_cor_der");
  	specialCaracters.put("(", "token_par_izq");
  	specialCaracters.put(")", "token_par_der");
  	specialCaracters.put(">", "token_mayor");
  	specialCaracters.put("<", "token_menor");
  	specialCaracters.put(">=", "token_mayor_igual");
  	specialCaracters.put("<=", "token_menor_igual");
  	specialCaracters.put("eq", "token_igual_str");
  	specialCaracters.put("==", "token_igual_num");
  	specialCaracters.put("ne", "token_diff_str");
  	specialCaracters.put("!=", "token_diff_num");
  	specialCaracters.put("&&", "token_and");
  	specialCaracters.put("||", "token_or");
  	specialCaracters.put("!", "token_not");
  	specialCaracters.put("+", "token_mas");
  	specialCaracters.put("-", "token_menos");
  	specialCaracters.put("*", "token_mul");
  	specialCaracters.put("/", "token_div");
  	specialCaracters.put("%", "token_mod");
  	specialCaracters.put("**", "token_pot");
  }

  public static Map<String, String> reservedWords = new HashMap<String, String>();

  public static void initReservedWordsMap(){
  	reservedWords.put("set", "set");
  	reservedWords.put("puts", "puts");
  	reservedWords.put("gets", "gets");
  	reservedWords.put("expr", "expr");
  	reservedWords.put("if", "if");
  	reservedWords.put("then", "then");
  	reservedWords.put("elseif", "elseif");
  	reservedWords.put("else", "else");
  	reservedWords.put("switch", "switch");
  	reservedWords.put("default", "default");
  	reservedWords.put("while", "while");
  	reservedWords.put("continue", "continue");
  	reservedWords.put("break", "break");
  	reservedWords.put("for", "for");
  	reservedWords.put("incr", "incr");
  	reservedWords.put("array", "array");
  	reservedWords.put("size", "size");
  	reservedWords.put("exists", "exists");
  	reservedWords.put("proc", "proc");
  	reservedWords.put("return", "return");
  }

  // Determina el siguiente estado.
  // Recibe el siguiente caracter y el estado actual.
  public static State nextState(char c, State currentState) {
    switch (currentState) {
	    case INITIAL:
	    case LINE_BREACK:
	    case WHITESPACE:
	    	//Comienza a leer una cadena.
        if (c == '"') {
            return State.STRING;
        }
        //Comienza a leer un identificador (puede llegar a ser una palabra reservada).
        if (c >= 'a' && c <= 'z' || c >= 'A' && c <= 'Z') {
            return State.IDENTIFIER;
        }
        // Lee un WHITESPACE
        if (c == '\r' || c == '\t'|| c == ' ') {
            return State.WHITESPACE;
        }
        // Lee un saldo de línea
        if (c == '\n'){
        	return State.LINE_BREACK;
        }
        // Comienza a leer un comentario
        if (c == '#') {
        	return State.COMMENT;
        }
       	return State.SPECIAL_CHARACTER;
      // Cuando vuelve a ver las " finaliza el string.
    	case STRING:
        if (c == '"') {
            return State.WHITESPACE;
        }
        return State.STRING;
      //Lee hasta que termina de ver letras o números.
  	  case IDENTIFIER:
        if (c >= 'a' && c <= 'z' || c >= 'A' && c <= 'Z' || c >= '0' && c <= '9') {
            return State.IDENTIFIER;
        }else if (c == '\r' || c == '\t'|| c == ' '){
        	return State.WHITESPACE;
        }else if (c == '\n'){
        	return State.LINE_BREACK;
        }
        return State.LEXICALE_ERROR;
      // Lee hasta que encuentra un salto de línea. Ignora toda la línea leída.
      case COMMENT:
      	if (c == '\n'){
      		return State.LINE_BREACK;
      	}
      	return State.COMMENT;
      case SPECIAL_CHARACTER:
      	if (c == '\n'){
      		return State.LINE_BREACK;
      	} else if (c == '\r' || c == '\t'|| c == ' '){
      		return State.WHITESPACE;
      	}
      	return State.COMMENT;
   		default:
        System.err.println("State desconocido...");
        System.exit(1);
        return null;
    }
  }

  public static void printer( int line, int column, PrinterType type, String token){	
  	column = column - token.length();
		switch (type) {
			case STRING:
				System.out.println('<' + "token_string, " + token + ", " + line + ", " + column + ">");
			break;	
			case SPECIAL_CHARACTER:
				System.out.println('<' + specialCaracters.get(token) + ", " + line + ", " + column + ">");
  		break;
  		case INTEGER:
  			System.out.println('<' + "token_integer, " + token + ", " + line + ", " + column + ">");
  		break;
  		case DOUBLE:
  			System.out.println('<' + "token_double, " + token + ", " + line + ", " + column + ">");
  		break;
  		case IDENTIFIER:
  		  

  			System.out.println('<' + "id, " + token + ", " + line + ", " + column + ">");
  		break;
  		case RESERVED_WORDS:
  			
  			System.out.println('<' + token + ", " + line + ", " + column + ">");
  		break;
		}
  	
  }

  public static void lexicaleError( int line, int column){
  	System.out.println(">>> Error lexico linea: " + line + ", posicion: " + column +")");
  }

    // Recibe la ubicación del archivo de prueva y retorna un InputStream del mismo
  public static InputStream fileToInputStream( String fileName ) throws IOException {
	  File initialFile = new File(fileName);
	  InputStream targetStream = new FileInputStream(initialFile);
		return targetStream;
	} 
  // Elimina el salto de línea que siempre queda.
  private static String removeLastChar(String str) {
    return str.substring(0, str.length() - 1);
}
	

	public static void main(String[] args) throws IOException {
		initSpecialCaractersMap();
		initReservedWordsMap();
    State currentState = State.INITIAL, nextState;
    String lexeme = "";
    int line = 1, column = 1;
    // Inicializamos el inputStream con uno de los archivos de entrada.
    PushbackInputStream in = new PushbackInputStream(fileToInputStream("examples/in00.txt"));
    Boolean isFinish = false;


    while (!isFinish) {
      int i = in.read();
      column++;
      if(i == -1){
        isFinish = true;
        // Envía un espacio a la función next() para que ignore esa "lectura".
        i = (int)' ';
      }
      char character = (char) i;
      lexeme += character;
      nextState = nextState(character, currentState);
      // Revisa cuál es el siguiente estado y toma la acción correspondiente.

      if (nextState == State.WHITESPACE || nextState == State.LINE_BREACK) {
        switch (currentState) {
          case INITIAL:
            break;
          case STRING:
          		printer(line, column, PrinterType.STRING, removeLastChar(lexeme));
              break;
          case IDENTIFIER:
          	//Eliminia el último caracter.
          	lexeme = removeLastChar(lexeme);
          	//Verifica si la palabra es reservada.
          	if (reservedWords.get(lexeme) != null){
          		printer(line, column, PrinterType.RESERVED_WORDS, lexeme);
          	//Verifica si es uno de los dos caracteres especiales que solo tienen letras.
          	} else if (specialCaracters.get(lexeme) != null){
          		printer(line, column, PrinterType.SPECIAL_CHARACTER, lexeme);
          	} else {
          		printer(line, column, PrinterType.IDENTIFIER, lexeme);
          	}
            in.unread((int) character);
            break;
            case SPECIAL_CHARACTER:
            	//Eliminia el último caracter.
          		lexeme = removeLastChar(lexeme);
          		if (specialCaracters.get(lexeme) != null){
          			printer(line, column, PrinterType.SPECIAL_CHARACTER, lexeme);
          		}
            break;
        }
        // Verifica si viene un salto de línea, de ser así aumenta el contador "column" y deja en 0 el contador "linea"
	      if (nextState == State.LINE_BREACK){
	      	column = 0;
	      	line++;
	      }
        lexeme = "";
      }

      currentState = nextState;
    }

	}
}