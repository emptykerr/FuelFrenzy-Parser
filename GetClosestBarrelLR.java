public class GetClosestBarrelLR implements IntNode {
    public GetClosestBarrelLR() {
    }

    @Override
    public int evaluate(Robot robot) {
        return robot.getClosestBarrelLR();
    }

    public String toString() {
        return "barrelLR";
    }

}
