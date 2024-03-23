public class ElseIfNode implements BooleanNode {

    private BooleanNode condition;
    private BlockNode block;

    public ElseIfNode(BooleanNode condition, BlockNode block) {
        this.condition = condition;
        this.block = block;
    }

    @Override
    public boolean evaluate(Robot robot) {
        if (condition.evaluate(robot)) {
            block.execute(robot);
        }
        return condition.evaluate(robot);
    }

    public String toString() {
        return "else if (" + condition + ") " + block;
    }

}
