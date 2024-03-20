public class NotNode implements BooleanNode {
    BooleanNode condition;

    public NotNode(BooleanNode condition) {
        this.condition = condition;
    }

    @Override
    public boolean evaluate(Robot robot) {
        return !condition.evaluate(robot);
    }

    public String toString() {
        return "not";
    }
}
