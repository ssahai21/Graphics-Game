public class PuzzleTest {
    private static int checks;
    public static void main(String[] args){
        PuzzleManager code = RoomManager.classroomBox.puzzle;
        code.digit('0'); code.submit();
        check(!code.solved && code.entry().isEmpty(), "wrong code retries");
        code.digit('3'); code.digit('x');
        check(code.entry().equals("3"), "non-digits ignored");
        code.digit('1'); code.backspace();
        check(code.entry().equals("3"), "delete digit");
        GameManager.start = false;
        GameManager.currRoom = RoomManager.room2;
        GameManager.handleClick(200, 300);
        check(PuzzleManager.isOpen(), "puzzle opens");
        GameManager.handleClick(20, 20);
        check(!GameManager.start && PuzzleManager.isOpen(), "puzzle blocks underlying menu");
        GameManager.handleClick(90, 365);
        check(code.showingHint, "hint button");
        GameManager.handleClick(570, 50);
        check(!PuzzleManager.isOpen(), "close button");
        GameManager.handleClick(200, 300);
        check(code.entry().equals("3") && code.showingHint, "reopening retains progress and hint");
        GameManager.handleClick(540, 365);
        check(code.entry().isEmpty() && !code.solved, "reset button");
        code.digit('3'); code.digit('1'); code.digit('4'); code.digit('9');
        check(code.entry().equals("314"), "entry length limited");
        code.submit(); code.reset();
        check(code.solved, "reset cannot relock solved puzzle");
        GameManager.handleClick(570, 50);
        GameManager.handleClick(200, 300);
        check(code.solved && !RoomManager.classroomBox.collected, "solved unclaimed box survives closing");
        PuzzleManager order = RoomManager.libraryBox.puzzle;
        order.choose(1); order.choose(0);
        check(!order.entry().isEmpty() && order.message.isEmpty(), "sequence waits for all three choices");
        order.choose(2);
        check(!order.solved && order.entry().isEmpty(), "wrong sequence resets");
        order.choose(-1); order.choose(99);
        check(order.entry().isEmpty(), "invalid choice ignored");
        order.choose(1); order.choose(2); order.choose(0); order.choose(0);
        check(order.solved, "order solves and ignores extra input");
        PuzzleManager lights = RoomManager.recordsSafe.puzzle;
        boolean[] original = new boolean[9];
        for(int i=0;i<9;i++) original[i]=lights.lightAt(i);
        lights.toggle(2); lights.toggle(2);
        for(int i=0;i<9;i++) check(original[i]==lights.lightAt(i), "double toggle restores board");
        lights.toggle(1); lights.reset();
        for(int i=0;i<9;i++) check(original[i]==lights.lightAt(i), "reset restores board");
        for(int index : new int[]{0,4,8}) lights.toggle(index);
        check(lights.solved, "light board is solvable");
        for(int i=0;i<9;i++) check(lights.lightAt(i), "solved lamps all on");
        Investigation.reset();
        check(!PuzzleManager.isOpen() && !RoomManager.classroomBox.puzzle.solved, "new case clears active puzzle");
        System.out.println("Passed " + checks + " puzzle checks.");
        System.exit(0);
    }
    private static void check(boolean condition, String message){
        if(!condition) throw new AssertionError(message);
        checks++;
    }
}
