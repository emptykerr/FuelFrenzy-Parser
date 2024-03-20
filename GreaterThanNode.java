
public class GreaterThanNode implements BooleanNode {

    private IntNode left;
    private IntNode right;

    public GreaterThanNode(IntNode left, IntNode right) {
        this.left = left;
        this.right = right;
    }

    public boolean evaluate(Robot robot) {
        return left.evaluate(robot) > right.evaluate(robot);
    }

    public String toString() {
        return "gt";
    }

}
