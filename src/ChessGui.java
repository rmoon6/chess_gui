import javafx.application.Application;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * I knew pretty much nothing about javaFX before watching a tutorial by some hilarious guy
 * named Bucky from upstate NY. Here is a link to it:
 *
 *      https://www.youtube.com/watch?v=q1LEN2assfU
 */
public class ChessGui extends Application {

    private static final String title = "Chess DB GUI";

    private static ChessDb chessDb;
    private static ChessGui chessGui;
    private static Predicate<ChessGame> currentFilter = chessGame -> true;

    private static Scene sceneHome;

    public static void main(String[] args) {
        chessDb = new ChessDb();
        chessGui = new ChessGui();
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        stage.setTitle(title);
        stage.setWidth(1500);
        stage.setHeight(600);

        readFilesIntoDb();

        stage.setScene(getHomeScene(stage));
        stage.show();
    }

    private enum Color {
        BLACK,
        WHITE
    }

    //I'll start this out having a text field for site, and also some radio buttons for black or white won
    private static Scene getSearchScene(Stage stage) {
        Scene searchScene = new Scene(new Group());

        Text titleText = new Text();
        titleText.setText("Search:");

        Label siteLabel = new Label();
        siteLabel.setText("Site: ");
        TextField siteSearch = new TextField();

        final HBox textFieldHbox = new HBox();
        textFieldHbox.setSpacing(5);
        textFieldHbox.setPadding(new Insets(10, 0, 0, 10));
        textFieldHbox.getChildren().addAll(siteLabel, siteSearch);

        Label winnerLabel = new Label();
        winnerLabel.setText("Winner:\t\t");
        Label whiteLabel = new Label();
        whiteLabel.setText("White");
        RadioButton whiteRB = new RadioButton();
        whiteRB.setUserData(Color.WHITE);
        Label blackLabel = new Label();
        blackLabel.setText("Black");
        RadioButton blackRB = new RadioButton();
        blackRB.setUserData(Color.BLACK);
        ToggleGroup toggleGroup = new ToggleGroup();
        whiteRB.setToggleGroup(toggleGroup);
        blackRB.setToggleGroup(toggleGroup);

        final HBox radioHbox = new HBox();
        radioHbox.setSpacing(5);
        radioHbox.setPadding(new Insets(10, 0, 0, 10));
        radioHbox.getChildren().addAll(winnerLabel, whiteLabel, whiteRB, blackLabel, blackRB);

        Button submitButton = new Button();
        submitButton.setText("Submit");
        submitButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                currentFilter = chessGame ->
                        (siteSearch.getCharacters().length() == 0 || chessGame.getSite().contains(siteSearch.getCharacters()))
                        &&
                        (toggleGroup.getSelectedToggle() == null ||
                                (toggleGroup.getSelectedToggle().getUserData() == Color.WHITE && chessGame.getResult().equals("1-0")) ||
                                (toggleGroup.getSelectedToggle().getUserData() == Color.BLACK && chessGame.getResult().equals("0-1")));
                stage.setScene(getHomeScene(stage));
            }
        });

        Button backButton = new Button();
        backButton.setText("Back");
        backButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                stage.setScene(getHomeScene(stage));
            }
        });

        final HBox buttonHbox = new HBox();
        buttonHbox.setSpacing(5);
        buttonHbox.setPadding(new Insets(10, 0, 0, 10));
        buttonHbox.getChildren().addAll(backButton, submitButton);

        final VBox vbox = new VBox();
        vbox.setSpacing(5);
        vbox.setPadding(new Insets(10, 0, 0, 10));
        vbox.getChildren().addAll(titleText, textFieldHbox, radioHbox, buttonHbox);

        ((Group) searchScene.getRoot()).getChildren().addAll(vbox);

        return searchScene;

    }

    private static Scene getGameViewScene(Stage stage, ChessGame selectedGame) {
        Scene gameViewScene = new Scene(new Group());

        Text gameViewTitleText = new Text();
        gameViewTitleText.setText("Breakdown of the game's moves:");

        Text gameInfoText = new Text();
        gameInfoText.setText("Event: " + selectedGame.getEvent() + "\n"
                + "Site: " + selectedGame.getSite() + "\n"
                + "Date: " + selectedGame.getDate() + "\n"
                + "White: " + selectedGame.getWhite() + "\n"
                + "Black: " + selectedGame.getBlack() + "\n"
                + "Result: " + selectedGame.getResult() + "\n" + "\n"
                + "Moves:\n" + selectedGame.getMovesAsString());

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setPrefSize(400, 440);
        scrollPane.setContent(gameInfoText);

        Button backButton = new Button();
        backButton.setText("Back");
        backButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                stage.setScene(sceneHome);
            }
        });

        final VBox sceneVbox = new VBox();
        sceneVbox.setSpacing(5);
        sceneVbox.setPadding(new Insets(10, 0, 0, 10));
        sceneVbox.getChildren().addAll(gameViewTitleText, scrollPane, backButton);

        ((Group) gameViewScene.getRoot()).getChildren().addAll(sceneVbox);

        return gameViewScene;
    }

    private static Scene getHomeScene(Stage stage) {
        sceneHome = new Scene(new Group());

        final Label tableLabel = new Label("Games");
        tableLabel.setFont(new Font("Arial", 20));

        TableView<ChessGame> gamesTable = new TableView<>();
        setColumns(gamesTable);
        gamesTable.setItems(getObservableListFromDb());

        Button viewGameButton = new Button();
        viewGameButton.setText("View Game");
        viewGameButton.setOnAction(event -> {
            ChessGame selectedGame = gamesTable.getSelectionModel().getSelectedItem();
            if (selectedGame != null) {
                stage.setScene(getGameViewScene(stage, selectedGame));
            }
        });

        Button searchButton = new Button();
        searchButton.setText("Search");
        searchButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                stage.setScene(getSearchScene(stage));
            }
        });

        Button dismissButton = new Button();
        dismissButton.setText("Dismiss");
        dismissButton.setOnAction(event -> System.exit(0));

        final HBox buttonsHbox = new HBox();
        buttonsHbox.setSpacing(5);
        buttonsHbox.setPadding(new Insets(10, 0, 0, 10));
        buttonsHbox.getChildren().addAll(viewGameButton, searchButton, dismissButton);

        final VBox sceneVbox = new VBox();
        sceneVbox.setSpacing(5);
        sceneVbox.setPadding(new Insets(10, 0, 0, 10));
        sceneVbox.getChildren().addAll(tableLabel, gamesTable, buttonsHbox);

        ((Group) sceneHome.getRoot()).getChildren().addAll(sceneVbox);

        return sceneHome;
    }

    private static ObservableList<ChessGame> getObservableListFromDb() {
        ObservableList<ChessGame> games = FXCollections.observableArrayList();

        for (ChessGame game : chessDb.getGames().stream().filter(currentFilter).collect(Collectors.toList())) {
            games.add(game);
        }

        return games;
    }

    private static void setColumns(TableView<ChessGame> table) {
        TableColumn<ChessGame, StringProperty> event = new TableColumn<>("Event");
        event.setMinWidth(200);
        event.setCellValueFactory(new PropertyValueFactory<>("event"));

        TableColumn<ChessGame, StringProperty> site = new TableColumn<>("Site");
        site.setMinWidth(200);
        site.setCellValueFactory(new PropertyValueFactory<>("site"));

        TableColumn<ChessGame, StringProperty> date = new TableColumn<>("Date");
        date.setMinWidth(200);
        date.setCellValueFactory(new PropertyValueFactory<>("date"));

        TableColumn<ChessGame, StringProperty> white = new TableColumn<>("White");
        white.setMinWidth(200);
        white.setCellValueFactory(new PropertyValueFactory<>("white"));

        TableColumn<ChessGame, StringProperty> black = new TableColumn<>("Black");
        black.setMinWidth(200);
        black.setCellValueFactory(new PropertyValueFactory<>("black"));

        TableColumn<ChessGame, StringProperty> result = new TableColumn<>("Result");
        result.setMinWidth(200);
        result.setCellValueFactory(new PropertyValueFactory<>("result"));

        TableColumn<ChessGame, StringProperty> opening = new TableColumn<>("Opening");
        opening.setMinWidth(200);
        opening.setCellValueFactory(new PropertyValueFactory<>("opening"));

        table.getColumns().addAll(
                event,
                site,
                date,
                white,
                black,
                result,
                opening
        );
    }

    //this will take all of the files in the current working directory and add them into the chessDb
    private void readFilesIntoDb() {
        File[] files = new PgnFileFetcher().getFiles();

        for (File f : files) {

            String content = getContent(f);
            ChessGame cg = new ChessGame(
                    tagValue("Event", content),
                    tagValue("Site", content),
                    tagValue("Date", content),
                    tagValue("White", content),
                    tagValue("Black", content),
                    tagValue("Result", content)
            );

            for (String move : getArrayOfMoves(content)) {
                cg.addMove(move);
            }

            chessDb.addGame(cg);

        }
    }

    private static String getContent(File file) {

        String game;

        try {
            Scanner fileScanner = new Scanner(file);
            game = fileScanner.useDelimiter("\\Z").next();
        } catch (FileNotFoundException fnfe) {
            fnfe.printStackTrace();
            return "Something went wrong with the file";
        }

        return game;

    }

    private static String tagValue(String tagName, String game) {

        String gameTags = game.split("\n\n")[0];

        String[] gameTagsSplit = gameTags.split("\\n");
        String line;
        String[] lineSplit;

        for (int i = 0; i < gameTagsSplit.length; i++) {
            line = gameTagsSplit[i];
            if (line.startsWith("[" + tagName)) {
                lineSplit = line.split("\"");
                return lineSplit[1];
            }

            if (!line.startsWith("[")) {
                return "NOT GIVEN";
            }
        }

        return "NOT GIVEN";
    }

    private static String[] getArrayOfMoves(String game) {
        String moves = game.split("\n\n")[1];

        String[] movesSplit = moves.split("[0-9]?[0-9]\\.");
        String[] movesSplitOut = new String[movesSplit.length-1];
        for (int i = 1; i < movesSplit.length; i++) {
            movesSplitOut[i-1] = movesSplit[i].trim();
        }

        return movesSplitOut;
    }
}
