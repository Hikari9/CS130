package project2;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;

import project2.compiler.CompileException;
import project2.compiler.CompilerGrammar;

/**
 * Tester program
 */
public class Main {

    /**
     * Driver program
     */
    public static void main(String[] args) throws IOException {
        new Main("sample-rico.in", "sample-rico.out");
    }

    /**
     * Compiles a given input file and outputs the debug text to the output file found in
     * the /project2/data/ folder.
     * @param programFilename the filename to the input file to compile
     * @param debugOutFilename the filename to the output file where the debug text will go
     * @throws IOException whenever files cannot be read/written
     */
    public Main(String programFilename, String debugOutFilename) throws IOException {

        // prepare paths
        String packageDir = "java/" + getClass().getPackage().getName();
        String dataFolder = packageDir + "/data/";
        String programPath = dataFolder + programFilename;
        String debugOutPath = dataFolder + debugOutFilename;

        // collect all the contents of input file
        String line;
        StringBuilder builder = new StringBuilder();
        BufferedReader bufferedReader = new BufferedReader(new FileReader(programPath));
        while ((line = bufferedReader.readLine()) != null)
            builder.append(line).append('\n');
        String program = builder.toString();
        bufferedReader.close();

        // create compiler grammar that debugs to debug output filename
        PrintStream debugStream = new PrintStream(debugOutPath);
        CompilerGrammar compiler = new CompilerGrammarWithDebug(debugStream);

        try {
            compiler.compile(program);
            System.out.println("Program ["
                + programPath
                + "] successfully compiled to ["
                + debugOutPath
                + "]");
        } catch (CompileException e) {
            System.out.println("Compile error");
            // show errors to console error stream
            e.printStackTrace(System.err);
            // show the errors to the debug stream
            e.printStackTrace(debugStream);
        }

    }


}


