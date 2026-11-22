import drawapp.DrawingPanel;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import javax.swing.ImageIcon;

public class GamePanel {
    private static final Color PANEL = new Color(41, 53, 73);
    private static final Color TEXT = new Color(255, 239, 212);
    private static final Color ACCENT = new Color(255, 203, 118);

    public static final Rectangle HAND_SLOT = new Rectangle(605, 365, 64, 64);

    public static boolean displayingDialouge = false;

    public static DialougeManager currDialouge;

    public static boolean isHovering = false;

    public static boolean nearExit = false;

    public static String hoveredItem = null;

    public static Map<Item, Coordinate> iItems = new HashMap<>();
    public static Map<Clue, Coordinate> cItems = new HashMap<>();

    private static final Map<String, ImageIcon> imageCache = new HashMap<>();

    public static ImageIcon loadImage(String name) {
        return imageCache.computeIfAbsent(name, n -> {
            return new ImageIcon("images/" + n);
        });
    }

    private static void drawImage(Graphics g, ImageIcon image, int x, int y, int width, int height) {
        int iw = image.getIconWidth();
        int ih = image.getIconHeight();
        if (iw <= 0 || ih <= 0) {
            return;
        }
        double scale = Math.min((double) width / iw, (double) height / ih);
        int w = Math.max(1, (int) Math.round(iw * scale));
        int h = Math.max(1, (int) Math.round(ih * scale));
        g.drawImage(image.getImage(), x + (width - w) / 2, y + (height - h) / 2, w, h, null);
    }

    public static void drawGame(DrawingPanel p, Room room, double playerPos, int x, int y) {
        p.clear();
        Graphics g = p.getGraphics();
        if (GameManager.storyScene != null) {
            drawStory(g, GameManager.storyScene, GameManager.storyFrame);
            return;
        }
        if (GameManager.start) {
            drawTitleScreen(g);
        }
        else if (PlayerInventory.showing) {
            if (GameManager.reviewingCase) {
                drawCaseReview(g);
            }
            else {
                drawInventory(g);
            }
            if (displayingDialouge) {
                dialougeDrawer(g, currDialouge);
            }
            else if (!GameManager.reviewingCase && hoveredItem!=null) {
                drawInventoryHover(g, x, y);
            }
        }
        else {
            drawRoom(g, room);
            drawRoomPuzzleClues(g, room);
            drawItem(g, room);
            for (Clue clue : room.clues) {
                if (clue.locked) {
                    g.setColor(new Color(113, 149, 181));
                    g.fillRect(clue.location.x + 20, clue.location.y + 20, 60, 50);
                    g.setColor(PANEL);
                    g.drawRect(clue.location.x + 20, clue.location.y + 20, 60, 50);
                    for (int line = 0; line < 4; line++) {
                        g.drawLine(clue.location.x + 28, clue.location.y + 30 + line * 10, clue.location.x + 72, clue.location.y + 30 + line * 10);
                    }
                }
                else {
                    drawImage(g, clue.image, clue.location.x + 20, clue.location.y + 20, Clue.BOUNDINGBOX - 40, Clue.BOUNDINGBOX - 40);
                }
            }
            drawContainers(g, room);
            drawPlayer(g, playerPos);
            drawSuspects(g, room);
            if (displayingDialouge) {
                dialougeDrawer(g, currDialouge);
            }
            drawMap(g);
            if (isHovering && !displayingDialouge) {
                drawInteract(g, x, y);
            }
            if (nearExit && !displayingDialouge) {
                Room destination;
                if (playerPos < 50) {
                    destination = room.left;
                }
                else {
                    destination = room.right;
                }
                drawExit(g, (int) playerPos, destination);
            }
        }
        if (!GameManager.start && !GameManager.reviewingCase && !displayingDialouge && !PuzzleManager.isOpen()) {
            drawHeldItem(g);
        }
        if (!GameManager.start && !displayingDialouge && !PuzzleManager.isOpen()
            && System.currentTimeMillis() < GameManager.noticeUntil) {
            drawNotice(g);
        }
        if (PuzzleManager.isOpen()) {
            drawPuzzle(g);
        }
    }

    private static void drawNotice(Graphics g) {
        g.setColor(PANEL);
        g.fillRoundRect(15, 404, 550, 44, 8, 8);
        g.setColor(TEXT);
        g.setFont(new Font("SansSerif", Font.PLAIN, 12));
        drawWrappedText(g, GameManager.notice, 25, 422, 530);
    }

    private static void drawContainers(Graphics g, Room room) {
        for (PuzzleContainer container : room.containers) {
            int x = container.location.x + 10;
            int y = container.location.y + 20;
            boolean safe = container.puzzle.type == PuzzleManager.LIGHTS;
            if (safe) {
                g.setColor(new Color(113, 149, 181));
            }
            else {
                g.setColor(new Color(169, 119, 97));
            }
            g.fillRoundRect(x, y, 80, 60, 6, 6);
            g.setColor(ACCENT);
            g.drawRoundRect(x, y, 80, 60, 6, 6);
            g.drawLine(x, y + 20, x + 80, y + 20);
            g.fillRect(x + 10, y, 6, 60);
            g.fillRect(x + 64, y, 6, 60);
            if (!container.puzzle.solved) {
                g.fillRect(x + 35, y + 15, 12, 18);
            }
            else {
                g.setColor(TEXT);
                g.setFont(new Font("SansSerif", Font.BOLD, 11));
                String label = "OPEN";
                if (container.collected) {
                    label = "EMPTY";
                }
                g.drawString(label, x + 24, y + 43);
            }
            if (container == RoomManager.classroomBox) {
                g.setColor(TEXT);
                drawHouseSymbol(g, 0, x + 24, y + 47);
                drawHouseSymbol(g, 1, x + 40, y + 47);
                drawHouseSymbol(g, 2, x + 56, y + 47);
            }
        }
    }

    private static void drawRoomPuzzleClues(Graphics g, Room room) {
        if (room == RoomManager.classroom) {
            // The board groups are shuffled. The box engraving gives their order.
            g.setColor(new Color(88, 113, 104));
            g.fillRect(161, 111, 312, 96);
            g.setColor(TEXT);
            g.setFont(new Font("SansSerif", Font.BOLD, 12));
            g.drawString("HOUSE POINTS", 176, 129);
            int[] symbols = {2, 0, 1};
            int[] points = {4, 3, 1};
            for (int column = 0; column < symbols.length; column++) {
                int center = 206 + column * 97;
                drawHouseSymbol(g, symbols[column], center, 151);
                for (int mark = 0; mark < points[column]; mark++) {
                    g.drawLine(center - 16 + mark * 9, 171, center - 16 + mark * 9, 191);
                }
            }
        }
        else if (room == RoomManager.library) {
            // Three labeled books stand apart on the upper shelf.
            g.setColor(new Color(105, 79, 82));
            g.fillRect(112, 158, 105, 43);
            String[] books = {"BOTANY", "CHEMISTRY", "HISTORY"};
            Color[] colors = {new Color(131, 177, 170), new Color(113, 149, 181), new Color(166, 109, 112)};
            for (int i = 0; i < books.length; i++) {
                int x = 114 + i * 35;
                g.setColor(colors[i]);
                g.fillRoundRect(x, 158, 25, 42, 4, 4);
                g.setColor(PANEL);
                g.drawRoundRect(x, 158, 25, 42, 4, 4);
                Graphics2D spine = (Graphics2D) g.create();
                spine.rotate(-Math.PI / 2, x + 17, 196);
                spine.setFont(new Font("SansSerif", Font.BOLD, 7));
                spine.drawString(books[i], x + 17, 196);
                spine.dispose();
            }
            if (!displayingDialouge && !PuzzleManager.isOpen()
                && GameManager.mouseX >= 112 && GameManager.mouseX < 217
                && GameManager.mouseY >= 151 && GameManager.mouseY < 202) {
                g.setColor(PANEL);
                g.fillRoundRect(245, 118, 310, 85, 10, 10);
                for (int i = 0; i < books.length; i++) {
                    int x = 255 + i * 99;
                    g.setColor(colors[i]);
                    g.fillRoundRect(x, 128, 91, 65, 5, 5);
                    g.setColor(PANEL);
                    g.setFont(new Font("SansSerif", Font.BOLD, 11));
                    g.drawString(books[i], x + (91 - g.getFontMetrics().stringWidth(books[i])) / 2, 164);
                }
            }
        }
    }

    private static void drawHouseSymbol(Graphics g, int symbol, int x, int y) {
        if (symbol == 0) {
            g.drawPolygon(new int[]{x, x - 5, x + 5}, new int[]{y - 5, y + 4, y + 4}, 3);
        }
        else if (symbol == 1) {
            g.drawOval(x - 5, y - 5, 10, 10);
        }
        else {
            g.drawRect(x - 5, y - 5, 10, 10);
        }
    }

    private static void drawPuzzle(Graphics g) {
        PuzzleContainer container = PuzzleManager.activeContainer;
        PuzzleManager puzzle = container.puzzle;
        g.setColor(new Color(0, 0, 0, 190));
        g.fillRect(0, 0, 679, 452);
        g.setColor(PANEL);
        g.fillRoundRect(40, 20, 599, 410, 20, 20);
        g.setColor(TEXT);
        g.setFont(new Font("SansSerif", Font.BOLD, 25));
        g.drawString(puzzle.title, 70, 60);
        button(g, 549, 35, 75, 30, "CLOSE");
        g.setColor(TEXT);
        g.setFont(new Font("SansSerif", Font.PLAIN, 12));
        if (puzzle.solved) {
            g.setFont(new Font("SansSerif", Font.BOLD, 30));
            g.drawString("UNLOCKED", 170, 170);
            g.setFont(new Font("SansSerif", Font.PLAIN, 14));
            int y = 215;
            for (Item item : container.items) {
                g.drawString("Inside: " + item.name, 160, y);
                y += 25;
            }
            for (Clue clue : container.clues) {
                g.drawString("Inside: " + clue.name, 160, y);
                y += 25;
            }
            button(g, 230, 330, 220, 42, "COLLECT CONTENTS");
            return;
        }
        drawWrappedText(g, puzzle.instructions, 70, 90, 535);
        if (puzzle.type == PuzzleManager.CODE) {
            g.setFont(new Font("Monospaced", Font.BOLD, 30));
            String code = puzzle.entry();
            if (code.isEmpty()) {
                code = "_ _ _";
            }
            g.drawString(code, 285, 175);
            for (int i = 0; i < 10; i++) {
                button(g, 115 + (i % 5) * 90, 200 + (i / 5) * 48, 75, 38, "" + i);
            }
            button(g, 205, 300, 120, 35, "DELETE");
            button(g, 350, 300, 120, 35, "UNLOCK");
        }
        else if (puzzle.type == PuzzleManager.ORDER) {
            String order = puzzle.entry();
            if (order.isEmpty()) {
                order = "Choose the first book tab.";
            }
            g.drawString(order, 110, 185);
            for (int i = 0; i < puzzle.choices.length; i++) {
                button(g, 95 + i * 170, 220, 150, 55, puzzle.choices[i]);
            }
        }
        else {
            for (int i = 0; i < 9; i++) {
                int x = 245 + (i % 3) * 65;
                int y = 140 + (i / 3) * 65;
                if (puzzle.lightAt(i)) {
                    g.setColor(ACCENT);
                }
                else {
                    g.setColor(new Color(79, 98, 124));
                }
                g.fillRoundRect(x, y, 55, 55, 8, 8);
                if (puzzle.lightAt(i)) {
                    g.setColor(PANEL);
                    g.drawString("ON", x + 15, y + 32);
                }
                else {
                    g.setColor(TEXT);
                    g.drawString("OFF", x + 15, y + 32);
                }
            }
        }
        g.setFont(new Font("SansSerif", Font.PLAIN, 11));
        g.setColor(TEXT);
        g.drawString(puzzle.message, 70, 345);
    }

    private static void drawWrappedText(Graphics g, String text, int x, int y, int width) {
        for (String line : wrapText(g, text, width)) {
            g.drawString(line, x, y);
            y += 17;
        }
    }

    private static java.util.List<String> wrapText(Graphics g, String text, int width) {
        java.util.ArrayList<String> lines = new java.util.ArrayList<>();
        for (String paragraph : text.split("\n", -1)) {
            String line = "";
            for (String word : paragraph.split(" ")) {
                String candidate = word;
                if (!line.isEmpty()) {
                    candidate = line + " " + word;
                }
                if (!line.isEmpty() && g.getFontMetrics().stringWidth(candidate) > width) {
                    lines.add(line);
                    line = word;
                }
                else {
                    line = candidate;
                }
            }
            lines.add(line);
        }
        return lines;
    }

    private static void drawItem(Graphics g, Room room) {
        for (Item i : room.items) {
            drawImage(g, i.image, i.location.x + 20, i.location.y + 20, Item.BOUNDINGBOX - 40, Item.BOUNDINGBOX - 40);
        }
    }

    private static void drawSuspects(Graphics g, Room room) {
        for (Suspect suspect : room.suspects) {
            if (suspect.location != room) {
                continue;
            }
            int x = suspect.position.x;
            int y = suspect.position.y;
            drawImage(g, suspect.image, x, y, Suspect.WIDTH, Suspect.HEIGHT);
            g.setFont(new Font("SansSerif", Font.BOLD, 12));
            String label = suspect.name + " - click to question";
            g.setColor(PANEL);
            int labelWidth = g.getFontMetrics().stringWidth(label) + 10;
            int labelX = Math.min(x - 10, 669 - labelWidth);
            g.fillRoundRect(labelX, y - 25, labelWidth, 22, 8, 8);
            g.setColor(TEXT);
            g.drawString(label, labelX + 5, y - 10);
        }
    }

    private static void drawPlayer(Graphics g, double playerPos) {
        drawImage(g, GameManager.player.image, (int) playerPos, 200, 120, 180);
    }

    private static void drawRoom(Graphics g, Room room) {
        drawImage(g, room.image, 0, 0, 679, 452);
        g.setColor(new Color(41, 53, 73, 238));
        g.fillRoundRect(10, 10, 50, 50, 12, 12);
        g.setColor(TEXT);
        g.setFont(new Font("SansSerif", Font.BOLD, 11));
        g.drawString("MENU", 17, 39);
        g.setColor(new Color(41, 53, 73, 238));
        g.fillRoundRect(70, 10, 210, 35, 10, 10);
        g.setFont(new Font("SansSerif", Font.BOLD, 20));
        g.setColor(TEXT);
        g.drawString(room.name, 82, 34);
    }

    public static void drawDialouge(DrawingPanel p, DialougeManager d) {
        if (d == null || !d.advance()) {
            removeDialouge(p);
            return;
        }
        currDialouge = d;
        displayingDialouge = true;

        drawGame(p, GameManager.currRoom, GameManager.player.position, GameManager.mouseX, GameManager.mouseY);
    }

    public static void dialougeDrawer(Graphics g, DialougeManager d) {
        g.setColor(new Color(41, 53, 73, 245));
        g.fillRect(10, 300, 650, 120);

        g.setColor(new Color(69, 85, 110));
        g.fillRect(10,330,90,90);
        g.setColor(PANEL);
        g.drawRect(10,330,90,90);

        drawImage(g, d.speakerImage, 15, 335, 75, 75);

        g.setColor(TEXT);
        g.setFont(new Font("SansSerif", Font.BOLD, 14));

        g.drawString(d.speakerName, 30, 320);

        if (d.currLine != null) {
            g.setColor(TEXT);
            StringBuilder line = new StringBuilder();
            int y = 355;
            for (String word : d.currLine.split(" ")) {
                if (g.getFontMetrics().stringWidth(line + word) > 535) {
                    g.drawString(line.toString(), 110, y);
                    line.setLength(0);
                    y += 20;
                }
                line.append(word).append(' ');
            }
            g.drawString(line.toString(), 110, y);
            g.setFont(new Font("SansSerif", Font.PLAIN, 10));
            g.drawString("Click to continue", 555, 410);
        }
    }

    public static void removeDialouge(DrawingPanel p) {
        displayingDialouge = false;
        currDialouge = null;
        drawGame(p, GameManager.currRoom, GameManager.player.position, GameManager.mouseX, GameManager.mouseY);
    }

    private static void drawInteract(Graphics g, int x, int y) {
        g.setColor(TEXT);
        g.drawOval(x - 9, y - 9, 18, 18);
        g.drawLine(x - 4, y, x + 4, y);
        g.drawLine(x, y - 4, x, y + 4);
    }

    private static void drawExit(Graphics g, int x, Room destination) {
        g.setFont(new Font("SansSerif", Font.BOLD, 13));
        String label = "E: " + destination.name;
        if (destination.locked) {
            label += " [locked]";
        }
        int width = g.getFontMetrics().stringWidth(label) + 12;
        int labelX = Math.min(x, 669 - width);
        g.setColor(PANEL);
        g.fillRoundRect(labelX, 155, width, 25, 6, 6);
        g.setColor(TEXT);
        g.drawString(label, labelX + 6, 173);
    }

    private static void drawInventory(Graphics g) {
        drawImage(g, PlayerInventory.img, 0, 0, 679, 452);
        g.setColor(new Color(41, 53, 73, 215));
        g.fillRoundRect(65, 65, 545, 340, 16, 16);
        g.setColor(TEXT);
        g.setFont(new Font("SansSerif", Font.BOLD, 26));
        g.drawString("CASE FILE / EVIDENCE", 85, 49);
        button(g, 450, 15, 160, 40, "REVIEW CASE");
        g.setColor(PANEL);
        g.fillRoundRect(75, 412, 257, 25, 8, 8);
        g.setColor(TEXT);
        g.setFont(new Font("SansSerif", Font.PLAIN, 12));
        g.drawString("Items: equip / Clues: read / I: return", 85, 430);
        updateInventoryLayout();
        iItems.forEach((item, c) -> {
            drawInventoryIcon(g, item.image, c);
            if (PlayerInventory.isHolding(item)) {
                g.setColor(ACCENT);
                g.drawRoundRect(c.x, c.y, 50, 50, 10, 10);
                g.drawRoundRect(c.x + 1, c.y + 1, 48, 48, 9, 9);
            }
        });
        cItems.forEach((clue, c) -> {
            drawInventoryIcon(g, clue.image, c);
        });
    }

    private static void drawHeldItem(Graphics g) {
        int x = HAND_SLOT.x;
        int y = HAND_SLOT.y;
        Item held = null;
        if (PlayerInventory.isHolding(PlayerInventory.heldItem)) {
            held = PlayerInventory.heldItem;
        }
        g.setColor(PANEL);
        g.fillRoundRect(x - 30, y - 24, 100, 107, 10, 10);
        g.setColor(TEXT);
        g.setFont(new Font("SansSerif", Font.BOLD, 10));
        g.drawString("IN HAND", x + 8, y - 7);
        g.setColor(new Color(72, 91, 113));
        g.fillRect(x, y, HAND_SLOT.width, HAND_SLOT.height);
        g.setColor(ACCENT);
        g.drawRect(x, y, HAND_SLOT.width - 1, HAND_SLOT.height - 1);
        if (held != null) {
            drawImage(g, held.image, x + 7, y + 7, 50, 50);
        }
        String name = "Empty";
        if (held != null) {
            name = held.name;
        }
        g.setColor(TEXT);
        g.setFont(new Font("SansSerif", Font.PLAIN, 11));
        g.drawString(name, x + 62 - g.getFontMetrics().stringWidth(name), y + 79);
    }

    public static void updateInventoryLayout() {
        iItems.clear();
        cItems.clear();
        int index = 0;
        for (Item item : PlayerInventory.items) {
            iItems.put(item, inventoryPosition(index++));
        }
        for (Clue clue : PlayerInventory.clues) {
            cItems.put(clue, inventoryPosition(index++));
        }
    }

    private static Coordinate inventoryPosition(int index) {
        return new Coordinate(100 + (index % 7) * 60, 100 + (index / 7) * 60);
    }

    private static void drawInventoryIcon(Graphics g, ImageIcon image, Coordinate c) {
        g.setColor(new Color(72, 91, 113));
        g.fillRoundRect(c.x, c.y, 50, 50, 10, 10);
        drawImage(g, image, c.x + 4, c.y + 4, 42, 42);
    }

    private static void drawInventoryHover(Graphics g, int x, int y) {
        g.setFont(new Font("SansSerif", Font.PLAIN, 12));
        String text = hoveredItem;
        int width = g.getFontMetrics().stringWidth(text) + 16;
        int left = Math.max(5, Math.min(x, 674 - width));
        int top = Math.max(5, Math.min(y, 390));
        g.setColor(PANEL);
        g.fillRoundRect(left, top, width, 30, 8, 8);
        g.setColor(TEXT);
        g.drawString(text, left + 8, top + 20);
    }

    private static void drawStory(Graphics g, StoryScene scene, int index) {
        String narration = scene.frames[index];
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, 679, 452);
        g.setColor(Color.DARK_GRAY);
        g.fillRoundRect(20,250,625,175,10,10);
        g.setColor(Color.LIGHT_GRAY);
        g.drawRoundRect(20,250,625,175,10,10);
        g.setColor(TEXT);

        g.setFont(new Font("SansSerif", Font.PLAIN, 14));
        java.util.List<String> lines = wrapText(g, narration, 560);
        FontMetrics metrics = g.getFontMetrics();
        int lineHeight = metrics.getHeight();
        int y = 265 + (105 - lines.size() * lineHeight) / 2 + metrics.getAscent();
        for (String line : lines) {
            int x = 20 + (625 - metrics.stringWidth(line)) / 2;
            g.drawString(line, x, y);
            y += lineHeight;
        }

        String next = "CONTINUE";
        if (index == scene.frames.length - 1) {
            if (scene == StoryScene.ENDING) {
                next = "HOME";
            }
            else {
                next = "START";
            }
        }
        button(g, 457, 378, 165, 36, next);
    }

    private static void drawTitleScreen(Graphics g) {
        drawImage(g, loadImage("liberty-after-hours.png"), 0, 0, 679, 452);
        g.setColor(new Color(41, 53, 73, 85));
        g.fillRect(0, 0, 679, 452);
        g.setColor(TEXT);

        g.setFont(new Font("SansSerif", Font.BOLD, 54));
        centered(g, "Murder Mystery", 180);

        g.setColor(ACCENT);
        g.fillRoundRect(290, 260, 100, 50, 10, 10);
        g.setColor(PANEL);
        g.setFont(new Font("SansSerif", Font.BOLD, 13));
        centered(g, "BEGIN", 290);
        g.setColor(TEXT);
        g.setFont(new Font("SansSerif", Font.PLAIN, 12));
    }

    private static void centered(Graphics g, String text, int y) {
        g.drawString(text, (679 - g.getFontMetrics().stringWidth(text)) / 2, y);
    }

    private static void drawMap(Graphics g) {
        g.setColor(new Color(41, 53, 73, 238));
        g.fillRoundRect(385, 10, 284, 85, 10, 10);
        g.setFont(new Font("SansSerif", Font.BOLD, 10));
        String[] labels = {"Hall", "Class", "Library", "Lab", "Records", "Security"};
        for (int i = 0; i < RoomManager.rooms.length; i++) {
            Room room = RoomManager.rooms[i];
            int x = 395 + (i % 3) * 90;
            int y = 18 + (i / 3) * 36;
            if (room == GameManager.currRoom) {
                g.setColor(ACCENT);
            }
            else {
                g.setColor(new Color(112, 134, 160));
            }
            g.fillRect(x, y, 78, 16);
            g.setColor(TEXT);
            String label = labels[i];
            if (room.locked) {
                label += " [L]";
            }
            g.drawString(label, x, y + 27);
        }
    }

    private static void button(Graphics g, int x, int y, int w, int h, String text) {
        g.setColor(ACCENT);
        g.fillRoundRect(x, y, w, h, 8, 8);
        g.setColor(PANEL);
        g.setFont(new Font("SansSerif", Font.BOLD, 12));
        g.drawString(text, x + (w - g.getFontMetrics().stringWidth(text)) / 2, y + h / 2 + 4);
    }

    private static void drawCaseReview(Graphics g) {
        drawImage(g, PlayerInventory.img, 0, 0, 679, 452);
        g.setColor(new Color(41, 53, 73, 246));
        g.fillRoundRect(50, 20, 580, 390, 16, 16);
        g.setColor(TEXT);
        g.setFont(new Font("SansSerif", Font.BOLD, 26));
        String title = "Case review";
        if (Investigation.caseSolved) {
            title = "Case closed";
        }
        g.drawString(title, 80, 58);

        Clue[] clues = {RoomManager.meetingNote, RoomManager.libraryLedger, RoomManager.solventReport,
            RoomManager.accessLog, RoomManager.cameraFootage};
        int found = 0;
        for (Clue clue : clues) {
            if (Investigation.has(clue)) {
                found++;
            }
        }
        Suspect[] suspects = {RoomManager.alex, RoomManager.maya, RoomManager.morgan};
        g.setFont(new Font("SansSerif", Font.PLAIN, 14));
        g.drawString("Evidence collected  " + found + " / " + clues.length, 80, 92);
        g.setFont(new Font("SansSerif", Font.BOLD, 17));
        String question = "Who killed Mr. Woffinden?";
        if (Investigation.caseSolved) {
            question = "Morgan is in custody.";
        }
        g.drawString(question, 80, 124);

        for (int i = 0; i < suspects.length; i++) {
            int x = 80 + i * 175;
            g.setColor(new Color(61, 77, 99));
            g.fillRoundRect(x, 140, 160, 150, 10, 10);
            drawImage(g, suspects[i].image, x + 40, 147, 80, 115);
            g.setColor(TEXT);
            g.setFont(new Font("SansSerif", Font.PLAIN, 11));
            String status = "Follow-up needed";
            if (suspects[i].questionedWithEvidence) {
                status = "Questioning complete";
            }
            g.drawString(status, x + (160 - g.getFontMetrics().stringWidth(status)) / 2, 280);
            button(g, x, 300, 160, 40, "Accuse " + suspects[i].name);
        }
        g.setColor(TEXT);
        g.setFont(new Font("SansSerif", Font.PLAIN, 12));
        if (!Investigation.caseSolved && !Investigation.readyToAccuse()) {
            g.drawString("Find the remaining evidence and finish questioning.", 80, 356);
        }
        button(g, 80, 365, 150, 35, "Back");
        button(g, 450, 365, 150, 35, "New case");
    }
}
