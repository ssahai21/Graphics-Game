    import drawapp.DrawingPanel;
    import javax.swing.ImageIcon;
    import javax.swing.Timer;


    public class GameManager {
        public static Room[] rooms = RoomManager.rooms;
        public static Player player = new Player(new ImageIcon("images/player.png"));

        public static DrawingPanel panel = new DrawingPanel(679, 452);
        
        public static Room currRoom = rooms[0];

        public static int mouseX;
        public static int mouseY;

        public static double roomCooldown = 128;

        public static boolean start = true;


        public static DialougeManager currDialouge;
        public static void main(String[] args){

            panel.onMouseClick((x,y) -> {
                if(!start){
                    boolean isItem = false;
                    for(Item i : currRoom.items){
                        if(x >= i.location.x && x<=i.location.x+Item.BOUNDINGBOX && y >= i.location.y && y<=i.location.y+Item.BOUNDINGBOX){
                            isItem = true;
                            if(i.content != null){    
                                currDialouge = i.content;
                                GamePanel.drawDialouge(panel, currDialouge);
                            }
                            PlayerInventory.items.add(i);
                            currRoom.items.remove(i);
                        }
                    }
                    for(Clue c : currRoom.clues){
                        if(x >= c.location.x && x<=c.location.x+Item.BOUNDINGBOX && y >= c.location.y && y<=c.location.y+Item.BOUNDINGBOX){
                            isItem = true;
                            if(c.content != null){    
                                currDialouge = c.content;
                                GamePanel.drawDialouge(panel, currDialouge);
                            }
                            PlayerInventory.clues.add(c);
                            currRoom.clues.remove(c);
                        }
                    }
                    if(!isItem){
                        if(GamePanel.displayingDialouge){
                            GamePanel.drawDialouge(panel, currDialouge);
                        }
                    }
                    if(x>=10 && y>= 10 && x<=60 && y<=60){
                        start = true;
                    }
                }
                else{
                    if(x>= 260 && x<=360 && y>=260 && y<=310){
                        start = false;
                    }
                }
            });

            panel.onMouseMove((x,y) -> {
                mouseX = x;
                mouseY = y;
                GamePanel.isHovering = false;
                GamePanel.hoveredItem = null;
                for(Item i : currRoom.items){
                    if(x >= i.location.x && x<=i.location.x+Item.BOUNDINGBOX && y >= i.location.y && y<=i.location.y+Item.BOUNDINGBOX){
                        GamePanel.isHovering = true;
                    }
                }
                
                if(PlayerInventory.showing){
                    if(GamePanel.iItems.size()>0){
                        GamePanel.iItems.forEach((item, coord) -> {
                            if(x>=coord.x && x<=coord.x+50 && y>=coord.y && y<=coord.y+50){
                                GamePanel.hoveredItem = item.content;
                            }
                        });
                    }
                    if(GamePanel.cItems.size()>0){    
                        GamePanel.cItems.forEach((item, coord) -> {
                            if(x>=coord.x && x<=coord.x+50 && y>=coord.y && y<=coord.y+50){
                                GamePanel.hoveredItem = item.content;
                            }
                        });
                    }
                }
            
            });
            
            double speed = 300.0;
            double deltaTime = 1.0 / 60.0;

            boolean[] keys = new boolean[3]; 
            panel.onKeyDown(key -> {
                System.out.println("Key pressed: " + key);
                if(!GamePanel.displayingDialouge){
                    if (key == 'a') keys[0] = true;
                    if (key == 'd') keys[1] = true;
                    if (key == 'e') keys[2] = true;
                    if (key == 'i') PlayerInventory.showing = !PlayerInventory.showing;
                }
            });

            panel.onKeyUp(key -> {
                if (key == 'a') keys[0] = false;
                if (key == 'd') keys[1] = false;
                if (key == 'e') keys[2] = false;
            });

            

            Timer timer = new Timer(16, e -> {
                roomCooldown -= 16;
                if (keys[0]) {
                    player.position -= speed*deltaTime;
                }
                if (keys[1]) {
                    player.position += speed*deltaTime;
                }
                if(player.position < 50 || player.position > 450){
                    if(player.position < 50){
                        if(currRoom.left != null){
                            GamePanel.nearExit = true;
                            if(keys[2]==true && roomCooldown <= 0){
                                currRoom = currRoom.left;
                                player.position = 500;
                                roomCooldown = 128;
                            }
                        }
                    }
                    else{
                        if(currRoom.right != null){
                            GamePanel.nearExit = true;
                            if(keys[2] == true && roomCooldown <= 0){
                                currRoom = currRoom.right;
                                player.position = 10;
                                roomCooldown = 128;
                            }
                        }
                    }
                } 
                else {
                    GamePanel.nearExit = false;
                }

                GamePanel.drawGame(panel, currRoom, (int) player.position, GamePanel.currDialouge, mouseX, mouseY);
        });

            timer.start();

        }
    }
