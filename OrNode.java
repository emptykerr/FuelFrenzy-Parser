public class OrNode implements BooleanNode {

    BooleanNode left;
    BooleanNode right;

    public OrNode(BooleanNode left, BooleanNode right) {
        this.left = left;
        this.right = right;
    }

    @Override
    public boolean evaluate(Robot robot) {
        return left.evaluate(robot) || right.evaluate(robot);
    }

    public String toString() {
        return "or";
    }

}