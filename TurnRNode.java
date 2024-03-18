public class TurnRNode implements ProgramNode {
    public TurnRNode() {

    }

    @Override
    public void execute(Robot robot) {
        robot.turnRight();
    }

    public String toString() {
        return "turnR";
    }

}
