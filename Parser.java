import java.util.*;
import java.util.regex.*;

/**
 * See assignment handout for the grammar.
 * You need to implement the parse(..) method and all the rest of the parser.
 * There are several methods provided for you:
 * - several utility methods to help with the parsing
 * See also the TestParser class for testing your code.
 */
public class Parser {

    // Useful Patterns

    static final Pattern NUMPAT = Pattern.compile("-?[1-9][0-9]*|0");
    static final Pattern OPENPAREN = Pattern.compile("\\(");
    static final Pattern CLOSEPAREN = Pattern.compile("\\)");
    static final Pattern OPENBRACE = Pattern.compile("\\{");
    static final Pattern CLOSEBRACE = Pattern.compile("\\}");
    static final Pattern SEMICOLON = Pattern.compile(";");
    static final Pattern ACTION = Pattern.compile("move|turnL|turnR|turnAround|shieldOn|shieldOff|takeFuel|wait");
    static final Pattern WHILE = Pattern.compile("while");
    static final Pattern LOOP = Pattern.compile("loop");
    static final Pattern IF = Pattern.compile("if");
    static final Pattern MOVE = Pattern.compile("move");
    static final Pattern RELOP = Pattern.compile("lt|gt|eq");
    static final Pattern SENS = Pattern.compile("fuelLeft|oppLR|oppFB|numBarrels|barrelLR|barrelFB|wallDist");
    static final Pattern COMMA = Pattern.compile(",");
    static final Pattern OP = Pattern.compile("add|sub|mul|div");
    static final Pattern VAR = Pattern.compile("\\$[A-Za-z][A-Za-z0-9]*");

    /**
     * Variables are identifiers starting with a $, and can hold integer values.
     * Assignment statements can assign a value to a variable, and variables can be
     * used inside expressions. Variables do not need to be declared. If a variable
     * is used in an expression before a value has been assigned to it, it is
     * assumed to have the value 0. The scope of all variables is the whole program.
     * 
     * Evaluating an expression now needs to be able to access a map containing all
     * the current variables and their values, and an assignment statement needs to
     * update the value of a variable in the map. If a variable being accessed which
     * is not in the map should be added and given the value 0.
     * 
     * @param s
     * @return
     */
    static Map<String, IntNode> variables = new HashMap<String, IntNode>();

    // ----------------------------------------------------------------
    /**
     * The top of the parser, which is handed a scanner containing
     * the text of the program to parse.
     * Returns the parse tree.
     */
    ProgramNode parse(Scanner s) {
        // Set the delimiter for the scanner.
        s.useDelimiter("\\s+|(?=[{}(),;])|(?<=[{}(),;])");
        // THE PARSER GOES HERE
        // Call the parseProg method for the first grammar rule (PROG) and return the
        // node
        return parsePROG(s);
    }

    /**
     * GRAMMAR
     * PROG ::= [ STMT ]*
     * STMT ::= ACT ";" | LOOP | IF | WHILE | ASSGN ";"
     * LOOP ::= "loop" BLOCK
     * IF ::= "if" "(" COND ")" BLOCK [ "elif" "(" COND ")" BLOCK ]* [ "else" BLOCK
     * ]
     * WHILE ::= "while" "(" COND ")" BLOCK
     * ASSGN ::= VAR "=" EXPR
     * BLOCK ::= "{" STMT+ "}"
     * ACT ::= "move" [ "(" EXPR ")" ] | "turnL" | "turnR" | "turnAround" |
     * "shieldOn" | "shieldOff" | "takeFuel" | "wait" [ "(" EXPR ")" ]
     * EXPR ::= NUM | SENS | VAR | OP "(" EXPR "," EXPR ")"
     * SENS ::= "fuelLeft" | "oppLR" | "oppFB" | "numBarrels" |
     * "barrelLR" [ "(" EXPR ")" ] | "barrelFB" [ "(" EXPR ")" ] | "wallDist"
     * OP ::= "add" | "sub" | "mul" | "div"
     * COND ::= RELOP "(" EXPR "," EXPR ")" | and ( COND, COND ) | or ( COND, COND )
     * | not ( COND )
     * RELOP ::= "lt" | "gt" | "eq"
     * VAR ::= "\\$[A-Za-z][A-Za-z0-9]*"
     * NUM ::= "-?[1-9][0-9]*|0"
     * /**
     * 
     * 
     * PROG ::= [ STMT ]*
     * Parses a program node
     * 
     * @param scanner
     * @return a program node
     */
    ProgramNode parsePROG(Scanner scanner) {
        List<ProgramNode> statements = new ArrayList<ProgramNode>();
        if (!scanner.hasNext()) {
            fail("no instruction", scanner);
        }
        while (scanner.hasNext()) {
            statements.add(parseSTMT(scanner));
        }
        return new RootNode(statements);
    }

    /**
     * Parses the first node in the tree given a list of statements.
     */
    class RootNode implements ProgramNode {
        private List<ProgramNode> statements;

        public RootNode(List<ProgramNode> statements) {
            this.statements = statements;
        }

        @Override
        public void execute(Robot robot) {
            for (ProgramNode statement : statements) {
                statement.execute(robot);
            }
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            for (ProgramNode statement : statements) {
                sb.append(statement.toString()).append(" ");
            }
            return sb.toString();
        }
    }

    // ----------------------------------------------------------------
    // utility methods for the parser
    // - fail(..) reports a failure and throws exception
    // - require(..) consumes and returns the next token as long as it matches the
    // pattern
    // - requireInt(..) consumes and returns the next token as an int as long as it
    // matches the pattern
    // - checkFor(..) peeks at the next token and only consumes it if it matches the
    // pattern

    /**
     * PROG ::= [ STMT ]*
     * Parses a statement node
     * 
     * @param scanner
     * @return a statement node
     */

    private ProgramNode parseSTMT(Scanner scanner) {
        if (scanner.hasNext(ACTION)) {
            ProgramNode actionNode = parseACT(scanner);
            require(SEMICOLON, "expecting ';'", scanner);
            return actionNode;
        } else if (scanner.hasNext(LOOP)) {
            return parseLOOP(scanner);
        } else if (scanner.hasNext(IF)) {
            return parseIF(scanner);
        } else if (scanner.hasNext(WHILE)) {
            return parseWHILE(scanner);
        } else if (scanner.hasNext(VAR)) {
            return parseASSGN(scanner);
        }
        fail("Unknown statement", scanner);
        return null;
    }

    /**
     * Parses a while node
     * 
     * @param scanner
     * @return
     */
    private ProgramNode parseWHILE(Scanner scanner) {
        require(WHILE, "expecting 'while'", scanner);
        require(OPENPAREN, "expecting '('", scanner);
        BooleanNode condition = parseCOND(scanner);
        require(CLOSEPAREN, "expecting ')'", scanner);
        return new WhileNode(condition, parseBLOCK(scanner));
    }

    /**
     * Parses a condition node
     * 
     * @param scanner
     * @return
     */
    private BooleanNode parseCOND(Scanner scanner) {
        if (scanner.hasNext(RELOP)) {
            return parseRELOP(scanner);
        } else if (scanner.hasNext("and")) {
            return parseAND(scanner);
        } else if (scanner.hasNext("or")) {
            return parseOR(scanner);
        } else if (scanner.hasNext("not")) {
            return parseNOT(scanner);
        }
        fail("Unknown condition", scanner);
        return null;
    }

    /**
     * Parses an AND node
     * 
     * @param scanner
     * @return
     */
    private BooleanNode parseAND(Scanner scanner) {
        require("and", "expecting 'and'", scanner);
        require(OPENPAREN, "expecting '('", scanner);
        BooleanNode left = parseCOND(scanner);
        require(COMMA, "expecting ','", scanner);
        BooleanNode right = parseCOND(scanner);
        require(CLOSEPAREN, "expecting ')'", scanner);
        return new AndNode(left, right);
    }

    /**
     * Parses an OR node
     * 
     * @param scanner
     * @return
     */
    private BooleanNode parseOR(Scanner scanner) {
        require("or", "expecting 'or'", scanner);
        require(OPENPAREN, "expecting '('", scanner);
        BooleanNode left = parseCOND(scanner);
        require(COMMA, "expecting ','", scanner);
        BooleanNode right = parseCOND(scanner);
        require(CLOSEPAREN, "expecting ')'", scanner);
        return new OrNode(left, right);
    }

    /**
     * Parses a NOT node
     * 
     * @param scanner
     * @return
     */
    private BooleanNode parseNOT(Scanner scanner) {
        require("not", "expecting 'not'", scanner);
        require(OPENPAREN, "expecting '('", scanner);
        BooleanNode condition = parseCOND(scanner);
        require(CLOSEPAREN, "expecting ')'", scanner);
        return new NotNode(condition);
    }

    /**
     * RELOP ::= "lt" | "gt" | "eq"
     * Parses a relop node
     * 
     * @param scanner
     * @return a relop node
     */
    private BooleanNode parseRELOP(Scanner scanner) {
        String relop = require(RELOP, "expecting a relop", scanner);
        require(OPENPAREN, "expecting '('", scanner);
        IntNode left = parseEXPR(scanner);
        require(COMMA, "expecting ','", scanner);
        IntNode right = parseEXPR(scanner);
        require(CLOSEPAREN, "expecting ')'", scanner);
        return parseRELOP(scanner, relop, left, right);
    }

    /**
     * Helper method for parses a relop node
     * "lt" | "gt" | "eq
     * 
     * @param scanner
     * @param relop
     * @param left
     * @param right
     * @return
     */
    private BooleanNode parseRELOP(Scanner scanner, String relop, IntNode left, IntNode right) {
        switch (relop) {
            case "lt":
                return new LessThanNode(left, right);
            case "gt":
                return new GreaterThanNode(left, right);
            case "eq":
                return new EqualToNode(left, right);
        }
        fail("Unknown relop", scanner);
        return null;
    }

    /**
     * IF ::= "if" "(" COND ")" BLOCK [ "elif" "(" COND ")" BLOCK ]* [ "else" BLOCK]
     * 
     * @param scanner
     * @return
     */
    private ProgramNode parseIF(Scanner scanner) {
        require(IF, "expecting 'if'", scanner);
        require(OPENPAREN, "expecting '('", scanner);
        BooleanNode condition = parseCOND(scanner);
        require(CLOSEPAREN, "expecting ')'", scanner);
        BlockNode ifBlock = parseBLOCK(scanner);
        List<ElseIfNode> elifs = new ArrayList<ElseIfNode>();
        while (scanner.hasNext("elif")) {
            elifs.add(parseELIF(scanner));
        }
        ElseNode elseBranch = null;
        if (checkFor("else", scanner)) {
            elseBranch = new ElseNode(parseBLOCK(scanner), elifs);
        }
        return new IfNode(condition, ifBlock, elifs, elseBranch);
    }

    private ElseIfNode parseELIF(Scanner scanner) {
        require("elif", "expecting 'elif'", scanner);
        require(OPENPAREN, "expecting '('", scanner);
        BooleanNode condition = parseCOND(scanner);
        require(CLOSEPAREN, "expecting ')'", scanner);
        return new ElseIfNode(condition, parseBLOCK(scanner));
    }

    /**
     * ACT ::= "move" | "turnL" | "turnR" | "takeFuel" | "wait"
     * Parses an action node
     * 
     * @param scanner
     * @return an action node
     */
    private ProgramNode parseACT(Scanner scanner) {
        String action = require(ACTION, "expecting an action", scanner);
        ProgramNode node = parseCMD(scanner, action);
        return new ActionNode(node);
    }

    /**
     * Parses an action for the action node
     * 
     * @param scanner
     * @param action
     * @return a an action node
     */
    private ProgramNode parseCMD(Scanner scanner, String action) {
        switch (action) {
            case "move":
                if (checkFor(OPENPAREN, scanner)) {
                    IntNode value = parseEXPR(scanner);
                    require(CLOSEPAREN, "expecting ')'", scanner);
                    return (ProgramNode) new MoveNode(value);
                }
                return (ProgramNode) new MoveNode();
            case "turnL":
                return new TurnLNode();
            case "turnR":
                return new TurnRNode();
            case "takeFuel":
                return new TakeFuelNode();
            case "wait":
                if (checkFor(OPENPAREN, scanner)) {
                    IntNode value = parseEXPR(scanner);
                    require(CLOSEPAREN, "expecting ')'", scanner);
                    return (ProgramNode) new WaitNode(value);
                }
                return new WaitNode();
            case "shieldOn":
                return new ShieldOnNode();
            case "shieldOff":
                return new ShieldOffNode();
            case "turnAround":
                return new TurnAroundNode();
        }
        fail("Unknown action", scanner);
        return null;
    }

    /**
     * EXPR ::= NUM | SENS | OP "(" EXPR "," EXPR ")"
     * Parses an expression node
     * 
     * @param scanner
     * @return an expression node
     */
    private IntNode parseEXPR(Scanner scanner) {
        if (scanner.hasNext(NUMPAT)) {
            return parseNUM(scanner);
        } else if (scanner.hasNext(SENS)) {
            return parseSENS(scanner);
        } else if (scanner.hasNext(OP)) {
            return parseOP(scanner);
        } else if (scanner.hasNext(VAR)) {
            return parseVAR(scanner);
        }
        fail("Unknown expression", scanner);
        return null;
    }

    /**
     * "fuelLeft" | "oppLR" | "oppFB" | "numBarrels" |
     * "barrelLR" | "barrelFB" | "wallDist"
     * 
     * @param scanner
     * @return
     */
    private SensorNode parseSENS(Scanner scanner) {
        String sensor = require(SENS, "expecting a sensor", scanner);
        return new SensorNode(parseSENS(scanner, sensor));
    }

    /**
     * ASSGN::= VAR "=" EXPR
     * 
     * @param scanner
     * @param sensor
     * @return
     */
    private ProgramNode parseASSGN(Scanner scanner) {
        String var = require(VAR, "expecting a variable", scanner);
        if (!variables.containsKey(var)) {
            variables.put(var, new NumberNode(0));
        }
        require("=", "expecting '='", scanner);
        IntNode value = parseEXPR(scanner);
        variables.put(var, value);
        require(SEMICOLON, "expecting ';'", scanner);
        return new AssignmentNode(var, value);
    }

    /**
     * Parses a variable node
     * VAR ::= "\\$[A-Za-z][A-Za-z0-9]*"
     * 
     * @param scanner
     * @return a variable node
     */
    private IntNode parseVAR(Scanner scanner) {
        String var = require(VAR, "expecting a variable", scanner);
        if (!variables.containsKey(var)) {
            variables.put(var, new NumberNode(0));
        }
        return new VariableNode(var);
    }

    /**
     * SENS ::= "fuelLeft" | "oppLR" | "oppFB" | "numBarrels" |
     * "barrelLR" [ "(" EXPR ")" ] | "barrelFB" [ "(" EXPR ")" ] | "wallDist"
     * 
     * @param scanner
     * @param sensor
     * @return
     */
    private IntNode parseSENS(Scanner scanner, String sensor) {
        switch (sensor) {
            case "fuelLeft":
                return new GetFuelNode();
            case "oppLR":
                return new GetOpponentLR();
            case "oppFB":
                return new GetOpponentFB();
            case "numBarrels":
                return new GetNumBarrels();
            case "barrelLR":
                if (checkFor(OPENPAREN, scanner)) {
                    IntNode value = parseEXPR(scanner);
                    require(CLOSEPAREN, "expecting ')'", scanner);
                    return new GetClosestBarrelLR(value);
                }
                return new GetClosestBarrelLR();
            case "barrelFB":
                if (checkFor(OPENPAREN, scanner)) {
                    IntNode value = parseEXPR(scanner);
                    require(CLOSEPAREN, "expecting ')'", scanner);
                    return new GetClosestBarrelFB(value);
                }
                return new GetClosestBarrelFB();
            case "wallDist":
                return new GetWallDistance();
        }
        fail("Unknown sensor", scanner);
        return null;
    }

    private IntNode parseNUM(Scanner scanner) {
        return new NumberNode(requireInt(NUMPAT, "expecting a number", scanner));
    }

    /**
     * OP ::= "add" | "sub" | "mul" | "div"
     * 
     * @param scanner
     * @return
     */
    private IntNode parseOP(Scanner scanner) {
        String op = require(OP, "expecting an operator", scanner);
        require(OPENPAREN, "expecting '('", scanner);
        IntNode left = parseEXPR(scanner);
        require(COMMA, "expecting ','", scanner);
        IntNode right = parseEXPR(scanner);
        require(CLOSEPAREN, "expecting ')'", scanner);
        return new OperationNode(op, left, right);
    }

    /**
     * LOOP ::= "loop" BLOCK
     * Parses a loop node
     * 
     * @param scanner
     * @return a loop node
     */
    private ProgramNode parseLOOP(Scanner scanner) {
        require(LOOP, "expecting 'loop'", scanner);
        return new LoopNode(parseBLOCK(scanner));
    }

    /**
     * BLOCK ::= "{" STMT+ "}"
     * Parses a block node
     * 
     * @param scanner
     * @return a block node
     */
    private BlockNode parseBLOCK(Scanner scanner) {
        require(OPENBRACE, "expecting '{'", scanner);
        List<ProgramNode> statements = new ArrayList<ProgramNode>();
        while (!scanner.hasNext(CLOSEBRACE)) {
            statements.add(parseSTMT(scanner));
        }
        if (statements.isEmpty()) {
            return null;
        }
        require(CLOSEBRACE, "expecting '}'", scanner);
        return new BlockNode(statements);
    }

    /**
     * Report a failure in the parser.
     */
    static void fail(String message, Scanner s) {
        String msg = message + "\n   @ ...";
        for (int i = 0; i < 5 && s.hasNext(); i++) {
            msg += " " + s.next();
        }
        throw new ParserFailureException(msg + "...");
    }

    /**
     * Requires that the next token matches a pattern if it matches, it consumes
     * and returns the token, if not, it throws an exception with an error
     * message
     */
    static String require(String p, String message, Scanner s) {
        if (s.hasNext(p)) {
            return s.next();
        }
        fail(message, s);
        return null;
    }

    static String require(Pattern p, String message, Scanner s) {
        if (s.hasNext(p)) {
            return s.next();
        }
        fail(message, s);
        return null;
    }

    /**
     * Requires that the next token matches a pattern (which should only match a
     * number) if it matches, it consumes and returns the token as an integer
     * if not, it throws an exception with an error message
     */
    static int requireInt(String p, String message, Scanner s) {
        if (s.hasNext(p) && s.hasNextInt()) {
            return s.nextInt();
        }
        fail(message, s);
        return -1;
    }

    static int requireInt(Pattern p, String message, Scanner s) {
        if (s.hasNext(p) && s.hasNextInt()) {
            return s.nextInt();
        }
        fail(message, s);
        return -1;
    }

    /**
     * Checks whether the next token in the scanner matches the specified
     * pattern, if so, consumes the token and return true. Otherwise returns
     * false without consuming anything.
     */
    static boolean checkFor(String p, Scanner s) {
        if (s.hasNext(p)) {
            s.next();
            return true;
        }
        return false;
    }

    static boolean checkFor(Pattern p, Scanner s) {
        if (s.hasNext(p)) {
            s.next();
            return true;
        }
        return false;
    }

}

// You could add the node classes here or as separate java files.
// (if added here, they must not be declared public or private)
// For example:
// class BlockNode implements ProgramNode {.....
// with fields, a toString() method and an execute() method
//

class ActionNode implements ProgramNode {
    private ProgramNode instruction;

    public ActionNode(ProgramNode instruction) {
        this.instruction = instruction;
    }

    @Override
    // Executes the corresponding action node for the robot
    public void execute(Robot robot) {
        instruction.execute(robot);

    }

    public String toString() {
        return instruction.toString() + ';';
    }
}

class AndNode implements BooleanNode {

    BooleanNode left;
    BooleanNode right;

    public AndNode(BooleanNode left, BooleanNode right) {
        this.left = left;
        this.right = right;
    }

    @Override
    public boolean evaluate(Robot robot) {
        return left.evaluate(robot) && right.evaluate(robot);
    }

    public String toString() {
        return "and";
    }

}

class AssignmentNode implements ProgramNode {

    private String variable;
    private IntNode expression;

    public AssignmentNode(String variable, IntNode expression) {
        this.variable = variable;
        this.expression = expression;
    }

    @Override
    public void execute(Robot robot) {
        if (!Parser.variables.containsKey(variable)) {
            Parser.variables.put(variable, new NumberNode(0));
        }
        int value = expression.evaluate(robot);
        Parser.variables.put(variable, new NumberNode(value));
    }

    public String toString() {
        return variable.toString() + " = " + expression.toString();
    }

}

class BlockNode implements ProgramNode {
    List<ProgramNode> statements = new ArrayList<ProgramNode>();

    @Override
    public void execute(Robot robot) {
        for (ProgramNode s : statements) {
            s.execute(robot);
        }
    }

    public BlockNode(List<ProgramNode> statements) {
        this.statements = statements;
    }

    public String toString() {
        String string = "";
        for (ProgramNode statement : statements) {
            string += statement.toString() + " ";
        }
        return "{" + string + "}";
    }
}

interface BooleanNode {
    public boolean evaluate(Robot robot);
}

class ConditionNode implements BooleanNode {

    private BooleanNode condition;

    public ConditionNode(BooleanNode condition) {
        this.condition = condition;
    }

    @Override
    public boolean evaluate(Robot robot) {
        return condition.evaluate(robot);
    }

    public String toString() {
        return condition.toString();
    }
}

class ElseIfNode implements BooleanNode {

    private BooleanNode condition;
    private BlockNode block;

    public ElseIfNode(BooleanNode condition, BlockNode block) {
        this.condition = condition;
        this.block = block;
    }

    @Override
    public boolean evaluate(Robot robot) {
        if (condition.evaluate(robot)) {
            block.execute(robot);
        }
        return condition.evaluate(robot);
    }

    public String toString() {
        return "elif (" + condition + ")" + block;
    }

}

class ElseNode implements ProgramNode {
    BlockNode block;

    public ElseNode(BlockNode block) {
        this.block = block;
    }

    public ElseNode(BlockNode block, List<ElseIfNode> elifs) {
        this.block = block;
    }

    @Override
    public void execute(Robot robot) {
        block.execute(robot);
    }

    public String toString() {
        return "else" + block.toString();
    }
}

class EqualToNode implements BooleanNode {

    private IntNode left;
    private IntNode right;

    public EqualToNode(IntNode left, IntNode right) {
        this.left = left;
        this.right = right;
    }

    public boolean evaluate(Robot robot) {
        return left.evaluate(robot) == right.evaluate(robot);
    }

    public String toString() {
        return "eq(" + left.toString() + "," + right.toString() + ")";
    }

}

class ExpressionNode implements ProgramNode {
    private ProgramNode node;

    public ExpressionNode(ProgramNode node) {
        this.node = node;
    }

    @Override
    public void execute(Robot robot) {
        node.execute(robot);
    }

    public String toString() {
        return node.toString();
    }
}

class GetClosestBarrelFB implements IntNode {
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

class GetClosestBarrelLR implements IntNode {
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

class GetFuelNode implements IntNode {

    public GetFuelNode() {
    }

    @Override
    public int evaluate(Robot robot) {
        return robot.getFuel();
    }

    public String toString() {
        return "fuelLeft";
    }
}

class GetNumBarrels implements IntNode {

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

class GetOpponentFB implements IntNode {

    public GetOpponentFB() {
    }

    @Override
    public int evaluate(Robot robot) {
        return robot.getOpponentFB();
    }

    public String toString() {
        return "oppFB";

    }
}

class GetOpponentLR implements IntNode {

    public GetOpponentLR() {
    }

    @Override
    public int evaluate(Robot robot) {
        return robot.getOpponentLR();
    }

    public String toString() {
        return "oppLR";
    }
}

class GetWallDistance implements IntNode {

    public GetWallDistance() {
    }

    @Override
    public int evaluate(Robot robot) {
        return robot.getDistanceToWall();
    }

    public String toString() {
        return "wallDist";
    }

}

class GreaterThanNode implements BooleanNode {

    private IntNode left;
    private IntNode right;

    public GreaterThanNode(IntNode left, IntNode right) {
        this.left = left;
        this.right = right;
    }

    public boolean evaluate(Robot robot) {
        return left.evaluate(robot) > right.evaluate(robot);
    }

    public String toString() {
        return "gt(" + left.toString() + "," + right.toString() + ")";
    }

}

class IfNode implements ProgramNode {

    BooleanNode condition;
    BlockNode block;
    private List<ElseIfNode> elifs;
    ElseNode elseBranch;

    public IfNode(BooleanNode condition, BlockNode block) {
        this.condition = condition;
        this.block = block;
    }

    public IfNode(BooleanNode condition, BlockNode block, List<ElseIfNode> elifs, ElseNode elseBranch) {
        this.condition = condition;
        this.block = block;
        this.elifs = elifs;
        this.elseBranch = elseBranch;
    }

    @Override
    public void execute(Robot robot) {
        if (condition.evaluate(robot)) {
            block.execute(robot);
        } else {
            for (ElseIfNode elif : elifs) {
                if (elif.evaluate(robot))
                    return;
            }
            if (elseBranch != null) {
                elseBranch.execute(robot);
                return;
            }
        }
    }

    public String toString() {
        String string = "if (" + condition.toString() + ") " + block;

        if (elifs.size() > 0) {
            for (ElseIfNode elif : elifs) {
                string += " " + elif.toString();
            }
        }

        if (elseBranch != null) {
            string += " " + elseBranch.toString();
        }
        return string;
    }

}

interface IntNode {
    public int evaluate(Robot robot);
}

class LessThanNode implements BooleanNode {

    private IntNode left;
    private IntNode right;

    public LessThanNode(IntNode left, IntNode right) {
        this.left = left;
        this.right = right;
    }

    public boolean evaluate(Robot robot) {
        int leftNode = left.evaluate(robot);
        int rightNode = right.evaluate(robot);
        return leftNode < rightNode;
    }

    public String toString() {
        return "lt(" + left.toString() + "," + right.toString() + ")";
    }
}

class LoopNode implements ProgramNode {

    BlockNode block;

    public LoopNode(BlockNode block) {
        this.block = block;
    }

    @Override
    public void execute(Robot robot) {
        while (true) {
            block.execute(robot);
        }
    }

    public String toString() {
        return "loop" + this.block.toString();
    }
}

class MoveNode implements ProgramNode {
    private IntNode value;

    public MoveNode() {
    }

    public MoveNode(IntNode value) {
        this.value = value;
    }

    @Override
    /**
     * Executes action based on number of value times
     */
    public void execute(Robot robot) {
        if (value == null) {
            robot.move();
            return;
        }
        int stepsToMove = value.evaluate(robot);
        for (int i = 0; i < stepsToMove; i++) {
            robot.move();
        }
    }

    public String toString() {
        return value == null ? "move" : "move(" + value.toString() + ")";
    }

}

class NotNode implements BooleanNode {
    BooleanNode condition;

    public NotNode(BooleanNode condition) {
        this.condition = condition;
    }

    @Override
    public boolean evaluate(Robot robot) {
        return !condition.evaluate(robot);
    }

    public String toString() {
        return "not";
    }
}

class NumberNode implements IntNode {
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

class OperationNode implements IntNode {

    IntNode left;
    IntNode right;
    String operation;

    public OperationNode(String operation, IntNode left, IntNode right) {
        this.left = left;
        this.right = right;
        this.operation = operation;
    }

    @Override
    public int evaluate(Robot robot) {
        switch (operation) {
            case "add":
                return left.evaluate(robot) + right.evaluate(robot);
            case "sub":
                return left.evaluate(robot) - right.evaluate(robot);
            case "mul":
                return left.evaluate(robot) * right.evaluate(robot);
            case "div":
                return left.evaluate(robot) / right.evaluate(robot);
            default:
                return 0;
        }
    }

    public String toString() {
        return operation + "(" + left.toString() + "," + right.toString() + ")";
    }
}

class OrNode implements BooleanNode {

    BooleanNode left;
    BooleanNode right;

    public OrNode(BooleanNode left, BooleanNode right) {
        this.left = left;
        this.right = right;
    }

    @Override
    public boolean evaluate(Robot robot) {
        return left.evaluate(robot) || right.evaluate(robot);
    }

    public String toString() {
        return "or";
    }

}

class SensorNode implements IntNode {

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

class ShieldOffNode implements ProgramNode {
    public ShieldOffNode() {
    }

    @Override
    public void execute(Robot robot) {
        robot.setShield(false);
    }

    public String toString() {
        return "shieldOFF";
    }

}

class ShieldOnNode implements ProgramNode {
    public ShieldOnNode() {
    }

    @Override
    public void execute(Robot robot) {
        robot.setShield(true);
    }

    public String toString() {
        return "shieldON";
    }

}

class StatementNode implements ProgramNode {
    ProgramNode statement;

    public StatementNode(ProgramNode statement) {
        this.statement = statement;
    }

    @Override
    public void execute(Robot robot) {
        statement.execute(robot);
    }

    public String toString() {
        return (statement instanceof ActionNode) ? (statement.toString() + " ;") : (statement.toString());
    }

}

class TakeFuelNode implements ProgramNode {
    public TakeFuelNode() {

    }

    @Override
    public void execute(Robot robot) {
        robot.takeFuel();
    }

    public String toString() {
        return "takeFuel";
    }

}

class TurnAroundNode implements ProgramNode {
    public TurnAroundNode() {
    }

    @Override
    public void execute(Robot robot) {
        robot.turnAround();
    }

    public String toString() {
        return "turnAround";
    }
}

class TurnLNode implements ProgramNode {
    public TurnLNode() {

    }

    @Override
    public void execute(Robot robot) {
        robot.turnLeft();
    }

    public String toString() {
        return "turnL";
    }

}

class TurnRNode implements ProgramNode {
    public TurnRNode() {

    }

    @Override
    public void execute(Robot robot) {
        robot.turnRight();
    }

    public String toString() {
        return "turnR";
    }

}

class VariableNode implements IntNode {

    private String variable;

    public VariableNode(String variable) {
        this.variable = variable;
    }

    @Override
    public int evaluate(Robot robot) {
        return Parser.variables.containsKey(variable) ? Parser.variables.get(variable).evaluate(robot) : 0;
    }

    public String toString() {
        return variable;
    }

}

class WaitNode implements ProgramNode {
    IntNode value;

    public WaitNode() {

    }

    public WaitNode(IntNode value) {
        this.value = value;
    }

    @Override
    public void execute(Robot robot) {

        if (value == null) {
            robot.idleWait();
            return;
        }
        int stepsToWait = value.evaluate(robot);
        for (int i = 0; i < stepsToWait; i++) {
            robot.idleWait();
        }
    }

    public String toString() {
        return value == null ? "wait" : "wait(" + value.toString() + ")";
    }

}

class WhileNode implements ProgramNode {

    private BooleanNode condition;
    private BlockNode block;

    public WhileNode(BooleanNode condition, BlockNode block) {
        this.condition = condition;
        this.block = block;
    }

    @Override
    public void execute(Robot robot) {
        while (condition.evaluate(robot)) {
            block.execute(robot);
        }
    }

    public String toString() {
        return "while (" + condition.toString() + ") " + block.toString();
    }

}
