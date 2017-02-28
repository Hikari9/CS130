package me.ricotiongson.cs130.project1.main;


import java.util.Scanner;

import me.ricotiongson.cs130.project1.enums.Token;

public class Main {

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        while (sc.hasNextLine()) {
            Tokenizer tokenizer = new Tokenizer(sc.nextLine());
            while (tokenizer.hasNextToken()) {
                Token token = tokenizer.nextToken();
                System.out.println(token.getTokenType().name() + ": " + token.getLexeme());
            }
        }
    }
}
