public class AssignmentNode implements ProgramNode {

    private String variable;
    private IntNode expression;

    public AssignmentNode(String variable, IntNode expression) {
        this.variable = variable;
        this.expression = expression;
    }

    @Override
    public void execute(Robot robot) {
        if (!Parser.variables.containsKey(variable)) {
            Parser.variables.put(variable, 0);
        } else if (Parser.variables.containsKey(variable)) {
            Parser.variables.put(variable, expression.evaluate(robot));
        }

    }

    public String toString() {
        return variable.toString() + " = " + expression.toString();
    }

}
