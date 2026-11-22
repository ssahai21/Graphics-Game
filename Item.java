import javax.swing.ImageIcon;

public class Item{
    public String name = "Item";
    public ImageIcon image;
    public Room unlocksRoom;
    public Coordinate location;
    public String description;

    public static final int BOUNDINGBOX = 100;

    public Item(Coordinate location, ImageIcon image, String description) {
        this.location = location;
        this.image = image;
        this.description = description;
    }
}
