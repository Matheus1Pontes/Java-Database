package Board.Project.Persistence.Entity;

public enum KindEnum {
    // later on, depending on the location of the card, it will determine, if the user just started with the card,
    // canceled it, has finalized their information, or if it's still pending
    INITIAL,
    CANCELLED,
    FINAL,
    PENDING
}
