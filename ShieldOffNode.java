public class ShieldOffNode implements ProgramNode {
    public ShieldOffNode() {
    }

    @Override
    public void execute(Robot robot) {
        robot.setShield(false);
    }

    public String toString() {
        return "shieldOFF";
    }

}
