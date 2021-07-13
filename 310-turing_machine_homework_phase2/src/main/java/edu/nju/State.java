package edu.nju;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;


/**
 * @Author: pkun
 * @CreateTime: 2021-05-30 21:08
 */
public class State {

    private String q;
    private final Map<String, TransitionFunction> delta;  // String: the input char  of tf --> current char

    public State(String q) {
        this.q = q;
        delta = new HashMap<>();
    }

    public String getQ() {
        return q;
    }

    public void setQ(String q) {
        this.q = q;
    }

    public Collection<TransitionFunction> getDeltas() {
        return delta.values();
    }

    public Map<String, TransitionFunction> getMap() {
        return delta;
    }

    public boolean containDelta(TransitionFunction transitionFunction) {
        return delta.containsValue(transitionFunction);
    }

    public TransitionFunction getDelta(String z) {
        return delta.get(z);
    }

    public void addTransitionFunction(TransitionFunction tf) {
        this.delta.put(tf.getInput(), tf);
    }



}
