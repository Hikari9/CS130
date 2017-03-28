package project1.enums;

public enum TokenType {

    // data
    NUMBER, IDENT, STRING, EOF,

    // arithmetic
    PLUS, MINUS, MULT, DIVIDE, MODULO, EXP,

    // symbols
    LPAREN, RPAREN, COMMA, PERIOD, SEMICOLON,

    // relational
    EQUALS, NOT_EQUALS, GREATER_THAN, GREATER_THAN_OR_EQUALS, LESS_THAN, LESS_THAN_OR_EQUALS,

    // conditional
    IF, PRINT, SQRT,

    // special
    ERROR, COMMENT,

    // operators
    ASSIGNMENT;

    @Override
    public String toString() {
        return this.name();
    }

}
