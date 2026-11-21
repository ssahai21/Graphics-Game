import javax.swing.*;

public class Item{
    public String name = "Item";
    public ImageIcon image;
    public boolean locked;
    public boolean active;
    public Room unlocksRoom;
    public Coordinate location;
    public DialougeManager content;

    public static final int BOUNDINGBOX = 100;

    public Item(Coordinate location, ImageIcon image, DialougeManager content){
        this.location = location;
        this.image = image;
        this.content = content;
        active = true;
    }



}