import java.util.Arrays;

/** Puzzle state and rules. Screen drawing and input stay in GamePanel/GameManager. */
public class PuzzleManager {
    public enum Type { CODE, ORDER, LIGHTS }
    public static PuzzleContainer activeContainer;
    public static boolean isOpen(){ return activeContainer != null; }
    public static void close(){ activeContainer = null; }

    public final Type type;
    public final String title;
    public final String instructions;
    public final String[] choices;
    public boolean solved;
    public boolean showingHint;
    public String message = "";
    private final String answer;
    private String entry = "";
    private String orderEntry = "";
    private final boolean[] lights = new boolean[9];

    private PuzzleManager(Type type, String title, String instructions, String answer, String[] choices){
        this.type = type;
        this.title = title;
        this.instructions = instructions;
        this.answer = answer;
        this.choices = choices.clone();
    }

    public static PuzzleManager code(String title, String instructions, String answer){
        return new PuzzleManager(Type.CODE, title, instructions, answer, new String[0]);
    }
    public static PuzzleManager order(String title, String instructions, String[] choices, String answer){
        return new PuzzleManager(Type.ORDER, title, instructions, answer, choices);
    }
    public static PuzzleManager lights(String title){
        PuzzleManager puzzle = new PuzzleManager(Type.LIGHTS, title,
            "Light all nine lamps. A switch flips itself and its neighbors.", "", new String[0]);
        puzzle.resetLights();
        return puzzle;
    }
    public String entry(){ return entry; }
    public boolean lightAt(int index){ return index >= 0 && index < 9 && lights[index]; }

    public void digit(char digit){
        if(!solved && type == Type.CODE && digit >= '0' && digit <= '9' && entry.length() < answer.length()){
            entry += digit;
            message = "";
        }
    }
    public void backspace(){
        if(!solved && type == Type.CODE && !entry.isEmpty()) entry = entry.substring(0, entry.length() - 1);
    }
    public void submit(){
        if(solved || type != Type.CODE) return;
        if(entry.equals(answer)) finish();
        else { message = "The lock stays shut. Look around the room and try again."; entry = ""; }
    }
    public void choose(int index){
        if(solved || type != Type.ORDER || index < 0 || index >= choices.length) return;
        orderEntry += index;
        entry += (entry.isEmpty() ? "" : " > ") + choices[index];
        message = "";
        if(orderEntry.length() == answer.length()){
            if(orderEntry.equals(answer)) finish();
            else {
                entry = "";
                orderEntry = "";
                message = "Wrong order. The tabs spring back. Try again.";
            }
        }
    }

    public void toggle(int index){
        if(solved || type != Type.LIGHTS || index < 0 || index >= lights.length) return;
        flip(index);
        for(boolean light : lights) if(!light) return;
        finish();
    }
    private void flip(int index){
        lights[index] = !lights[index];
        if(index >= 3) lights[index - 3] = !lights[index - 3];
        if(index < 6) lights[index + 3] = !lights[index + 3];
        if(index % 3 > 0) lights[index - 1] = !lights[index - 1];
        if(index % 3 < 2) lights[index + 1] = !lights[index + 1];
    }
    public void reset(){
        if(solved) return;
        entry = "";
        orderEntry = "";
        message = "Puzzle reset.";
        if(type == Type.LIGHTS) resetLights();
    }
    private void resetLights(){
        Arrays.fill(lights, true);
        // Scramble from a solved board so every new case is solvable.
        flip(0); flip(4); flip(8);
    }
    private void finish(){ solved = true; message = "The lock clicks open."; }
}
