public class GetOpponentFB implements IntNode {

    public GetOpponentFB() {
    }

    @Override
    public int evaluate(Robot robot) {
        return robot.getOpponentFB();
    }

    public String toString() {
        return "opponentFB";
    }
}
