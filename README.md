# animated-guacamole - TCL project

###### TODO:

- [] Identificar los caracteres especiales.
- [] Imprimir identificadores y caracteres especiales.
- [x] Comentar el código apropiadamente.
- [x] Dejar el código en un solo idioma (Ingles o español).
- [x] Identificar líneas de comentarios.
- [x] Definida la función que imprime error léxico (no implementada aún).
- [x] Definida el implementada la función que imprime en el formato requerido las cadenas.

Última actualización 5/09/2017

### Definición de los estados del autómata:

A continuación se enumeran y explican todos los estados a los que el autómata puede llegar.

```java
	enum State {
    INITIAL,
    WHITESPACE,
    LINE_BREACK,
    STRING,
    IDENTIFIER,
    COMMENT,
    SPECIAL_CHARACTER,
    LEXICALE_ERROR
  };
```
###### INITIAL

Es el estado inicial del autómata. El autómata solo está en este estado cuando inicia la ejecución del programa.

###### WHITESPACE

Es el estado al que se transita cuando se lee un caracter de espacio, es decir: ' ', \t, \r. Al ver un espacio, el autómata verificará el último token leído, e imprimirá la línea correspondiénte. El autómata también transita a este estado cuando se ven las comillas de cierre de una cadena.

###### LINE_BREACK

Es el estado al que se transita cuando se lee un salto de línea: \n. El autómata solo entrará en este estado cuando ve un salto de línea.
El autómata también verificará el último token leído cuando ve un salto de línea.

###### STRING

Es el estado al que se transita cuando se ven las primeras comillas (") que abren la cadena. El autómatá permanecerá en este estado hasta que vualva a ver unas comillas, indicando que el final de la cadena. Al ver las comillas de cierre el autómata transita al estado WHITESPACE.

###### IDENTIFIER

Es el estado al que se transita cuando se lee una letra después de un espacio en blanco. El autómata asume que el token que está leyendo es un identificador. Una vez encuentra un espacio en blanco, el autómata verifica si el token leído es una palabra reservada, e imprime en el formato correspondiente al de un identificador o una palabra reservada, respectivamente. Si el autómata lee un caracter difierente a letras o números después de la primera letra transitará al estado LEXICALE_ERROR.

###### COMMENT

Es el estado al que se transita si se lee un númeral (#), el autómata ignorará todo lo que sigue hasta encontrar un salto de línea, y una vez leído el salto de línea transita al estado LINE_BREACK.

###### SPECIAL_CHARACTER

Es el estado al que se transita cuando no se lee ni un número, ni una letra, el autómata leerá todo el token, hasta encontrar un espacio en blanco, y verificará que el token leído sea un caracter especial, de ser así imprimirá en el formato correspondiente, de lo contrario transitará a LEXICALE_ERROR.

###### LEXICALE_ERROR

El autómata asumirá que hay un error léxico, imprimirá la línea de error correspondiénte y detendrá la ejecución.

## PrinterTypes

El autómata debe imprimir en un formato diferente en función del tipo de token que lee. A continuación la definición de estos formatos:

```java
enum PrinterType {
  	STRING,
  	SPECIAL_CHARACTER,
  	INTEGER,
  	DOUBLE,
  	IDENTIFIER,
  	RESERVED_WORDS
  }
```

###### STRING

```java
<token_string,lexema,fila,columna>
```

###### SPECIAL_CHARACTER

```java
<nombre_token,fila,columna>
```

###### INTEGER

```java
<token_integer,lexema,fila,columna>
```

###### DOUBLE

```java
<token_double,lexema,fila,columna>
```

###### INDENTIFIER

```java
<id,lexema,fila,columna>
```

###### RESERVED_WORDS

```java
<tipo_de_token,fila,columna>
```

# Defición de las funciones

A continuación se definen las funciones importantes del autómata.

##### public static State nextState(char c, State currentState)

```java
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
```

