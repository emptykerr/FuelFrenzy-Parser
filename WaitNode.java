public class WaitNode implements ProgramNode {
    IntNode value;

    public WaitNode() {

    }

    public WaitNode(IntNode value) {
        this.value = value;
    }

    @Override
    public void execute(Robot robot) {
        for (int i = 0; i < value.evaluate(robot); i++) {
            robot.idleWait();
        }
    }

    public String toString() {
        return value == null ? "wait()" : "wait(" + value.toString() + ")";
    }

}
