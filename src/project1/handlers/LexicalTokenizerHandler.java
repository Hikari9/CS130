package project1.handlers;

import java.util.HashMap;
import java.util.Map;

import generic.DFA;
import generic.DFA.State;
import project1.enums.Symbol;
import project1.enums.TokenType;

import static project1.enums.Symbol.DIGIT;
import static project1.enums.Symbol.DIVIDE;
import static project1.enums.Symbol.DOUBLE_QUOTE;
import static project1.enums.Symbol.ENDLINE;
import static project1.enums.Symbol.EOF;
import static project1.enums.Symbol.ERROR;
import static project1.enums.Symbol.HASHTAG;
import static project1.enums.Symbol.LETTER_E;
import static project1.enums.Symbol.LETTER_NOT_E;
import static project1.enums.Symbol.MINUS;
import static project1.enums.Symbol.MULT;
import static project1.enums.Symbol.PERIOD;
import static project1.enums.Symbol.PLUS;
import static project1.enums.Symbol.SINGLE_QUOTE;
import static project1.enums.Symbol.WHITESPACE;

/**
 * The singleton handler class for lexically tokenizing a String. This class handles the creation
 * of the DFA graph used for the default Tokenizer.
 */
public class LexicalTokenizerHandler extends TokenizerHandler {

    private static LexicalTokenizerHandler instance = null;

    /**
     * Private constructor for creating a TokenizerHandler based on a DFA, the set of rollback
     * states, and a mapper for final states to tokens.
     * @param dfa the DFA used for tokenizing
     * @param rollbackStateMap a map of states to integers for when a final state needs to undo
     *                         a number of characters
     * @param stateTokenMap a map of states to tokens to identify when the token to output when
     *                      a final state is reached
     */
    private LexicalTokenizerHandler(DFA dfa,
                                    Map<State, Integer> rollbackStateMap,
                                    Map<State, TokenType> stateTokenMap) {
        super(dfa, rollbackStateMap, stateTokenMap);
        // System.out.println("Created lexical tokenizer handler, with " + this.getDfaTable().length + " states");
    }

    /**
     * Gets the singleton instance of this tokenizer handler. Creates the handler DFA and respective
     * state maps when first called.
     * @return the lexical tokenizer handler instance
     */
    public static LexicalTokenizerHandler getInstance() {
        if (instance != null)
            return instance;

        // this is where we create the DFA graph
        DFA dfa = new DFA();
        Map<State, Integer> rollbackStates = new HashMap<>();
        Map<State, TokenType> tokenMap = new HashMap<>();

        // create root state, skipping initial whitespace / endlines
        State root = dfa.getStartState().loop(WHITESPACE).loop(ENDLINE);

        // create a trap state for error characters
        State trap = root.transition(ERROR);
        trap.setFinal(true);
        tokenMap.put(trap, TokenType.ERROR);
        transitionOtherwise(trap, trap);

        { // NUMBER tokenizer

            // whole number (nonnegative)
            State num = root.transition(DIGIT).loop(DIGIT);

            // decimal point, with whole number
            State decimalPoint = num.transition(PERIOD);
            State decimalDigit = decimalPoint.transition(DIGIT).loop(DIGIT);

            // optional exponent from whole number
            State exponentSymbol = num.transition(LETTER_E);

            // optional exponent from decimal number
            decimalDigit.transition(LETTER_E, exponentSymbol);

            // signed exponent
            State signedExponent = exponentSymbol.transition(MINUS);
            exponentSymbol.transition(PLUS, signedExponent);

            // whole number in exponent (without sign)
            State exponentDigit = exponentSymbol.transition(DIGIT).loop(DIGIT);

            // whole number in exponent (with sign)
            signedExponent.transition(DIGIT, exponentDigit);

            // accepting state from whole number, decimal number, exponent number
            State fin = transitionOtherwise(num, null);
            transitionOtherwise(decimalDigit, fin);
            transitionOtherwise(exponentDigit, fin);

            // add final state to set of acceptable states
            fin.setFinal(true);
            rollbackStates.put(fin, 1);
            tokenMap.put(fin, TokenType.NUMBER);
            transitionOtherwise(fin, trap);

            /* USE CASE: badly formed numbers as an error with a rollback

            // add traps for badly formed numbers (with rollback)
            State trapWithRollback = transitionOtherwise(decimalPoint, null);
            transitionOtherwise(exponentSymbol, trapWithRollback);
            transitionOtherwise(signedExponent, trapWithRollback);
            trapWithRollback.setFinal(true);
            tokenMap.put(trapWithRollback, TokenType.ERROR);
            rollbackStates.put(trapWithRollback, 1);
            transitionOtherwise(trapWithRollback, trapWithRollback);

            // */

            // /* USE CASE: rollback if badly formed number

            // another final state for unfinished decimal point/exponents
            // note: this is a double rollback state
            State finDoubleRollback = transitionOtherwise(decimalPoint, null);
            transitionOtherwise(exponentSymbol, finDoubleRollback);

            finDoubleRollback.setFinal(true);
            rollbackStates.put(finDoubleRollback, 2);
            tokenMap.put(finDoubleRollback, TokenType.NUMBER);
            transitionOtherwise(finDoubleRollback, trap);

            // another final state for unfinished signed exponent
            // note: this is a triple rollback state
            State finTripleRollback = transitionOtherwise(signedExponent, null);

            finTripleRollback.setFinal(true);
            rollbackStates.put(finTripleRollback, 3);
            tokenMap.put(finTripleRollback, TokenType.NUMBER);
            transitionOtherwise(finTripleRollback, trap);

            // */

        }
        { // IDENT tokenizer

            // get state for first letter
            State letter = root.transition(LETTER_NOT_E);
            root.transition(LETTER_E, letter);

            // loop with more letters
            letter.loop(LETTER_NOT_E).loop(LETTER_E);

            // other characters end the identifier
            State fin = transitionOtherwise(letter, null);
            fin.setFinal(true);
            rollbackStates.put(fin, 1);
            tokenMap.put(fin, TokenType.IDENT);
            transitionOtherwise(fin, trap);

        }
        { // STRING tokenizer

            // start with a single quote
            State singleQuote = root.transition(SINGLE_QUOTE);

            // trap state when ending strings with endline/EOF
            State trapWithRollback = singleQuote.transition(ENDLINE, null);
            singleQuote.transition(EOF, trap);

            // end with another single quote
            State fin = singleQuote.transition(SINGLE_QUOTE);

            // accept all other characters
            transitionOtherwise(singleQuote, singleQuote);

            // start with a double quote
            State doubleQuote = root.transition(DOUBLE_QUOTE);

            // trap state when ending strings with endline/EOF
            doubleQuote.transition(ENDLINE, trapWithRollback);
            doubleQuote.transition(EOF, trap);

            // end with another double quote
            doubleQuote.transition(DOUBLE_QUOTE, fin);

            // accept all other characters
            transitionOtherwise(doubleQuote, doubleQuote);

            // mark final state
            fin.setFinal(true);
            tokenMap.put(fin, TokenType.STRING);
            transitionOtherwise(fin, trap);

            // trap with rollback for endline
            trapWithRollback.setFinal(true);
            tokenMap.put(trapWithRollback, TokenType.ERROR);
            rollbackStates.put(trapWithRollback, 1);
            transitionOtherwise(trapWithRollback, trapWithRollback);

        }
        { // MULT and EXP

            // separate transition for MULT and EXP
            State mult = root.transition(MULT);
            State exp = mult.transition(MULT);
            State multFin = transitionOtherwise(mult, null);

            /// setup final states
            multFin.setFinal(true);
            exp.setFinal(true);
            rollbackStates.put(multFin, 1);
            tokenMap.put(exp, TokenType.EXP);
            tokenMap.put(multFin, TokenType.MULT);
            transitionOtherwise(exp, trap);
            transitionOtherwise(multFin, trap);
        }
        { // DIVIDE and COMMENT
            State div = root.transition(DIVIDE);
            State comment = div.transition(DIVIDE);
            root.transition(HASHTAG, comment);
            State commentEnd = comment.transition(ENDLINE);
            comment.transition(EOF, commentEnd);
            transitionOtherwise(comment, comment);
            State divEnd = transitionOtherwise(div, null);
            divEnd.setFinal(true);
            commentEnd.setFinal(true);
            rollbackStates.put(divEnd, 1);
            rollbackStates.put(commentEnd, 1); // remove EOF and ENDLINE from comment
            tokenMap.put(divEnd, TokenType.DIVIDE);
            tokenMap.put(commentEnd, TokenType.COMMENT);
            transitionOtherwise(divEnd, trap);
            transitionOtherwise(commentEnd, trap);
        }
        { // everything  have there own single states
            for (Symbol symbol : Symbol.values()) {
                if (!root.hasTransition(symbol)) {
                    State fin = root.transition(symbol);
                    fin.setFinal(true);
                    TokenType type = null;
                    try {
                        type = TokenType.valueOf(symbol.name());
                    } catch (IllegalArgumentException e) {
                    }
                    if (type != null)
                        tokenMap.put(fin, type);
                    transitionOtherwise(fin, trap);
                }
            }
            transitionOtherwise(root, trap);
        }
        return instance = new LexicalTokenizerHandler(dfa, rollbackStates, tokenMap);
    }

    private static State transitionOtherwise(State currentState, State res) {
        for (Symbol symbol : Symbol.values())
            if (!currentState.hasTransition(symbol)) {
                if (res == null) res = currentState.transition(symbol);
                else currentState.transition(symbol, res);
            }
        return res;
    }
}
