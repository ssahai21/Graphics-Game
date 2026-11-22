public class PuzzleManager {
    public static final int CODE = 0;
    public static final int ORDER = 1;
    public static final int LIGHTS = 2;

    public static PuzzleContainer activeContainer;
    public static boolean isOpen() {
        return activeContainer != null;
    }

    public static void close() {
        activeContainer = null;
    }

    public final int type;
    public final String title;
    public final String instructions;
    public final String[] choices;
    public boolean solved;
    public String message = "";
    private final String answer;
    private String entry = "";
    private String orderEntry = "";
    private final boolean[] lights = {
        true, false, false,
        false, false, false,
        true, false, true
    };

    private PuzzleManager(int type, String title, String instructions, String answer, String[] choices) {
        this.type = type;
        this.title = title;
        this.instructions = instructions;
        this.answer = answer;
        this.choices = choices.clone();
    }

    public static PuzzleManager code(String title, String instructions, String answer) {
        return new PuzzleManager(CODE, title, instructions, answer, new String[0]);
    }

    public static PuzzleManager order(String title, String instructions, String[] choices, String answer) {
        return new PuzzleManager(ORDER, title, instructions, answer, choices);
    }

    public static PuzzleManager lights(String title) {
        PuzzleManager puzzle = new PuzzleManager(LIGHTS, title,
            "Light all nine lamps. A switch flips itself and its neighbors.", "", new String[0]);
        return puzzle;
    }

    public String entry() {
        return entry;
    }

    public boolean lightAt(int index) {
        return index >= 0 && index < 9 && lights[index];
    }

    public void digit(char digit) {
        if (!solved && type == CODE && digit >= '0' && digit <= '9' && entry.length() < answer.length()) {
            entry += digit;
            message = "";
        }
    }

    public void backspace() {
        if (!solved && type == CODE && !entry.isEmpty()) {
            entry = entry.substring(0, entry.length() - 1);
        }
    }

    public void submit() {
        if (solved || type != CODE) {
            return;
        }
        if (entry.equals(answer)) {
            finish();
        }
        else {
            message = "That code didn't work.";
            entry = "";
        }
    }

    public void choose(int index) {
        if (solved || type != ORDER || index < 0 || index >= choices.length) {
            return;
        }
        orderEntry += index;
        if (!entry.isEmpty()) {
            entry += " > ";
        }
        entry += choices[index];
        message = "";
        if (orderEntry.length() == answer.length()) {
            if (orderEntry.equals(answer)) {
                finish();
            }
            else {
                entry = "";
                orderEntry = "";
                message = "That order didn't work.";
            }
        }
    }

    public void toggle(int index) {
        if (solved || type != LIGHTS || index < 0 || index >= lights.length) {
            return;
        }
        flip(index);
        for (boolean light : lights) {
            if (!light) {
                return;
            }
        }
        finish();
    }

    private void flip(int index) {
        lights[index] = !lights[index];
        if (index >= 3) {
            lights[index - 3] = !lights[index - 3];
        }
        if (index < 6) {
            lights[index + 3] = !lights[index + 3];
        }
        if (index % 3 > 0) {
            lights[index - 1] = !lights[index - 1];
        }
        if (index % 3 < 2) {
            lights[index + 1] = !lights[index + 1];
        }
    }

    private void finish() {
        solved = true;
        message = "It's open.";
    }
}
