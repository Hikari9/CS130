package project2;

import project2.compiler.CompilerGrammar;
import project2.compiler.Environment;

public class CompilerGrammarWithDebug extends CompilerGrammar {

    /**
     * Set a flag to continue whenever a boolean expression is encountered during IF statements.
     */
    Boolean condition = null;

    @Override
    protected void print(Object message) {
        if (condition == null || condition)
            System.out.println("output(" + message + ")");
    }

    @Override
    protected boolean B() {
        condition = super.B();
        if (condition)
            System.out.print("condition met, ");
        else
            System.out.println("condition not met");
        return condition;
    }

    @Override
    protected void A() {
        super.A();
        if (condition == null || condition) {
            Environment env = getEnvironment();
            System.out.println("computation performed(" + env.identifier + " = " + env.value + ")");
        }
    }

    @Override
    protected void R() {
        condition = null;
        super.R();
    }

}