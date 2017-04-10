package project2;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import project1.handlers.Tokenizer;
import project1.handlers.TokenizerNoComments;

/**
 * An LL1 grammar parser with environment bindings
 */
public class CompilerGrammar {

    /**
     * Captures the bindings of the grammar parser
     */
    public final Environment bindings = new Environment();

    /**
     * Contains the list of all errors after compiling the grammar.
     */
    public final List<Error> errors = new ArrayList<>();
    /**
     * The currently used tokenizer for this compiler grammar. You can instantiate this by
     * calling setTokenizerClass() before calling compile().
     */
    private Tokenizer tokenizer = null;
    /**
     * The tokenizer class used for instantiating the tokenizer.
     */
    private Class<? extends Tokenizer> tokenizerClass = TokenizerNoComments.class;

    /**
     * Utility method for adding an error.
     * @param error the name of the grammar method where the error was called
     */
    protected void error(Error error) {
        errors.add(new Error(error));
    }

    /**
     * Gets the tokenizer class used for instantiating the tokenizer during compilation. The default
     * tokenizer class used by this compiler is is TokenizerNoComments.
     * @return a class that extends Tokenizer with a (String) constructor
     */
    public Class<? extends Tokenizer> getTokenizerClass() {
        return tokenizerClass;
    }

    /**
     * Sets the tokenizer class to be used for instantiating the tokenizer during compilation.
     * The class should have a declared constructor with a single String argument.
     * @param tokenizerClass a class that extends Tokenizer with a (String) constructor
     */
    public void setTokenizerClass(Class<? extends Tokenizer> tokenizerClass) throws NoSuchMethodException {
        if (tokenizerClass == null)
            throw new NullPointerException("Cannot have empty tokenizer class");
        Constructor constructor = tokenizerClass.getDeclaredConstructor(String.class);
        if (!constructor.isAccessible())
            throw new NoSuchMethodException("tokenizerClass must have an accessible (String) constructor");
        this.tokenizerClass = tokenizerClass;
    }

    /**
     * Compiles a given program and returns true if the program compiled without errors. All
     * bindings from previously compiled programs will be kept. Internally calls the method
     * compile(program, true). Errors can be accessed via this.errors property.
     * @param program the program to be tokenized and compiled
     * @return true if the program compiled without generating new errors
     */
    public boolean compile(String program) {
        return compile(program, true);
    }

    /**
     * Compiles a given program and returns true if the program compiled without errors.
     * @param program the program to be tokenized and compiled
     * @param keepBindings flag if previously compiled bindings should be kept or not
     * @return true if the program compiled without generating new errors
     */
    public boolean compile(String program, boolean keepBindings) {
        Constructor constructor;
        try {
            constructor = getTokenizerClass().getDeclaredConstructor(String.class);
            tokenizer = (Tokenizer) constructor.newInstance(program);
        } catch (NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException e) {
            error(new Error("compile: error in constructing tokenizer", e));
            return false;
        }
        if (keepBindings)
            bindings.clear();
        int currentErrorCount = errors.size();
        S();
        return errors.size() <= currentErrorCount;
    }

    protected void S() {

    }

}
