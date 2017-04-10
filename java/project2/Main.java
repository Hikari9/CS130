package project2;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;

import project2.compiler.CompilerGrammar;

/**
 * Tester program
 */
public class Main {

    public Main(String filein, String fileout) throws IOException {

        // read file
        String pack = "java/" + getClass().getPackage().getName();
        String folder = pack + "/data/" + filein;
        String folderout = pack + "/data/" + fileout;
        BufferedReader br = new BufferedReader(new FileReader(folder));

        // collect contents of file
        String line;
        StringBuilder builder = new StringBuilder();
        while ((line = br.readLine()) != null)
            builder.append(line).append('\n');

        // create compiler
        String program = builder.toString();
        CompilerGrammar compiler = new CompilerGrammarWithDebug(new PrintStream(folderout));
        compiler.compile(program);

        // show errors
        System.out.println();
        for (Error error : compiler.errors) {
            error.printStackTrace();
        }

    }

    public static void main(String[] args) throws IOException {
        new Main("sample2.in", "sample2.out");
    }


}


