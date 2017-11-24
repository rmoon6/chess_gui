import javafx.application.Application;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class ChessGui extends Application {

    private static final String title = "Chess DB GUI";

    private static TableView<ChessGame> table;
    private static ChessDb chessDb;
    private static ChessGui chessGui;

    public static void main(String[] args) {
        table = new TableView<>();
        chessDb = new ChessDb();
        chessGui = new ChessGui();
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        Scene scene = new Scene(new Group());
        stage.setTitle(title);
        stage.setWidth(1300);
        stage.setHeight(500);

        final Label label = new Label("Games");
        label.setFont(new Font("Arial", 20));

        setColumns();
        table.setItems(getObservableListFromDb());

        final VBox vbox = new VBox();
        vbox.setSpacing(5);
        vbox.setPadding(new Insets(10, 0, 0, 10));
        vbox.getChildren().addAll(label, table);

        ((Group) scene.getRoot()).getChildren().addAll(vbox);

        stage.setScene(scene);
        stage.show();
    }

    private ObservableList<ChessGame> getObservableListFromDb() {
        ObservableList<ChessGame> games = FXCollections.observableArrayList();
        games.add(chessDb.getGames().get(0));
        games.add(chessDb.getGames().get(1));

        return games;
    }

    private void setColumns() {
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

        table.getColumns().addAll(
                event,
                site,
                date,
                white,
                black,
                result
        );
    }
}
