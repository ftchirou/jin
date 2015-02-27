package com.github.ftchirou.yajl.lexer.fsm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class FSM {

    private int initialState;

    private ArrayList<HashMap<Character, Integer>> transitions;

    private List<Integer> finalStates;

    public FSM(int states) {
        this.transitions = new ArrayList<>(states);
        for (int i = 0; i < states; ++i) {
            this.transitions.add(new HashMap<Character, Integer>());
        }

        finalStates = new ArrayList<>();
    }

    public void addTransition(int start, char symbol, int end) {
        transitions.get(start).put(symbol, end);
    }

    public Output run(String stream) {
        String buffer = "";
        int length = stream.length();
        int cursor = 0;
        int state = initialState;

        while (cursor < length) {
            char symbol = stream.charAt(cursor);

            HashMap<Character, Integer> map = transitions.get(state);
            if (!map.containsKey(symbol)) {
                break;
            }

            state = map.get(symbol);

            buffer += symbol;

            cursor++;
        }

        if (finalStates.contains(state)) {
            return new Output(true, buffer);
        }

        return new Output(false, buffer);
    }

    public int getInitialState() {
        return initialState;
    }

    public void setInitialState(int initialState) {
        this.initialState = initialState;
    }

    public List<Integer> getFinalStates() {
        return finalStates;
    }

    public void setFinalStates(int... finalStates) {
        for (int state: finalStates) {
            this.finalStates.add(state);
        }
    }

    public static class Output {
        private boolean recognized;

        private String value;

        public Output() {

        }

        public Output(boolean recognized) {
            this.recognized = recognized;
        }

        public Output(boolean recognized, String value) {
            this.recognized = recognized;
            this.value = value;
        }

        public boolean isRecognized() {
            return recognized;
        }

        public void setRecognized(boolean recognized) {
            this.recognized = recognized;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }
}
