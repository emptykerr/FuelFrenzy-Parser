public class GetWallDistance implements IntNode {

    public GetWallDistance() {
    }

    @Override
    public int evaluate(Robot robot) {
        return robot.getDistanceToWall();
    }

    public String toString() {
        return "wallDistance";
    }

}
