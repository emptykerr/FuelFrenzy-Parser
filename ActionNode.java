public class ActionNode implements ProgramNode {
    private ProgramNode instruction;

    public ActionNode(ProgramNode instruction) {
        this.instruction = instruction;
    }

    @Override
    // Executes the corresponding action node for the robot
    public void execute(Robot robot) {
        instruction.execute(robot);

    }

    public String toString() {
        return instruction.toString();
    }
}
