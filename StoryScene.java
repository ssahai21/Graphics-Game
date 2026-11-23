public class StoryScene {
    public static final StoryScene OPENING = new StoryScene(new String[]{
        "You get the call just after six. A teacher at Liberty High has been found dead. The school is keeping everyone inside until you arrive.",
        "Alex, Maya, and Morgan were the last three people seen inside the school.",
        "The office left a key at reception. You'll need to look through his room and speak to the people who stayed behind.",
        "A / D - move left and right.\nE - go through a nearby door.\nClick objects and people to investigate.\nClick to advance conversations.",
        "I - open or close your inventory.\nClick a key or tool to hold it. Click a clue to read it.\nLook around the room for puzzle clues.\nChoose Review Case in your inventory when you're ready to accuse someone."
    });

    public static final StoryScene ENDING = new StoryScene(new String[]{
        "When you show Morgan the recording, he stops arguing. The officers take him out through the side entrance.",
        "The missing tablets turn up in Morgan's car. Woffinden had worked out who was taking them and wanted an explanation.",
        "Maya locks the library. Alex calls his parents for a ride. You stay to finish the report."
    });

    public final String[] frames;

    private StoryScene(String[] frames) {
        this.frames = frames;
    }
}
