package project1.enums;

public enum TokenType {

    // data
    NUMBER, IDENT, STRING, EOF,

    // arithmetic
    PLUS, MINUS, MULT, DIVIDE, MODULO, EXP,

    // symbols
    LPAREN, RPAREN, COMMA, PERIOD, EQUALS,

    // special
    ERROR, COMMENT;

    @Override
    public String toString() {
        return this.name();
    }

}
