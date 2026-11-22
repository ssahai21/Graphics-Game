import java.lang.reflect.Method;

/** Run with -Djava.awt.headless=true -Ddrawingpanel.headless=true. */
public class ProgressionTest {
    private static int checks;

    public static void main(String[] args) throws Exception {
        GameManager.handleClick(310, 285);
        check(GameManager.storyScene == StoryScene.OPENING, "begin case opens the introduction");
        GameManager.handleClick(170, 295);
        check(PlayerInventory.items.isEmpty(), "opening blocks clicks into the hall");
        for (int i = 0; i < StoryScene.OPENING.frames.length; i++) {
            GameManager.handleClick(530, 395);
        }
        check(GameManager.storyScene == null && !GameManager.start, "opening leads into investigation");
        check(RoomManager.rooms.length == 6, "six rooms");
        enter(true, RoomManager.hall); // No classroom key yet.
        check(RoomManager.classroom.locked, "missing key blocks travel");
        pickup(RoomManager.classroomKey);
        enter(true, RoomManager.classroom);
        collect(RoomManager.meetingNote);
        enter(false, RoomManager.hall);
        question(RoomManager.alex);
        check(PlayerInventory.items.contains(RoomManager.libraryKey), "Alex gives library key");
        int count = PlayerInventory.items.size();
        question(RoomManager.alex);
        check(PlayerInventory.items.size() == count, "reward given only once");
        enter(true, RoomManager.classroom);
        enter(true, RoomManager.library);
        question(RoomManager.maya);
        check(!PlayerInventory.items.contains(RoomManager.recordsKey), "Maya's reward needs evidence");
        collect(RoomManager.libraryLedger);
        // Inventory clicks must not open the chest underneath.
        PlayerInventory.showing = true;
        GameManager.handleClick(RoomManager.libraryBox.location.x + 50, RoomManager.libraryBox.location.y + 50);
        finishDialogue();
        check(!PuzzleManager.isOpen(), "inventory blocks room puzzle");
        PlayerInventory.showing = false;
        check(!Investigation.examine(RoomManager.solventReport), "sealed vent requires screwdriver");
        pickup(RoomManager.labKey);
        check(PlayerInventory.items.contains(RoomManager.screwdriver), "repair chest gives both tools");
        enter(true, RoomManager.laboratory);
        check(!Investigation.examine(RoomManager.solventReport), "owned screwdriver must be held");
        equip(RoomManager.screwdriver);
        check(PlayerInventory.isHolding(RoomManager.screwdriver), "screwdriver equipped");
        collect(RoomManager.solventReport);
        check(PlayerInventory.items.contains(RoomManager.screwdriver), "tool retained");
        check(PlayerInventory.isHolding(RoomManager.screwdriver), "tool stays equipped after use");
        enter(false, RoomManager.library);
        question(RoomManager.maya);
        check(PlayerInventory.items.contains(RoomManager.recordsKey), "Maya gives records key");
        enter(true, RoomManager.laboratory);
        enter(true, RoomManager.records);
        collect(RoomManager.accessLog);
        pickup(RoomManager.securityKey);
        question(RoomManager.morgan);
        Investigation.accuse(RoomManager.morgan);
        check(!Investigation.caseSolved, "premature correct accusation blocked");
        enter(true, RoomManager.security);
        collect(RoomManager.cameraFootage);
        check(Investigation.readyToAccuse(), "complete investigation ready");
        Investigation.accuse(RoomManager.alex);
        check(!Investigation.caseSolved, "wrong accusation allows retry");
        PlayerInventory.showing = true;
        GameManager.handleClick(500, 30); // Review case.
        check(GameManager.reviewingCase, "case review reachable");
        GameManager.handleClick(500, 320); // Accuse Morgan.
        check(Investigation.caseSolved, "correct accusation closes case");
        check(GameManager.storyScene == StoryScene.ENDING, "correct accusation opens the ending");
        for (int i = 0; i < StoryScene.ENDING.frames.length; i++) {
            GameManager.handleClick(530, 395);
        }
        check(GameManager.storyScene == null && GameManager.start, "ending returns home");
        check(!GameManager.reviewingCase && !PlayerInventory.showing, "ending closes the case review");
        GameManager.handleClick(380, 285); // Start another case from the home button's right edge.
        check(GameManager.storyScene == StoryScene.OPENING, "restart replays introduction");
        for (int i = 0; i < StoryScene.OPENING.frames.length; i++) {
            GameManager.handleClick(530, 395);
        }
        check(GameManager.storyScene == null, "restarted intro completes");
        finishDialogue();
        check(!Investigation.caseSolved && PlayerInventory.items.isEmpty() && PlayerInventory.clues.isEmpty(), "new case clears progress");
        check(RoomManager.classroom.locked && !RoomManager.alex.rewardGiven, "new case restores locks and rewards");
        check(GameManager.currRoom == RoomManager.hall, "restart returns to hall");
        check(PlayerInventory.heldItem == null, "new case clears held item");
        check(!RoomManager.classroomBox.puzzle.solved && !RoomManager.libraryBox.collected && !RoomManager.recordsSafe.puzzle.solved,
            "new case resets puzzle state");
        for (Room room : RoomManager.rooms) {
            check(room.image.getIconWidth() > 0, "room artwork loads");
        }
        System.out.println("Passed " + checks + " progression checks.");
        System.exit(0);
    }

    private static void enter(boolean right, Room expected) throws Exception {
        Room destination;
        if (right) {
            destination = GameManager.currRoom.right;
        }
        else {
            destination = GameManager.currRoom.left;
        }
        if (destination != null && destination.locked) {
            for (Item item : PlayerInventory.items) {
                if (item.unlocksRoom == destination) {
                    check(!destination.tryUnlock(PlayerInventory.items), "owning a key alone does not unlock " + destination.name);
                    equip(item);
                    break;
                }
            }
        }
        if (right) {
            GameManager.player.position = 480;
        }
        else {
            GameManager.player.position = 10;
        }
        GameManager.roomCooldown = 0;
        Method exit = GameManager.class.getDeclaredMethod("tryExit");
        exit.setAccessible(true);
        exit.invoke(null);
        check(GameManager.currRoom == expected, "expected room: " + expected.name);
        check(!GamePanel.displayingDialouge, "door feedback does not open dialogue");
        finishDialogue();
    }

    private static void equip(Item item) {
        PlayerInventory.showing = true;
        GameManager.reviewingCase = false;
        GamePanel.updateInventoryLayout();
        Coordinate position = GamePanel.iItems.get(item);
        GameManager.handleClick(position.x + 25, position.y + 25);
        check(PlayerInventory.isHolding(item), "inventory click equips " + item.name);
        GameManager.handleClick(position.x + 25, position.y + 25);
        check(PlayerInventory.heldItem == null, "second click puts item away");
        GameManager.handleClick(position.x + 25, position.y + 25);
        GameManager.handleClick(GamePanel.HAND_SLOT.x + 10, GamePanel.HAND_SLOT.y + 10);
        check(PlayerInventory.heldItem == null, "hand slot clears held item");
        GameManager.handleClick(position.x + 25, position.y + 25);
        PlayerInventory.showing = false;
    }

    private static void pickup(Item item) {
        PuzzleContainer box = null;
        for (PuzzleContainer candidate : GameManager.currRoom.containers) {
            if (candidate.items.contains(item)) {
                box = candidate;
            }
        }
        if (box != null) {
            solve(box);
        }
        else {
            GameManager.handleClick(item.location.x + 50, item.location.y + 50);
        }
        check(PlayerInventory.items.contains(item), "picked up " + item.name);
        check(!GamePanel.displayingDialouge, "item pickup does not open dialogue");
        finishDialogue();
    }

    private static void collect(Clue clue) {
        PuzzleContainer box = null;
        for (PuzzleContainer candidate : GameManager.currRoom.containers) {
            if (candidate.clues.contains(clue)) {
                box = candidate;
            }
        }
        if (box != null) {
            solve(box);
        }
        else {
            GameManager.handleClick(clue.location.x + 50, clue.location.y + 50);
        }
        check(Investigation.has(clue), "collected " + clue.name);
        check(!GameManager.currRoom.clues.contains(clue), "clue removed from room");
        finishDialogue();
    }

    private static void solve(PuzzleContainer box) {
        check(!box.collect(), "locked contents inaccessible");
        GameManager.handleClick(box.location.x + 50, box.location.y + 50);
        check(PuzzleManager.activeContainer == box, "container opens puzzle");
        if (box.puzzle.type == PuzzleManager.CODE) {
            for (int digit : new int[]{3, 1, 4}) {
                GameManager.handleClick(130 + (digit % 5) * 90, 215 + (digit / 5) * 48);
            }
            GameManager.handleClick(400, 315);
        }
        else if (box.puzzle.type == PuzzleManager.ORDER) {
            for (int index : new int[]{1, 2, 0}) {
                GameManager.handleClick(120 + index * 170, 240);
            }
        }
        else {
            int solution = PuzzleTest.lightSolution(box.puzzle);
            check(solution >= 0, "fixed lights puzzle is solvable");
            for (int index = 0; index < 9; index++) {
                if ((solution & (1 << index)) != 0) {
                    GameManager.handleClick(265 + (index % 3) * 65, 160 + (index / 3) * 65);
                }
            }
        }
        check(box.puzzle.solved, "puzzle solved through mouse controls");
        GameManager.handleClick(300, 350);
        check(box.collected && !PuzzleManager.isOpen(), "contents claimed");
        check(!GamePanel.displayingDialouge, "container rewards do not open dialogue");
        int count = PlayerInventory.items.size() + PlayerInventory.clues.size();
        check(!box.collect() && count == PlayerInventory.items.size() + PlayerInventory.clues.size(), "contents claimed only once");
    }

    private static void question(Suspect suspect) {
        GameManager.handleClick(suspect.position.x + 30, suspect.position.y + 30);
        check(GamePanel.displayingDialouge && GamePanel.currDialouge != null, "questioned " + suspect.name);
        DialougeManager conversation = GamePanel.currDialouge;
        int lineCount = conversation.contents.size();
        finishDialogue();
        check(suspect.question(PlayerInventory.clues) == conversation, "suspect reuses conversation");
        check(conversation.contents.size() == lineCount, "repeated conversation keeps the same lines");
    }

    private static void finishDialogue() {
        int limit = 20;
        while (GamePanel.displayingDialouge && limit-- > 0) {
            GameManager.handleClick(650, 400);
        }
        check(!GamePanel.displayingDialouge, "dialogue finishes");
    }

    private static void check(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
        checks++;
    }
}
