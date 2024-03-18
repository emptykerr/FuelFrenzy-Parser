public class IfNode implements ProgramNode {

    BooleanNode condition;
    BlockNode block;

    public IfNode(BooleanNode condition, BlockNode block) {
        this.condition = condition;
        this.block = block;
    }

    @Override
    public void execute(Robot robot) {
        if (condition.evaluate(robot)) {
            block.execute(robot);
        }
    }

    public String toString() {
        return "if";
    }

}
