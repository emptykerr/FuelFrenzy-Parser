public class ExpressionNode implements ProgramNode {
    private ProgramNode node;
    private SensorNode sensorNode;
    private IntNode intNode;
    private BooleanNode booleanNode;

    public ExpressionNode(ProgramNode node) {
        this.node = node;
    }

    public ExpressionNode(SensorNode sensorNode) {
        this.sensorNode = sensorNode;
    }

    public ExpressionNode(IntNode intNode) {
        this.intNode = intNode;
    }

    public ExpressionNode(BooleanNode booleanNode) {
        this.booleanNode = booleanNode;
    }

    @Override
    public void execute(Robot robot) {
        node.execute(robot);
    }

    public String toString() {
        return node.toString();
    }
}