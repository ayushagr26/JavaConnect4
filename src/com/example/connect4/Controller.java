package com.example.connect4;

import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Point2D;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.util.Duration;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Controller implements Initializable {

	private static final int COLUMNS = 7;
	private static final int ROWS = 6;
	private static final int CIRCLE_DIAMETER = 80;
	private static final String DISC_COLOR1 = "#24303E";
	private static final String DISC_COLOR2 = "#4CAA88";
	private static String PlayerOne = "Player One";
	private static String PlayerTwo = "Player Two";
	private boolean isPlayerOneTurn = true;

	private final Disc[][] insertedDiscsArray = new Disc[ROWS][COLUMNS];


	@FXML
	public GridPane rootGridPane;

	@FXML
	public Pane insertedDiscsPane;

	@FXML
	public Pane rootPane;

	@FXML
	public Label labelName;

	@FXML
	public TextField p1TextField, p2TextField;

	@FXML
	public Button setNamesButton;

	private boolean isAllowedToInsert = true;

	public void createPlayground() {

		Platform.runLater(() -> setNamesButton.requestFocus());
		Shape rectangleWithHoles = createGameStructuralGrid();
		rectangleWithHoles.setFill(Color.WHITE);
		rootGridPane.add(rectangleWithHoles, 0, 1);
		createClickableButtons();
		setNamesButton.setOnAction(event -> {
			if (!p1TextField.getText().equals("")) {
				PlayerOne = p1TextField.getText();
			}
			if (!p2TextField.getText().equals("")) {
				PlayerTwo = p2TextField.getText();
			}
			if (isPlayerOneTurn) {
				labelName.setText(PlayerOne);
			} else {
				labelName.setText(PlayerTwo);
			}
		});

	}

	private Shape createGameStructuralGrid() {
		Shape rectangleWithHoles = new Rectangle((COLUMNS + 1) * (CIRCLE_DIAMETER), (ROWS + 1) * (CIRCLE_DIAMETER));
		for (int row = 0; row < ROWS; row++) {
			for (int col = 0; col < COLUMNS; col++) {
				Circle circle = new Circle();
				circle.setRadius(CIRCLE_DIAMETER / 2.0);
				circle.setCenterX(CIRCLE_DIAMETER / 2.0);
				circle.setCenterY(CIRCLE_DIAMETER / 2.0);
				circle.setSmooth(true);
				circle.setTranslateX((col * (CIRCLE_DIAMETER + 5)) + CIRCLE_DIAMETER / 4.0);
				circle.setTranslateY((row * (CIRCLE_DIAMETER + 5)) + CIRCLE_DIAMETER / 4.0);
				rectangleWithHoles = Shape.subtract(rectangleWithHoles, circle);
			}
		}
		return rectangleWithHoles;
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {

	}


	public void createClickableButtons() {

		List<Rectangle> rectangleList = new ArrayList<>();

		for (int col = 0; col < COLUMNS; col++) {
			Rectangle rectangle = new Rectangle((CIRCLE_DIAMETER), (ROWS + 1) * (CIRCLE_DIAMETER));
			rectangle.setFill(Color.TRANSPARENT);
			rectangle.setTranslateX((col * (CIRCLE_DIAMETER + 5)) + CIRCLE_DIAMETER / 4.0);
			rectangle.setOnMouseEntered(event -> rectangle.setFill(Color.valueOf("#eeeeee26")));
			int column = col;
			rectangle.setOnMouseClicked(event -> {
				if (isAllowedToInsert) {
					isAllowedToInsert = false;
					insertDisc(new Disc(isPlayerOneTurn), column);
				}
			});
			rectangle.setOnMouseExited(event -> rectangle.setFill(Color.TRANSPARENT));
			rectangleList.add(rectangle);
			rootGridPane.add(rectangle, 0, 1);
		}

	}

	private void insertDisc(Disc disc, int column) {
		int row = ROWS - 1;
		while (row >= 0) {
			if (insertedDiscsArray[row][column] == null) {
				break;
			}
			row--;
		}
		if (row < 0) {
			return;
		}
		insertedDiscsArray[row][column] = disc;
		insertedDiscsPane.getChildren().add(disc);
		disc.setTranslateX((column * (CIRCLE_DIAMETER + 5)) + CIRCLE_DIAMETER / 4.0);
		TranslateTransition translateTransition = new TranslateTransition(Duration.seconds(0.5 * ((row + 1) / 5.0)), disc);
		translateTransition.setToY(((row) * (CIRCLE_DIAMETER + 5)) + CIRCLE_DIAMETER / 4.0);
		int currentRow = row;
		translateTransition.setOnFinished(event -> {
			isAllowedToInsert = true;
			isPlayerOneTurn = !isPlayerOneTurn;
			if (gameEnded(currentRow, column)) {
				gameOver();
			} else {
				labelName.setText(isPlayerOneTurn ? PlayerOne : PlayerTwo);
			}
		});
		translateTransition.play();
	}

	private void gameOver() {
		String winner = !isPlayerOneTurn ? PlayerOne : PlayerTwo;
		Alert win = new Alert(Alert.AlertType.INFORMATION);
		win.setTitle("Connect Four");
		win.setHeaderText("The Winner is: " + winner);
		win.setContentText("Want to play again? ");
		ButtonType yes = new ButtonType("Yes");
		ButtonType no = new ButtonType("No, Exit");
		win.getButtonTypes().setAll(yes, no);
		Platform.runLater(() -> {
			Optional<ButtonType> isClick = win.showAndWait();
			if (isClick.isPresent() && isClick.get() == yes) {
				resetGame();
			} else {
				Platform.exit();
				System.exit(0);
			}
		});

	}

	public void resetGame() {
		insertedDiscsPane.getChildren().clear();
		for (int row = 0; row < ROWS; row++) {
			for (int col = 0; col < COLUMNS; col++) {
				insertedDiscsArray[row][col] = null;
			}
		}
		isPlayerOneTurn = true;
		labelName.setText(PlayerOne);
		createPlayground();
	}

	private boolean gameEnded(int row, int column) {
		List<Point2D> verticalPoints = IntStream.rangeClosed(row - 3, row + 3).mapToObj(r -> new Point2D(r, column))
				.collect((Collectors.toList()));
		List<Point2D> horizontalPoints = IntStream.rangeClosed(column - 3, column + 3).mapToObj(r -> new Point2D(row, r))
				.collect((Collectors.toList()));
		Point2D startPoint1 = new Point2D(row - 3, column + 3);
		List<Point2D> diagonal1Points = IntStream.rangeClosed(0, 6).mapToObj(i -> startPoint1.add(i, -i))
				.collect((Collectors.toList()));
		Point2D startPoint2 = new Point2D(row - 3, column - 3);
		List<Point2D> diagonal2Points = IntStream.rangeClosed(0, 6).mapToObj(i -> startPoint2.add(i, i))
				.collect((Collectors.toList()));

		return checkCombinations(verticalPoints) || checkCombinations(horizontalPoints)
				|| checkCombinations(diagonal1Points) || checkCombinations(diagonal2Points);
	}

	private boolean checkCombinations(List<Point2D> points) {
		int chain = 0;
		for (Point2D point : points) {
			int rowIndexForArray = (int) point.getX();
			int columnIndexForArray = (int) point.getY();
			Disc disc = getDiscIfPresent(rowIndexForArray, columnIndexForArray);
			if (disc != null && disc.isPlayerOneMove == isPlayerOneTurn) {
				chain++;
				if (chain == 4) return true;
			} else {
				chain = 0;
			}
		}

		return false;
	}

	private Disc getDiscIfPresent(int row, int col) {
		if (row >= ROWS || row < 0 || col >= COLUMNS || col < 0) {
			return null;
		}
		return insertedDiscsArray[row][col];
	}

	public static class Disc extends Circle {
		private final boolean isPlayerOneMove;

		public Disc(boolean isPlayerOneMove) {
			this.isPlayerOneMove = !isPlayerOneMove;
			setFill(isPlayerOneMove ? Color.valueOf(DISC_COLOR1) : Color.valueOf(DISC_COLOR2));
			setRadius(CIRCLE_DIAMETER / 2.0);
			setCenterX(CIRCLE_DIAMETER / 2.0);
			setCenterY(CIRCLE_DIAMETER / 2.0);
		}
	}
}