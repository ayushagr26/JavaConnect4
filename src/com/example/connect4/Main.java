package com.example.connect4;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class Main extends Application {

	private Controller controller;

	@Override
	public void start(Stage primaryStage) throws Exception {

		FXMLLoader loader = new FXMLLoader(getClass().getResource("game.fxml"));
		GridPane rootGridPane = loader.load();
		controller = loader.getController();
		controller.createPlayground();
		Scene scene = new Scene(rootGridPane);
		MenuBar menuBar = createMenu();
		menuBar.prefWidthProperty().bind(primaryStage.widthProperty());
		Pane menuPane = (Pane) rootGridPane.getChildren().get(0);
		menuPane.getChildren().add(menuBar);
		primaryStage.setScene(scene);
		primaryStage.setTitle("Connect Four");
		primaryStage.setResizable(false);
		primaryStage.show();
	}

	private MenuBar createMenu() {
		Menu fileMenu = new Menu("File");
		Menu helpMenu = new Menu("Help");
		MenuItem newGame = new MenuItem("New Game");
		newGame.setOnAction(event -> controller.resetGame());
		MenuItem resetGame = new MenuItem("Reset Game");
		resetGame.setOnAction(event -> controller.resetGame());
		SeparatorMenuItem separatorMenuItemFile = new SeparatorMenuItem();
		SeparatorMenuItem separatorMenuItemHelp = new SeparatorMenuItem();
		MenuItem exitGame = new MenuItem("Exit Game");
		exitGame.setOnAction(event -> {
			Platform.exit();
			System.exit(0);
		});
		MenuItem aboutApp = new MenuItem("About App");
		aboutApp.setOnAction(event -> {
			Alert about = new Alert(Alert.AlertType.INFORMATION);
			about.setTitle("About Connect4");
			about.setHeaderText("How To Play?");
			about.setContentText("Connect Four is a two-player connection game in which the players first choose a color and then take turns dropping colored discs from the top into a seven-column, six-row vertically suspended grid. The pieces fall straight down, occupying the next available space within the column. The objective of the game is to be the first to form a horizontal, vertical, or diagonal line of four of one's own discs. Connect Four is a solved game. The first player can always win by playing the right moves.\n");
			about.show();
		});

		MenuItem aboutMe = new MenuItem("About Me");
		aboutMe.setOnAction(event -> {
			Alert about = new Alert(Alert.AlertType.INFORMATION);
			about.setTitle("About the Developer");
			about.setHeaderText("Ayush Agrawal");
			about.setContentText("Learning new things!");
			about.show();
		});

		MenuBar menuBar = new MenuBar();
		menuBar.getMenus().addAll(fileMenu, helpMenu);
		fileMenu.getItems().addAll(newGame, resetGame, separatorMenuItemFile, exitGame);
		helpMenu.getItems().addAll(aboutApp, separatorMenuItemHelp, aboutMe);
		return menuBar;
	}


	public static void main(String[] args) {
		launch(args);
	}
}
