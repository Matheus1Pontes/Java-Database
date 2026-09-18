package Board.Project.Persistence.Entity;

import lombok.Data;

@Data 
public class BoardColumnEntity {

    private Long id;

    private String name;

    private KindEnum kind;

    private int order;
    // each board column belongs to a board, hence there is a representation of that board
    // makes it possible to get || var column = column.getBoard().getId(); for example
    private BoardEntity board = new BoardEntity();

}
