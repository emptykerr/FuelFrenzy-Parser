/**
 * Interface for all nodes that can be executed,
 * including the top level program node
 * * PROG ::= [ STMT ]*
 * 
 */

interface ProgramNode {

    public void execute(Robot robot);

    public String toString();
}
