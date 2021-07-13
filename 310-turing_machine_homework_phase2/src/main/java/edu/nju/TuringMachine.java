package edu.nju;

import org.apache.commons.lang3.StringUtils;

import java.util.*;

/**
 * @Author: pkun
 * @CreateTime: 2021-05-23 16:15
 */
public class TuringMachine {

    // 状态集合
    private final Map<String, State> Q;
    // 输入符号集
    private Set<Character> S;
    // 磁带符号集
    private Set<Character> G;
    // 初始状态
    private String q0;
    // 终止状态集
    private Set<String> F;
    // 空格符号
    private Character B;
    // 磁带数
    private Integer tapeNum;

    private final Set<TransitionFunction> delta = new HashSet<>();

    Set<Character> sets;

    ArrayList<Integer> errorLines = new ArrayList<>();

    boolean validDelta = false;

    public TuringMachine(Set<String> Q, Set<Character> S, Set<Character> G, String q, Set<String> F, char B, int tapeNum, Set<TransitionFunction> Delta) {
        this.S = S;
        this.G = G;
        this.F = F;
        this.B = B;
        this.q0 = q;
        this.Q = new HashMap<>();
        for (String state : Q) {
            State temp = new State(state);
            temp.setQ(state);
            this.Q.put(state, temp);
        }
        this.tapeNum = tapeNum;
        for (TransitionFunction t : Delta) {
            this.Q.get(t.getSourceState().getQ()).addTransitionFunction(t);
        }
    }

    /**
     * TODO
     * is done in Lab1 ~
     *
     * @param tm
     */
    public TuringMachine(String tm) {
        Q = new HashMap<>();
        S = new HashSet<>();
        G = new HashSet<>();
        F = new HashSet<>();
        sets = new HashSet<>();
        initSet(sets);

        String[] tm2 = tm.split("\n");
        ArrayList<String> lines = new ArrayList<>(Arrays.asList(tm2)); //copy the origin version
        ArrayList<String> simpleTm = simplifyString(tm);
        ArrayList<String> newtm = new ArrayList<>();
        for (String item : tm2) {
            item = item.trim();
            newtm.add(item);
        }

        // initialize the turing machine
        for (String s : newtm) {
            if (isValid(s)) {
                if (sets.contains(s.charAt(1)) && s.charAt(0) == '#') {
                    deleteSet(sets, s);
                }
                if (validDelta) deleteSet(sets, "#D");
            } else {
                for (String line : lines) {
                    if (line.substring(0,4).equals(s.substring(0,4)))
                        errorLines.add(lines.indexOf(line) + 1);
                }
            }
        }

        if (!errorLines.isEmpty()){
            for (int num : errorLines)
                System.err.println("Error: " + num);
        }
        else if (!sets.isEmpty()) {
            for (char ch : sets) {
                if (ch == 'q')
                    System.err.println("Error: lack " + ch +'0');
                else {
                    System.err.println("Error: lack " + ch);
                }
            }
        }

        if (errorLines.isEmpty() && sets.isEmpty()){
            for (String line : simpleTm) {
                String[] strings;
                if (line.charAt(1) == 'Q'){
                    strings = StringUtils.strip(line.substring(5), "{}").split(",");
                    for (String s : strings) Q.put(s.trim(), new State(s.trim()));
                } else if (line.charAt(1) == 'S') {
                    strings = StringUtils.strip(line.substring(5), "{}").split(",");
                    for (String item : strings) S.add(item.charAt(0));
                } else if (line.charAt(1) == 'G') {
                    strings = StringUtils.strip(line.substring(5), "{}").split(",");
                    for (String item : strings) G.add(item.charAt(0));
                } else if (line.charAt(1) == 'F') {
                    strings = StringUtils.strip(line.substring(5), "{}").split(",");
                    F.addAll(Arrays.asList(strings));
                } else if (line.charAt(1) == 'D') {
                    strings = line.split(" ");
                    TransitionFunction newTF = new TransitionFunction(line.substring(3),Q);
                    for (TransitionFunction tf : delta) {
                        if (newTF.equals(tf)) System.err.println("Error: 9");
                    }
                    delta.add(newTF);
                    Q.get(strings[1]).addTransitionFunction(newTF);
                } else if (line.charAt(1) == 'q') {
                    q0 = line.substring(6);
                } else if (line.charAt(1) == 'N') {
                    tapeNum = Integer.valueOf(line.substring(5));
                } else if (line.charAt(1) == 'B') {
                    B = line.charAt(5);
                }
            }
        }
        // check errors in lab2
        if (!Utils.isSubSet(F, Q.keySet())) {
            System.err.println("Error: 3");
        }
        if (S.contains(B)) {
            System.err.println("Error: 4");
        }
        if (!G.contains(B)) {
            System.err.println("Error: 5");
        }
        if (!Utils.isSubSet(S, G)) {
            System.err.println("Error: 6");
        }

        for (TransitionFunction tf : delta) {
            if (tf.getDestinationState() == null) {
//                if (!Q.containsKey(tf.getSourceState().getQ()))
                    System.err.println("Error: 7");
                if (!Utils.isSubSet(Utils.stringToCharSet(tf.getInput()),G))
                    System.err.println("Error: 8");
                if (!Utils.isSubSet(Utils.stringToCharSet(tf.getOutput()),G))
                    System.err.println("Error: 8");
            } else {
                if (!Q.containsKey(tf.getSourceState().getQ())
                        || !Q.containsKey(tf.getDestinationState().getQ()))
                    System.err.println("Error: 7");
                if (!Utils.isSubSet(Utils.stringToCharSet(tf.getInput()),G))
                    System.err.println("Error: 8");
                if (!Utils.isSubSet(Utils.stringToCharSet(tf.getOutput()),G))
                    System.err.println("Error: 8");
            }

        }
    }

    public State getInitState() {
        return Q.get(q0);
    }

    /**
     * TODO
     * 停止的两个条件 1. 到了终止态 2. 无路可走，halts
     *
     * @param q Z
     * @return
     */
    public boolean isStop(State q, String Z) {
        if (F.contains(q.getQ())) return true;
        if (!q.getMap().containsKey(Z)) return true;
        return false;
    }

    public boolean checkTape(Set<Character> tape) {
        for (Character ch : tape) {
            if (!S.contains(ch) && ch != B) return false;
        }
        return true;
    }

    public boolean checkTapeNum(int tapeNum) {
        return tapeNum == this.tapeNum;
    }

    public Character getB() {
        return B;
    }

    public Set<String> getF() {
        return F;
    }

    public Set<TransitionFunction> getDelta(){
        return delta;
    }

    /**
     * TODO
     * 检查迁移函数是否符合要求
     * @param s
     * @param lineno
     */
    private boolean resolverTransitionFunction(String s, int lineno) {
        String[] lines = Utils.sProcess(s);
        String[] tf = lines[lineno-1].split(" ");

        if (!Q.containsKey(tf[1]) || !Q.containsKey(tf[5])) {
            System.err.println("Error: 7");
            return false;
        }
        if (!G.contains(tf[2].charAt(0)) || !G.contains(tf[3].charAt(0))) {
            System.err.println("Error: 8");
            return false;
        }
        return true;
    }


    /**
     * TODO
     * is done in lab1 ~
     *
     * @return
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        // Q
        sb.append(Utils.SetToString("Q",Q.keySet()));
        // S
        sb.append(Utils.SetToString("S",S));
        // G
        sb.append(Utils.SetToString("G",G));
        // F
        sb.append(Utils.SetToString("F",F));
        // N
        sb.append("#N = ");
        sb.append(tapeNum);
        sb.append(System.lineSeparator());
        // q0
        sb.append("#q0 = ");
        sb.append(q0);
        sb.append(System.lineSeparator());
        // B
        sb.append("#B = ");
        sb.append(B);
        sb.append(System.lineSeparator());
        // D
        for (TransitionFunction item : delta) {
            sb.append(item.toString());
        }
        sb.deleteCharAt(sb.length()-1);
        return sb.toString();
    }


    // analyze the input String

    // simplification (delete the annotations)
    public ArrayList<String> simplifyString(String tm) {
        String[] strings = tm.split("\n");  // remove the blanks
        ArrayList<String> info = new ArrayList<>();

        for (String str : strings) {
            String newstr = str.trim();
            if (newstr.charAt(0) == '#')
                info.add(newstr);
        }

        return info;

    }

    // check error j
    public boolean isValid(String str) {
        Set<Character> sampleSet = new HashSet<>();
        initSet(sampleSet);
        if (str.charAt(0) != ';' && str.charAt(0) != '#') return false;
        if (str.charAt(0) == ';') return true;
        if (str.endsWith("!")) return false;
        if (str.charAt(1) == 'q' && str.charAt(2) == '0') return true;
        if (str.charAt(0) == '#') {
            if (str.charAt(1) == 'D') {
                if (isValidDelta(str)) {
                    validDelta = true;
                    return true;
                }
            }
            if (!sampleSet.contains(str.charAt(1)) || str.charAt(2) != ' ') return false;
            if (str.charAt(1) == 'Q' || str.charAt(1) == 'S' ||
                    str.charAt(1) == 'G' || str.charAt(1) == 'F')
            {
                boolean flag = str.charAt(5) == '{';
                return flag && str.endsWith("}");
            }
        }
        return true;
    }

    public boolean isValidDelta(String delta) {
        TransitionFunction tf = new TransitionFunction(delta.substring(3),Q);
        return tf.getInput().length() == tf.getOutput().length();
    }

    // check whether the input loses some of the sets
    public void deleteSet(Set<Character> sets, String str) {
        sets.remove(str.charAt(1));
    }

    // initialization of sets
    public void initSet(Set<Character> sets) {
        sets.add('Q');
        sets.add('S');
        sets.add('G');
        sets.add('q');
        sets.add('F');
        sets.add('B');
        sets.add('N');
        sets.add('D');
    }

}
