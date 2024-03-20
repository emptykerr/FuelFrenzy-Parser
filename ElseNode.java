public class ElseNode implements ProgramNode {
    BlockNode block;

    public ElseNode(BlockNode block) {
        this.block = block;
    }

    @Override
    public void execute(Robot robot) {
        block.execute(robot);
    }

    public String toString() {
        return "else";
    }
}
