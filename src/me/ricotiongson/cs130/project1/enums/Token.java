package me.ricotiongson.cs130.project1.enums;

public class Token {

    private TokenType type;
    private String lexeme;

    public Token(TokenType type, String lexeme) {
        this.type = type;
        this.lexeme = lexeme;
    }

    public Token(TokenType type) {
        this(type, "");
    }

    public String getLexeme() {
        return lexeme;
    }

    public TokenType getTokenType() {
        return type;
    }

    public int getId() {
        return type.ordinal();
    }

}