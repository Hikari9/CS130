package project2.compiler;

import java.util.ArrayList;
import java.util.List;

import project1.enums.Token;
import project1.enums.TokenType;
import project1.handlers.Tokenizer;
import project1.handlers.TokenizerNoComments;

/**
 * An LL1 grammar parser with environment.
 * IMPORTANT: this compiler DOES NOT allow booleans to be assigned to variables, as per the
 * project specs.
 */
public class CompilerGrammar {

    /**
     * Contains the list of all errors after compiling the grammar.
     */
    public final List<Error> errors = new ArrayList<>();
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
     * Defines a new identifier for the current environment of this compiler.
     *
     * @param identifier the identifier to the value to be bound
     * @param value      the value to be bound
     */
    public void define(String identifier, Object value) {
        setEnvironment(getEnvironment().define(identifier, value));
    }

    /**
     * Hook method for creating a tokenizer. By default, this constructs a TokenizerNoComments for
     * the program.
     *
     * @param program the program to tokenize
     * @return the Tokenizer object to be used by this compiler grammar
     */
    public Tokenizer onCreateTokenizer(String program) {
        return new TokenizerNoComments(program);
    }

    /**
     * Utility method for adding an error.
     *
     * @param error the Error that occured
     */
    protected void error(Error error) {
        errors.add(error);
    }

    /**
     * Utility method for adding an error message. Internally calls error(new Error(message)).
     *
     * @param message the error message
     */
    protected void error(String message) {
        error(new Error(message));
    }

    /**
     * Compiles a given program and returns true if the program compiled without errors. All
     * environment from previously compiled programs will be kept. Internally calls the method
     * compile(program, true). Errors can be accessed via this.errors property.
     *
     * @param program the program to be tokenized and compiled
     * @return true if the program compiled without generating new errors
     */
    public boolean compile(String program) {
        return compile(program, true);
    }

    /**
     * Compiles a given program and returns true if the program compiled without errors.
     *
     * @param program      the program to be tokenized and compiled
     * @param keepBindings flag if previously compiled environment should be kept or not
     * @return true if the program compiled without generating new errors
     */
    public boolean compile(String program, boolean keepBindings) {
        this.tokenizer = onCreateTokenizer(program);
        if (keepBindings)
            setEnvironment(new Environment());
        int currentErrorCount = errors.size();
        // start with the first token
        consumeNextToken();
        try {S();}
        catch (Throwable e) {
            error(new Error(e));
        }
        return errors.size() <= currentErrorCount;
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
    protected void consumeNextToken() {
        token = tokenizer.nextToken();
    }

    /**
     * Checks if the current token is of expected type. Calls consumeNextToken() if the expectation
     * was correct. Internally calls expect(tokenType, true).
     *
     * @param tokenType the token type expected
     * @return true if the current token has type equal to tokenType
     */
    protected boolean expect(TokenType tokenType) {
        return expect(tokenType, true);
    }

    /**
     * Checks if the current token is of expected type, and consumes if the consume flag is true.
     *
     * @param tokenType the token type expected
     * @param consume   whether to consume the next token if the expected token type matches
     * @return true if the current token has type equal to tokenType
     */
    protected boolean expect(TokenType tokenType, boolean consume) {
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
     * this method in a subclass to hook what actually happens when PRINT() is called.
     *
     * @param message the message to print
     */
    protected void print(Object message) {
        Object print = getEnvironment().getValue("PRINT");
        if (print == null) print = "";
        define("PRINT", (String) print + message);
    }

    /**
     * The main entry point of the compiler program. Essentially collects all semi-colon statements.
     * <p>
     * Accepts grammar of the form:
     * S -> EOF | R;S
     */
    protected void S() {
        if (expect(TokenType.EOF))
            return;
        R();
        if (!expect(TokenType.SEMICOLON))
            error("S: expected semicolon after R");
        else
            S();
    }

    /**
     * Utility method for expecting an expression wrapped in parenthesis. Returns the value of the
     * expression.
     *
     * @param errorLabel1  the label for the error if there was no left parenthesis
     * @param errorLabel2  the label for the error if there was no right parenthesis
     * @param lexemeBefore the lexeme used before this wrapped expression
     * @return the value of the wrapped expression
     */
    protected Object expectWrappedExpression(String errorLabel1,
                                             String errorLabel2,
                                             String lexemeBefore) {
        if (!expect(TokenType.LPAREN))
            error(errorLabel1 + ": expected left parenthesis"
                + (lexemeBefore != null ? " after " + lexemeBefore : ""));
        Object result = E();
        if (!expect(TokenType.RPAREN))
            error(errorLabel2 + ": expected right parenthesis"
                + (lexemeBefore != null ? " after " + lexemeBefore + "(<expression>" : ""));
        return result;
    }

    /**
     * The grammar symbol for a result statement. Essentially collects a single-line statement
     * just right before a semi-colon. Note: no nested IFs yet.
     *
     * Accepts grammar of the form:
     * R -> PRINT(E) | IF(B) PRINT(E) | IF(B) A | A
     */
    protected void R() {

        if (expect(TokenType.PRINT))
            print(expectWrappedExpression("R1", "R2", "PRINT"));

        else if (expect(TokenType.IF)) {

            if (!expect(TokenType.LPAREN))
                error("R3: expected left parenthesis");

            // save the current environment first in case condition is not met
            boolean condition = B();

            if (!expect(TokenType.RPAREN))
                error("R4: expected right parenthesis");

            if (expect(TokenType.PRINT)) {
                Object wrappedResult = expectWrappedExpression("R5", "R6", "PRINT");
                if (condition)
                    print(wrappedResult);
            } else {
                Environment previousEnvironment = getEnvironment();
                A();
                if (!condition) {
                    // reset the bindings because we don't want to assign the variable anymore
                    setEnvironment(previousEnvironment);
                }
            }
        } else
            A();
    }


    /**
     * The grammar symbol for logical (boolean) expressions. Note that this does not yet accept
     *
     *
     * Accepts grammar of the form:
     * B -> E <= E | E >= E | E < E | E > E | E == E | E != E
     *
     * @return the result of a boolean or internal arithmetic expression
     */
    protected boolean B() {
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
     * The grammar symbol for an assignment statement.
     * <p>
     * Accepts grammar of the form:
     * A -> IDENT = E
     */
    protected void A() {
        if (!expect(TokenType.IDENT, false))
            error("M1: expected an identifier as left value of an assignment statement");
        else {
            String identifier = getToken().getLexeme();
            consumeNextToken();
            if (!expect(TokenType.ASSIGNMENT))
                error("M2: expected an equal sign after variable during assignment");
            else {
                Object value = E();
                define(identifier, value);
            }
        }
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
     *
     * Accepts grammar of the form:
     * E -> (E) | (E) + E | (E) - E | (E) * E | (E) / E | (E) % E | (E) ** E
     * E -> F | F + E | F - E
     *
     * @return the resulting value of the expression
     */
    protected Object E() {
        if (expect(TokenType.LPAREN, false)) {
            Object a = expectWrappedExpression("E1", "E2", null);
            TokenType op = getToken().getTokenType();
            switch (op) {
                case MINUS: {
                    // special case, perform a plus without consuming the token
                    Object b = E();
                    if (a instanceof Double && b instanceof Double)
                        return (double) a + (double) b;
                    error("E2: invalid " + op.name() + " on doubles");
                    break;
                }
                case PLUS: case MULT: case DIVIDE: case MODULO: case EXP: {
                    consumeNextToken();
                    Object b = E();
                    if (a instanceof Double && b instanceof Double) {
                        double x = (double) a;
                        double y = (double) b;
                        switch (op) {
                            case PLUS: return x + y;
                            case MULT: return x * y;
                            case DIVIDE: return x / y;
                            case MODULO: return x % y; // note: double modulo
                            case EXP: return Math.pow(x, y);
                        }
                    } else {
                        switch (op) {
                            case PLUS:
                                return "" + a + b;
                            case MULT:
                                if (b instanceof Double) {
                                    // concatenate string n times
                                    StringBuilder sb = new StringBuilder();
                                    int numTimes = (int) (double) b;
                                    for (int i = 0; i < numTimes; ++i)
                                        sb.append(a);
                                    return sb.toString();
                                }
                            default:
                                error("E3: invalid " + op.name() + " on non-doubles");
                        }
                    }
                    break;
                }
                default:
                    return a;
            }
        } else {
            Object a = F();
            if (expect(TokenType.PLUS)) {
                Object b = E();
                if (!(a instanceof Double) || !(b instanceof Double))
                    return "" + a + b;
                return (double) a + (double) b;
            } else if (expect(TokenType.MINUS, false)) {
                Object b = E();
                if (!(a instanceof Double) || !(b instanceof Double)) {
                    error("E4: expected doubles after MINUS token");
                    return 0.0;
                }
                return (double) a + (double) b;
            } else {
                return a;
            }
        }
        return 0.0;
    }

    /**
     * The grammar symbol for factor expressions.
     *
     * Accepts grammar of the form:
     * F -> U | U * F | U / F | U % F
     * F -> U * (E) | U / (E) | U % (E)
     */
    protected Object F() {
        Object a = U();
        TokenType op = getToken().getTokenType();
        switch (op) {
            case MULT: case DIVIDE: case MODULO: {
                consumeNextToken();
                Object b;
                if (expect(TokenType.LPAREN, false))
                    b = expectWrappedExpression("F1", "F2", null);
                else
                    b = F();
                if (!(a instanceof Double) || !(b instanceof Double)) {
                    error("F3: invalid " + op.name() + "on non-doubles");
                }
                double x = (double) a;
                double y = (double) b;
                switch (op) {
                    case MULT: return x * y;
                    case DIVIDE: return x / y;
                    case MODULO: return x % y;
                }
                return 0.0;
            }
            default:
                return a;
        }
    }

    /**
     * The grammar symbol for unary negation.
     *
     * Accepts grammar of the form:
     * U -> X | -X | -(E)
     */
    protected Object U() {
        if (expect(TokenType.MINUS)) {
            Object b;
            if (expect(TokenType.LPAREN, false))
                b = expectWrappedExpression("U1", "U2", "negation");
            else
                b = X();
            if (!(b instanceof Double)) {
                error("U3: expected negation of a double");
                return 0.0;
            }
            return -(double) b;
        }
        return X();
    }

    /**
     * The grammar symbol for exponentiation.
     *
     * Accepts grammar of the form:
     * X -> D | D ** U | D ** (E)
     */
    protected Object X() {
        Object a = D();
        if (expect(TokenType.EXP)) {
            Object b;
            if (expect(TokenType.LPAREN, false))
                b = expectWrappedExpression("X1", "X2", "exponentiation (**)");
            else
                b = U();
            if (!(a instanceof Double) || !(b instanceof Double)) {
                error("X3: expected exponentiation of doubles");
                return 0.0;
            }
            return Math.pow((double) a, (double) b);
        }
        return a;
    }

    /**
     * The grammar symbol representing any data type.
     *
     * Accepts grammar of the form:
     * D -> IDENT | NUMBER | STRING | SQRT(E)
     *
     * @return
     */
    protected Object D() {
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
                error("D3: expected double for expression");
                break;
            }
            default:
                error("D4: expected variable or literal");
        }
        return 0.0;
    }

}
