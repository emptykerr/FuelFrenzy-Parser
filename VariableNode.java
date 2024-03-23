public class VariableNode implements IntNode {

    private String variable;

    public VariableNode(String variable) {
        this.variable = variable;
    }

    @Override
    public int evaluate(Robot robot) {
        return;
    }

    public String toString() {
        return variable;
    }

}
