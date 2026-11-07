import javax.swing.ImageIcon;

public class Clue {
    
    public ImageIcon image;
    public boolean locked;
    public boolean active;
    public DialougeManager content;
    public Coordinate location;

    public static final int BOUNDINGBOX = 100;

    public Clue(Coordinate location, ImageIcon image, DialougeManager content){
        this.location = location;
        this.image = image;
        this.content = content;
        content.speakerImage = this.image;
        active = true;
    }
}
