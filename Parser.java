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
    static Map<String, Integer> variables = new HashMap<String, Integer>();

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
        return new BlockNode(statements);
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

    private ProgramNode parseWHILE(Scanner scanner) {
        require(WHILE, "expecting 'while'", scanner);
        require(OPENPAREN, "expecting '('", scanner);
        BooleanNode condition = parseCOND(scanner);
        require(CLOSEPAREN, "expecting ')'", scanner);
        return new WhileNode(condition, parseBLOCK(scanner));

    }

    private BooleanNode parseCOND(Scanner scanner) {
        if (scanner.hasNext("and")) {
            return parseAND(scanner);
        } else if (scanner.hasNext("or")) {
            return parseOR(scanner);
        } else if (scanner.hasNext("not")) {
            return parseNOT(scanner);
        } else if (scanner.hasNext(RELOP)) {
            return parseRELOP(scanner);
        }
        fail("Unknown condition", scanner);
        return null;
    }

    private BooleanNode parseAND(Scanner scanner) {
        require("and", "expecting 'and'", scanner);
        require(OPENPAREN, "expecting '('", scanner);
        BooleanNode left = parseCOND(scanner);
        require(COMMA, "expecting ','", scanner);
        BooleanNode right = parseCOND(scanner);
        require(CLOSEPAREN, "expecting ')'", scanner);
        return new AndNode(left, right);
    }

    private BooleanNode parseOR(Scanner scanner) {
        require("or", "expecting 'or'", scanner);
        require(OPENPAREN, "expecting '('", scanner);
        BooleanNode left = parseCOND(scanner);
        require(COMMA, "expecting ','", scanner);
        BooleanNode right = parseCOND(scanner);
        require(CLOSEPAREN, "expecting ')'", scanner);
        return new OrNode(left, right);
    }

    private BooleanNode parseNOT(Scanner scanner) {
        require("not", "expecting 'not'", scanner);
        require(OPENPAREN, "expecting '('", scanner);
        BooleanNode condition = parseCOND(scanner);
        require(CLOSEPAREN, "expecting ')'", scanner);
        return new NotNode(condition);
    }

    private BooleanNode parseRELOP(Scanner scanner) {
        String relop = require(RELOP, "expecting a relop", scanner);
        require(OPENPAREN, "expecting '('", scanner);
        IntNode left = parseEXPR(scanner);
        require(COMMA, "expecting ','", scanner);
        IntNode right = parseEXPR(scanner);
        require(CLOSEPAREN, "expecting ')'", scanner);
        return parseRELOP(scanner, relop, left, right);
    }

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

    private SensorNode parseSENS(Scanner scanner) {
        String sensor = require(SENS, "expecting a sensor", scanner);
        return new SensorNode(parseSENS(scanner, sensor));
    }

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
     * @param scanner
     * @param sensor
     * @return
     */
    private ProgramNode parseASSGN(Scanner scanner) {
        String var = require(VAR, "expecting a variable", scanner);
        if (!variables.containsKey(var)) {
            variables.put(var, 0);
        }
        require("=", "expecting '='", scanner);
        IntNode value = parseEXPR(scanner);
        variables.put(var, value.evaluate(null));
        require(SEMICOLON, "expecting ';'", scanner);
        return new AssignmentNode(var, value);
    }

    private IntNode parseVAR(Scanner scanner) {
        String var = require(VAR, "expecting a variable", scanner);
        if (!variables.containsKey(var)) {
            variables.put(var, 0);
        }
        return new NumberNode(variables.get(var));
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
