public class WhileNode implements ProgramNode {

    private BooleanNode condition;
    private BlockNode block;

    public WhileNode(BooleanNode condition, BlockNode block) {
        this.condition = condition;
        this.block = block;
    }

    @Override
    public void execute(Robot robot) {
        while (condition.evaluate(robot)) {
            block.execute(robot);
        }
    }

    public String toString() {
        return "while (" + condition.toString() + ") " + block.toString();
    }

}
