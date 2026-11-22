import drawapp.DrawingPanel;
import javax.swing.ImageIcon;
import javax.swing.Timer;

public class GameManager {
    public static Player player = new Player(new ImageIcon("images/detective.png"));

    public static DrawingPanel panel = new DrawingPanel(679, 452);

    public static Room currRoom = RoomManager.rooms[0];

    public static int mouseX;
    public static int mouseY;

    public static double roomCooldown = 128;

    public static boolean start = true;
    public static boolean reviewingCase = false;
    private static boolean introducedCase = false;
    static StoryScene storyScene;
    static int storyFrame;

    public static String notice = "";
    public static long noticeUntil;
    public static void main(String[] args) {
        panel.setFitToWindow(true);

        panel.onMouseClick(GameManager::handleClick);

        panel.onMouseMove((x,y) -> {
            mouseX = x;
            mouseY = y;
            updateHover();
        });

        double speed = 300.0;
        double deltaTime = 1.0 / 60.0;

        boolean[] keys = new boolean[2];
        boolean[] interactKeyDown = new boolean[1];
        boolean[] inventoryKeyDown = new boolean[1];
        panel.onKeyDown(key -> {
            if (storyScene != null) {
                return;
            }
            if (PuzzleManager.isOpen()) {
                PuzzleManager puzzle = PuzzleManager.activeContainer.puzzle;
                if (key == 27) {
                    PuzzleManager.close();
                }
                else if (key >= '0' && key <= '9') {
                    puzzle.digit(key);
                }
                else if (key == '\b' || key == 127) {
                    puzzle.backspace();
                }
                else if (key == '\n' || key == '\r') {
                    puzzle.submit();
                }
                return;
            }
            if (key == 'e') {
                if (!interactKeyDown[0] && !start && !PlayerInventory.showing && !GamePanel.displayingDialouge) {
                    tryExit();
                }
                interactKeyDown[0] = true;
                return;
            }
            if (key == 'i') {
                if (!inventoryKeyDown[0] && !start && !GamePanel.displayingDialouge) {
                    PlayerInventory.showing = !PlayerInventory.showing;
                    reviewingCase = false;
                }
                inventoryKeyDown[0] = true;
                return;
            }
            if (!start && !PlayerInventory.showing && !GamePanel.displayingDialouge) {
                if (key == 'a') {
                    keys[0] = true;
                }
                if (key == 'd') {
                    keys[1] = true;
                }
            }
        });

        panel.onKeyUp(key -> {
            if (key == 'i') {
                inventoryKeyDown[0] = false;
            }
            if (key == 'a') {
                keys[0] = false;
            }
            if (key == 'd') {
                keys[1] = false;
            }
            if (key == 'e') {
                interactKeyDown[0] = false;
            }
        });

        Timer timer = new Timer(16, e -> {
            GamePanel.nearExit = false;
            if (start || storyScene != null || PlayerInventory.showing || GamePanel.displayingDialouge || PuzzleManager.isOpen()) {
                java.util.Arrays.fill(keys, false);
            }
            else {
                roomCooldown -= 16;
                if (keys[0]) {
                    player.position -= speed*deltaTime;
                }
                if (keys[1]) {
                    player.position += speed*deltaTime;
                }
                player.position = Math.max(0, Math.min(500, player.position));
                GamePanel.nearExit = nearbyExit() != null;
            }
            updateHover();
            GamePanel.drawGame(panel, currRoom, (int) player.position, mouseX, mouseY);
        });

        timer.start();
    }

    public static void handleClick(int x, int y) {
        mouseX = x;
        mouseY = y;
        if (storyScene != null) {
            if (contains(x, y, 457, 378, 165, 36)) {
                if (++storyFrame >= storyScene.frames.length) {
                    if (storyScene == StoryScene.ENDING) {
                        start = true;
                        reviewingCase = false;
                        PlayerInventory.showing = false;
                        notice = "";
                        noticeUntil = 0;
                    }
                    storyScene = null;
                }
            }
            return;
        }
        if (start) {
            if (contains(x, y, 290, 260, 100, 50)) {
                start = false;
                if (Investigation.caseSolved) {
                    restartCase();
                }
                else if (!introducedCase) {
                    introducedCase = true;
                    beginStory(StoryScene.OPENING);
                }
            }
            return;
        }
        if (GamePanel.displayingDialouge) {
            GamePanel.drawDialouge(panel, GamePanel.currDialouge);
            return;
        }
        if (PuzzleManager.isOpen()) {
            handlePuzzleClick(x, y);
            return;
        }
        if (!reviewingCase && GamePanel.HAND_SLOT.contains(x, y)) {
            PlayerInventory.heldItem = null;
            return;
        }
        if (PlayerInventory.showing) {
            if (reviewingCase) {
                if (contains(x, y, 80, 365, 150, 35)) {
                    reviewingCase = false;
                }
                else if (contains(x, y, 450, 365, 150, 35)) {
                    restartCase();
                }
                else {
                    Suspect[] suspects = {RoomManager.alex, RoomManager.maya, RoomManager.morgan};
                    for (int i = 0; i < suspects.length; i++) {
                        if (contains(x, y, 80 + i * 175, 300, 160, 40)) {
                            boolean alreadySolved = Investigation.caseSolved;
                            String result = Investigation.accuse(suspects[i]);
                            if (!alreadySolved && Investigation.caseSolved) {
                                beginStory(StoryScene.ENDING);
                            }
                            else {
                                showNotice(result);
                            }
                            return;
                        }
                    }
                }
                return;
            }
            if (contains(x, y, 450, 15, 160, 40)) {
                reviewingCase = true;
                return;
            }
            GamePanel.updateInventoryLayout();
            for (Item item : PlayerInventory.items) {
                Coordinate c = GamePanel.iItems.get(item);
                if (contains(x, y, c.x, c.y, 50, 50)) {
                    if (PlayerInventory.heldItem == item) {
                        PlayerInventory.heldItem = null;
                    }
                    else {
                        PlayerInventory.heldItem = item;
                    }
                    return;
                }
            }
            for (Clue clue : PlayerInventory.clues) {
                Coordinate c = GamePanel.cItems.get(clue);
                if (contains(x, y, c.x, c.y, 50, 50)) {
                    openDialouge(clue.content);
                    return;
                }
            }
            return;
        }
        if (contains(x, y, 10, 10, 50, 50)) {
            start = true;
            return;
        }
        for (PuzzleContainer container : currRoom.containers) {
            if (container.contains(x, y)) {
                if (container.collected) {
                    showNotice("Nothing left in there.");
                }
                else {
                    PuzzleManager.activeContainer = container;
                }
                return;
            }
        }
        for (Suspect suspect : currRoom.suspects) {
            if (suspect.location == currRoom && suspect.contains(x, y)) {
                openDialouge(Investigation.question(suspect));
                return;
            }
        }
        
        
        for (int index = 0; index < currRoom.items.size(); index++) {
            Item item = currRoom.items.get(index);
            if (contains(x, y, item.location.x, item.location.y, Item.BOUNDINGBOX, Item.BOUNDINGBOX)) {
                currRoom.items.remove(index);
                PlayerInventory.items.add(item);
                showNotice("Picked up " + item.name.toLowerCase() + ".");
                return;
            }
        }
        for (int index = 0; index < currRoom.clues.size(); index++) {
            Clue clue = currRoom.clues.get(index);
            if (contains(x, y, clue.location.x, clue.location.y, Clue.BOUNDINGBOX, Clue.BOUNDINGBOX)) {
                if (!Investigation.examine(clue)) {
                    if (PlayerInventory.items.contains(clue.requiredTool)) {
                        showNotice("I need to hold the " + clue.requiredTool.name.toLowerCase() + " first.");
                    }
                    else {
                        showNotice(clue.lockedHint);
                    }
                    return;
                }
                currRoom.clues.remove(index);
                PlayerInventory.clues.add(clue);
                openDialouge(clue.content);
                return;
            }
        }
    }

    private static void handlePuzzleClick(int x, int y) {
        PuzzleContainer container = PuzzleManager.activeContainer;
        PuzzleManager puzzle = container.puzzle;
        if (contains(x, y, 549, 35, 75, 30)) {
            PuzzleManager.close();
            return;
        }
        if (puzzle.solved) {
            if (contains(x, y, 230, 330, 220, 42)) {
                if (container.collect()) {
                    java.util.ArrayList<String> names = new java.util.ArrayList<>();
                    for (Item item : container.items) {
                        names.add(item.name);
                    }
                    for (Clue clue : container.clues) {
                        names.add(clue.name);
                    }
                    showNotice("Added to inventory - " + String.join(", ", names) + ".");
                }
                PuzzleManager.close();
            }
            return;
        }
        if (puzzle.type == PuzzleManager.CODE) {
            for (int i = 0; i < 10; i++) {
                if (contains(x, y, 115 + (i % 5) * 90, 200 + (i / 5) * 48, 75, 38)) {
                    puzzle.digit((char) ('0' + i));
                    return;
                }
            }
            if (contains(x, y, 205, 300, 120, 35)) {
                puzzle.backspace();
            }
            else if (contains(x, y, 350, 300, 120, 35)) {
                puzzle.submit();
            }
        }
        else if (puzzle.type == PuzzleManager.ORDER) {
            for (int i = 0; i < puzzle.choices.length; i++) {
                if (contains(x, y, 95 + i * 170, 220, 150, 55)) {
                    puzzle.choose(i);
                }
            }
        }
        else {
            for (int i = 0; i < 9; i++) {
                if (contains(x, y, 245 + (i % 3) * 65, 140 + (i / 3) * 65, 55, 55)) {
                    puzzle.toggle(i);
                }
            }
        }
    }

    private static void restartCase() {
        Investigation.reset();
        notice = "";
        noticeUntil = 0;
        currRoom = RoomManager.rooms[0];
        player.position = 0;
        roomCooldown = 0;
        reviewingCase = false;
        GamePanel.displayingDialouge = false;
        GamePanel.currDialouge = null;
        beginStory(StoryScene.OPENING);
    }

    private static void beginStory(StoryScene scene) {
        storyScene = scene;
        storyFrame = 0;
    }

    private static Room nearbyExit() {
        if (player.position < 50) {
            return currRoom.left;
        }
        if (player.position > 450) {
            return currRoom.right;
        }
        return null;
    }

    private static void tryExit() {
        Room destination = nearbyExit();
        if (destination == null || roomCooldown > 0) {
            return;
        }
        boolean wasLocked = destination.locked;
        if (!destination.tryUnlock(PlayerInventory.items)) {
            showNotice("Locked. I need the " + destination.name.toLowerCase() + " key in my hand.");
            return;
        }
        boolean goingLeft = player.position < 50;
        currRoom = destination;
        if (goingLeft) {
            player.position = 500;
        }
        else {
            player.position = 10;
        }
        roomCooldown = 128;
        GamePanel.nearExit = false;
        if (wasLocked) {
            showNotice("Unlocked.");
        }
    }

    private static void showNotice(String text) {
        notice = text;
        noticeUntil = System.currentTimeMillis() + 4500;
    }

    private static boolean contains(int x, int y, int left, int top, int width, int height) {
        return x >= left && x < left + width && y >= top && y < top + height;
    }

    private static void openDialouge(DialougeManager dialogue) {
        if (dialogue == null) {
            return;
        }
        dialogue.reset();
        GamePanel.drawDialouge(panel, dialogue);
    }

    private static void updateHover() {
        GamePanel.isHovering = false;
        GamePanel.hoveredItem = null;
        if (start || storyScene != null || GamePanel.displayingDialouge || PuzzleManager.isOpen()) {
            return;
        }
        if (PlayerInventory.showing) {
            GamePanel.updateInventoryLayout();
            GamePanel.iItems.forEach((item, c) -> {
                if (contains(mouseX, mouseY, c.x, c.y, 50, 50)) {
                    GamePanel.hoveredItem = item.name + " - " + item.description;
                }
            });
            GamePanel.cItems.forEach((clue, c) -> {
                if (contains(mouseX, mouseY, c.x, c.y, 50, 50)) {
                    GamePanel.hoveredItem = clue.name;
                }
            });
            return;
        }
        if (currRoom == RoomManager.library && contains(mouseX, mouseY, 112, 151, 105, 51)) {
            GamePanel.isHovering = true;
        }
        for (PuzzleContainer container : currRoom.containers) {
            if (container.contains(mouseX, mouseY)) {
                GamePanel.isHovering = true;
            }
        }
        for (Suspect suspect : currRoom.suspects) {
            if (suspect.location == currRoom && suspect.contains(mouseX, mouseY)) {
                GamePanel.isHovering = true;
            }
        }
        for (Item item : currRoom.items) {
            if (contains(mouseX, mouseY, item.location.x, item.location.y, Item.BOUNDINGBOX, Item.BOUNDINGBOX)) {
                GamePanel.isHovering = true;
            }
        }
        for (Clue clue : currRoom.clues) {
            if (contains(mouseX, mouseY, clue.location.x, clue.location.y, Clue.BOUNDINGBOX, Clue.BOUNDINGBOX)) {
                GamePanel.isHovering = true;
            }
        }
    }
}
