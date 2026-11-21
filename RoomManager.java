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

        classroomKey = key("Classroom key", room2, 120, 245,
            "The classroom key was left at reception.", "Mr. Woffinden was found dead there after school.");
        room1.items.add(classroomKey);
        libraryKey = key("Library key", library, 0, 0,
            "Alex's library key. His alibi should be recorded there.");
        labKey = key("Lab key", laboratory, 100, 245,
            "A science lab key, left at the library desk.");
        recordsKey = key("Records key", records, 0, 0,
            "Maya's spare records key. Check the staff access log.");
        securityKey = key("Security key", security, 100, 245,
            "The security key was filed beside the access log.", "The camera archive can settle the timeline.");
        screwdriver = item("Screwdriver", "screwdriver.png", 250, 245,
            "A flat-head screwdriver from the library repair kit.", "It could open the lab's loose ventilation panel.");
        library.items.add(labKey);
        library.items.add(screwdriver);
        records.items.add(securityKey);

        meetingNote = clue("Meeting note", room2, 150, 250,
            "Alex asked Mr. Woffinden to meet at 5:10.",
            "The note is signed by Alex. Ask him about it.");
        libraryLedger = clue("Library register", library, 400, 245,
            "Alex signed into supervised study from 5:00 to 5:30.",
            "The attack was at 5:15. Alex was in the library.");
        solventReport = clue("Maintenance rag", laboratory, 160, 245,
            "Inside the vent: a rag with brass polish and blood.",
            "Its tag says Maintenance. Check the staff access log.");
        solventReport.image = GamePanel.loadImage("maintenance-rag.png");
        solventReport.content.speakerImage = solventReport.image;
        solventReport.requiredTool = screwdriver;
        solventReport.locked = true;
        solventReport.lockedHint = "The vent is screwed shut. Find a screwdriver.";
        accessLog = clue("Staff access log", records, 250, 245,
            "Morgan entered the classroom at 5:12, leaving at 5:18.",
            "His equipment audit was scheduled for tomorrow.",
            "Question Morgan, then check the security archive.");
        cameraFootage = clue("Camera archive", security, 250, 245,
            "5:15: Morgan strikes Mr. Woffinden with a brass trophy.",
            "He hides a rag in the lab vent and returns to records.",
            "The victim had discovered Morgan's equipment theft.",
            "Review the case in your inventory to make an accusation.");

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
            Arrays.asList("I waited in the library after class.", "I did not meet Mr. Woffinden. Why do you ask?"), meetingNote,
            Arrays.asList("I wrote the note, but he never came.",
                "Maya supervised my study session at 5:15.", "Take my library key. The register proves I was there."));
        alex.reward = libraryKey;
        maya = new Suspect("Maya", GamePanel.loadImage("maya-suspect.png"), library, new Coordinate(520, 200),
            Arrays.asList("Alex was here during the attack. Check the register.",
                "Morgan was repairing a vent in the lab this afternoon."), solventReport,
            Arrays.asList("That rag belongs to Morgan's maintenance kit.",
                "He said he was filing records during the attack.",
                "Take this spare records key. Check his story."));
        maya.reward = recordsKey;
        morgan = new Suspect("Morgan", GamePanel.loadImage("morgan-suspect.png"), records, new Coordinate(450, 200),
            Arrays.asList("I was filing records all evening.", "I never went near the classroom."), accessLog,
            Arrays.asList("The log says I was there? I was only cleaning.",
                "I had to speak to him about the equipment audit.",
                "There is nothing else you need to know."));
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

    private static Item item(String name, String art, int x, int y, String... lines){
        ImageIcon image = GamePanel.loadImage(art);
        Item item = new Item(new Coordinate(x, y), image, dialogue(image, name, lines));
        item.name = name;
        return item;
    }

    private static Item key(String name, Room target, int x, int y, String... lines){
        Item key = item(name, "classroom-key.png", x, y, lines);
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
