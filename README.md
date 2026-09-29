# Java-Database

A console-based board application built with Java, JDBC, MySQL, and Liquibase. 
It lets users create boards with customizable columns and manage cards through their workflow.

## Features

- Create, select, and delete boards.
- Create boards with initial, pending, final, and canceled columns.
- Create cards in a board’s initial column.
- Move cards between columns.
- Block and unblock cards with a reason.
- Cancel or finalize cards.
- Prevent blocked cards from being moved, canceled, or finalized.
- Create and update the database schema with Liquibase migrations.

## Technologies

- Java 26
- Maven
- JDBC
- MySQL
- Liquibase
- Lombok

## Requirements

- JDK 26
- MySQL Server

## Setup

1. Create a MySQL database named `board`.
2. Configure the MySQL connection for your local environment. 
3. Open the `Board` directory as a Maven project in your IDE.
4. Run `Board.Project.Main`.

The application runs the Liquibase migrations when it starts, then opens the main menu.

## Using the application

From the main menu, you can create a board, select an existing board, delete a board, or exit. 
When creating a board, provide names for the initial and final columns, choose how many pending columns to add, and provide a name for the canceled column.

After selecting a board, use its menu to create and manage cards.

## Project structure

- `Board/src/main/java/Board/Project/Main.java` — runs database migrations and starts the application.
- `Persistence/Config` — JDBC connection setup.
- `Persistence/DAO` — SQL database operations.
- `Persistence/Entity` — board, column, card, and block data classes.
- `Persistence/Migrations` — Liquibase migration setup.
- `Service` — database operations and transaction handling.
- `UI` — console menus.
- `Board/src/main/resources/db/changelog` — Liquibase changelog and SQL migrations.