package sudoku;

import java.util.*;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.application.*;
import javafx.util.*;
import javafx.scene.control.*;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.stage.Stage;
import javafx.geometry.*;
import javafx.animation.*;

public class Main extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        // Stage
		primaryStage.setTitle("Sudoku Solver (Pro Edition)");
		Group root = new Group();
		// Create scene
		Scene scene = new Scene(root, 800, 600);
		// Create gridpane
		GridPane grid = new GridPane();

		// Create canvas
		Canvas canvas = new Canvas(scene.getWidth(), scene.getHeight());
		GraphicsContext gc = canvas.getGraphicsContext2D();

		// Header
		HBox hbox = new HBox();
		hbox.setPadding(new Insets(15, 12, 15, 90));
		hbox.setSpacing(10);
		hbox.setStyle("-fx-background-color: #AADDDD;");
		hbox.setPrefWidth(scene.getWidth());

		Puzzle puzzle = new Puzzle(10, 10, 48);

		// Solve button
		Button solveBtn = new Button("Solve/Continue");
		solveBtn.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent e) {
				puzzle.solving = true;
			}
		});
		hbox.getChildren().add(solveBtn);

		// Pause button
		Button pauseBtn = new Button("Pause");
		pauseBtn.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent e) {
				puzzle.solving = false;
			}
		});
		hbox.getChildren().add(pauseBtn);

		// Reset button
		Button resetBtn = new Button("Reset");
		resetBtn.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent e) {
				puzzle.solving = false;
				puzzle.reset();
			}
		});
		hbox.getChildren().add(resetBtn);

		// Canvas mouse click event listener
		canvas.setOnMouseClicked(event -> {
			double x = event.getX();
			double y = event.getY();
			if (event.getButton() == MouseButton.PRIMARY)
				puzzle.changeCell(x, y, 1);
			else
				puzzle.changeCell(x, y, -1);
		});

		// Begin animation loop
		run(gc, puzzle);

		// Add hbox to gridpane
		grid.add(hbox, 0, 0);
		// Add canvas to gridpane
		grid.add(canvas, 0, 1);

		// Add canvas to root
		root.getChildren().add(grid);

		// Create scene with group and show
        primaryStage.setScene(scene);
        primaryStage.show();
    }

	private void run(GraphicsContext gc, Puzzle puzzle) {
		KeyFrame k = new KeyFrame(Duration.millis(1),
			e-> {
				clearCanvas(gc);
				puzzle.update();
				puzzle.draw(gc);
			}
		);
		Timeline t = new Timeline(k);
		t.setCycleCount(Timeline.INDEFINITE);
		t.play();
	}

	private void clearCanvas(GraphicsContext gc) {
		// Clear the canvas
        gc.setFill(Color.rgb(230, 230, 230));
        gc.fillRect(0, 0, 800, 600);
    }
}
