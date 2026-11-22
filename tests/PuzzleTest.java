public class PuzzleTest {
    private static int checks;
    public static void main(String[] args) {
        PuzzleManager code = RoomManager.classroomBox.puzzle;
        code.digit('0');
        code.submit();
        check(!code.solved && code.entry().isEmpty(), "wrong code retries");
        code.digit('3');
        code.digit('x');
        check(code.entry().equals("3"), "non-digits ignored");
        code.digit('1');
        code.backspace();
        check(code.entry().equals("3"), "delete digit");
        GameManager.start = false;
        GameManager.currRoom = RoomManager.classroom;
        GameManager.handleClick(200, 300);
        check(PuzzleManager.isOpen(), "puzzle opens");
        GameManager.handleClick(20, 20);
        check(!GameManager.start && PuzzleManager.isOpen(), "puzzle blocks underlying menu");
        GameManager.handleClick(90, 365);
        check(code.entry().equals("3"), "empty area does not change the code");
        GameManager.handleClick(570, 50);
        check(!PuzzleManager.isOpen(), "close button");
        GameManager.handleClick(200, 300);
        check(code.entry().equals("3"), "reopening retains progress");
        GameManager.handleClick(540, 365);
        check(code.entry().equals("3") && !code.solved, "removed reset area does not change the code");
        code.backspace();
        code.digit('3');
        code.digit('1');
        code.digit('4');
        code.digit('9');
        check(code.entry().equals("314"), "entry length limited");
        code.submit();
        code.digit('9');
        code.backspace();
        code.submit();
        check(code.solved && code.entry().equals("314"), "solved code ignores further input");
        GameManager.handleClick(570, 50);
        GameManager.handleClick(200, 300);
        check(code.solved && !RoomManager.classroomBox.collected, "solved unclaimed box survives closing");
        PuzzleManager order = RoomManager.libraryBox.puzzle;
        order.choose(1);
        order.choose(0);
        check(!order.entry().isEmpty() && order.message.isEmpty(), "sequence waits for all three choices");
        order.choose(2);
        check(!order.solved && order.entry().isEmpty(), "wrong sequence resets");
        order.choose(-1);
        order.choose(99);
        check(order.entry().isEmpty(), "invalid choice ignored");
        order.choose(1);
        order.choose(2);
        order.choose(0);
        order.choose(0);
        check(order.solved, "order solves and ignores extra input");
        PuzzleManager lights = RoomManager.recordsSafe.puzzle;
        boolean[] original = new boolean[9];
        for (int i = 0; i < 9; i++) {
            original[i] = lights.lightAt(i);
        }
        lights.toggle(2);
        lights.toggle(2);
        for (int i = 0; i < 9; i++) {
            check(original[i] == lights.lightAt(i), "double toggle restores board");
        }
        lights.toggle(-1);
        lights.toggle(9);
        for (int i = 0; i < 9; i++) {
            check(original[i] == lights.lightAt(i), "invalid switches do not change board");
        }
        int solution = lightSolution(lights);
        check(solution >= 0 && Integer.bitCount(solution) == 6, "fixed starting board needs six presses");
        for (int index = 0; index < 9; index++) {
            if ((solution & (1 << index)) != 0) {
                lights.toggle(index);
            }
        }
        check(lights.solved, "light board is solvable");
        for (int i = 0; i < 9; i++) {
            check(lights.lightAt(i), "solved lamps all on");
        }
        PuzzleManager freshBoard = PuzzleManager.lights("Test safe");
        for (int index = 0; index < 9; index++) {
            check(freshBoard.lightAt(index) == original[index], "new puzzles use the fixed starting board");
        }
        Investigation.reset();
        check(!PuzzleManager.isOpen() && !RoomManager.classroomBox.puzzle.solved, "new case clears active puzzle");
        for (int index = 0; index < 9; index++) {
            check(RoomManager.recordsSafe.puzzle.lightAt(index) == original[index], "new case restores the fixed starting board");
        }
        System.out.println("Passed " + checks + " puzzle checks.");
        System.exit(0);
    }
    // Exhaustively checks the displayed board, independent of its setup.
    public static int lightSolution(PuzzleManager puzzle) {
        int best = -1;
        for (int presses = 0; presses < 512; presses++) {
            boolean solved = true;
            for (int cell = 0; cell < 9; cell++) {
                boolean on = puzzle.lightAt(cell);
                for (int press = 0; press < 9; press++) {
                    int distance = Math.abs(cell / 3 - press / 3) + Math.abs(cell % 3 - press % 3);
                    if ((presses & (1 << press)) != 0 && distance <= 1) {
                        on = !on;
                    }
                }
                if (!on) {
                    solved = false;
                    break;
                }
            }
            if (solved && (best < 0 || Integer.bitCount(presses) < Integer.bitCount(best))) {
                best = presses;
            }
        }
        return best;
    }

    private static void check(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
        checks++;
    }
}
