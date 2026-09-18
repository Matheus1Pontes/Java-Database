package Board.Project.Persistence.Entity;

import lombok.Data;
import java.util.ArrayList;
import java.util.List;

@Data 
public class BoardEntity {
    
    private Long id;

    private String name;
    // a board can possess many columns, hence it is a list of board columns
    private List<BoardColumnEntity> columns = new ArrayList<>();

}
