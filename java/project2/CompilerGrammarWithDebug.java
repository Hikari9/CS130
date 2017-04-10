package project2;

import java.io.PrintStream;

import project2.compiler.CompilerGrammar;
import project2.compiler.Environment;

public class CompilerGrammarWithDebug extends CompilerGrammar {

    /**
     * Set a flag to continue whenever a boolean expression is encountered during IF statements.
     */
    Boolean condition = null;
    PrintStream out;

    public CompilerGrammarWithDebug() {
        this(System.out);
    }

    public CompilerGrammarWithDebug(PrintStream printStream) {
        super();
        out = printStream;
    }

    @Override
    protected void print(Object message) {
        if (condition == null || condition) {
            out.println("output (" + message + ")");
        }
    }

    @Override
    protected boolean B() {
        condition = super.B();
        if (condition)
            out.print("condition met, ");
        else
            out.println("condition not met");
        return condition;
    }

    @Override
    protected void A() {
        super.A();
        if (condition == null || condition) {
            Environment env = getEnvironment();
            out.printf("computation performed (%s = %s)\n", env.identifier, ""  + env.value);
        }
    }

    @Override
    protected void R() {
        condition = null;
        super.R();
    }

}