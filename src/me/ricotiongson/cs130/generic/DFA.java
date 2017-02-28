package me.ricotiongson.cs130.generic;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Stack;

public class DFA {

    private State start = new State();
    public State getStartState() {return start;}

    public interface DFSCallback<T> {
        void onVisit(T transition, State prevState, State nextState);
    }

    public static class State {
        private HashMap<Object, State> children = new HashMap<>();
        private State() {}
        private boolean isFinalState = false;
        public State transition(Object trans, State nextState) {
            if (nextState == null)
                return transition(trans);
            children.put(trans, nextState);
            return nextState;
        }
        public State transition(Object trans) {
            State newState = new State();
            transition(trans, newState);
            return newState;
        }
        public State loop(Object trans) {
            return transition(trans, this);
        }
        public Map<Object, State> getChildren() {
            return children;
        }
        public boolean hasTransition(Object trans) {
            return children.containsKey(trans);
        }
        public State getNext(Object trans) {
            return children.get(trans);
        }
        public void setFinal(boolean yes) {
            isFinalState = yes;
        }
        public boolean isFinal() {
            return isFinalState;
        }
    }

    public <T> void dfs(DFSCallback<T> callback) {
        HashSet<State> visited = new HashSet<>();
        Stack<State> dfsStack = new Stack<>();
        State root = getStartState();
        dfsStack.push(start);
        visited.add(root);
        while (!dfsStack.empty()) {
            State current = dfsStack.pop();
            for (Map.Entry<Object, State> entry : current.getChildren().entrySet()) {
                T transition = (T) entry.getKey();
                State nextState = entry.getValue();
                callback.onVisit(transition, current, nextState);
                if (!visited.contains(nextState)) {
                    visited.add(nextState);
                    dfsStack.push(nextState);
                }
            }
        }
    }


}
