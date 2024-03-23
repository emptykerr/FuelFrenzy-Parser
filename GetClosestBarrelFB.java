public class GetClosestBarrelFB implements IntNode {
    private IntNode expr;

    public GetClosestBarrelFB() {
    }

    public GetClosestBarrelFB(IntNode expr) {
        this.expr = expr;
    }

    @Override
    public int evaluate(Robot robot) {
        if (expr != null) {
            return robot.getBarrelFB(expr.evaluate(robot));
        }
        return robot.getClosestBarrelFB();
    }

    public String toString() {
        return expr == null ? "barrelFB" : "barrelFB (" + expr + ")";
    }

}
