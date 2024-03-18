public class TurnLNode implements ProgramNode {
    public TurnLNode() {

    }

    @Override
    public void execute(Robot robot) {
        robot.turnLeft();
    }

    public String toString() {
        return "turnL";
    }

}
