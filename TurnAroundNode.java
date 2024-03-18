public class TurnAroundNode implements ProgramNode {
    public TurnAroundNode() {
    }

    @Override
    public void execute(Robot robot) {
        robot.turnAround();
    }

    public String toString() {
        return "turnAround";
    }
}
