public class GetOpponentLR implements IntNode {

    public GetOpponentLR() {
    }

    @Override
    public int evaluate(Robot robot) {
        return robot.getOpponentLR();
    }

    public String toString() {
        return "opponentLR";
    }
}
