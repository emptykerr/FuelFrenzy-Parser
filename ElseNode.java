import java.util.List;

public class ElseNode implements ProgramNode {
    BlockNode block;
    private List<ElseIfNode> elifs;

    public ElseNode(BlockNode block) {
        this.block = block;
    }

    public ElseNode(BlockNode block, List<ElseIfNode> elifs) {
        this.block = block;
        this.elifs = elifs;
    }

    @Override
    public void execute(Robot robot) {
        block.execute(robot);
    }

    public String toString() {
        return "else";
    }
}
