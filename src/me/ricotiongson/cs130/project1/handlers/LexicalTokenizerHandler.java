package me.ricotiongson.cs130.project1.handlers;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import me.ricotiongson.cs130.project1.enums.Symbol;
import me.ricotiongson.cs130.project1.enums.TokenType;
import me.ricotiongson.cs130.generic.DFA;

import static me.ricotiongson.cs130.project1.enums.Symbol.DIGIT;
import static me.ricotiongson.cs130.project1.enums.Symbol.DOUBLE_QUOTE;
import static me.ricotiongson.cs130.project1.enums.Symbol.LETTER_E;
import static me.ricotiongson.cs130.project1.enums.Symbol.LETTER_NOT_E;
import static me.ricotiongson.cs130.project1.enums.Symbol.MINUS;
import static me.ricotiongson.cs130.project1.enums.Symbol.MULT;
import static me.ricotiongson.cs130.project1.enums.Symbol.PERIOD;
import static me.ricotiongson.cs130.project1.enums.Symbol.SINGLE_QUOTE;
import static me.ricotiongson.cs130.project1.enums.Symbol.WHITESPACE;

// Singleton class for tokenizing
public class LexicalTokenizerHandler extends TokenizerHandler {

    private static LexicalTokenizerHandler instance = null;

    public static LexicalTokenizerHandler getInstance() {
        if (instance != null)
            return instance;

        // create handlers
        DFA dfa = new DFA();
        Set<DFA.State> rollbackStates = new HashSet<>();
        Map<DFA.State, TokenType> tokenMap = new HashMap<>();
        DFA.State root = dfa.getStartState().loop(WHITESPACE);
        { // NUMBER tokenizer
            DFA.State num = root.transition(DIGIT).loop(DIGIT);
            DFA.State decimal = num.transition(PERIOD);
            DFA.State decimalDigit = decimal.transition(DIGIT).loop(DIGIT);
            DFA.State exponent = num.transition(LETTER_E);
            decimalDigit.transition(LETTER_E, exponent);
            DFA.State negExponent = exponent.transition(MINUS);
            DFA.State exponentDigit = exponent.transition(DIGIT).loop(DIGIT);
            negExponent.transition(DIGIT, exponentDigit);
            DFA.State fin = transitionOtherwise(num, null);
            transitionOtherwise(exponentDigit, fin);
            transitionOtherwise(decimalDigit, fin);
            fin.setFinal(true);
            rollbackStates.add(fin);
            tokenMap.put(fin, TokenType.NUMBER);
        }{ // IDENT tokenizer
            DFA.State letter = root.transition(LETTER_NOT_E);
            root.transition(LETTER_E, letter);
            letter.loop(LETTER_NOT_E).loop(LETTER_E);
            DFA.State fin = transitionOtherwise(letter, null);
            fin.setFinal(true);
            rollbackStates.add(fin);
            tokenMap.put(fin, TokenType.IDENT);
        }{ // STRING tokenizer
            DFA.State singleQuote = root.transition(SINGLE_QUOTE);
            DFA.State fin = singleQuote.transition(SINGLE_QUOTE);
            transitionOtherwise(singleQuote, singleQuote); // loop for every other symbol
            DFA.State doubleQuote = root.transition(DOUBLE_QUOTE);
            doubleQuote.transition(DOUBLE_QUOTE, fin);
            transitionOtherwise(doubleQuote, doubleQuote);
            fin.setFinal(true);
            tokenMap.put(fin, TokenType.STRING);
        }{ // MULT and EXP
            DFA.State mult = root.transition(MULT);
            DFA.State exp = mult.transition(MULT);
            DFA.State multFin = transitionOtherwise(mult, null);
            multFin.setFinal(true);
            exp.setFinal(true);
            rollbackStates.add(multFin);
            tokenMap.put(exp, TokenType.EXP);
            tokenMap.put(multFin, TokenType.MULT);
        }{ // everything  have there own single states
            for (Symbol symbol : Symbol.values()) {
                if (!root.hasTransition(symbol)) {
                    DFA.State fin = root.transition(symbol);
                    fin.setFinal(true);
                    tokenMap.put(fin, TokenType.valueOf(symbol.name()));
                }
            }
        }
        return instance = new LexicalTokenizerHandler(dfa, rollbackStates, tokenMap);
    }

    private LexicalTokenizerHandler(DFA dfa, Set<DFA.State> rollbackStateSet, Map<DFA.State, TokenType> stateTokenMap) {
        super(dfa, rollbackStateSet, stateTokenMap);
    }

    private static DFA.State transitionOtherwise(DFA.State currentState, DFA.State res) {
        for (Symbol symbol : Symbol.values())
            if (!currentState.hasTransition(symbol)) {
                if (res == null) res = currentState.transition(symbol);
                else currentState.transition(symbol, res);
            }
        return res;
    }
}
