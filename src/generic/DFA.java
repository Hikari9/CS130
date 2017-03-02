package generic;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Stack;

/**
 * A Discrete Finite Automaton represented by a graph, and states as nodes.
 */
public class DFA {

    /**
     * The starting state of this DFA. Created upon construction.
     */
    private State start = new State();

    /**
     * Gets the starting state of this DFA.
     * @return
     */
    public State getStartState() {
        return start;
    }

    /**
     * Perform a DFS on the DFA structure and apply a callback for every transition.
     * @param callback the function object called after every transition.
     * @param <T>
     */
    public <T> void dfs(DFSCallback<T> callback) {
        HashSet<State> visited = new HashSet<>();
        Stack<State> dfsStack = new Stack<>();
        State root = getStartState();
        dfsStack.push(start);
        visited.add(root);
        while (!dfsStack.empty()) {
            State current = dfsStack.pop();
            for (Map.Entry<Object, State> entry : current.getTransitionMap().entrySet()) {
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

    /**
     * A function object for DFS callbacks.
     * @param <T> parameter type for the transition used
     */
    public interface DFSCallback<T> {
        void onVisit(T transition, State prevState, State nextState);
    }

    /**
     * Represents a node in the DFA graph.
     */
    public static class State {

        private HashMap<Object, State> children = new HashMap<>();

        private boolean isFinalState = false;

        private State() {} // keep constructor private

        /**
         * Assigns a transition from this state to another state.
         * Creates a new State if the provided nextState is null.
         * @param trans the transition symbol
         * @param nextState the next state, can be null
         * @return the next State
         */
        public State transition(Object trans, State nextState) {
            if (nextState == null)
                return transition(trans);
            children.put(trans, nextState);
            return nextState;
        }

        /**
         * Creates and assigns a new State for transition.
         * @param trans the transition symbol
         * @return the newly created State
         */
        public State transition(Object trans) {
            State newState = new State();
            transition(trans, newState);
            return newState;
        }

        /**
         * Creates a loop for a given transition symbol
         * @param trans the transition symbol
         * @return the same state
         */
        public State loop(Object trans) {
            return transition(trans, this);
        }

        /**
         * Gets a transition map of this State.
         * @return a reference to the transition map used by this state.
         */
        public Map<Object, State> getTransitionMap() {
            return children;
        }

        /**
         * Checks whether a transition symbol exists for this State.
         * @param trans the transition symbol to query
         * @return true if transition symbol exists
         */
        public boolean hasTransition(Object trans) {
            return children.containsKey(trans);
        }

        /**
         * Removes a transition symbol if it exists in the transition map.
         * @param trans the transition symbol to remove
         */
        public void removeTransition(Object trans) {
            children.remove(trans);
        }

        /**
         * Queries the next State given a transition symbol.
         * @param trans the transition symbol to use
         * @return the next State after applying the transition symbol
         */
        public State getNext(Object trans) {
            return children.get(trans);
        }

        /**
         * Checks if this State is a final state.
         * @return true if this State is a final state
         */
        public boolean isFinal() {
            return isFinalState;
        }

        /**
         * Sets the finality of this State.
         * @param yes boolean for finality
         */
        public void setFinal(boolean yes) {
            isFinalState = yes;
        }
    }


}
