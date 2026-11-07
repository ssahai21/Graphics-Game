import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.ImageIcon;

public class RoomManager {
    
    public static Room room1 = new Room(new ImageIcon("images/room1.jpg"), new ArrayList<>(Arrays.asList(new Item(new Coordinate(50,50), new ImageIcon("images/key1.png"), new DialougeManager(new ImageIcon("images/key1.png"), "Key", new ArrayList<>(Arrays.asList("I am a key!", "I open a door!")), "Opens Left Room")))), null, null);
    public static Room room2 = new Room(new ImageIcon("images/room2.jpg"), new ArrayList<>(Arrays.asList(new Item(new Coordinate(50,50), new ImageIcon("images/key1.png"), new DialougeManager(new ImageIcon("images/key1.png"), "Key", new ArrayList<>(Arrays.asList("I am a key!", "I open a door!")), "Opnes Right room")))), null, null);
    
    public static Room[][] rooms;

    static {
        room1.right = room2;
        room2.left = room1;
        rooms = new Room[][]{{room1, room2}, {room2, null}};
    }
}
