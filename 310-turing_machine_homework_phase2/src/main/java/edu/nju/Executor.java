package edu.nju;

//import jdk.jshell.execution.Util;

import java.util.ArrayList;
import java.util.HashSet;

/**
 * @Author: pkun
 * @CreateTime: 2021-05-25 23:53
 */
public class Executor {

    ArrayList<Tape> tapes = new ArrayList<>();
    TuringMachine tm;
    State q;
    int steps = 0;
    boolean canRun = true;

    public Executor(TuringMachine tm, ArrayList<Tape> tapes) {
        this.tm = tm;
        q = tm.getInitState();
        loadTape(tapes);
    }

    int maxTrack = 0;

    /**
     * TODO
     * 1. 检查能否运行
     * 2. 调用tm.delta
     * 3. 更新磁带
     * 4. 返回下次能否执行
     *
     * @return
     */
    public Boolean execute() {
        if (!canRun) return false;
        StringBuilder snapshot = new StringBuilder();
        for (Tape tape : tapes) {
            snapshot.append(tape.getChar()); // get the char at head
        }
        String snap = snapshot.toString();
        // halt
//        if (tm.isStop(q, snap)) {
//            canRun = false;
//            return false;
//        }
        TransitionFunction tf = q.getDelta(snap);
        String direction = tf.getDirection();
        String newTape = tf.getOutput();
        this.updateTape(newTape);
        this.moveHeads(direction);
        q = q.getDelta(snap).getDestinationState();
        steps++;
        StringBuilder snapshot2 = new StringBuilder();
        for (Tape tape : tapes) {
            snapshot2.append(tape.getChar()); // get the char at head
        }
        String snap2 = snapshot2.toString();
        if (tm.isStop(q, snap2)) {
            canRun = false;
            return false;
        }
        return canRun;
    }

    /**
     * TODO
     * 1. 检查磁带的数量是否正确 ( checkTapeNum )
     * 2. 检查磁带上的字符是否是输入符号组的 ( checkTape )
     *
     * @param tapes
     */
    public void loadTape(ArrayList<Tape> tapes) {
        boolean flag1 = tm.checkTapeNum(tapes.size());
        boolean flag2 = true;
        if (!flag1) System.err.println("Error: 2");
        for (Tape tape : tapes) {
            this.tapes.add(tape);
            for (StringBuilder track : tape.tracks) {
                flag2 = tm.checkTape(Utils.stringToCharSet(track.toString()));
                if (!flag2) {
                    System.err.println("Error: 1");
                    break;
                }
            }
            if (!flag2) break;
        }
        if (!flag1 || !flag2) canRun = false;
    }

    /**
     * TODO
     * 获取所有磁带的快照，也就是把每个磁带上磁头指向的字符全都收集起来
     *
     * @return
     */
    private String snapShotTape() {
        StringBuilder snapshot = new StringBuilder();
        for (Tape tape : tapes) {
            snapshot.append(tape.getChar()); // get the char at head
        }
        return snapshot.toString();
    }

    /**
     * TODO
     * 按照README给出当前图灵机和磁带的快照
     *
     * @return
     */
    public String snapShot() {
        StringBuilder snapshot = new StringBuilder();
        snapshot.append("Step  : ").append(steps).append(System.lineSeparator());
        int cnt = 0;
        for (Tape tape : tapes) {
            snapshot.append("Tape").append(cnt).append(" :").append(System.lineSeparator());
            snapshot.append(tape.snapShot(cnt));
            cnt++;
        }
        snapshot.append("State : ").append(q.getQ());
        return snapshot.toString();
    }


    /**
     * TODO
     * 不断切割newTapes，传递给每个Tape的updateTape方法
     *
     * @param newTapes
     */
    private void updateTape(String newTapes) {
        char[] newTape = newTapes.toCharArray();
        int i = 0;
        for (Tape tape : tapes) {
            tape.updateTape(String.valueOf(newTape[i++]));
        }
    }

    /**
     * TODO
     * 将每个direction里的char都分配给Tape的updateHead方法
     *
     * @param direction
     */
    private void moveHeads(String direction) {
        char[] directions = direction.toCharArray();
        int i = 0;
        for (Tape tape : tapes) {
            tape.updateHead(directions[i++]);
        }
    }


}
