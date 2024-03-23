public class ExpressionNode implements ProgramNode {
    private ProgramNode node;

    public ExpressionNode(ProgramNode node) {
        this.node = node;
    }

    @Override
    public void execute(Robot robot) {
        node.execute(robot);
    }

    public String toString() {
        return node.toString();
    }
}