
public class LessThanNode implements BooleanNode {

    private IntNode left;
    private IntNode right;

    public LessThanNode(IntNode left, IntNode right) {
        this.left = left;
        this.right = right;
    }

    public boolean evaluate(Robot robot) {
        return left.evaluate(robot) < right.evaluate(robot);
    }

    public String toString() {
        return "lt";
    }

}
