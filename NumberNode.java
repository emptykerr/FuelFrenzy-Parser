public class NumberNode implements IntNode {
    private int number;

    public NumberNode(int number) {
        this.number = number;
    }

    @Override
    public int evaluate(Robot robot) {
        return number;
    }

    public String toString() {
        return "" + number;
    }

}
