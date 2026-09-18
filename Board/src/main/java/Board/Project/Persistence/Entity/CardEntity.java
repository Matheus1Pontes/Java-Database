package Board.Project.Persistence.Entity;

import java.time.OffsetDateTime;
import lombok.Data;

@Data 
public class CardEntity {

    private Long id;

    private String title;

    private String description;

    private OffsetDateTime createdAt;
    // every card belongs to a board column, hence the representation of the board column
    private BoardColumnEntity boardColumn =  new BoardColumnEntity();

}
