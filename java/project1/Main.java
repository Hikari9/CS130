package project1;


import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import project1.handlers.TokenizerHandler;
import project1.enums.Token;
import project1.enums.TokenType;
import project1.handlers.Tokenizer;

/**
 * The main driver program for this project.
 */
public class Main {

    /**
     * Main driver method.
     */
    public static void main(String[] args) throws IOException {

        TokenizerHandler handler = Tokenizer.getHandler();
        handler.printDfaTable();
        runTokenizer("sample1.in", "program1.out");
        runTokenizer("sample2.in", "program2.out");
        runTokenizer("sample3.in", "program3.out");
    }

    /**
     * Runs the tokenizer for a given input file and output file in the package data directory.
     * @param inputFilename the filename of the input file to tokenize, with respect to the data directory
     * @param outputFilename the filename of the output file to tokenize, with respect to the data directory
     * @throws IOException when there is an error in reading/writing files
     */
    public static void runTokenizer(String inputFilename, String outputFilename) throws IOException {

        // Get the path to the data folder
        String pack = "java/" + Main.class.getPackage().getName();
        String folder = pack + "/data/";

        // Create file reader/writer
        BufferedReader br = new BufferedReader(new FileReader(folder + inputFilename));
        PrintWriter pw = new PrintWriter(new FileWriter(folder + outputFilename));

        // Build input string
        String line;
        StringBuilder builder = null;
        while ((line = br.readLine()) != null) {
            if (builder == null)
                builder = new StringBuilder();
            else
                builder.append("\n");
            builder.append(line);
        }

        // Tokenize
        String input = builder.toString();
        Tokenizer tokenizer = new Tokenizer(input);
        while (tokenizer.hasNextToken()) {
            Token token = tokenizer.nextToken();
            if (token.getTokenType() != TokenType.COMMENT)
                pw.println(token.getTokenType() + "\t" + token.getLexeme());
        }

        pw.close();
    }

}
