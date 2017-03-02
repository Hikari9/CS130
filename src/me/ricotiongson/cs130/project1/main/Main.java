package me.ricotiongson.cs130.project1.main;


import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import me.ricotiongson.cs130.project1.enums.Token;
import me.ricotiongson.cs130.project1.enums.TokenType;

public class Main {

    public static void run(String inputFilename, String outputFilename) throws IOException {
        String folder = "src/me/ricotiongson/cs130/project1/data/";
        BufferedReader br = new BufferedReader(new FileReader(folder + inputFilename));
        PrintWriter pw = new PrintWriter(new FileWriter(folder + outputFilename));

        String line;
        StringBuilder builder = null;
        while ((line = br.readLine()) != null) {
            if (builder == null)
                builder = new StringBuilder();
            else
                builder.append("\n");
            builder.append(line);
        }

        String input = builder.toString();
        pw.println(input);
        Tokenizer tokenizer = new Tokenizer(input);
        while (tokenizer.hasNextToken()) {
            Token token = tokenizer.nextToken();
            if (token.getTokenType() != TokenType.COMMENT)
                pw.println(token.getTokenType() + "\t" + token.getLexeme());
        }

        pw.close();
    }

    public static void main(String[] args) throws IOException {
        run("sample1.in", "program1.out");
        run("sample2.in", "program2.out");
        run("sample3.in", "program3.out");
    }
}
