public class AndNode implements BooleanNode {

    BooleanNode left;
    BooleanNode right;

    public AndNode(BooleanNode left, BooleanNode right) {
        this.left = left;
        this.right = right;
    }

    @Override
    public boolean evaluate(Robot robot) {
        return left.evaluate(robot) && right.evaluate(robot);
    }

    public String toString() {
        return "and";
    }

}
