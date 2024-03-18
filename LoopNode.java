public class LoopNode implements ProgramNode {

    BlockNode block;

    public LoopNode(BlockNode block) {
        this.block = block;
    }

    @Override
    public void execute(Robot robot) {
        while (true) {
            block.execute(robot);
        }
    }

    public String toString() {
        return "loop" + this.block.toString();
    }
}
