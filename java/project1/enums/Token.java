package project1.enums;

/**
 * A class representing a token with its type and lexeme.
 */
public class Token {

    private TokenType type;
    private String lexeme;

    /**
     * Constructs a token given a token type and lexeme.
     * @param type the token type
     * @param lexeme the lexeme
     */
    public Token(TokenType type, String lexeme) {
        this.type = type;
        this.lexeme = lexeme;
    }

    /**
     * Constructs a token with an empty lexeme.
     * @param type the token type
     */
    public Token(TokenType type) {
        this(type, "");
    }

    /**
     * Getter for the lexeme.
     * @return the lexeme String
     */
    public String getLexeme() {
        return lexeme;
    }

    /**
     * Getter for the token type.
     * @return the token type
     */
    public TokenType getTokenType() {
        return type;
    }

    /**
     * Getter for the ID of the token type.
     * @return a unique ID represented by the token type
     */
    public int getId() {
        return type.ordinal();
    }

}