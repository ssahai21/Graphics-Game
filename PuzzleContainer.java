import java.util.ArrayList;
import java.util.Arrays;
import javax.swing.ImageIcon;

/** A room object that keeps its contents inaccessible until its puzzle is solved. */
public class PuzzleContainer {
    public final String name;
    public final Coordinate location;
    public final PuzzleManager puzzle;
    public final ArrayList<Item> items = new ArrayList<>();
    public final ArrayList<Clue> clues = new ArrayList<>();
    public boolean collected;

    public PuzzleContainer(String name, Coordinate location, PuzzleManager puzzle){
        this.name = name;
        this.location = location;
        this.puzzle = puzzle;
    }
    public boolean contains(int x, int y){
        return x >= location.x && x < location.x + 100 && y >= location.y && y < location.y + 100;
    }
    public DialougeManager collect(){
        if(!puzzle.solved || collected) return null;
        collected = true;
        ArrayList<String> lines = new ArrayList<>(Arrays.asList("Opened: " + name + "."));
        for(Item item : items){
            if(!PlayerInventory.items.contains(item)) PlayerInventory.items.add(item);
            lines.add("Collected: " + item.name + ".");
        }
        for(Clue clue : clues){
            if(!PlayerInventory.clues.contains(clue)) PlayerInventory.clues.add(clue);
            lines.addAll(clue.content.contents);
        }
        ImageIcon image = !items.isEmpty() ? items.get(0).image : !clues.isEmpty() ? clues.get(0).image : GamePanel.loadImage("signed-note.png");
        return new DialougeManager(image, name, lines, name);
    }
}
