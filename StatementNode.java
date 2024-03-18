
public class StatementNode implements ProgramNode {
    ProgramNode statement;

    public StatementNode(ProgramNode statement) {
        this.statement = statement;
    }

    @Override
    public void execute(Robot robot) {
        statement.execute(robot);
    }

    public String toString() {
        return (statement instanceof ActionNode) ? (statement.toString() + " ;") : (statement.toString());
    }

}
