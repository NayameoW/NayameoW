package edu.nju;

import java.util.ArrayList;
import java.util.Collections;

/**
 * @Author: pkun
 * @CreateTime: 2021-05-23 19:37
 */
public class Tape {

    ArrayList<StringBuilder> tracks;
    private final char B;
    private int head;

    public Tape(ArrayList<StringBuilder> tracks, int head, char B) {
        this.tracks = tracks;
        this.head = head;
        this.B = B;
    }

    public String snapShot(int num) {
        StringBuilder sb = new StringBuilder();
        for (StringBuilder track : tracks) {
            int start=0,end=0;
            sb.append("Index").append(num).append(":");
            for (int i = 0; i < track.length(); i++) {
                if (track.toString().charAt(i) != B) {
                    start = i;
                    break;
                }
            }
            for (int i = track.length()-1; i >= 0; i--) {
                if (head == track.length()-1) {
                    end = head;
                    break;
                }
                if (track.toString().charAt(i) != B) {
                    end = i;
                    break;
                }
            }
            start = (start==0 && track.toString().charAt(0) == B) ? head : Math.min(start,head);
            end = Math.max(end, head);
            //end = Math.max(start, end);
            for (int i = start; i <= end; i++) {
                sb.append(" ").append(i);
            }
            sb.append(System.lineSeparator());
            sb.append("Track0:");
            for (int i = start; i <= end; i++) {
                sb.append(" ");
                if (i > 10) sb.append(" ");
                sb.append(track.toString().charAt(i));
            }
            sb.append(System.lineSeparator());
//            for (int i = 0; i < track.length(); i++) {
//                if (track.toString().charAt(i) == B) {
//                    if (i >= head) sb.append(" ").append(i);
//                }
//                    sb.append(" ").append(i);
//            }
//            sb.append(System.lineSeparator());
//            sb.append("Track0:");
//            for (int i = 0; i < track.length(); i++) {
//                if (track.toString().charAt(i) != B || i >= head)
//                    sb.append(" ").append(track.toString().charAt(i));
//            }
//            sb.append(System.lineSeparator());
            sb.append("Head").append(num).append(" : ").append(head);

        }
        sb.append(System.lineSeparator());
        return sb.toString();
    }

    public void updateHead(char c) {
        if (c == 'l') head--;
        if (c == 'r') head++;
        //expanding
        if (head < 0) {
            for (StringBuilder track : tracks) {
                track.insert(0,"_");
            }
            head = 0;
        }
        int min = 100;
        for (StringBuilder track : tracks) {
            min = Math.min(min, track.length());
        }
        if (head > min-1) {
            for (StringBuilder track : tracks) {
                track.append("_");
            }
        }
    }


    public void updateTape(String newTape) {
        int i = 0;
        for (StringBuilder track : tracks) {
            track.replace(head,head+1,newTape);
        }
    }

    public int getHead() {
        return head;
    }

    public int trackSize() {
        return tracks.size();
    }

    public char getB() {
        return B;
    }

    public char getChar() {
        for (StringBuilder track : tracks ){
            return track.toString().charAt(head);
        }
        return B;
    }

}
