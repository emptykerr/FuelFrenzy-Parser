import java.util.List;

public class IfNode implements ProgramNode {

    BooleanNode condition;
    BlockNode block;
    private List<ElseIfNode> elifs;
    ElseNode elseBranch;

    public IfNode(BooleanNode condition, BlockNode block) {
        this.condition = condition;
        this.block = block;
    }

    public IfNode(BooleanNode condition, BlockNode block, List<ElseIfNode> elifs, ElseNode elseBranch) {
        this.condition = condition;
        this.block = block;
        this.elifs = elifs;
        this.elseBranch = elseBranch;
    }

    @Override
    public void execute(Robot robot) {
        if (condition.evaluate(robot)) {
            block.execute(robot);
        } else {
            for (var elif : elifs) {
                if (elif.evaluate(robot))
                    return;
            }
            if (elseBranch != null) {
                elseBranch.execute(robot);
                return;
            }
        }
    }

    public String toString() {
        return elifs == null ? "if (" + condition + ") " + block
                : "if (" + condition + ") " + block + " " + elifs.toString();
    }

}
