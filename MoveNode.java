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
        if (value == null) {
            robot.move();
            return;
        }
        int stepsToMove = value.evaluate(robot);
        for (int i = 0; i < stepsToMove; i++) {
            robot.move();
        }
    }

    public String toString() {
        return value == null ? "move()" : "move(" + value.toString() + ")";
    }

}
