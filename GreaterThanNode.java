
public class GreaterThanNode implements BooleanNode {

    private IntNode condition;
    private int value;

    public GreaterThanNode(IntNode condition, int value) {
        this.condition = condition;
        this.value = value;
    }

    @Override
    public boolean evaluate(Robot robot) {
        return condition.evaluate(robot) > value;
    }

    public String toString() {
        return "gt";
    }

}
