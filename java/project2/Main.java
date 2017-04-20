package project2;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;

import project2.compiler.CompileException;
import project2.compiler.CompilerGrammar;

public class Main {

    /**
     * Driver program
     */
    public static void main(String[] args) throws IOException {
        new Main("sample-rico.in", "sample-rico.out");
        new Main("sample1.in", "sample1.out");
        new Main("sample2.in", "sample2.out");
        new Main("sample3-error-cases.in", "sample3-error-cases.out");
    }

    /**
     * Compiles a given input file and outputs the debug text to the output file found in
     * the /project2/data/ folder.
     * @param programFilename the filename to the input file to compile
     * @param debugOutFilename the filename to the output file where the debug text will go
     * @throws IOException whenever files cannot be read/written
     */
    public Main(String programFilename, String debugOutFilename) throws IOException {

        System.out.println();
        System.out.println("Compiling [" + programFilename + "]");

        // prepare paths
        String packageDir = "java/" + getClass().getPackage().getName();
        String dataFolder = packageDir + "/data/";
        String programPath = dataFolder + programFilename;
        String debugOutPath = dataFolder + debugOutFilename;

        // create compiler grammar that debugs to debug output filename
        PrintStream debugStream = new PrintStream(debugOutPath);
        CompilerGrammarWithDebug compiler = new CompilerGrammarWithDebug(debugStream);

        // collect all the contents of input file
        StringBuilder buffer = new StringBuilder();
        BufferedReader bufferedReader = new BufferedReader(new FileReader(programPath));

        // compile for every semicolon so that we can perform line-number-based debugging
        int startingLineNumber = 1;
        for (int lineNumber = 1; ; lineNumber++) {
            String line = bufferedReader.readLine();
            int semiColon = line != null ? line.indexOf(";") : 0;
            int hashtagComment = line != null ? line.indexOf("#") : 0;
            int slashComment = line != null ? line.indexOf("//") : 0;
            if (hashtagComment < 0)
                hashtagComment = Integer.MAX_VALUE;
            if (slashComment < 0)
                slashComment = Integer.MAX_VALUE;
            if (line == null || (semiColon >= 0 && semiColon < hashtagComment && semiColon < slashComment)) {
                if (line != null)
                    buffer.append(line.substring(0, semiColon + 1));
                try {
                    // try compiling this statement
                    compiler.compile(buffer.toString());
                } catch (CompileException ignore) {
                    // for compiler grammar debug, exceptions are logged and not thrown
                }
                if (compiler.getErrors().size() > 0) {
                    // compile error!
                    String compileErrorMessage = "compile error on line"
                        + (startingLineNumber == lineNumber
                        ? " " + lineNumber
                        : "s " + startingLineNumber + "-" + lineNumber)
                        + " "
                        + compiler.getErrors().toString();
                    // print error to console and to debugStream
                    System.out.println(compileErrorMessage);
                    debugStream.println(compileErrorMessage);
                    compiler.getErrors().clear();
                }
                startingLineNumber = lineNumber + 1;
                buffer = new StringBuilder();
                if (line != null)
                    buffer.append(line.substring(semiColon + 1));
                buffer.append('\n');
            } else {
                buffer.append(line).append('\n');
            }
            if (line == null) {
                break;
            }
        }

        // close the streams
        bufferedReader.close();
        debugStream.close();

    }


}


