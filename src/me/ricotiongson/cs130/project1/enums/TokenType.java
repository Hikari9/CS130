package me.ricotiongson.cs130.project1.enums;

public enum TokenType {

    // data
    NUMBER, IDENT, STRING, EOF,

    // arithmetic
    PLUS, MINUS, MULT, DIVIDE, MODULO, EXP,

    // symbols
    LPAREN, RPAREN, COMMA, PERIOD, EQUALS,

    // special
    ERROR, COMMENT;

    /**
     * Checks whether this TokenType type is between a range of two other TokenType types.
     *
     * @param left  the lower bound TokenType type
     * @param right the upper bound TokenType type
     * @return true if the order of this TokenType type is indeed between left and right
     */
    private boolean isBetween(TokenType left, TokenType right) {
        return ordinal() >= left.ordinal() && ordinal() <= right.ordinal();
    }

    @Override
    public String toString() {
        return this.name();
    }

    /**
     * Checks whether this TokenType is a data type.
     *
     * @return true if TokenType type is data type
     */
    public boolean isData() {
        return isBetween(NUMBER, EOF);
    }

    /**
     * Checks whether this TokenType is an arithmetic operator.
     *
     * @return true if TokenType type is an arithmetic operator
     */
    public boolean isArithmeticOperator() {
        return isBetween(PLUS, EXP);
    }

    /**
     * Checks whether this TokenType is a symbol.
     *
     * @return true if TokenType type is a symbol
     */
    public boolean isSymbol() {
        return isBetween(LPAREN, EQUALS);
    }

}
