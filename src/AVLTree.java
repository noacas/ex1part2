import java.io.PrintStream;

/**
 *
 * AVLTree
 *
 * An implementation of aמ AVL Tree with
 * distinct integer keys and info.
 *
 */

public class AVLTree {

    private IAVLNode root;
    private IAVLNode max;
    private static IAVLNode virtualNode = new AVLNode();

    public AVLTree() {
        root = virtualNode;
        max = virtualNode;
    }

    /**
     * public boolean empty()
     * <p>
     * Returns true if and only if the tree is empty.
     * complexity: O(1)
     */
    public boolean empty() {
        return !root.isRealNode(); // to be replaced by student code
    }

    /**
     * public String search(int k)
     * <p>
     * Returns the info of an item with key k if it exists in the tree.
     * otherwise, returns null.
     * complexity: O(log n)
     */
    public String search(int k) {
        return searchInner(root, k);
    }

    /**
     * private static String searchInner(IAVLNode x, int k)
     * <p>
     * Returns the info of an item with key k if it exists in the subtree
     * that x is its root.
     * otherwise, returns null.
     * this is a recursive function
     * complexity: O(log n)
     */
    private static String searchInner(IAVLNode x, int k) {
        if ((x == null) || (!x.isRealNode())) {
            return null;
        }
        if (x.getKey() == k) {
            return x.getValue();
        }
        if (k < x.getKey()) {
            return searchInner(x.getLeft(), k);
        }
        return searchInner(x.getRight(), k);
    }

    /**
     * public int insert(int k, String i)
     * <p>
     * Inserts an item with key k and info i to the AVL tree.
     * The tree must remain valid, i.e. keep its invariants.
     * Returns the number of re-balancing operations, or 0 if no re-balancing operations were necessary.
     * A promotion/rotation counts as one re-balance operation, double-rotation is counted as 2.
     * Returns -1 if an item with key k already exists in the tree.
     * Complexity: O(log n)
     */
    public int insert(int k, String i) {
        AVLNode node = new AVLNode(k, i);
        node.setLeft(virtualNode);
        node.setRight(virtualNode);
        node.setHeight(0);
        if (empty()) {
            root = node;
            max = node;
            return 0;
        }
        int res = treePositionByFingerSearch(k);
        IAVLNode parent = treePosition(k);
        if (max.getKey() < k) {
            max = node;
        }
        node.setParent(parent);
        if (node.getKey() == parent.getKey())
            return -1;
        else if (node.getKey() < parent.getKey())
            parent.setLeft(node);
        else
            parent.setRight(node);
        if (parent.getHeight() != 0)
            // if parent is not a leaf then no rebalance is needed
            //return 0;
            return res;
        parent.setHeight(1);
        //return 1 + rebalanceAfterInsert(parent.getParent(), parent);
        rebalanceAfterInsert(parent.getParent(), parent);
        return res;
    }

    /**
     * private int rebalanceAfterInsert(IAVLNode node, IAVLNode son)
     * <p>
     * Recursively rebalance the tree and returns the number of re-balancing operations done
     * input are node and its son that their rank difference might be illegal
     * Complexity: O(log n)
     */
    private int rebalanceAfterInsert(IAVLNode node, IAVLNode son) {
        if ((node == null) || (!node.isRealNode())) {
            return 0;
        }
        int diffRight = getRightHeightDiff(node);
        int diffLeft = getLeftHeightDiff(node);
        if (isBalanced(diffRight, diffLeft)) {
            return 0;
        }
        // case 1:
        if (diffRight + diffLeft == 1) {
            promote(node);
            return 1 + rebalanceAfterInsert(node.getParent(), node);
        }
        int sonDiffRight = getRightHeightDiff(son);
        int sonDiffLeft = getLeftHeightDiff(son);
        // case 2:
        if ((diffLeft == 0) && (sonDiffLeft == 1)) {
            rotateRight(son, node);
            demote(node);
            return 2;
        }
        else if ((diffRight == 0) && (sonDiffRight == 1)) {
            rotateLeft(son, node);
            demote(node);
            return 2;
        }
        //case 3:
        else {
            if (diffRight == 0) {
                doubleRotateRightLeft(son, node);
            } else {
                doubleRotateLeftRight(son, node);
            }
            demote(node);
            demote(son);
            promote(node.getParent());
            return 5;
        }
    }

    /** private int getLeftHeightDiff(IAVLNode node)
     * <p>
     * return the height difference between node and its left son
     * Complexity: O(1)
     */
    private int getLeftHeightDiff(IAVLNode node) {
        return node.getHeight() - node.getLeft().getHeight();
    }

    /** private int getRightHeightDiff(IAVLNode node)
     * <p>
     * return the height difference between node and its right son
     * Complexity: O(1)
     */
    private int getRightHeightDiff(IAVLNode node) {
        return node.getHeight() - node.getRight().getHeight();
    }

    /** private boolean isBalanced(int diffRight, int diffLeft)
     * <p>
     * check if the node has legal height difference with its sons
     * Complexity O(1)
     */
    private boolean isBalanced(int diffRight, int diffLeft) {
        return ((diffRight == 1) && (diffLeft == 1)) || ((diffRight == 1) && (diffLeft == 2)) || ((diffRight == 2) && (diffLeft == 1));
    }

    /** private void promote(IAVLNode node)
     * <p>
     * promote node's rank(=height) by 1
     * Complexity: O(1)
     */
    private void promote(IAVLNode node) {
        node.setHeight(node.getHeight() + 1);
    }

    /** private void demote(IAVLNode node)
     * <p>
     * demote node's rank(=height) by 1
     * Complexity: O(1)
     */
    private void demote(IAVLNode node) {
        node.setHeight(node.getHeight() - 1);
    }

    /** private void demote(IAVLNode node)
     * <p>
     * demote node's rank(=height) by 2
     * Complexity: O(1)
     */
    private void doubleDemote(IAVLNode node) {
        node.setHeight(node.getHeight() - 2);
    }

    /**
     * private void rotateRight(IAVLNode son, IAVLNode parent)
     * <p>
     * perform a right rotation on son and parent nodes
     * Complexity: O(1)
     */
    private void rotateRight(IAVLNode son, IAVLNode parent) {
        parent.setLeft(son.getRight());
        parent.getLeft().setParent(parent);
        son.setRight(parent);
        updateParentsAfterRotation(son, parent);
    }

    /**
     * private void rotateLeft(IAVLNode son, IAVLNode parent)
     * <p>
     * perform a left rotation on son and parent nodes
     * Complexity: O(1)
     */
    private void rotateLeft(IAVLNode son, IAVLNode parent) {
        parent.setRight(son.getLeft());
        parent.getRight().setParent(parent);
        son.setLeft(parent);
        updateParentsAfterRotation(son, parent);
    }

    /**
     * private void updateParentsAfterRotation(IAVLNode son, IAVLNode parent)
     * <p>
     * Updates the parent fields for nodes after a rotation
     * Complexity: O(1)
     */
    private void updateParentsAfterRotation(IAVLNode son, IAVLNode parent) {
        son.setParent(parent.getParent());
        parent.setParent(son);
        if (son.getParent() != null) {
            if (son.getParent().getRight() == parent) {
                son.getParent().setRight(son);
            } else {
                son.getParent().setLeft(son);
            }
        }
        if (root == parent) {
            root = son;
        }
    }

    /**
     * private void doubleRotateLeftRight(IAVLNode son, IAVLNode parent)
     * <p>
     * perform a double rotation (left then right) on son and parent nodes
     * Complexity: O(1)
     */
    private void doubleRotateLeftRight(IAVLNode son, IAVLNode parent) {
        rotateLeft(son.getRight(), son);
        rotateRight(parent.getLeft(), parent);
    }

    /**
     * private void doubleRotateLeftRight(IAVLNode son, IAVLNode parent)
     * <p>
     * perform a double rotation (right then left) on son and parent nodes
     * Complexity: O(1)
     */
    private void doubleRotateRightLeft(IAVLNode son, IAVLNode parent) {
        rotateRight(son.getLeft(), son);
        rotateLeft(parent.getRight(), parent);
    }

    /**
     * precondition: !empty()
     * return the IAVLNode with key x if exists
     * else returns the Node that should be its parent
     * Complexity: O(1)
     */
    private IAVLNode treePosition(int k) {
        IAVLNode x = root;
        IAVLNode y = null;
        while (x.isRealNode()) {
            y = x;
            if (k == x.getKey()) {
                return x;
            }
            else if (k < x.getKey()) {
                x = x.getLeft();
            }
            else {
                x = x.getRight();
            }
        }
        return y;
    }

    private int treePositionByFingerSearch(int k) {
        IAVLNode x = max;
        if (k > x.getKey())
            return 0;
        int res = 0;
        while ((x.getParent() != null) && (x.getKey() > k) && (x.getParent().getKey() > k)) {
            x = x.getParent();
            res++;
        }
        while (x.isRealNode()) {
            if (k == x.getKey()) {
                return 0;
            }
            else if (k < x.getKey()) {
                x = x.getLeft();
            }
            else {
                x = x.getRight();
            }
            res++;
        }
        return res;
    }

    /**
     * public int delete(int k)
     *
     * Deletes an item with key k from the binary tree, if it is there.
     * The tree must remain valid, i.e. keep its invariants.
     * Returns the number of re-balancing operations, or 0 if no re-balancing operations were necessary.
     * A promotion/rotation counts as one re-balance operation, double-rotation is counted as 2.
     * Returns -1 if an item with key k was not found in the tree.
     * Complexity: O(log n)
     */
    public int delete(int k)
    {
        if (empty()) {
            return -1;
        }
        IAVLNode x = treePosition(k);
        if (x.getKey() != k) {
            return -1;
        }
        // is x is a leaf
        if (x.getHeight() == 0 ) {
            if (x.getParent() == null) {
                root = virtualNode;
            }
            else if (x.getParent().getRight() == x) {
                x.getParent().setRight(virtualNode);
            }
            else {
                x.getParent().setLeft(virtualNode);
            }
        }
        // is x has two sons
        else if (x.getLeft().isRealNode() && (x.getRight().isRealNode())) {
            // find x's successor
            IAVLNode suc = successor(x);
            // remove successor from tree, it is unary
            deleteUnaryNode(suc);
            // replace x with its successor
            suc.setRight(x.getRight());
            suc.setLeft(x.getLeft());
            suc.setParent(x.getParent());
            suc.setHeight(x.getHeight());
        }
        // if x is unary
        else {
            deleteUnaryNode(x);
        }
        return rebalanceAfterDelete(x.getParent());
    }

    /**
     * private int rebalanceAfterDelete(IAVLNode node)
     *
     * Recursively perform rebalancing operations until the tree is balanced
     * Returns the number of re-balancing operations done.
     * the input node x is the node that might have
     * illegal height difference with its sons
     * Complexity: O(log n)
     */
    private int rebalanceAfterDelete(IAVLNode node) {
        if ((node == null) || (!node.isRealNode())) {
            return 0;
        }
        int diffRight = getRightHeightDiff(node);
        int diffLeft = getLeftHeightDiff(node);
        if (isBalanced(diffRight, diffLeft)) {
            return 0;
        }
        // case 1
        if ((diffLeft == 2) && (diffRight == 2)) {
            demote(node);
            return 1 + rebalanceAfterDelete(node.getParent());
        }
        IAVLNode son;
        // cases 2,3,4 if the illegal height difference is on left side
        if (diffLeft == 3) {
            son = node.getRight();
            int sonDiffRight = getRightHeightDiff(son);
            int sonDiffLeft = getLeftHeightDiff(son);
            // case 2
            if ((sonDiffLeft == 1) && (sonDiffRight == 1)) {
                rotateLeft(son, node);
                demote(node);
                promote(son);
                return 3;
            }
            // case 3
            else if (sonDiffLeft == 2) {
                rotateLeft(son, node);
                doubleDemote(node);
                return 2 + rebalanceAfterDelete(node.getParent().getParent());
            }
            // case 4
            else {
                doubleRotateRightLeft(son, node);
                doubleDemote(node);
                promote(node.getParent());
                demote(son);
                return 5 + rebalanceAfterDelete(node.getParent().getParent());
            }
        }
        // cases 2,3,4 if the illegal height difference is on right side
        else {
            son = node.getLeft();
            int sonDiffRight = getRightHeightDiff(son);
            int sonDiffLeft = getLeftHeightDiff(son);
            // case 2
            if ((sonDiffLeft == 1) && (sonDiffRight == 1)) {
                rotateRight(son, node);
                demote(node);
                promote(son);
                return 3;
            }
            // case 3
            else if (sonDiffRight == 2) {
                rotateRight(son, node);
                demote(node);
                demote(node);
                return 3 + rebalanceAfterDelete(node.getParent().getParent());
            }
            // case 4
            else {
                doubleRotateLeftRight(son, node);
                doubleDemote(node);
                promote(node.getParent());
                demote(son);
                return 5 + rebalanceAfterDelete(node.getParent().getParent());
            }
        }

    }

    /**
     * private void deleteUnaryNode(IAVLNode x)
     * Deletes node x from tree
     * precondition: x is unary node
     * Complexity: O(1)
     */
    private void deleteUnaryNode(IAVLNode x) {
        if (x.getRight().isRealNode()) {
            if (x.getParent() == null) {
                root = x.getRight();
                root.setParent(null);
            }
            else {
                x.getRight().setParent(x.getParent());
                if (x.getParent().getRight() == x) {
                    x.getParent().setRight(x.getRight());
                }
                else {
                    x.getParent().setLeft(x.getRight());
                }
            }
        }
        else {
            if (x.getParent() == null) {
                root = x.getLeft();
                root.setParent(null);
            }
            else {
                x.getLeft().setParent(x.getParent());
                if (x.getParent().getRight() == x) {
                    x.getParent().setRight(x.getLeft());
                }
                else {
                    x.getParent().setLeft(x.getLeft());
                }
            }
        }
    }

    /**
     * private IAVLNode successor(IAVLNode node)
     * Returns the successor of node is tree
     * If node is the maximum in tree, returns null
     * Complexity: O(log n)
     */
    private IAVLNode successor(IAVLNode node) {
        if (node.getRight().isRealNode()) {
            return getMinNode(node.getRight());
        }
        IAVLNode parent = node.getParent();
        while ((parent != null) && (node == parent.getRight())) {
            node = parent;
            parent = node.getParent();
        }
        return parent;
    }

    /**
     * private IAVLNode getMinNode(IAVLNode node)
     * <p>
     * Return the min node in mode's subtree
     * Complexity O(log n)
     */
    private IAVLNode getMinNode(IAVLNode node) {
        while (node.getLeft().isRealNode()) {
            node = node.getLeft();
        }
        return node;
    }

    /**
     * private IAVLNode getMaxnNode(IAVLNode node)
     * <p>
     * Return the max node in mode's subtree
     * Complexity O(log n)
     */
    private IAVLNode getMaxNode(IAVLNode node) {
        while (node.getRight().isRealNode()) {
            node = node.getRight();
        }
        return node;
    }

    /**
     * public String min()
     *
     * Returns the info of the item with the smallest key in the tree,
     * or null if the tree is empty.
     * Complexity: O(log n)
     */
    public String min()
    {
        if (!root.isRealNode()) {
            return null;
        }
        else {
            return getMinNode(root).getValue();
        }
    }

    /**
     * public String max()
     *
     * Returns the info of the item with the largest key in the tree,
     * or null if the tree is empty.
     * Complexity: O(log n)
     */
    public String max()
    {
        if (!root.isRealNode()) {
            return null;
        }
        else {
            return getMaxNode(root).getValue();
        }
    }

    /**
     * public int[] keysToArray()
     *
     * Returns a sorted array which contains all keys in the tree,
     * or an empty array if the tree is empty.
     * Complexity: O(n)
     */
    public int[] keysToArray()
    {

        int[] keys = new int[size()];
        keysToArrayInner(root, keys, 0);
        return keys;// to be replaced by student code
    }

    /**
     * private int keysToArrayInner(IAVLNode node, int[] keys, int i)
     *
     * Fills the keys in a sorted array from index i
     * Returns next index in array (this is a recursive function)
     * Complexity: O(n)
     */
    private int keysToArrayInner(IAVLNode node, int[] keys, int i) {
        if (node.isRealNode()) {
            i = keysToArrayInner(node.getLeft(), keys, i);
            keys[i] = node.getKey();
            return keysToArrayInner(node.getRight(), keys, i+1);
        }
        return i;
    }

    /**
     * public String[] infoToArray()
     *
     * Returns an array which contains all info in the tree,
     * sorted by their respective keys,
     * or an empty array if the tree is empty.
     * Complexity: O(n)
     */
    public String[] infoToArray()
    {
        String[] info = new String[size()];
        infoToArrayInner(root, info, 0);
        return info;// to be replaced by student code
    }

    /**
     * private int infosToArrayInner(IAVLNode node, int[] keys, int i)
     *
     * Fills the infos in a sorted array from index i
     * Returns next index in array (this is a recursive function)
     * Complexity: O(n)
     */
    private int infoToArrayInner(IAVLNode node, String[] info, int i) {
        if (node.isRealNode()) {
            i = infoToArrayInner(node.getLeft(), info, i);
            info[i] = node.getValue();
            return infoToArrayInner(node.getRight(), info, i+1);
        }
        return i;
    }

    /**
     * public int size()
     *
     * Returns the number of nodes in the tree.
     * Complexity: O(n)
     */
    public int size() {
        return sizeInner(root);
    }

    /**
     * private int sizeInner(IAVLNode node)
     *
     * Returns the number of nodes in the node's subtree.
     * Complexity: O(n)
     */
    private int sizeInner(IAVLNode node) {
        if ((node == null) || (!node.isRealNode())) {
            return 0;
        }
        return 1 + sizeInner(node.getLeft()) + sizeInner(node.getRight());
    }

    /**
     * public int getRoot()
     *
     * Returns the root AVL node, or null if the tree is empty
     * Complexity O(1);
     */
    public IAVLNode getRoot()
    {
        return root;
    }

    /**
     * public AVLTree[] split(int x)
     *
     * splits the tree into 2 trees according to the key x.
     * Returns an array [t1, t2] with two AVL trees. keys(t1) < x < keys(t2).
     *
     * precondition: search(x) != null (i.e. you can also assume that the tree is not empty)
     * postcondition: none
     * Complexity: O(log n)
     */
    public AVLTree[] split(int x)
    {
        IAVLNode nodeX = treePosition(x);
        AVLTree[] result = {new AVLTree(), new AVLTree()};
        result[0].root = nodeX.getLeft();
        result[1].root = nodeX.getRight();
        AVLTree tempTree = new AVLTree();
        nodeX = nodeX.getParent();
        IAVLNode son = nodeX;
        int changeIndex;
        while (nodeX != null) {
            if (nodeX.getRight() == son) {
                tempTree.root = nodeX.getLeft();
                changeIndex = 0;
            }
            else {
                tempTree.root = nodeX.getRight();
                changeIndex = 1;
            }
            son = nodeX;
            nodeX = nodeX.getParent();
            result[changeIndex].join(son, tempTree);
        }
        return result;
    }

    /**
     * public int join(IAVLNode x, AVLTree t)
     *
     * joins t and x with the tree.
     * Returns the complexity of the operation (|tree.rank - t.rank| + 1).
     *
     * precondition: keys(t) < x < keys() or keys(t) > x > keys(). t/tree might be empty (rank = -1).
     * postcondition: none
     * Complexity: O(log n)
     */
    public int join(IAVLNode x, AVLTree t)
    {
        if (root.isRealNode()) {
            if (t.root.isRealNode()) {
                if (x.getKey() < root.getKey()) {
                    return innerJoin(t, x, this);
                }
                else {
                    return innerJoin(this, x, t);
                }
            }
            else {
                return joinToTree(x);
            }
        }
        else {
            if (t.root.isRealNode()) {
                root = t.root;
                return joinToTree(x);
            }
            else {
                root = x;
                x.setLeft(virtualNode);
                x.setRight(virtualNode);
                x.setParent(null);
                x.setHeight(0);
                return 1;
            }
        }
    }

    /**
     *  private int innerJoin(AVLTree t1, IAVLNode x, AVLTree t2)
     *
     *  Join the trees to the node
     *  Precondition: keys(t1) < x < keys(t2), trees not empty
     *  Complexity: O(log n)
     */
    private int innerJoin(AVLTree t1, IAVLNode x, AVLTree t2) {
        // if x can be root
        if (Math.abs(t1.root.getHeight() - t2.root.getHeight()) < 2) {
            x.setParent(null);
            x.setHeight(Math.max(t1.root.getHeight(), t2.root.getHeight()) + 1);
            x.setLeft(t1.root);
            t1.root.setParent(x);
            x.setRight(t2.root);
            t2.root.setParent(x);
            root = x;
            return 1;
        }
        IAVLNode a, b, c;
        int counter = 0;
        if (t1.root.getHeight() < t2.root.getHeight()) {
            a = t1.root;
            c = t2.root;
            b = c.getLeft();
            while (b.getHeight() > a.getHeight()) {
                c = b;
                b = b.getLeft();
                counter++;
            }
            c.setLeft(x);
            x.setLeft(a);
            a.setParent(x);
            x.setRight(b);
            b.setParent(x);
            x.setParent(c);
            x.setHeight(Math.max(b.getHeight(), a.getHeight()) + 1);
            root = t2.root;
            return counter + rebalanceAfterInsert(c, x);
        }
        else {
            a = t2.root;
            c = t1.root;
            b = c.getRight();
            while (b.getHeight() > a.getHeight()) {
                c = b;
                b = b.getRight();
                counter++;
            }
            c.setRight(x);
            x.setRight(a);
            a.setParent(x);
            x.setLeft(b);
            b.setParent(x);
            x.setParent(c);
            x.setHeight(Math.max(b.getHeight(), a.getHeight()) + 1);
            root = t1.root;
            return counter + rebalanceAfterInsert(c, x);
        }
    }

    /**
     * private int joinToTree(IAVLNode x)
     * Adds node x to tree
     * Complexity: O(log n)
     */
    private int joinToTree(IAVLNode x) {
        x.setLeft(virtualNode);
        x.setRight(virtualNode);
        x.setHeight(0);
        IAVLNode parent;
        if (x.getKey() < root.getKey()) {
            parent = getMinNode(root);
            parent.setLeft(x);
        }
        else {
            parent = getMaxNode(root);
            parent.setRight(x);
        }
        x.setParent(parent);
        parent.setHeight(1);
        return 1 + rebalanceAfterInsert(parent.getParent(), x);
    }


    // functions for printing
    public void print(PrintStream os) {
        print(os, root);
    }

    private void print(PrintStream os, IAVLNode node) {
        os.print(traversePreOrder(node));
    }

    private String traversePreOrder(IAVLNode root) {

        if (root == null) {
            return "";
        }

        StringBuilder sb = new StringBuilder();
        sb.append("\n");
        sb.append(root.getKey() + " rank is " + root.getHeight());

        String pointerRight = "└──";
        String pointerLeft = (root.getRight() != null) ? "├──" : "└──";

        traverseNodes(sb, "", pointerLeft, root.getLeft(), root.getRight() != null);
        traverseNodes(sb, "", pointerRight, root.getRight(), false);

        sb.append("\n");
        return sb.toString();
    }


    private void traverseNodes(StringBuilder sb, String padding, String pointer, IAVLNode node,
                              boolean hasRightSibling) {
        if (node != null) {
            sb.append("\n");
            sb.append(padding);
            sb.append(pointer);
            sb.append(node.getKey() + " rank is " + node.getHeight());

            StringBuilder paddingBuilder = new StringBuilder(padding);
            if (hasRightSibling) {
                paddingBuilder.append("│  ");
            } else {
                paddingBuilder.append("   ");
            }

            String paddingForBoth = paddingBuilder.toString();
            String pointerRight = "└──";
            String pointerLeft = (node.getRight() != null) ? "├──" : "└──";

            traverseNodes(sb, paddingForBoth, pointerLeft, node.getLeft(), node.getRight() != null);
            traverseNodes(sb, paddingForBoth, pointerRight, node.getRight(), false);
        }
    }

    /**
     * public interface IAVLNode
     * ! Do not delete or modify this - otherwise all tests will fail !
     */
    public interface IAVLNode{
        public int getKey(); // Returns node's key (for virtual node return -1).
        public String getValue(); // Returns node's value [info], for virtual node returns null.
        public void setLeft(IAVLNode node); // Sets left child.
        public IAVLNode getLeft(); // Returns left child, if there is no left child returns null.
        public void setRight(IAVLNode node); // Sets right child.
        public IAVLNode getRight(); // Returns right child, if there is no right child return null.
        public void setParent(IAVLNode node); // Sets parent.
        public IAVLNode getParent(); // Returns the parent, if there is no parent return null.
        public boolean isRealNode(); // Returns True if this is a non-virtual AVL node.
        public void setHeight(int height); // Sets the height of the node.
        public int getHeight(); // Returns the height of the node (-1 for virtual nodes).
    }

    /**
     * public class AVLNode
     *
     * If you wish to implement classes other than AVLTree
     * (for example AVLNode), do it in this file, not in another file.
     *
     * This class can and MUST be modified (It must implement IAVLNode).
     */
    public static class AVLNode implements IAVLNode{

        private static final int virtualNodeHeight = -1;
        private int key;
        private String value;
        private IAVLNode left;
        private IAVLNode right;
        private IAVLNode parent;
        private int height;

        public AVLNode() {
            height = virtualNodeHeight;
            left = null;
            right = null;
            parent = null;
            key = -1;
            value = null;
        }

        public AVLNode(int k, String value) {
            this();
            this.key = k;
            this.value = value;
        }

        public int getKey()
        {
            return key; // to be replaced by student code
        }
        public String getValue()
        {
            return value; // to be replaced by student code
        }
        public void setLeft(IAVLNode node)
        {
            left = node; // to be replaced by student code
        }
        public IAVLNode getLeft()
        {
            return left;
        }
        public void setRight(IAVLNode node)
        {
            right = node;
        }
        public IAVLNode getRight()
        {
            return right;
        }
        public void setParent(IAVLNode node)
        {
            parent = node; // to be replaced by student code
        }
        public IAVLNode getParent()
        {
            return parent; // to be replaced by student code
        }
        public boolean isRealNode()
        {
            return (height != virtualNodeHeight); // to be replaced by student code
        }
        public void setHeight(int height)
        {
            this.height = height; // to be replaced by student code
        }
        public int getHeight()
        {
            return height; // to be replaced by student code
        }
    }

}

