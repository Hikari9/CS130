package me.ricotiongson.cs130.project1.main;

import java.util.Map;
import java.util.Set;

import me.ricotiongson.cs130.project1.enums.Symbol;
import me.ricotiongson.cs130.project1.enums.Token;
import me.ricotiongson.cs130.project1.enums.TokenType;
import me.ricotiongson.cs130.project1.handlers.LexicalTokenizerHandler;
import me.ricotiongson.cs130.project1.handlers.TokenizerHandler;

/**
 * Tokenizer class for parsing tokens from a character buffer.
 */
public class Tokenizer {

    public static TokenizerHandler getHandler() {
        return LexicalTokenizerHandler.getInstance();
    }

    char[] buffer;
    int pointer;

    /**
     * Constructs a tokenizer from a String buffer
     * @param buffer the String buffer to tokenize
     */
    public Tokenizer(String buffer) {
        this.buffer = buffer.toCharArray();
        this.pointer = 0;
    }

    /**
     * Checks if there is a next token is not EOF.
     * @return true if the next token is not EOF
     */
    public boolean hasNextToken() {
        int currentPointer = pointer;
        TokenType type = nextToken().getTokenType();
        pointer = currentPointer;
        return type != TokenType.EOF;
    }

    /**
     * Parses the next token from the buffer, until a final state is reached. Note that does not
     * append the symbols that loop in the start state to the lexeme.
     * @return the next Token along with its lexeme
     */
    public Token nextToken() {

        // initialize
        TokenizerHandler handler = getHandler();
        Set finalStates = handler.getFinalStates();
        Map<Symbol, Integer> transitionMap = handler.getTransitionMap();
        int[][] dfaTable = handler.getDfaTable();
        int startState = handler.getStartState();
        StringBuilder builder = new StringBuilder();

        // traverse dfa states
        int state;
        for (state = startState; !finalStates.contains(state); ++pointer) {
            Symbol symbol = peekSymbol();
            Integer symbolId = transitionMap.get(symbol);
            if (symbol == null || symbolId == null || symbolId == -1) {
                // invalid character
                builder.append(peekCharacter());
                pointer++;
                return new Token(TokenType.ERROR, builder.toString());
            }
            state = dfaTable[state][symbolId];
            if (state == -1) {
                // trap state
                pointer++;
                return new Token(TokenType.ERROR, builder.toString());
            }
            if (state != startState)
                builder.append(peekCharacter());
        }


        // rollback if needed
        Integer rollback = handler.getRollbackStateMap().get(state);
        if (rollback != null) {
            for (int i = 0; i < rollback && pointer > 0; ++i) {
                --pointer;
                builder.deleteCharAt(builder.length() - 1);
            }
        }

        // return token with lexeme
        TokenType type = handler.getTokenMap()[state];
        return new Token(type, builder.toString());

    }

    /**
     * Peeks the next character in the buffer without removing it from the buffer.
     * @return the next character in the buffer
     */
    public char peekCharacter() {
        if (pointer < buffer.length)
            return buffer[pointer];
        return '\0';
    }

    /**
     * Peeks the next Symbol in the buffer without removing it from the buffer.
     * @return the next Symbol representing the next character in the buffer
     */
    public Symbol peekSymbol() {
        return Symbol.fromCharacter(peekCharacter());
    }

}
