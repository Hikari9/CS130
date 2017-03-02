package project1.handlers;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import generic.CounterMap;
import generic.DFA;
import project1.enums.Symbol;
import project1.enums.TokenType;

/**
 * A handler class that transforms a DFA graph into a DFA table, ready for tokenizing.
 */
public class TokenizerHandler {

    private int[][] dfaTable; // compiled finite state machine DFA
    private int startState; // starting state of DFA
    private Set<Integer> finalStates = new HashSet<>(); // set of finite states of DFA
    private Map<Integer, Integer> rollbackStates = new HashMap<>(); // map of rollbacks
    private Map<Symbol, Integer> transitionMap; // maps symbols to their integer index
    private TokenType[] tokenMap; // maps integer states to their output tokens

    // create a tokenizer based
    public TokenizerHandler(DFA dfa,
                            Map<DFA.State, Integer> rollbackStateMap,
                            Map<DFA.State, TokenType> stateTokenMap) {

        CounterMap<DFA.State> stateMap = new CounterMap<>();
        CounterMap<Symbol> transitionMap = new CounterMap<>();

        this.transitionMap = transitionMap;
        this.startState = stateMap.get(dfa.getStartState());

        // initial dfs to get contents of DFA graph (and rollbacks)
        dfa.dfs((Symbol transition, DFA.State prevState, DFA.State nextState) -> {

            // get ID of state
            transitionMap.get(transition);
            int prevStateId = stateMap.get(prevState);
            int nextStateId = stateMap.get(nextState);
            if (prevState.isFinal()) finalStates.add(prevStateId);
            if (nextState.isFinal()) finalStates.add(nextStateId);
            if (rollbackStateMap.containsKey(prevState))
                rollbackStates.put(prevStateId, rollbackStateMap.get(prevState));
            if (rollbackStateMap.containsKey(nextState))
                rollbackStates.put(nextStateId, rollbackStateMap.get(nextState));

        });

        // prepare the token map
        this.tokenMap = new TokenType[stateMap.size()];
        for (Map.Entry<DFA.State, Integer> entry : stateMap.entrySet()) {

            int stateId = entry.getValue();

            TokenType token = stateTokenMap.get(entry.getKey());
            if (token == null)
                token = TokenType.ERROR;

            tokenMap[stateId] = token;

        }

        // prepare compiled dfa table
        this.dfaTable = new int[stateMap.size()][transitionMap.size()];
        for (int i = 0; i < dfaTable.length; ++i)
            Arrays.fill(dfaTable[i], -1);

        // dfs again to assign the correct transitions
        dfa.dfs((Symbol transition, DFA.State prevState, DFA.State nextState) -> {
            int prevStateId = stateMap.get(prevState);
            int nextStateId = stateMap.get(nextState);
            int transitionId = transitionMap.get(transition);
            dfaTable[prevStateId][transitionId] = nextStateId;
        });

    }

    /**
     * Getter for the state/transition DFA table.
     * @return a 2D array of state/transitions
     */
    public int[][] getDfaTable() {
        return dfaTable;
    }

    /**
     * Getter fot the start state of the DFA used by this handler.
     * @return integer index of the start state of DFA used by the handler
     */
    public int getStartState() {
        return startState;
    }

    /**
     * Getter for the set of final states.
     * @return a set of integer indices of the final states
     */
    public Set<Integer> getFinalStates() {
        return finalStates;
    }

    /**
     * Getter for the map of rollbacks.
     * @return the map of rollbacks
     */
    public Map<Integer, Integer> getRollbackStateMap() {
        return rollbackStates;
    }

    /**
     * Getter for the symbol to integer mapper used by the DFA table columns.
     * @return a map of symbols to integer
     */
    public Map<Symbol, Integer> getTransitionMap() {
        return transitionMap;
    }

    /**
     * Getter for the token map from integer indices to TokenType output.
     * @return the array used for mapping integers to token types
     */
    public TokenType[] getTokenMap() {
        return tokenMap;
    }

    /**
     * Pretty prints the DFA table.
     */
    public void printDfaTable() {

        int[][] dfaTable = getDfaTable();
        TokenType[] tokenMap = getTokenMap();
        Map<Symbol, Integer> transitionMap = getTransitionMap();
        Set<Integer> finalStates = getFinalStates();

        Symbol[] inverseTransitionMap = new Symbol[dfaTable[0].length];
        Arrays.fill(inverseTransitionMap, Symbol.ERROR);

        for (Map.Entry<Symbol, Integer> entry : transitionMap.entrySet()) {
            Symbol symbol = entry.getKey();
            Integer index = entry.getValue();
            inverseTransitionMap[index] = symbol;
        }

        int totalNumberOfStates = dfaTable.length;
        System.out.println("Created tokenizer: " + totalNumberOfStates + " states");
        System.out.println("DFA table:");
        System.out.printf("%10s   |", "");
        for (Symbol symbol : inverseTransitionMap) {
            System.out.printf(" %s |", symbol.name());
        }
        System.out.println();
        for (int i = 0; i < 10; ++i)
            System.out.print("-");
        System.out.printf("---+", "");
        for (Symbol symbol : inverseTransitionMap) {
            System.out.print("-");
            for (int len = symbol.name().length(); len > 0; --len)
                System.out.print("-");
            System.out.print("-|");
        }
        System.out.println();
        for (int i = 0; i < dfaTable.length; ++i) {
            if (finalStates.contains(i))
                System.out.printf("%10s", "[" + tokenMap[i] + "] ");
            else
                System.out.printf("%10s", "");
            System.out.printf("%2d |", i);
            for (int j = 0; j < dfaTable[i].length; ++j) {
                System.out.printf(" %" + inverseTransitionMap[j].name().length() + "d |", dfaTable[i][j]);
            }
            System.out.println();
        }
    }
}
