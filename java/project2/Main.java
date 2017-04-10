package project2;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import project2.compiler.CompilerGrammar;

/**
 * Tester program
 */
public class Main {

    public Main(String filename) throws IOException {

        // read file
        String pack = "java/" + getClass().getPackage().getName();
        String folder = pack + "/data/" + filename;
        BufferedReader br = new BufferedReader(new FileReader(folder));

        // collect contents of file
        String line;
        StringBuilder builder = new StringBuilder();
        while ((line = br.readLine()) != null)
            builder.append(line).append('\n');

        // create compiler
        String program = builder.toString();
        CompilerGrammar compiler = new CompilerGrammarWithDebug();
        compiler.compile(program);

    }

    public static void main(String[] args) throws IOException {
        new Main("sample.in");
    }


}


