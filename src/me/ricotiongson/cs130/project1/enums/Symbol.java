package me.ricotiongson.cs130.project1.enums;

import java.util.HashMap;

public enum Symbol {

    // note: symbol groups must be disjoint with each other!

    // clustered symbols
    DIGIT,
    LETTER_NOT_E,
    LETTER_E,
    SINGLE_QUOTE,
    DOUBLE_QUOTE,
    WHITESPACE,

    // literals
    EOF,
    PLUS, MULT, MINUS, DIVIDE, MODULO,
    LPAREN, RPAREN, COMMA, PERIOD, EQUALS

    // you can add more symbols here
    ;

    private static HashMap<Character, Symbol> characterMap = new HashMap<>();
    static {
        for (Symbol symbol : Symbol.values()) {
            String pattern = symbol.toString();
            for (Character ch : pattern.toCharArray()) {
                characterMap.put(ch, symbol);
            }
        }
    }

    public static Symbol fromCharacter(Character ch) {
        return characterMap.get(ch);
    }

    // check if character matches symbol
    public boolean matches(Character c) {
        return Symbol.fromCharacter(c) == this;
    }

    /**
     * Get available characters per Symbol
     */
    @Override
    public String toString() {
        switch (this) {
            case DIGIT: return "0123456789";
            case LETTER_NOT_E: return "QWRTYUIOPASDFGHJKLZXCVBNMqwrtyuiopasdfghjklzxcvbnm";
            case LETTER_E: return "eE";
            case SINGLE_QUOTE: return "'";
            case DOUBLE_QUOTE: return "\"";
            case WHITESPACE: return " \t\n";
            case EOF: return "\0";
            case PLUS: return "+";
            case MULT: return "*";
            case MINUS: return "-";
            case DIVIDE: return "/";
            case MODULO: return "%";
            case LPAREN: return "(";
            case RPAREN: return ")";
            case COMMA: return ",";
            case PERIOD: return ".";
            case EQUALS: return "=";
            default: return "";
        }
    }
}
