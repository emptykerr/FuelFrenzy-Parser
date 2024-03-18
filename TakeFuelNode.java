public class TakeFuelNode implements ProgramNode {
    public TakeFuelNode() {

    }

    @Override
    public void execute(Robot robot) {
        robot.takeFuel();
    }

    public String toString() {
        return "takeFuel";
    }

}
