package Board.Project.UI;

import Board.Project.Persistence.Entity.BoardColumnEntity;
import Board.Project.Persistence.Entity.BoardEntity;
import Board.Project.Persistence.Entity.KindEnum;
import Board.Project.Service.BoardService;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import static Board.Project.Persistence.Config.ConnectionConfig.getConnection;

public class MainMenu {

    private final Scanner sc = new Scanner(System.in);

    // Displays the main menu
    public void execute() {
        System.out.println("Welcome to the main menu!");

        var option = -1;

        while (true) {
            System.out.println("1 > Create a new board.");

            System.out.println("2 > Select an existing board.");

            System.out.println("3 > Delete a board.");

            System.out.println("4 > Exit.");

            option = sc.nextInt();

            switch (option) {
                case 1:
                    createdBoard();
                    break;
                case 2:
                    selectBoard();
                    break;
                case 3:
                    deleteBoard();
                    break;
                case 4:
                    System.exit(0);
                    break;
                default:
                    System.out.println("Invalid option. Please try again.");
            }

        }
    }

    // Creates a board and its columns
    private void createdBoard() {
        System.out.println("Enter board name: ");
        String name = sc.next();

        var board = new BoardEntity();
        board.setName(name);

        System.out.println("Will you add pending columns to the board?\n " +
                           "YES? How many? || NO? Type 0.");
        var addColumn = sc.nextInt();

        List<BoardColumnEntity> columns = new ArrayList<>();

        // Creates the initial column
        System.out.println("First column name: ");
        var firstColumnName = sc.next();
        var firstColumn = createColumn(firstColumnName, KindEnum.INITIAL, 0);
        columns.add(firstColumn);

        // Creates the pending columns
        for (int i = 0; i < addColumn; i++) {
            System.out.println("Pending column name: ");
            var pendingColumnName = sc.next();
            var pendingColumn = createColumn(pendingColumnName, KindEnum.PENDING, i + 1);
            columns.add(pendingColumn);
        }

        // Creates the final column
        System.out.println("Last column name: ");
        var lastColumnName = sc.next();
        var lastColumn = createColumn(lastColumnName, KindEnum.FINAL, addColumn + 1);
        columns.add(lastColumn);

        // Creates the canceled column
        System.out.println("Canceled column name: ");
        var cancelColumnName = sc.next();
        var cancelColumn = createColumn(cancelColumnName, KindEnum.CANCELLED, addColumn + 2);
        columns.add(cancelColumn);

        board.setColumns(columns);

        // Saves the board and columns
        try (var connection = getConnection()) {
            var boardService = new BoardService(connection);
            boardService.insert(board);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Finds a board and opens its menu
    private void selectBoard() {
        System.out.println("Enter board id: ");
        long id = sc.nextLong();

        try (var connection = getConnection()) {
            var boardService = new BoardService(connection);
            boardService.findById(id);

            if (boardService.findById(id).isPresent()) {
                var menu = new BoardMenu(boardService.findById(id).get());
                menu.execute();
            } else {
                System.out.println("There is no such board.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Deletes the board
    private void deleteBoard() {
        System.out.println("Enter board id: ");
        long id = sc.nextLong();

        try (var connection = getConnection()) {
            var boardService = new BoardService(connection);
            boardService.delete(id);
            System.out.println(id + " has been deleted.");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Creates a column and assigns its basic information
    private BoardColumnEntity createColumn(final String name, final KindEnum kind, final int order) {
        var boardColumn = new BoardColumnEntity();
        boardColumn.setName(name);
        boardColumn.setKind(kind);
        boardColumn.setOrder(order);
        return boardColumn;
    }

}
