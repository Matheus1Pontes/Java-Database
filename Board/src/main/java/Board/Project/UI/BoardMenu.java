package Board.Project.UI;

import Board.Project.Persistence.Entity.BlockEntity;
import Board.Project.Persistence.Entity.BoardEntity;
import Board.Project.Persistence.Entity.CardEntity;
import Board.Project.Persistence.Entity.KindEnum;
import Board.Project.Service.BlockService;
import Board.Project.Service.CardService;
import lombok.AllArgsConstructor;
import java.sql.SQLException;
import java.time.OffsetDateTime;
import java.util.Scanner;
import static Board.Project.Persistence.Config.ConnectionConfig.getConnection;

@AllArgsConstructor
public class BoardMenu {

    // The board currently selected by the user
    private final BoardEntity board;

    private final Scanner sc = new Scanner(System.in).useDelimiter("\n");

    // Displays the board menu
    public void execute() {

        System.out.printf("Welcome to board: %s\n", board.getName());

        var option = -1;

        while (true) {
            System.out.println("1 > Create card.");

            System.out.println("2 > Move card.");

            System.out.println("3 > Block card.");

            System.out.println("4 > Unblock card.");

            System.out.println("5 > Cancel card.");

            System.out.println("6 > Finalize card.");

            System.out.println("7 > Return to main menu.");

            System.out.println("8 > Exit.");

            option = sc.nextInt();
            sc.nextLine();

            switch (option) {
                case 1:
                    createCard();
                    break;
                case 2:
                    moveCard();
                    break;
                case 3:
                    blockCard();
                    break;
                case 4:
                    unblockCard();
                    break;
                case 5:
                    cancelCard();
                    break;
                case 6:
                    finalizeCard();
                    break;
                case 7:
                    System.out.println("Returning to main menu...\n");
                    return;
                case 8:
                    System.exit(0);
                    break;
                default:
                    System.out.println("Invalid option. Please try again.\n");
            }
        }
    }

    // Creates a new card in the initial column
    private void createCard() {
        System.out.println("Enter card title: ");
        var title = sc.nextLine();

        System.out.println("Enter card description: ");
        var description = sc.nextLine();

        // Finds the initial column for the new card
        var firstColumn = board.getColumns()
                .stream()
                .filter(column -> column.getKind() == KindEnum.INITIAL)
                .findFirst()
                .orElseThrow(() ->  new IllegalStateException("No such column found.\n"));

        var card = new CardEntity();
        card.setTitle(title);
        card.setDescription(description);
        card.setCreatedAt(OffsetDateTime.now());
        card.setBoardColumn(firstColumn);

        // Saves the card in the database
        try (var connection = getConnection()) {
            var cardService = new CardService(connection);
            cardService.insert(card);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Moves a card to another column
    private void moveCard() {
        System.out.println("Enter card id: ");
        long id = sc.nextLong();
        sc.nextLine();

        System.out.println("Which column would you like to move to: ");

        // Displays all available columns
        for (int i = 0; i < board.getColumns().size(); i++) {
            var column = board.getColumns().get(i);
            System.out.println((i + 1) + " > " + column.getName());
        }

        var column = sc.nextInt();

        if (column < 1 || column > board.getColumns().size()) {
            System.out.println("Invalid column. Please try again.\n");
            return;
        }

        // Gets the column selected by the user
        var columnDestination = board.getColumns().get(column - 1);

        try (var connection = getConnection()) {
            var blockService = new BlockService(connection);
            var cardService = new CardService(connection);

            // Blocked cards cannot be moved
            if (blockService.isBlocked(id)) {
                System.out.println("This card is BLOCKED and cannot be moved. Please unblock to continue.\n");
                return;
            }

            var card = cardService.findById(id)
                    .orElseThrow(() ->
                            new IllegalStateException("Card not found.\n"));

            // Finalized and canceled cards cannot be moved

            if (card.getBoardColumn().getKind() == KindEnum.FINAL) {
                System.out.println("Card has already been FINALIZED. Please create a new card.\n");
                return;
            }

            if (card.getBoardColumn().getKind() == KindEnum.CANCELLED) {
                System.out.println("Card has already been CANCELED. Please create a new card.\n");
                return;
            }

            card.setBoardColumn(columnDestination);
            cardService.update(card);
            System.out.println("Card moved successfully.\n");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Blocks a card
    private void blockCard() {
        System.out.println("Enter card id to block: ");
        long id = sc.nextLong();
        sc.nextLine();

        System.out.println("Cause for blocking the card: ");
        var cause = sc.nextLine();

        var blockEntity = new BlockEntity();

        blockEntity.getCard().setId(id);
        blockEntity.setBlockedCause(cause);
        blockEntity.setBlockedAt(OffsetDateTime.now());

        try (var connection = getConnection()) {

            var cardService = new CardService(connection);
            var blockService = new BlockService(connection);

            var card = cardService.findById(id)
                    .orElseThrow(() ->
                            new IllegalStateException("Card not found.\n"));

            // Completed cards cannot be blocked
            if (card.getBoardColumn().getKind() == KindEnum.FINAL) {
                System.out.println("Card has already been FINALIZED. Please create a new card.\n");
                return;
            }

            // Canceled cards cannot be blocked
            if (card.getBoardColumn().getKind() == KindEnum.CANCELLED) {
                System.out.println("Card has already been CANCELED. Please create a new card.\n");
                return;
            }

            // Prevents a card from having multiple active blocks
            if (blockService.isBlocked(id)) {
                System.out.println("This card is already BLOCKED.\n");
                return;
            }

            blockService.insert(blockEntity);
            System.out.println("Successfully blocked card.\n");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Unblocks a card
    private void unblockCard() {
        System.out.println("Enter card id: ");
        long id = sc.nextLong();
        sc.nextLine();

        System.out.println("Cause for unblocking the card: ");
        var cause = sc.nextLine();

        try (var connection = getConnection()) {
            var blockService = new BlockService(connection);

            if (!(blockService.isBlocked(id))) {
                System.out.println("This card is not BLOCKED.\n");
                return;
            }

            blockService.unblock(id, cause);
            System.out.println("Card has been unblocked.\n");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Moves a card to the canceled column
    private void cancelCard() {
        System.out.println("Enter card id: ");
        long id = sc.nextLong();
        sc.nextLine();

        // Finds the canceled column
        var cancelled = board.getColumns()
                .stream()
                .filter(column -> column.getKind() == KindEnum.CANCELLED)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Column Not Found.\n")
                );

        try (var connection = getConnection()) {

            var blockService = new BlockService(connection);
            var cardService = new CardService(connection);

            var card = cardService.findById(id)
                    .orElseThrow(() -> new IllegalStateException("Card Not Found.\n"));

            // Blocked cards cannot be canceled
            if (blockService.isBlocked(id)) {
                System.out.println("Please UNBLOCK to CANCEL card.\n");
                return;
            }

            // Canceled cards cannot be canceled again
            if (card.getBoardColumn().getKind() == KindEnum.CANCELLED) {
                System.out.println("Card has already been CANCELED.\n");
                return;
            }

            // Finished cards cannot be canceled
            if (card.getBoardColumn().getKind() == KindEnum.FINAL) {
                System.out.println("Card has already been FINALIZED.\n");
                return;
            }

            card.setBoardColumn(cancelled);
            cardService.update(card);

            System.out.println("Card has been canceled.\n");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Moves a card to the final column
    private void finalizeCard() {
        System.out.println("Enter card id: ");
        long id = sc.nextLong();
        sc.nextLine();

        // Finds the final column
        var finalized = board.getColumns()
                .stream()
                .filter(column -> column.getKind() == KindEnum.FINAL)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Column Not Found.\n")
                );

        try (var connection = getConnection()) {
            var blockService = new BlockService(connection);
            var service = new CardService(connection);

            // Blocked cards cannot be finalized
            if (blockService.isBlocked(id)) {
                System.out.println("Please UNBLOCK to FINALIZE card.\n");
                return;
            }

            var card = service.findById(id)
                    .orElseThrow(() -> new IllegalStateException("Card Not Found.\n"));

            // Canceled cards cannot be finalized
            if (card.getBoardColumn().getKind() == KindEnum.CANCELLED) {
                System.out.println("Card has already been canceled.\n");
                return;
            }

            // Prevents a card from being finalized twice
            if (card.getBoardColumn().getKind() == KindEnum.FINAL) {
                System.out.println("Card has already been FINALIZED.\n");
                return;
            }

            card.setBoardColumn(finalized);
            service.update(card);
            System.out.println("Card has been finalized.\n");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
