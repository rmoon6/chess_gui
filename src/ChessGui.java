import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
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

import java.util.List;

/**
 * THIS DOES NOT INCLUDE THE EXTRA CREDIT FOR RIGHT NOW
 *
 * I knew pretty much nothing about javaFX before watching a tutorial by some hilarious guy
 * named Bucky from upstate NY. Here is a link to it:
 *
 *      https://www.youtube.com/watch?v=q1LEN2assfU
 */
public class ChessGui extends Application {

    private static final String title = "Chess DB GUI";

    private static ChessDb chessDb;
    private static ChessGui chessGui;

    private static Scene sceneHome;
    private static TableView<ChessGame> table;

    public static void main(String[] args) {
        table = new TableView<>();
        chessDb = new ChessDb();
        chessGui = new ChessGui();
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        stage.setTitle(title);
        stage.setWidth(1500);
        stage.setHeight(600);

        initializeHomeScene(stage);

        stage.setScene(sceneHome);
        stage.show();
    }

    private static Scene getGameViewScene(Stage stage, ChessGame selectedGame) {

        Scene gameViewScene = new Scene(new Group());

        Text titleText = new Text();
        titleText.setText("Breakdown of the game's moves:");

        Text text = new Text();
        text.setText("Event: " + selectedGame.getEvent() + "\n"
                + "Site: " + selectedGame.getSite() + "\n"
                + "Date: " + selectedGame.getDate() + "\n"
                + "White: " + selectedGame.getWhite() + "\n"
                + "Black: " + selectedGame.getBlack() + "\n"
                + "Result: " + selectedGame.getResult() + "\n" + "\n"
                + "Moves:\n" + selectedGame.getMovesAsString());

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setVmax(200);
        scrollPane.setPrefSize(400, 440);
        scrollPane.setContent(text);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);

        Button backButton = new Button();
        backButton.setText("Back");
        backButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                stage.setScene(sceneHome);
            }
        });

        final VBox vbox = new VBox();
        vbox.setSpacing(5);
        vbox.setPadding(new Insets(10, 0, 0, 10));
        vbox.getChildren().addAll(titleText, scrollPane, backButton);

        ((Group) gameViewScene.getRoot()).getChildren().addAll(vbox);

        return gameViewScene;
    }

    private static void initializeHomeScene(Stage stage) {
        sceneHome = new Scene(new Group());

        final Label label = new Label("Games");
        label.setFont(new Font("Arial", 20));

        setColumns();
        table.setItems(getObservableListFromDb());

        Button viewGameButton = new Button();
        viewGameButton.setText("View Game");
        viewGameButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                ChessGame selectedGame = table.getSelectionModel().getSelectedItem();
                if (selectedGame != null) {
                    stage.setScene(getGameViewScene(stage, selectedGame));
                }
            }
        });

        Button dismissButton = new Button();
        dismissButton.setText("Dismiss");
        dismissButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                System.exit(0);
            }
        });

        final HBox hbox = new HBox();
        hbox.setSpacing(5);
        hbox.setPadding(new Insets(10, 0, 0, 10));
        hbox.getChildren().addAll(viewGameButton, dismissButton);

        final VBox vbox = new VBox();
        vbox.setSpacing(5);
        vbox.setPadding(new Insets(10, 0, 0, 10));
        vbox.getChildren().addAll(label, table, hbox);

        ((Group) sceneHome.getRoot()).getChildren().addAll(vbox);
    }

    private static ObservableList<ChessGame> getObservableListFromDb() {
        ObservableList<ChessGame> games = FXCollections.observableArrayList();
        games.add(chessDb.getGames().get(0));
        games.add(chessDb.getGames().get(1));

        System.out.println(chessDb.getGames().get(0).getOpening());
        System.out.println(chessDb.getGames().get(1).getOpening());

        return games;
    }

    private static void setColumns() {
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
}
