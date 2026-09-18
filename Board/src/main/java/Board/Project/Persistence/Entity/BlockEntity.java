package Board.Project.Persistence.Entity;

import java.time.OffsetDateTime;
import lombok.Data;

@Data 
public class BlockEntity {

    private Long id;

    private String blockedCause;

    private OffsetDateTime blockedAt;

    private String unblockedCause;

    private OffsetDateTime unblockedAt;

    // you can block cards, this connects the information of the blocked card, retrieving id, etc
    private CardEntity card  = new CardEntity();

}
