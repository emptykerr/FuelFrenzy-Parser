public class GetFuelNode implements IntNode {

    public GetFuelNode() {
    }

    @Override
    public int evaluate(Robot robot) {
        return robot.getFuel();
    }

    public String toString() {
        return "fuelLeft";
    }
}
