package project1.enums;

import java.util.HashMap;

/**
 * A class representing the alphabet used for tokenizing
 */
public enum Symbol {

    // note: symbol groups must be disjoint with each other!

    // clustered symbols
    DIGIT,
    LETTER_NOT_E,
    LETTER_E,
    SINGLE_QUOTE,
    DOUBLE_QUOTE,
    WHITESPACE,
    ENDLINE,

    // relational
    EQUALS, EXCLAMATION_POINT,
    GREATER_THAN,
    LESS_THAN,

    // literals
    EOF,
    PLUS, MULT, MINUS, DIVIDE, MODULO,
    LPAREN, RPAREN, COMMA, PERIOD,
    HASHTAG,
    SEMICOLON,
    ERROR

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

    /**
     * Maps a character to a Symbol.
     * @param ch the character to map
     * @return the Symbol representing this character, Symbol.ERROR if character does not exist
     */
    public static Symbol fromCharacter(Character ch) {
        return characterMap.getOrDefault(ch, Symbol.ERROR);
    }

    /**
     * Get available characters per Symbol.
     */
    @Override
    public String toString() {
        switch (this) {
            case DIGIT:
                return "0123456789";
            case LETTER_NOT_E:
                return "QWRTYUIOPASDFGHJKLZXCVBNMqwrtyuiopasdfghjklzxcvbnm";
            case LETTER_E:
                return "eE";
            case SINGLE_QUOTE:
                return "'";
            case DOUBLE_QUOTE:
                return "\"";
            case WHITESPACE:
                return " \t";
            case ENDLINE:
                return "\n";
            case EOF:
                return "\0";
            case PLUS:
                return "+";
            case MULT:
                return "*";
            case MINUS:
                return "-";
            case DIVIDE:
                return "/";
            case MODULO:
                return "%";
            case LPAREN:
                return "(";
            case RPAREN:
                return ")";
            case COMMA:
                return ",";
            case PERIOD:
                return ".";
            case EQUALS:
                return "=";
            case EXCLAMATION_POINT:
                return "!";
            case GREATER_THAN:
                return ">";
            case LESS_THAN:
                return "<";
            case SEMICOLON:
                return ";";
            case HASHTAG:
                return "#";
            default:
                return "";
        }
    }
}
