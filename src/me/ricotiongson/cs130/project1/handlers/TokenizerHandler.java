package me.ricotiongson.cs130.project1.handlers;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import me.ricotiongson.cs130.generic.CounterMap;
import me.ricotiongson.cs130.generic.DFA;
import me.ricotiongson.cs130.project1.enums.Symbol;
import me.ricotiongson.cs130.project1.enums.TokenType;

public class TokenizerHandler {

    private int[][] dfaTable;
    private int startState;
    private Set<Integer> finalStates = new HashSet<>();
    private Set<Integer> rollbackStates = new HashSet<>();
    private Map<Symbol, Integer> transitionMap;
    private TokenType[] tokenMap;

    public int[][] getDfaTable() {
        return dfaTable;
    }

    public int getStartState() {
        return startState;
    }

    public Set<Integer> getFinalStates() {
        return finalStates;
    }

    public Set<Integer> getRollbackStates() {
        return rollbackStates;
    }

    public Map<Symbol, Integer> getTransitionMap() {
        return transitionMap;
    }

    public TokenType[] getTokenMap() {
        return tokenMap;
    }
    // create a tokenizer based
    public TokenizerHandler(DFA dfa,
                            Set<DFA.State> rollbackStateSet,
                            Map<DFA.State, TokenType> stateTokenMap) {
        CounterMap<DFA.State> stateMap = new CounterMap<>();
        CounterMap<Symbol> transitionMap = new CounterMap<>();
        this.transitionMap = transitionMap;
        this.startState = stateMap.get(dfa.getStartState()); // 0

        // initial DFS to get contents of DFA graph
        dfa.dfs((Symbol transition, DFA.State prevState, DFA.State nextState) -> {

            // get ID of state
            transitionMap.get(transition);
            int prevStateId = stateMap.get(prevState);
            int nextStateId = stateMap.get(nextState);
            if (prevState.isFinal()) finalStates.add(prevStateId);
            if (nextState.isFinal()) finalStates.add(nextStateId);
            if (rollbackStateSet.contains(prevState)) rollbackStates.add(prevStateId);
            if (rollbackStateSet.contains(nextState)) rollbackStates.add(nextStateId);

        });

        // prepare token map
        this.tokenMap = new TokenType[stateMap.size()];
        for (Map.Entry<DFA.State, Integer> entry : stateMap.entrySet()) {
            int stateId = entry.getValue();
            TokenType token = stateTokenMap.get(entry.getKey());
            tokenMap[stateId] = token;
        }

        // prepare dfa table
        this.dfaTable = new int[stateMap.size()][transitionMap.size()];
        for (int i = 0; i < dfaTable.length; ++i)
            Arrays.fill(dfaTable[i], -1);

        // dfs again to assign transitions
        dfa.dfs((Symbol transition, DFA.State prevState, DFA.State nextState) -> {
            int prevStateId = stateMap.get(prevState);
            int nextStateId = stateMap.get(nextState);
            int transitionId = transitionMap.get(transition);
            dfaTable[prevStateId][transitionId] = nextStateId;
        });
    }


}
