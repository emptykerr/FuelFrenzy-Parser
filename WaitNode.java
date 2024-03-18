public class WaitNode implements ProgramNode {
    public WaitNode() {

    }

    @Override
    public void execute(Robot robot) {
        robot.idleWait();
    }

    public String toString() {
        return "wait";
    }

}
