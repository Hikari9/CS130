package project2;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import project2.compiler.CompileException;
import project2.compiler.CompilerGrammar;
import project2.compiler.Environment;

/**
 * This class hooks onto CompilerGrammar methods to print debug text.
 */
public class CompilerGrammarWithDebug extends CompilerGrammar {

    // Set a flag to continue whenever a boolean expression is encountered during IF statements.
    Boolean condition = null;
    PrintStream out;
    List<String> errors = new ArrayList<>();

    /**
     * Constructs a default CompilerGrammarWithDebug object that wraps to the System.out stream.
     */
    public CompilerGrammarWithDebug() {
        this(System.out);
    }

    /**
     * Constructs a CompilerGrammarWithDebug that wraps to the given PrintStream.
     * @param printStream
     */
    public CompilerGrammarWithDebug(PrintStream printStream) {
        super();
        out = printStream;
    }

    /**
     * Getter to the list of errors produced by this compiler grammar.
     * @return a reference to the list of errors
     */
    public List<String> getErrors() {
        return errors;
    }

    /**
     * Log errors instead of throwing an exception.
     * @param message the error message
     */
    @Override
    protected void onError(String message) throws CompileException {
        // super.onError(message);
        errors.add(message);
    }

    /**
     * Prints "output ({message})" whenever a PRINT statement is encountered.
     */
    @Override
    protected Object print(Object message) {
        if (condition == null || condition) {
            if (message instanceof Double)
                out.printf("output (%.2f)\n", (Double) message);
            else
                out.println("output (" + message + ")");
        }
        return super.print(message);
    }

    /**
     * Prints "condition met, " or "condition not met" whenever a boolean expression is encountered.
     * Toggles the condition flag for the current statement.
     */
    @Override
    protected Object B() throws CompileException {
        this.condition = (boolean) super.B();
        if (condition) out.print("condition met, ");
        else out.println("condition not met");
        return condition;
    }

    /**
     * Prints "computation performed ({identifier} = {result})" whenever an assignment statement
     * is encountered.
     */
    @Override
    protected Object A() throws CompileException {
        Object result = super.A();
        if (condition == null || condition) {
            Environment env = getEnvironment();
            out.printf("computation performed (%s = ", env.identifier);
            if (env.value instanceof Double)
                out.printf("%.2f)\n", (Double) env.value);
            else
                out.print(env.value + ")\n");
        }
        return result;
    }

    /**
     * Reset the condition flag for every statement.
     */
    @Override
    protected Object R() throws CompileException {
        condition = null;
        return super.R();
    }

}