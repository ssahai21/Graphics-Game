import drawapp.DrawingPanel;
import java.awt.*;
import javax.swing.ImageIcon;

import java.util.HashMap;
import java.util.Map;

public class GamePanel {

    public static boolean displayingDialouge = false;

    public static DialougeManager currDialouge;

    public static boolean isHovering = false;

    public static boolean nearExit = false;

    public static DialougeManager hoveredItem = null;

    public static Map<Item, Coordinate> iItems = new HashMap<>();
    public static Map<Clue, Coordinate> cItems = new HashMap<>();

    public static void drawGame(DrawingPanel p, Room room, double playerPos, DialougeManager d, int x, int y){
        p.clear();
        Graphics g = p.getGraphics();
        if(GameManager.start){
            drawTitleScreen(g);
        }
        else if(PlayerInventory.showing){
            drawInventory(g);
            if(hoveredItem!=null){
                drawInventoryHover(g, x, y);
            }
        }
        else{
            drawRoom(g, room);
            drawItem(g, room);
            drawPlayer(g, playerPos);
            if(displayingDialouge){
                dialougeDrawer(g, d);
            }
            drawMap(g);
            if(isHovering) drawInteract(g, x, y);
            if(nearExit) drawExit(g, (int) playerPos);
            drawMouse(g, x, y);
        }
    }

    private static void drawItem(Graphics g, Room room){
        for(Item i : room.items){
            if(i.active){
                g.drawImage(i.image.getImage(), i.location.x, i.location.y, null);
            }
        }
    }

    private static void drawPlayer(Graphics g, double playerPos){
        g.drawImage(GameManager.player.image.getImage(), (int) playerPos, 0, null);
    }

    private static void drawRoom(Graphics g, Room room){
        g.drawImage(room.image.getImage(),0,0,null);
        ImageIcon temp = new ImageIcon("images/home.png");
        Image tempI = temp.getImage().getScaledInstance(50,50, java.awt.Image.SCALE_SMOOTH);
        g.drawImage(new ImageIcon(tempI).getImage(), 10,10,null);
    }



    public static void drawDialouge(DrawingPanel p, DialougeManager d){
        if(d.contents.isEmpty()){
            removeDialouge(p);
            return;
        }
        currDialouge = d;
        displayingDialouge = true;

        d.currLine = d.contents.removeFirst();
        
        drawGame(p, GameManager.currRoom, GameManager.player.position, d, GameManager.mouseX, GameManager.mouseY);
    } 

    public static void dialougeDrawer(Graphics g, DialougeManager d){
        g.setColor(new Color(131, 111, 91, 220)); // Semi-transparent dark gray
        g.fillRect(10, 300, 650, 120);

        g.setColor(new Color(74, 57, 42, 240));
        g.fillRect(10,330,90,90);
        g.setColor(Color.BLACK);
        g.drawRect(10,330,90,90);

        g.drawImage(d.speakerImage.getImage(), 5, 325, null);

        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 14));
        
        g.drawString(d.speakerName, 30, 320);
        
        if (d.currLine != null) {
            g.setColor(Color.YELLOW); 
            g.drawString(d.currLine, 110, 365);
        }
    }  

    public static void removeDialouge(DrawingPanel p){
        displayingDialouge = false;
        currDialouge = null;
        drawGame(p, GameManager.currRoom, GameManager.player.position, null, GameManager.mouseX, GameManager.mouseY);
    }

    private static void drawMouse(Graphics g, int x, int y){
        g.drawImage(new ImageIcon("images/mouse.png").getImage(), x, y, null);
    }

    private static void drawInteract(Graphics g, int x, int y){
        g.drawImage(new ImageIcon("images/interact.png").getImage(), x, y, null);
    }

    private static void drawExit(Graphics g, int x){
        g.drawImage(new ImageIcon("images/exit.png").getImage(), x, 100, null);
        }

    private static void drawInventory(Graphics g){
        g.drawImage(PlayerInventory.img.getImage(), 0, 0, null);
        int x = 100;
        int y = 100;
        int yCounter = 1;
        for(Item i : PlayerInventory.items){
            ImageIcon temp = i.image;
            Image tempI = temp.getImage().getScaledInstance(50,50, java.awt.Image.SCALE_SMOOTH);
            g.drawImage(new ImageIcon(tempI).getImage(), x, y, null);
            iItems.put(i, new Coordinate(x,y));
            x+=60;
            yCounter++;
            if(yCounter==8){
                yCounter=1;
                y+=60;
            }
        }
    }

    private static void drawInventoryHover(Graphics g, int x, int y){
        g.setColor(Color.DARK_GRAY);
        g.fillRect(x, y, 80, 40);
        g.setColor(Color.WHITE);
        g.drawRect(x,y,80,40);
        g.drawString(hoveredItem.inventoryBlurb, x, y+20);
    }

    private static void drawTitleScreen(Graphics g){
        g.drawImage(new ImageIcon("images/titleScreen.jpg").getImage(),0,0,null);
        g.setColor(Color.MAGENTA);
        g.fillRect(260, 260, 100, 50);
        g.setColor(Color.BLACK);
        g.drawString("Start", 280, 260);
    }

    private static void drawMap(Graphics g){
        int startX = 560;
        int startY = 20;
        int x = Integer.valueOf(startX);
        int y = Integer.valueOf(startY);
        g.setColor(Color.BLACK);
        g.drawRect(startX, startY, 100,100);
        g.setColor(Color.LIGHT_GRAY);
        g.fillRect(startX, startY, 100, 100);
        for(int i = 0; i<GameMap.rooms.length; i++){
            for(int j =0; j<GameMap.rooms[i].length; j++){
                if(GameMap.rooms[i][j]){
                    g.setColor(Color.BLACK);
                    g.drawRect(x, y, 25,25);
                    g.setColor(Color.WHITE);
                    g.fillRect(x,y,25,25);
                    x+=30;
                }
            }
            y+=30;
            x=Integer.valueOf(startX);
        }
    }
}
