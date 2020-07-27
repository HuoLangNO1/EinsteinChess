package EinsteinChess;

import javafx.application.Application;
import javafx.stage.Stage;


public class Main extends Application{
	
	public static void main(String[] args) {
		Application.launch(args);
	}
	
	
	public void start(Stage primaryStage) throws Exception {
		
		ChessBoard board = new ChessBoard();
		primaryStage.setScene(board.showBoard());
        primaryStage.setTitle("Einstein");
        primaryStage.show();
	}
}
