package project2.compiler;

import project1.enums.Token;
import project1.enums.TokenType;
import project1.handlers.Tokenizer;
import project1.handlers.TokenizerNoComments;

/**
 * An LL1 grammar parser with environment. This compiler DOES NOT allow booleans to be assigned to
 * variables, as per the project specs. Expressions compiled follow PEMDAS.
 * Grammar rules:
 * S -> EOF | R;S
 * R -> PRINT(E) | IF(B) PRINT(E) | IF(B) A | A
 * B -> E <= E | E >= E | E < E | E > E | E == E | E != E
 * A -> IDENT = E
 * E -> F | F + E | F - E
 * F -> U | U * F | U / F | U % F
 * U -> X | -X
 * X -> P | P ** U
 * P -> D | (E)
 * D -> IDENT | NUMBER | STRING | SQRT(E)
 */
public class CompilerGrammar {

    /**
     * Captures the environment of the grammar parser
     */
    private Environment environment = new Environment();
    /**
     * The currently used tokenizer for this compiler grammar. You can instantiate this by
     * overriding the onCreateTokenizer() method.
     */
    private Tokenizer tokenizer = null;

    /**
     * The current token to be processed during compilation.
     */
    private Token token;

    /**
     * Gets the current working environment for this compiler.
     *
     * @return the current working environment
     */
    public Environment getEnvironment() {
        return environment;
    }

    /**
     * Sets a new working environment for this compiler.
     *
     * @param environment the new current working environment
     */
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    /**
     * Defines and binds a value to an identifier for the current working environment.
     *
     * @param identifier the identifier to the value to be bound
     * @param value      the value to be bound
     */
    public void define(String identifier, Object value) {
        setEnvironment(getEnvironment().define(identifier, value));
    }

    /**
     * Hook method for creating a tokenizer. By default, this constructs a TokenizerNoComments
     * object for the compiler program.
     *
     * @param program the program to tokenize
     * @return the Tokenizer object to be used by this compiler grammar
     */
    public Tokenizer onCreateTokenizer(String program) {
        return new TokenizerNoComments(program);
    }

    /**
     * Hook that handles what happens when an error was thrown during compilation of a grammar.
     *
     * @param message the error message
     */
    protected void onError(String message) throws CompileException {
        throw new CompileException(message);
    }

    /**
     * Compiles a given program and returns true if the program compiled without errors. All
     * environment from previously compiled programs will be kept. Internally calls the method
     * compile(program, true).
     *
     * @param program the program to be tokenized and compiled
     * @throws CompileException if the program compiled while generating errors
     */
    public void compile(String program) throws CompileException {
        compile(program, true);
    }

    /**
     * Compiles a given program.
     *
     * @param program      the program to be tokenized and compiled
     * @param keepBindings flag if previously compiled environment should be kept or not
     * @throws CompileException if the program compiled while generating errors
     */
    public void compile(String program, boolean keepBindings) throws CompileException {
        this.tokenizer = onCreateTokenizer(program);
        if (!keepBindings)
            setEnvironment(new Environment());
        // start with the first token
        consumeNextToken();
        S();
    }

    /**
     * Getter for the current token.
     *
     * @return the current token
     */
    protected Token getToken() {
        return token;
    }

    /**
     * Consumes the current token and assigns the next token from the tokenizer.
     */
    protected void consumeNextToken() throws CompileException {
        token = tokenizer.nextToken();
        if (token.getTokenType().equals(TokenType.ERROR))
            onError("lexical error: invalid token " + token.getLexeme());
    }

    /**
     * Checks if the current token is of expected type. Calls consumeNextToken() if the expectation
     * was correct. Internally calls expect(tokenType, true).
     *
     * @param tokenType the token type expected
     * @return true if the current token has type equal to tokenType
     */
    protected boolean expect(TokenType tokenType) throws CompileException {
        return expect(tokenType, true);
    }

    /**
     * Checks if the current token is of expected type, and consumes if the consume flag is true.
     *
     * @param tokenType the token type expected
     * @param consume   whether to consume the next token if the expected token type matches
     * @return true if the current token has type equal to tokenType
     */
    protected boolean expect(TokenType tokenType, boolean consume) throws CompileException {
        if (getToken().getTokenType().equals(tokenType)) {
            if (consume)
                consumeNextToken();
            return true;
        }
        return false;
    }

    /**
     * Handles what happens when the PRINT() method was called by a compiled program. By default,
     * this method appends to the String variable named "PRINT" in the environment. You can override
     * this method in a subclass to hook what actually happens when PRINT() is called. Note that
     * according to the specs, all statements must return something. By default, this method returns
     * the message.
     *
     * @param message the message to print
     */
    protected Object print(Object message) {
        Object print = getEnvironment().getValue("PRINT");
        if (print == null) print = "";
        define("PRINT", (String) print + message);
        return message;
    }

    /**
     * The main entry point of the compiler program. Essentially collects all semi-colon statements.
     * According to the project specs, S() must return something so it returns an Object by default.
     * For the purposes of this program, this method just returns null.
     * Accepts grammar of the form:
     * S -> EOF | R;S
     *
     * @return an Object representing the list of statements
     */
    protected Object S() throws CompileException {
        if (expect(TokenType.EOF))
            return null;
        R();
        if (!expect(TokenType.SEMICOLON))
            onError("S: invalid statement or missing semicolon");
        else
            S();
        return null;
    }

    /**
     * Utility method for expecting an expression wrapped in parenthesis. Returns the value of the
     * expression.
     *
     * @param errorLabel1  the label for the error if there was no left parenthesis
     * @param errorLabel2  the label for the error if there was no right parenthesis
     * @param lexemeBefore the lexeme used before this wrapped expression
     * @return the value returned by the wrapped expression
     */
    protected Object expectWrappedExpression(String errorLabel1,
                                             String errorLabel2,
                                             String lexemeBefore) throws CompileException {
        if (!expect(TokenType.LPAREN))
            onError(errorLabel1 + ": expected left parenthesis"
                + (lexemeBefore != null ? " after " + lexemeBefore : ""));
        Object result = E();
        if (!expect(TokenType.RPAREN))
            onError(errorLabel2 + ": expected right parenthesis"
                + (lexemeBefore != null ? " after " + lexemeBefore + "(<expression>" : ""));
        return result;
    }

    /**
     * The grammar symbol for a result statement. Essentially collects a single-line statement
     * just right before a semi-colon. Note: no nested IFs allowed. According to the project specs,
     * all statements (which is essentially represented by this method) must return something so
     * this method returns an Object by default. However, for the purposes of this program, this
     * method returns whatever the delegate statement returns.
     * Accepts grammar of the form:
     * R -> PRINT(E) | IF(B) PRINT(E) | IF(B) A | A
     *
     * @return the value returned by the nested IF or PRINT expression
     */
    protected Object R() throws CompileException {

        if (expect(TokenType.PRINT))
            return print(expectWrappedExpression("R1", "R2", "PRINT"));

        else if (expect(TokenType.IF)) {

            if (!expect(TokenType.LPAREN))
                onError("R3: expected left parenthesis after IF");

            // save the current environment first in case condition is not met
            boolean condition = (boolean) B();

            if (!expect(TokenType.RPAREN))
                onError("R4: expected right parenthesis in IF");

            if (expect(TokenType.PRINT)) {
                Object wrappedResult = expectWrappedExpression("R5", "R6", "PRINT");
                if (condition)
                    return print(wrappedResult);
            } else {
                Environment previousEnvironment = getEnvironment();
                Object result = A();
                if (!condition) {
                    // reset the bindings because we don't want to assign the variable anymore
                    setEnvironment(previousEnvironment);
                } else {
                    return result;
                }
            }
        } else
            return A();

        return null;

    }


    /**
     * The grammar symbol for logical (boolean) expressions.
     * Accepts grammar of the form:
     * B -> E <= E | E >= E | E < E | E > E | E == E | E != E
     *
     * @return the result of a boolean or internal arithmetic expression
     */
    protected Object B() throws CompileException {
        Object lhs = E();
        Token op = getToken();
        consumeNextToken();
        Object rhs = E();
        switch (op.getTokenType()) {
            case EQUALS:
                return lhs.equals(rhs);
            case NOT_EQUALS:
                return !lhs.equals(rhs);
            case LESS_THAN:
                if (lhs instanceof String || rhs instanceof String)
                    return ("" + lhs).compareTo("" + rhs) < 0;
                else
                    return ((double) lhs) < ((double) rhs);
            case LESS_THAN_OR_EQUALS:
                if (lhs instanceof String || rhs instanceof String)
                    return ("" + lhs).compareTo("" + rhs) <= 0;
                else
                    return ((double) lhs) <= ((double) rhs);
            case GREATER_THAN:
                if (lhs instanceof String || rhs instanceof String)
                    return ("" + lhs).compareTo("" + rhs) > 0;
                else
                    return ((double) lhs) > ((double) rhs);
            case GREATER_THAN_OR_EQUALS:
                if (lhs instanceof String || rhs instanceof String)
                    return ("" + lhs).compareTo("" + rhs) >= 0;
                else
                    return ((double) lhs) >= ((double) rhs);
        }
        return false;
    }

    /**
     * The grammar symbol for an assignment statement. Note that according to the project specs,
     * all statements must return something. By default, this method returns null.
     * Accepts grammar of the form:
     * A -> IDENT = E
     */
    protected Object A() throws CompileException {
        if (!expect(TokenType.IDENT, false))
            onError("M1: expected an identifier as left value of an assignment statement");
        else {
            String identifier = getToken().getLexeme();
            consumeNextToken();
            if (!expect(TokenType.ASSIGNMENT))
                onError("M2: expected an equal sign after variable during assignment");
            else {
                Object value = E();
                define(identifier, value);
            }
        }
        return null;
    }

    /**
     * The grammar symbol for arithmetic and String expressions. The order is PENMDMAS:
     * P - parenthesis
     * E - exponent
     * N - unary negation
     * M - multiplication
     * D - division
     * M - modulo
     * A - addition
     * S - subtraction
     * Accepts grammar of the form:
     * E -> M | M + E | M - E
     *
     * @return the resulting value of the expression
     */
    protected Object E() throws CompileException {
        Object a = M();
        if (expect(TokenType.PLUS)) {
            Object b = E();
            if (!(a instanceof Double) || !(b instanceof Double))
                return "" + a + b;
            return (double) a + (double) b;
        } else if (expect(TokenType.MINUS, false)) {
            // To perform MINUS, we don't consume the MINUS token. We proceed to addition and just
            // perform unary negation later. This is the preferred approach because of issues when
            // for example E -> a - b + c becomes E -> a - (b + c) when subtraction is processed
            // immediately.
            Object b = E();
            if (!(a instanceof Double) || !(b instanceof Double)) {
                onError("E1: expected doubles after MINUS token");
                return 0.0;
            }
            return (double) a + (double) b;
        } else {
            return a;
        }
    }

    /**
     * The grammar symbol for modulo expressions. Note that unlike C/Java, the implementation
     * below performs right-to-left modulo, since the grammar parser is LL1.
     * Accepts grammar of the form:
     * M -> F | F % M
     */
    protected Object M() throws CompileException {
        Object a = F();
        if (expect(TokenType.MODULO)) {
            Object b = M();
            if (!(a instanceof Double) || !(b instanceof Double))
                onError("M1: invalid MODULO on non-doubles");
            return (double) a * (double) b;
        }
        return a;
    }

    /**
     * The grammar symbol for factor expressions.
     * Accepts grammar of the form:
     * F -> G | G * F | G / F
     */
    protected Object F() throws CompileException {
        Object a = G();
        if (expect(TokenType.MULT) || expect(TokenType.DIVIDE, false)) {
            Object b = F();
            if (!(a instanceof Double) || !(b instanceof Double))
                onError("F1: invalid MULT/DIVIDE on non-doubles");
            return (double) a * (double) b;
        }
        return a;
    }

    /**
     * The grammar symbol for mult-inverse operation (/x). Needed
     * as a flag for division to override the right-to-left default
     * behavior of an LL1 parser. A limitation, however, is that
     * this grammar implies that it is possible to perform inversion
     * by itself now, ergo "PRINT(/2);" is now a valid statement.
     * Accepts grammar of the form:
     * G -> U | / U
     */
    protected Object G() throws CompileException {
        boolean invert = expect(TokenType.DIVIDE);
        Object b = U();
        if (invert) {
            if (b instanceof Double)
                return 1.0 / (double) b;
            else
                onError("U1: expected negation of a double");
        }
        return b;
    }

    /**
     * The grammar symbol for unary negation.
     * Accepts grammar of the form:
     * U -> X | -X
     */
    protected Object U() throws CompileException {
        boolean negate = expect(TokenType.MINUS);
        Object b = X();
        if (negate) {
            if (b instanceof Double)
                return - (double) b;
            else
                onError("U1: expected negation of a double");
        }
        return b;
    }

    /**
     * The grammar symbol for exponentiation.
     * Accepts grammar of the form:
     * X -> P | P ** U
     */
    protected Object X() throws CompileException {
        Object a = P();
        if (expect(TokenType.EXP)) {
            Object b = X();
            if (!(a instanceof Double) || !(b instanceof Double)) {
                onError("X1: expected exponentiation of doubles");
                return 0.0;
            }
            return Math.pow((double) a, (double) b);
        }
        return a;
    }

    /**
     * The grammar symbol for parentheses.
     * Accepts grammar of the form:
     * P -> D | (E)
     */
    protected Object P() throws CompileException {
        if (expect(TokenType.LPAREN, false))
            return expectWrappedExpression("P1", "P2", null);
        return D();
    }

    /**
     * The grammar symbol representing any unit data type.
     * Accepts grammar of the form:
     * D -> IDENT | NUMBER | STRING | SQRT(E)
     */
    protected Object D() throws CompileException {
        Token token = getToken();
        switch (token.getTokenType()) {
            case IDENT: {
                consumeNextToken();
                Object value = getEnvironment().getValue(token.getLexeme());
                if (value == null)
                    value = 0.0;
                return value;
            }
            case NUMBER:
                consumeNextToken();
                return Double.parseDouble(token.getLexeme());
            case STRING:
                consumeNextToken();
                return token.getLexeme().substring(1, token.getLexeme().length() - 1);
            case SQRT: {
                consumeNextToken();
                Object value = expectWrappedExpression("D1", "D2", "SQRT");
                if (value instanceof Double) {
                    double val = (double) value;
                    if (val < 0) return 0.0; // dummy value
                    return Math.sqrt(val);
                }
                onError("D3: expected double for expression");
                break;
            }
            default:
                onError("D4: expected variable or literal");
        }
        return 0.0;
    }

}
