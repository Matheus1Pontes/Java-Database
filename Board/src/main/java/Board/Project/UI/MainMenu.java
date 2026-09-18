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

    private void createdBoard() {
        System.out.println("Enter board name: ");
        String name = sc.next();

        var board = new BoardEntity();
        board.setName(name);

        System.out.println("Will you add pending columns to the board?\n " +
                           "YES? How many? || NO? Type 0.");
        var addColumn = sc.nextInt();

        List<BoardColumnEntity> columns = new ArrayList<>();

        System.out.println("First column name: ");
        var firstColumnName = sc.next();
        var firstColumn = createColumn(firstColumnName, KindEnum.INITIAL, 0);
        columns.add(firstColumn);

        for (int i = 0; i < addColumn; i++) {
            System.out.println("Pending column name: ");
            var pendingColumnName = sc.next();
            var pendingColumn = createColumn(pendingColumnName, KindEnum.PENDING, i + 1);
            columns.add(pendingColumn);
        }

        System.out.println("Last column name: ");
        var lastColumnName = sc.next();
        var lastColumn = createColumn(lastColumnName, KindEnum.FINAL, addColumn + 1);
        columns.add(lastColumn);

        System.out.println("Cancelled column name: ");
        var cancelColumnName = sc.next();
        var cancelColumn = createColumn(cancelColumnName, KindEnum.CANCELLED, addColumn + 2);
        columns.add(cancelColumn);

        board.setColumns(columns);

        try (var connection = getConnection()) {
            var service = new BoardService(connection);
            service.insert(board);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void selectBoard() {
        System.out.println("Enter board id: ");
        long id = sc.nextLong();

        try (var connection = getConnection()) {
            var service = new BoardService(connection);
            service.findById(id);

            if (service.findById(id).isPresent()) {
                var menu = new BoardMenu(service.findById(id).get());
                menu.execute();
            } else {
                System.out.println("There is no such board.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void deleteBoard() {
        System.out.println("Enter board id: ");
        long id = sc.nextLong();

        try (var connection = getConnection()) {
            var service = new BoardService(connection);
            service.delete(id);
            System.out.println(id + " has been deleted.");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private BoardColumnEntity createColumn(final String name, final KindEnum kind, final int order) {
        var boardColumn = new BoardColumnEntity();
        boardColumn.setName(name);
        boardColumn.setKind(kind);
        boardColumn.setOrder(order);
        return boardColumn;
    }

}
