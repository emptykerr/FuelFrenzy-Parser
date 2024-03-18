public class SensorNode implements IntNode {

    private IntNode sensor;

    public SensorNode(IntNode sensor) {
        this.sensor = sensor;
    }

    @Override
    public int evaluate(Robot robot) {
        return sensor.evaluate(robot);
    }

    public String toString() {
        return sensor.toString();
    }
}
