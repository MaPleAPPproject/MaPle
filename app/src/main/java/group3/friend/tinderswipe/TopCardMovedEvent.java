package group3.friend.tinderswipe;

public class TopCardMovedEvent {

    // region Fields
    private final float posX;
    // endregion

    // region Constructors
    public TopCardMovedEvent(float posX) {
        this.posX = posX;
    }
    // endregion

    // region Getters
    public float getPosX() {
        return posX;
    }
    // endregion
}
