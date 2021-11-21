import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class main {

    public static void main(String[] args) {
        question1();
    }

    public static void question1() {
        for (int i=1; i<=5; i++) {
            List<Integer> l = new ArrayList<Integer>();
            System.out.println("i: " + i);
            for (int num = (int) ((int) 1000*Math.pow(2,i)); num > 0; num--) {
                l.add(num);
            }
            printRes(l);
            Collections.shuffle(l);
            printRes(l);
        }
    }

    public static void printRes(List<Integer> l) {
        System.out.print("number of switches: " + numSwitches(l) + ", ");
        AVLTree t = new AVLTree();
        int s = 0;
        for (int k: l) {
            s+= t.insert(k, "k");
        }
        System.out.print("search cost: " + s);
        System.out.println();
    }

    public static int numSwitches(List<Integer> l) {
        int s = 0;
        for (int i=0; i<l.size(); i++) {
            for (int j=i+1; j < l.size(); j++) {
                if (l.get(i) > l.get(j)) {
                    s++;
                }
            }
        }
        return s;
    }
}
