public enum StoryScene {
    OPENING(new Frame[]{
        new Frame("The last bell rang hours ago. A call from Liberty High brings you back to the school."),
        new Frame("Mr. Woffinden was found dead in a locked classroom. Someone knows what happened before the doors closed."),
        new Frame("Start at reception. Find the classroom key, search the room, and compare each suspect's story with the evidence.")
    }),
    ENDING(new Frame[]{
        new Frame("The access log places Morgan at the classroom. The camera confirms the attack, and the rag ties him to the lab vent."),
        new Frame("Mr. Woffinden discovered Morgan's equipment theft. The audit was due the next morning."),
        new Frame("Morgan is in custody. You file the evidence as the lights go out at Liberty High.")
    });

    public record Frame(String narration) {}
    public final Frame[] frames;

    StoryScene(Frame[] frames) {
        this.frames = frames;
    }
}
