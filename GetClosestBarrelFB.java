public class GetClosestBarrelFB implements IntNode {

    public GetClosestBarrelFB() {
    }

    @Override
    public int evaluate(Robot robot) {
        return robot.getClosestBarrelFB();
    }

    public String toString() {
        return "barrelFB";
    }

}
