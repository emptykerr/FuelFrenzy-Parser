public class GetClosestBarrelLR implements IntNode {
    private IntNode expr;

    public GetClosestBarrelLR() {
    }

    public GetClosestBarrelLR(IntNode expr) {
        this.expr = expr;
    }

    @Override
    public int evaluate(Robot robot) {
        if (expr != null) {
            return robot.getBarrelLR(expr.evaluate(robot));
        }
        return robot.getClosestBarrelLR();
    }

    public String toString() {
        return expr == null ? "barrelLR" : "barrelLR (" + expr + ")";
    }

}
