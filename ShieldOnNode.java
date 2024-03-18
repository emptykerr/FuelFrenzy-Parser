public class ShieldOnNode implements ProgramNode {
    public ShieldOnNode() {
    }

    @Override
    public void execute(Robot robot) {
        robot.setShield(true);
    }

    public String toString() {
        return "shieldON";
    }

}
