import java.util.ArrayList;
import java.util.Arrays;
import javax.swing.ImageIcon;

/** The investigation's rooms, evidence, and suspects. Keep story setup here. */
public class RoomManager {
    public static Room room1, room2, library, laboratory, records, security;
    public static Room[] rooms;
    public static Item classroomKey, libraryKey, labKey, recordsKey, securityKey, screwdriver;
    public static Clue meetingNote, libraryLedger, solventReport, accessLog, cameraFootage;
    public static Suspect alex, maya, morgan;
    public static PuzzleContainer classroomBox, libraryBox, recordsSafe;

    static { reset(); }

    public static void reset(){
        room1 = room("Main Hall", "school-hallway.png", false);
        room2 = room("Classroom", "classroom.png", true);
        library = room("Library", "library.png", true);
        laboratory = room("Science Lab", "science-lab.png", true);
        records = room("Records Office", "records-office.png", true);
        security = room("Security Room", "security-room.png", true);
        rooms = new Room[]{room1, room2, library, laboratory, records, security};
        for(int i = 0; i < rooms.length; i++){
            if(i > 0) rooms[i].left = rooms[i - 1];
            if(i < rooms.length - 1) rooms[i].right = rooms[i + 1];
        }

        classroomKey = key("Classroom key", room2, 120, 245, "Room 204. The tag is nearly worn off.");
        room1.items.add(classroomKey);
        libraryKey = key("Library key", library, 0, 0, "Alex's copy of the library key.");
        labKey = key("Lab key", laboratory, 100, 245, "Science lab. Kept with the repair tools.");
        recordsKey = key("Records key", records, 0, 0, "The library's spare key to the records office.");
        securityKey = key("Security key", security, 100, 245, "Security room. Kept in the office safe.");
        screwdriver = item("Screwdriver", "screwdriver.png", 250, 245, "Flat head. Small enough for the vent screws.");

        meetingNote = clue("Alex's note", room2, 150, 250,
            "Mr. Woffinden - could you sign my trip form? I'll be in the library at 5:10. - Alex");
        libraryLedger = clue("Library sign-in sheet", library, 400, 245,
            "Alex Chen. In at 5:00, out at 5:30. Maya's initials are beside both times.",
            "There's a note underneath: Stayed to finish his trip form.");
        solventReport = clue("Bloodstained rag", laboratory, 160, 245,
            "A cleaning rag, pushed behind the vent cover. There's blood on one side.",
            "Someone has written MORGAN on the cloth tag.");
        solventReport.image = GamePanel.loadImage("maintenance-rag.png");
        solventReport.content.speakerImage = solventReport.image;
        solventReport.requiredTool = screwdriver;
        solventReport.locked = true;
        solventReport.lockedHint = "The cover's screwed down.";
        accessLog = clue("Door log and memo", records, 250, 245,
            "Room 204. Morgan's staff card was used at 5:12 and again at 5:18.",
            "A memo is clipped to the printout.",
            "Morgan - we're still missing six tablets. Meet me tomorrow morning. We need to sort this out. - Woffinden");
        cameraFootage = clue("Security recording", security, 250, 245,
            "5:15. Through the open classroom door, you can see Morgan and Mr. Woffinden arguing.",
            "Morgan picks up a trophy from the desk and swings. Mr. Woffinden falls out of view.",
            "Morgan leaves carrying a rag. He goes into the lab, then comes back without it.");
        cameraFootage.image = GamePanel.loadImage("camera-archive.png");
        cameraFootage.content.speakerImage = cameraFootage.image;

        classroomBox = new PuzzleContainer("Classroom lockbox", new Coordinate(150, 250),
            PuzzleManager.code("Classroom lockbox",
                "Enter the three-digit combination.",
                 "314"));
        classroomBox.clues.add(meetingNote);
        room2.clues.remove(meetingNote);
        room2.containers.add(classroomBox);

        libraryBox = new PuzzleContainer("Library repair chest", new Coordinate(180, 245),
            PuzzleManager.order("Library repair chest",
                "Press the three book tabs in the correct order.",
                new String[]{"HISTORY", "BOTANY", "CHEMISTRY"}, "120"));
        libraryBox.items.add(labKey);
        libraryBox.items.add(screwdriver);
        library.items.remove(labKey);
        library.items.remove(screwdriver);
        library.containers.add(libraryBox);

        recordsSafe = new PuzzleContainer("Records safe", new Coordinate(100, 245),
            PuzzleManager.lights("Records safe"));
        recordsSafe.items.add(securityKey);
        records.items.remove(securityKey);
        records.containers.add(recordsSafe);

        alex = new Suspect("Alex", GamePanel.loadImage("alex-suspect.png"), room1, new Coordinate(390, 200),
            Arrays.asList("They said I should wait here. Do you know when I can go home?",
                "I was in the library after class. You can ask Maya."), meetingNote,
            Arrays.asList("Yeah, that's mine. I needed him to sign my trip form.",
                "He never showed up. I thought he'd forgotten.",
                "Here, take the library key. I help Maya close up most days."));
        alex.reward = libraryKey;
        maya = new Suspect("Maya", GamePanel.loadImage("maya-suspect.png"), library, new Coordinate(520, 200),
            Arrays.asList("Alex was here until half past five. I signed him out myself.",
                "Morgan came through earlier. Said he had a vent to fix in the lab."), solventReport,
            Arrays.asList("That's from Morgan's kit. He puts his name on everything.",
                "He told me he'd been in records all evening. Why would that be in the lab?",
                "I've got a spare key to records. Here."));
        maya.reward = recordsKey;
        morgan = new Suspect("Morgan", GamePanel.loadImage("morgan-suspect.png"), records, new Coordinate(450, 200),
            Arrays.asList("I've been catching up on paperwork. Ask anyone, it piles up.",
                "No, I haven't seen Woffinden since lunch."), accessLog,
            Arrays.asList("My card? Right. I did stop by his room.",
                "He wanted to talk about some missing tablets. I told him I'd look into it.",
                "It was a two-minute conversation. That's all."));
        room1.suspects.add(alex);
        library.suspects.add(maya);
        records.suspects.add(morgan);
    }

    private static Room room(String name, String art, boolean locked){
        Room room = new Room(GamePanel.loadImage(art), new ArrayList<>(), null, null);
        room.name = name;
        room.locked = locked;
        return room;
    }

    private static Item item(String name, String art, int x, int y, String description){
        ImageIcon image = GamePanel.loadImage(art);
        Item item = new Item(new Coordinate(x, y), image, description);
        item.name = name;
        return item;
    }

    private static Item key(String name, Room target, int x, int y, String description){
        Item key = item(name, "classroom-key.png", x, y, description);
        key.unlocksRoom = target;
        return key;
    }

    private static Clue clue(String name, Room room, int x, int y, String... lines){
        ImageIcon image = GamePanel.loadImage("signed-note.png");
        Clue clue = new Clue(new Coordinate(x, y), image, dialogue(image, name, lines));
        clue.name = name;
        room.clues.add(clue);
        return clue;
    }

    private static DialougeManager dialogue(ImageIcon image, String name, String... lines){
        return new DialougeManager(image, name, new ArrayList<>(Arrays.asList(lines)), name);
    }
}
