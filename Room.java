import java.util.*;
import javax.swing.ImageIcon;

public class Room{
    public String name = "Room";
    public ImageIcon image;
    public ArrayList<Item> items = new ArrayList<>();
    public ArrayList<Clue> clues = new ArrayList<>();   
    public ArrayList<Suspect> suspects = new ArrayList<>();
    public ArrayList<PuzzleContainer> containers = new ArrayList<>();
    public Room left;
    public Room right;
    public boolean locked;
    
    public Room(ImageIcon image, ArrayList<Item> items, Room left, Room right){
        this.image = image;
        this.items = items;
        this.left = left;
        this.right = right;
    }

    public boolean tryUnlock(java.util.List<Item> inventory){
        if(!locked) return true;
        Item key = PlayerInventory.heldItem;
        if(key != null && inventory.contains(key) && key.unlocksRoom == this){
            locked = false;
            return true;
        }
        return false;
    }

}
