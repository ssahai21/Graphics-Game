import javax.swing.ImageIcon;

public class Clue {
    public String name = "Clue";
    public ImageIcon image;
    public Item requiredTool;
    public String lockedHint = "I need a tool to examine this.";
    public boolean locked;
    public DialougeManager content;
    public Coordinate location;

    public static final int BOUNDINGBOX = 100;

    public Clue(Coordinate location, ImageIcon image, DialougeManager content) {
        this.location = location;
        this.image = image;
        this.content = content;
    }
}
