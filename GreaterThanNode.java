
public class GreaterThanNode implements BooleanNode {

    private String condition;
    private int value;

    public GreaterThanNode(String condition, int value) {
        this.condition = condition;
        this.value = value;
    }

    @Override
    public boolean evaluate(Robot robot) {
        switch (condition) {
            case "fuelLeft":
                return robot.getFuel() > value;
            case "oppLR":
                return robot.getOpponentLR() > value;
            case "oppFB":
                return robot.getOpponentFB() > value;
            case "numBarrels":
                return robot.numBarrels() > value;
            case "barrelLR":
                return robot.getClosestBarrelLR() > value;
            case "barrelFB":
                return robot.getClosestBarrelFB() > value;
            case "wallDist":
                return robot.getDistanceToWall() > value;
            default:
                return false;

        }
    }

    public String toString() {
        return "gt";
    }

}
