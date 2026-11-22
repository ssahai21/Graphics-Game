import java.util.ArrayList;

public class PuzzleContainer {
    public final String name;
    public final Coordinate location;
    public final PuzzleManager puzzle;
    public final ArrayList<Item> items = new ArrayList<>();
    public final ArrayList<Clue> clues = new ArrayList<>();
    public boolean collected;

    public PuzzleContainer(String name, Coordinate location, PuzzleManager puzzle) {
        this.name = name;
        this.location = location;
        this.puzzle = puzzle;
    }

    public boolean contains(int x, int y) {
        return x >= location.x && x < location.x + 100 && y >= location.y && y < location.y + 100;
    }

    public boolean collect() {
        if (!puzzle.solved || collected) {
            return false;
        }
        collected = true;
        for (Item item : items) {
            if (!PlayerInventory.items.contains(item)) {
                PlayerInventory.items.add(item);
            }
        }
        for (Clue clue : clues) {
            if (!PlayerInventory.clues.contains(clue)) {
                PlayerInventory.clues.add(clue);
            }
        }
        return true;
    }
}
