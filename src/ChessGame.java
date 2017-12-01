import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.util.ArrayList;
import java.util.List;

public class ChessGame {

    private StringProperty event = new SimpleStringProperty(this, "NA");
    private StringProperty site = new SimpleStringProperty(this, "NA");
    private StringProperty date = new SimpleStringProperty(this, "NA");
    private StringProperty white = new SimpleStringProperty(this, "NA");
    private StringProperty black = new SimpleStringProperty(this, "NA");
    private StringProperty result = new SimpleStringProperty(this, "NA");
    private StringProperty opening = new SimpleStringProperty(this, "NA");
    private List<String> moves;

    public ChessGame(String event, String site, String date,
                     String white, String black, String result) {
        this.event.set(event);
        this.site.set(site);
        this.date.set(date);
        this.white.set(white);
        this.black.set(black);
        this.result.set(result);
        this.opening.set("");   //this will start out as nothing and then get populated
        moves = new ArrayList<>();
    }

    public String getMovesAsString() {
        String outString = "";
        for (String move : moves) {
            outString += (move + "\n");
        }
        return outString;
    }

    private enum Opening {
        GIUOCO(
                new String[] {"e4 e5", "Nf3 Nc6", "Bc4 Bc5"},
                "Giuoco Piano"
        ),
        RUY(
                new String[] {"e4 e5", "Nf3 Nc6", "Bb5"},
                "Ruy Lopez"
        ),
        SICILIAN(
                new String[] {"e4 c5"},
                "Sicilian Defense"
        ),
        QUEEN_GAMBIT(
                new String[] {"d4 d5", "c4"},
                "Queen\'s Gambit"
        ),
        INDIAN(
                new String[] {"d4 Nf6"},
                "Indian Defense"
        ),
        PHILIDOR(
                new String[] {"e4 e5", "Nf3 d6"},
                "Philidor Defense"
        );


        private final String[] openingMoves;
        private final String prettyName;

        Opening(String[] openingMoves, String prettyName) {
            this.openingMoves = openingMoves;
            this.prettyName = prettyName;
        }

        public static Opening getMatchingOpening(List<String> moves) {
            boolean foundMatch = true;

            for (Opening o : Opening.values()) {
                if (o.openingMoves.length > moves.size()) {
                    continue;
                }

                foundMatch = true;

                for (int i = 0; i < o.openingMoves.length; i++) {
                    if (!moves.get(i).startsWith(o.openingMoves[i])) {
                        foundMatch = false;
                        break;
                    }
                }

                if (foundMatch) {
                    return o;
                }
            }

            return null;
        }
    }

    private void setOpening() {
        try {
            this.opening.set(Opening.getMatchingOpening(moves).prettyName);
        } catch (NullPointerException npe) {
            //oh well
        }
    }

    //not super effecient, but I guess I can just set the opening every time a move is added
    public void addMove(String move) {
        moves.add(move);
        setOpening();
    }

    public String getMove(int n) {
        return moves.get(n - 1);
    }

    public String getEvent() {
        return event.get();
    }

    public String getSite() {
        return site.get();
    }

    public String getDate() {
        return date.get();
    }

    public String getWhite() {
        return white.get();
    }

    public String getBlack() {
        return black.get();
    }

    public String getResult() {
        return result.get();
    }

    public String getOpening() {
        return opening.get();
    }
}
