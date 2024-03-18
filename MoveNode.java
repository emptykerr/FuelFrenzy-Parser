public class MoveNode implements ProgramNode {
    public MoveNode() {

    }

    @Override
    public void execute(Robot robot) {
        robot.move();
    }

    public String toString() {
        return "move";
    }

}
