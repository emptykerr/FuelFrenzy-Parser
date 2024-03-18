import java.util.ArrayList;
import java.util.List;

public class BlockNode implements ProgramNode {
    List<ProgramNode> statements = new ArrayList<ProgramNode>();

    @Override
    public void execute(Robot robot) {
        for (ProgramNode s : statements) {
            s.execute(robot);
        }
    }

    public BlockNode(List<ProgramNode> statements) {
        this.statements = statements;
    }

    public String toString() {
        String string = "";
        for (ProgramNode statement : statements) {
            string += statement.toString() + " ";
        }
        return "{" + string + "}";
    }
}
