
public class ConditionNode implements BooleanNode {

    private BooleanNode condition;

    public ConditionNode(BooleanNode condition) {
        this.condition = condition;
    }

    @Override
    public boolean evaluate(Robot robot) {
        return condition.evaluate(robot);
    }

    public String toString() {
        return condition.toString();
    }
}
