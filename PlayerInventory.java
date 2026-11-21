import java.awt.image.ImageConsumer;
import java.util.*;
import javax.swing.*;

public class PlayerInventory {
    public static ImageIcon img = new ImageIcon("images/case-desk.png");

    public static boolean showing = false;
    public static Item heldItem;

    public static boolean isHolding(Item item){
        return item != null && heldItem == item && items.contains(item);
    }

    public static ArrayList<Item> items = new ArrayList<>();
    public static ArrayList<Clue> clues = new ArrayList<>();
}
