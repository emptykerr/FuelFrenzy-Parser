public class WaitNode implements ProgramNode {
    IntNode value;

    public WaitNode() {

    }

    public WaitNode(IntNode value) {
        this.value = value;
    }

    @Override
    public void execute(Robot robot) {

        if (value == null) {
            robot.idleWait();
            return;
        }
        int stepsToWait = value.evaluate(robot);
        for (int i = 0; i < stepsToWait; i++) {
            robot.idleWait();
        }
    }

    public String toString() {
        return value == null ? "wait()" : "wait(" + value.toString() + ")";
    }

}
