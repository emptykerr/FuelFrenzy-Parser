public class MoveNode implements ProgramNode {
    private IntNode value;

    public MoveNode() {
    }

    public MoveNode(IntNode value) {
        this.value = value;
    }

    @Override
    /**
     * Executes action based on number of value times
     */
    public void execute(Robot robot) {
        for (int i = 0; i < value.evaluate(robot); i++) {
            robot.move();
        }
    }

    public String toString() {
        return value == null ? "move()" : "move(" + value.toString() + ")";
    }

}
