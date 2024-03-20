public class OperationNode implements IntNode {

    IntNode left;
    IntNode right;
    String operation;

    public OperationNode(String operation, IntNode left, IntNode right) {
        this.left = left;
        this.right = right;
        this.operation = operation;
    }

    @Override
    public int evaluate(Robot robot) {
        switch (operation) {
            case "add":
                return left.evaluate(robot) + right.evaluate(robot);
            case "sub":
                return left.evaluate(robot) - right.evaluate(robot);
            case "mul":
                return left.evaluate(robot) * right.evaluate(robot);
            case "div":
                return left.evaluate(robot) / right.evaluate(robot);
            default:
                return 0;
        }
    }

    public String toString() {
        return operation;
    }
}
