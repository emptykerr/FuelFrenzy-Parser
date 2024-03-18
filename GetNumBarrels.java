public class GetNumBarrels implements IntNode{
    
    public GetNumBarrels() {
    }

    @Override
    public int evaluate(Robot robot) {
        return robot.numBarrels();
    }

    public String toString() {
        return "numBarrels";
    }
}
