import javax.swing.ImageIcon;
import java.util.*;

public class Room{
    public ImageIcon image;
    public ArrayList<Item> items = new ArrayList<>();
    public ArrayList<Clue> clues = new ArrayList<>();   
    public Room left;
    public Room right;
    public boolean locked;
    
    public Room(ImageIcon image, ArrayList<Item> items, Room left, Room right){
        this.image = image;
        this.items = items;
        this.left = left;
        this.right = right;
    }

}