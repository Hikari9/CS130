package me.ricotiongson.cs130.project1.main;

import java.util.Map;
import java.util.Set;

import me.ricotiongson.cs130.project1.enums.Symbol;
import me.ricotiongson.cs130.project1.enums.Token;
import me.ricotiongson.cs130.project1.enums.TokenType;
import me.ricotiongson.cs130.project1.handlers.LexicalTokenizerHandler;
import me.ricotiongson.cs130.project1.handlers.TokenizerHandler;

public class Tokenizer {

    char[] buffer;
    int pointer;

    public static TokenizerHandler getHandler() {
        return LexicalTokenizerHandler.getInstance();
    }

    public Tokenizer(String buffer) {
        this.buffer = buffer.toCharArray();
        this.pointer = 0;
    }

    // check if there's a next token (note: this is slow!)
    public boolean hasNextToken() {
        int currentPointer = pointer;
        TokenType type = nextToken().getTokenType();
        pointer = currentPointer;
        return type != TokenType.EOF;
    }

    // gets the next token
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

    public char peekCharacter() {
        if (pointer < buffer.length)
            return buffer[pointer];
        return Symbol.EOF.toString().charAt(0);
    }

    public Symbol peekSymbol() {
        if (pointer < buffer.length)
            return Symbol.fromCharacter(buffer[pointer]);
        return Symbol.EOF;
    }

}
