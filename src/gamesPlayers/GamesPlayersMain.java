// Package declaration
package gamesPlayers;

/*
 **************************************************************************************
 *                                                                                    *
 * Example of Java application that allows the players to submit information          *
 * about themselves and the games that they are playing on-line.                      *
 * The information is stored in a SQL Server database.                                *
 *                                                                                    *
 **************************************************************************************
 */

// Libraries used by this class
// JavaFX is a set of graphics and media packages
// to create rich client applications:

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.layout.Pane;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.sql.*;
import java.util.LinkedList;

// Games_Players_Start class inherits from Application class
public class GamesPlayersMain extends Application {

	/*
	 * ******************************** * * Global fields for this class * *
	 * ******************************** *
	 */

	private static final ObservableList<String> provinceList = FXCollections.observableArrayList();
	private static TextField textFirstName, textLastName, textAddress, textPostalCode, textPhoneNumber, textGameTitle;
	private static TextField updTextFirstName, updTextLastName, updTextAddress, updTextPostalCode, updTextPhoneNumber,
			updTextPlayingDate, updTextGameScore;
	private static TextArea newPlayerResult, newGameResult, updatedResult;
	private static ComboBox<String> provinceComboBox, updProvinceComboBox, playerIdComboBox, updGameComboBox,
			playerIdComboBoxFinal;

	private TableView<Player> table = new TableView<Player>();
	private ObservableList<Player> data = FXCollections
			.observableArrayList(new Player("", "", "", "", "", "", "", "", ""));

	private PreparedStatement pst;

	// JDBC driver name and database URL
	private static final String DRIVER = "com.microsoft.sqlserver.jdbc.SQLServerDriver";

	private static final String DATABASE_URL = "jdbc:sqlserver://localhost\\MSSQLSERVER;database=LDAlmeida_Games_01;integratedSecurity=true;";

	// SQL command to insert data into Player table:
	private static final String INSERT_PLAYER = "INSERT INTO Player (player_id, first_name, last_name, address, postal_code, province, phone_number) VALUES(?,?,?,?,?,?,?)";
	// SQL command to retrieve data from Player table:
	private static final String SELECT_PLAYER = "SELECT * FROM Player";
	// Variable to control the last existing Primary Key in Player table:
	private static int lastPlayerPK;

	// SQL command to insert data into Game table:
	private static final String INSERT_GAME = "INSERT INTO Game (game_id, game_title) VALUES(?,?)";
	// SQL command to retrieve data from Game table:
	private static final String SELECT_GAME = "SELECT * FROM Game";
	// Variable to control the last existing Primary Key in Game table:
	private static int lastGamePK;

	// SQL command to insert data into PlayerAndGame table:
	private static final String INSERT_PLAYERANDGAME = "INSERT INTO PlayerAndGame (player_game_id, game_id, player_id, playing_date, score) VALUES(?,?,?,?,?)";
	// SQL command to retrieve data from PlayerAndGame table:
	private static final String SELECT_PLAYERANDGAME = "SELECT * FROM PlayerAndGame";
	// Variable to control the last existing Primary Key in PlayerAndGame table:
	private static int lastPlayerAndGamePK;

	/*
	 * ***************************** * * start(Stage primaryStage) * *
	 * ***************************** *
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public void start(Stage primaryStage) {

		/*
		 * ***************************************** * * Local instance fields for this
		 * method * * ***************************************** *
		 */
		// Size of the main pane:
		final int PANE_PREF_WIDTH = 1245;
		final int PANE_PREF_HEIGHT = 700;

		// Size of the New Player Info's Pane and its elements
		final int PANE_PLAYER_PREF_WIDTH = 430;
		final int PANE_PLAYER_PREF_HEIGHT = 340;
		final int PLAYER_LABEL_FIELDS_X_POSITION = 10;
		final int PLAYER_TEXT_FIELDS_X_POSITION = 130;
		final int PLAYER_TEXT_FIELDS_PREF_WIDTH = 270;

		// Size of the New Game Info's Pane and its elements
		final int PANE_GAME_PREF_WIDTH = 430;
		final int PANE_GAME_PREF_HEIGHT = 200;
		final int GAME_LABEL_FIELDS_X_POSITION = 10;
		final int GAME_TEXT_FIELDS_X_POSITION = 130;
		final int GAME_TEXT_FIELDS_PREF_WIDTH = 270;

		// Size of the Update Pane and its elements
		final int UPD_PANE_PREF_WIDTH = 790;
		final int UPD_PANE_PREF_HEIGHT = 300;
		final int UPD_PANE_LABEL_FIELDS_X_POSITION1 = 10;
		final int UPD_PANE_TEXT_FIELDS_X_POSITION1 = 140;
		final int UPD_PANE_LABEL_FIELDS_X_POSITION2 = 400;
		final int UPD_PANE_TEXT_FIELDS_X_POSITION2 = 540;
		final int UPD_PANE_TEXT_FIELDS_PREF_WIDTH = 230;

		provinceList.addAll("AB", "BC", "MB", "NB", "NL", "NS", "ON", "PE", "QC", "SK", "NT", "NU", "YT");

		/*
		 * ************************************** * * Instantiating mainPane and setting
		 * * its width and height * * ************************************** *
		 */
		Pane mainPane;
		mainPane = new Pane();
		mainPane.setPrefSize(PANE_PREF_WIDTH, PANE_PREF_HEIGHT);

		/*
		 * ******************** * * Games' Logo Pane * * ******************** *
		 */
		// Game's Logo Icon:
		ImageView gamesLogo = new ImageView(new Image(getClass().getResourceAsStream("gamesIcon.jpg")));

		// Creation of the Games Logo Pane with the same background of the form:
		// (in case of window resize from user)
		Pane gamesLogoPane = new Pane();
		gamesLogoPane.setStyle("-fx-background-color: #f4f4f4");
		gamesLogoPane.setPrefSize(PANE_PREF_WIDTH, PANE_PREF_HEIGHT / 6);
		gamesLogoPane.setLayoutX(2);

		// Addition of the image into the Games Logo Pane:
		gamesLogoPane.getChildren().add(gamesLogo);

		/*
		 * ************************** * * Insert New Player Pane * *
		 * ************************** *
		 */
		// Creation of a Label to act as a title for the new player info:
		Label playerData = new Label("1- Insert New Player");
		playerData.setStyle(
				"-fx-font-size: 14px; -fx-font-weight: bold;" + "-fx-font-family: 'Copperplate Gothic Light'; ");
		playerData.setLayoutX(PLAYER_TEXT_FIELDS_X_POSITION);
		playerData.setLayoutY(10);

		// Creation of a Label for First Name:
		Label labelFirstName = new Label("First Name: ");
		labelFirstName.setStyle("-fx-font-size: 14px; -fx-font-family: 'Courier New'; -fx-font-weight: 200 ");
		labelFirstName.setLayoutX(PLAYER_LABEL_FIELDS_X_POSITION);
		labelFirstName.setLayoutY(playerData.getLayoutY() + 30);

		// Creation of a TextField for First Name:
		textFirstName = new TextField("");
		textFirstName.setStyle("-fx-font-size: 14px; -fx-font-family: 'Courier New'");
		textFirstName.setLayoutX(PLAYER_TEXT_FIELDS_X_POSITION);
		textFirstName.setLayoutY(playerData.getLayoutY() + 30);
		textFirstName.setPrefWidth(PLAYER_TEXT_FIELDS_PREF_WIDTH);
		textFirstName.setTooltip(new Tooltip("Please enter your First Name here"));

		// Creation of a Label for Last Name:
		Label labelLastName = new Label("Last Name: ");
		labelLastName.setStyle("-fx-font-size: 14px; -fx-font-family: 'Courier New'; -fx-font-weight: 200 ");
		labelLastName.setLayoutX(PLAYER_LABEL_FIELDS_X_POSITION);
		labelLastName.setLayoutY(labelFirstName.getLayoutY() + 30);

		// Creation of a TextField for Last Name:
		textLastName = new TextField("");
		textLastName.setStyle("-fx-font-size: 14px; -fx-font-family: 'Courier New'");
		textLastName.setLayoutX(PLAYER_TEXT_FIELDS_X_POSITION);
		textLastName.setLayoutY(labelFirstName.getLayoutY() + 30);
		textLastName.setPrefWidth(PLAYER_TEXT_FIELDS_PREF_WIDTH);
		textLastName.setTooltip(new Tooltip("Please enter your Last Name here"));

		// Creation of a Label for Address:
		Label labelAddress = new Label("Address: ");
		labelAddress.setStyle("-fx-font-size: 14px; -fx-font-family: 'Courier New'; -fx-font-weight: 200 ");
		labelAddress.setLayoutX(PLAYER_LABEL_FIELDS_X_POSITION);
		labelAddress.setLayoutY(labelLastName.getLayoutY() + 30);

		// Creation of a TextField for Address:
		textAddress = new TextField("");
		textAddress.setStyle("-fx-font-size: 14px; -fx-font-family: 'Courier New'");
		textAddress.setLayoutX(PLAYER_TEXT_FIELDS_X_POSITION);
		textAddress.setLayoutY(labelLastName.getLayoutY() + 30);
		textAddress.setPrefWidth(PLAYER_TEXT_FIELDS_PREF_WIDTH);
		textAddress.setTooltip(new Tooltip("Please enter your Address here"));

		// Creation of a Label for Postal Code:
		Label labelPostalCode = new Label("Postal Code: ");
		labelPostalCode.setStyle("-fx-font-size: 14px; -fx-font-family: 'Courier New'; -fx-font-weight: 200 ");
		labelPostalCode.setLayoutX(PLAYER_LABEL_FIELDS_X_POSITION);
		labelPostalCode.setLayoutY(labelAddress.getLayoutY() + 30);

		// Creation of a TextField for Postal Code:
		textPostalCode = new TextField("");
		textPostalCode.setStyle("-fx-font-size: 14px; -fx-font-family: 'Courier New'");
		textPostalCode.setLayoutX(PLAYER_TEXT_FIELDS_X_POSITION);
		textPostalCode.setLayoutY(labelAddress.getLayoutY() + 30);
		textPostalCode.setPrefWidth(PLAYER_TEXT_FIELDS_PREF_WIDTH);
		textPostalCode.setTooltip(new Tooltip("Please enter your Postal Code here"));

		// Creation of a Label for Province:
		Label labelProvince = new Label("Province: ");
		labelProvince.setStyle("-fx-font-size: 14px; -fx-font-family: 'Courier New'; -fx-font-weight: 200 ");
		labelProvince.setLayoutX(PLAYER_LABEL_FIELDS_X_POSITION);
		labelProvince.setLayoutY(labelPostalCode.getLayoutY() + 30);

		// Creation of a ComboBox for Province:
		provinceComboBox = new ComboBox(provinceList);
		provinceComboBox.setStyle("-fx-font-size: 14px; -fx-font-family: 'Courier New'");
		provinceComboBox.setLayoutX(PLAYER_TEXT_FIELDS_X_POSITION);
		provinceComboBox.setLayoutY(labelPostalCode.getLayoutY() + 30);
		provinceComboBox.setPrefWidth(PLAYER_TEXT_FIELDS_PREF_WIDTH);
		provinceComboBox.setTooltip(new Tooltip("Please choose your Province here"));
		provinceComboBox.getSelectionModel().clearSelection();

		// Creation of a Label for Phone Number:
		Label labelPhoneNumber = new Label("Phone Number: ");
		labelPhoneNumber.setStyle("-fx-font-size: 14px; -fx-font-family: 'Courier New'; -fx-font-weight: 200 ");
		labelPhoneNumber.setLayoutX(PLAYER_LABEL_FIELDS_X_POSITION);
		labelPhoneNumber.setLayoutY(labelProvince.getLayoutY() + 30);

		// Creation of a TextField for Phone Number:
		textPhoneNumber = new TextField("");
		textPhoneNumber.setStyle("-fx-font-size: 14px; -fx-font-family: 'Courier New'");
		textPhoneNumber.setLayoutX(PLAYER_TEXT_FIELDS_X_POSITION);
		textPhoneNumber.setLayoutY(labelProvince.getLayoutY() + 30);
		textPhoneNumber.setPrefWidth(PLAYER_TEXT_FIELDS_PREF_WIDTH);
		textPhoneNumber.setTooltip(new Tooltip("Please enter your Phone Number here"));

		// Creation of a button to submit the new player's info:
		Button submitNewPlayerBtn = new Button("Insert Player");
		submitNewPlayerBtn.setStyle("-fx-font-size: 16px; -fx-font-family: 'Courier New';"
				+ " -fx-font-weight: 600; -fx-text-alignment: center; -fx-border-radius: 10;"
				+ " -fx-background-radius: 10; -fx-border-color: #223322; -fx-base: #90ee90;");
		submitNewPlayerBtn.setLayoutX(PLAYER_LABEL_FIELDS_X_POSITION + 13);
		submitNewPlayerBtn.setLayoutY(labelPhoneNumber.getLayoutY() + 40);

		// Creation of a listener for the button to submit the new player's info:
		submitNewPlayerBtn.setOnAction(event -> submitNewPlayerBtnHandler());

		// Creation of a button to clear the new player's form:
		Button clearPlayerBtn = new Button("Clear form");
		clearPlayerBtn.setStyle("-fx-font-size: 16px; -fx-font-family: 'Courier New';"
				+ " -fx-font-weight: 400; -fx-text-alignment: center; -fx-border-radius: 10;"
				+ " -fx-background-radius: 10; -fx-border-color: #223322; -fx-base: #90eeee;");
		clearPlayerBtn.setLayoutX(PLAYER_LABEL_FIELDS_X_POSITION + 200);
		clearPlayerBtn.setLayoutY(labelPhoneNumber.getLayoutY() + 40);

		// Creation of a listener for the button to clear the new player's form:
		clearPlayerBtn.setOnAction(event -> clearNewPlayerBtnHandler());

		// Creation of a TextArea to display the result:
		newPlayerResult = new TextArea("Result...");
		newPlayerResult.setPrefSize(375, 7);
		newPlayerResult.setStyle("-fx-font-size: 14px; -fx-font-family: 'Courier New';"
				+ " -fx-text-alignment: center; -fx-border-color: #223322;"
				+ " -fx-base: #eecccc; -fx-font-color: rgb(0,0,0); ");
		newPlayerResult.setLayoutX(PLAYER_LABEL_FIELDS_X_POSITION + 13);
		newPlayerResult.setLayoutY(submitNewPlayerBtn.getLayoutY() + 50);

		// Creation of the Insert New Player Pane:
		Pane newPlayerInfoPane = new Pane();
		newPlayerInfoPane.setStyle("-fx-background-color: #ddddee; -fx-border-radius: 25;"
				+ " -fx-background-radius: 25; -fx-border-color: rgb(28, 132, 203);");
		newPlayerInfoPane.setPrefSize(PANE_PLAYER_PREF_WIDTH, PANE_PLAYER_PREF_HEIGHT);
		newPlayerInfoPane.setLayoutX(5);
		newPlayerInfoPane.setLayoutY(PANE_PREF_HEIGHT / 5 - 3);

		// Addition of all elements for the Insert New Player Pane into it:
		newPlayerInfoPane.getChildren().add(playerData);
		newPlayerInfoPane.getChildren().add(labelFirstName);
		newPlayerInfoPane.getChildren().add(textFirstName);
		newPlayerInfoPane.getChildren().add(labelLastName);
		newPlayerInfoPane.getChildren().add(textLastName);
		newPlayerInfoPane.getChildren().add(labelAddress);
		newPlayerInfoPane.getChildren().add(textAddress);
		newPlayerInfoPane.getChildren().add(labelPostalCode);
		newPlayerInfoPane.getChildren().add(textPostalCode);
		newPlayerInfoPane.getChildren().add(labelProvince);
		newPlayerInfoPane.getChildren().add(provinceComboBox);
		newPlayerInfoPane.getChildren().add(labelPhoneNumber);
		newPlayerInfoPane.getChildren().add(textPhoneNumber);
		newPlayerInfoPane.getChildren().add(submitNewPlayerBtn);
		newPlayerInfoPane.getChildren().add(clearPlayerBtn);
		newPlayerInfoPane.getChildren().add(newPlayerResult);

		/*
		 * ************************ * * New Game Info's Pane * *
		 * ************************ *
		 */
		// Creation of a Label to act as a title for the new game:
		Label gameData = new Label("2- Insert New Game");
		gameData.setStyle(
				"-fx-font-size: 14px; -fx-font-weight: bold;" + "-fx-font-family: 'Copperplate Gothic Light'; ");
		gameData.setLayoutX(GAME_TEXT_FIELDS_X_POSITION);
		gameData.setLayoutY(10);

		// Creation of a Label for Game Title:
		Label labelGameTitle = new Label("Game Title: ");
		labelGameTitle.setStyle("-fx-font-size: 14px; -fx-font-family: 'Courier New'; -fx-font-weight: 200 ");
		labelGameTitle.setLayoutX(GAME_LABEL_FIELDS_X_POSITION);
		labelGameTitle.setLayoutY(gameData.getLayoutY() + 30);

		// Creation of a TextField for Game Title:
		textGameTitle = new TextField("");
		textGameTitle.setStyle("-fx-font-size: 14px; -fx-font-family: 'Courier New'");
		textGameTitle.setLayoutX(GAME_TEXT_FIELDS_X_POSITION);
		textGameTitle.setLayoutY(gameData.getLayoutY() + 30);
		textGameTitle.setPrefWidth(GAME_TEXT_FIELDS_PREF_WIDTH);
		textGameTitle.setTooltip(new Tooltip("Please enter the Game Title here"));

		// Creation of a button to submit the new game's info:
		Button submitNewGameBtn = new Button("Insert game");
		submitNewGameBtn.setStyle("-fx-font-size: 16px; -fx-font-family: 'Courier New';"
				+ " -fx-font-weight: 600; -fx-text-alignment: center; -fx-border-radius: 10;"
				+ " -fx-background-radius: 10; -fx-border-color: #223322; -fx-base: #90ee90;");
		submitNewGameBtn.setLayoutX(GAME_LABEL_FIELDS_X_POSITION + 13);
		submitNewGameBtn.setLayoutY(labelGameTitle.getLayoutY() + 40);

		// Creation of a listener for the button to submit the new game's info:
		submitNewGameBtn.setOnAction(event -> submitNewGameBtnHandler());

		// Creation of a button to clear the new game's form:
		Button clearGameBtn = new Button("Clear form");
		clearGameBtn.setStyle("-fx-font-size: 16px; -fx-font-family: 'Courier New';"
				+ " -fx-font-weight: 400; -fx-text-alignment: center; -fx-border-radius: 10;"
				+ " -fx-background-radius: 10; -fx-border-color: #223322; -fx-base: #90eeee;");
		clearGameBtn.setLayoutX(GAME_LABEL_FIELDS_X_POSITION + 180);
		clearGameBtn.setLayoutY(labelGameTitle.getLayoutY() + 40);

		// Creation of a listener for the button to clear the new game's form:
		clearGameBtn.setOnAction(event -> clearNewGameBtnHandler());

		// Creation of a TextArea to display the result:
		newGameResult = new TextArea("Result...");
		newGameResult.setPrefSize(375, 7);
		newGameResult.setStyle("-fx-font-size: 14px; -fx-font-family: 'Courier New';"
				+ " -fx-text-alignment: center; -fx-border-color: #223322;"
				+ " -fx-base: #eecccc; -fx-font-color: rgb(0,0,0); ");
		newGameResult.setLayoutX(GAME_LABEL_FIELDS_X_POSITION + 13);
		newGameResult.setLayoutY(submitNewGameBtn.getLayoutY() + 50);

		// Creation of the New Game Info Pane:
		Pane newGameInfoPane = new Pane();
		newGameInfoPane.setStyle("-fx-background-color: #ffffcc; -fx-border-radius: 25;"
				+ " -fx-background-radius: 25; -fx-border-color: rgb(28, 132, 203);");
		newGameInfoPane.setPrefSize(PANE_GAME_PREF_WIDTH, PANE_GAME_PREF_HEIGHT);
		newGameInfoPane.setLayoutX(5);
		newGameInfoPane.setLayoutY(PANE_PREF_HEIGHT / 2 + 140);

		// Addition of all elements for the New Game Info Pane into it:
		newGameInfoPane.getChildren().add(gameData);
		newGameInfoPane.getChildren().add(labelGameTitle);
		newGameInfoPane.getChildren().add(textGameTitle);
		newGameInfoPane.getChildren().add(submitNewGameBtn);
		newGameInfoPane.getChildren().add(clearGameBtn);
		newGameInfoPane.getChildren().add(newGameResult);

		/*
		 * ************************************************* * * Pane to update players'
		 * info and played games * * ************************************************* *
		 */
		// Creation of a Label to act as a title for the update pane:
		Label updatePaneLabel = new Label("3 - Update Players' Info and Played Games");
		updatePaneLabel.setStyle(
				"-fx-font-size: 14px; -fx-font-weight: bold;" + "-fx-font-family: 'Copperplate Gothic Light'; ");
		updatePaneLabel.setLayoutX(210);
		updatePaneLabel.setLayoutY(UPD_PANE_LABEL_FIELDS_X_POSITION1);

		// Creation of a Label for player id:
		Label labelPlayerId = new Label("Select Player: ");
		labelPlayerId.setStyle("-fx-font-size: 14px; -fx-font-family: 'Courier New'; -fx-font-weight: 200 ");
		labelPlayerId.setLayoutX(UPD_PANE_LABEL_FIELDS_X_POSITION1);
		labelPlayerId.setLayoutY(updatePaneLabel.getLayoutY() + 35);

		// Creation of a ComboBox for player id:
		playerIdComboBox = new ComboBox(fillPlayerIdNameComboBox());
		playerIdComboBox.setStyle("-fx-font-size: 14px; -fx-font-family: 'Courier New'");
		playerIdComboBox.setLayoutX(UPD_PANE_TEXT_FIELDS_X_POSITION1);
		playerIdComboBox.setLayoutY(updatePaneLabel.getLayoutY() + 30);
		playerIdComboBox.setPrefWidth(UPD_PANE_TEXT_FIELDS_PREF_WIDTH);
		playerIdComboBox.setTooltip(new Tooltip("Please pick up a Player here."));
		playerIdComboBox.getSelectionModel().selectedItemProperty().addListener((options, oldValue, newValue) -> {
			updatePlayerTextFields();
		});

		// Creation of a Label to act as a title for the player update:
		Label updatePlayerLabel = new Label("UPDATE PLAYER...");
		updatePlayerLabel.setStyle(
				"-fx-font-size: 14px; -fx-font-weight: bold;" + "-fx-font-family: 'Copperplate Gothic Light'; ");
		updatePlayerLabel.setLayoutX(UPD_PANE_LABEL_FIELDS_X_POSITION1);
		updatePlayerLabel.setLayoutY(labelPlayerId.getLayoutY() + 40);

		// Creation of a Label for First Name:
		Label updLabelFirstName = new Label("First Name: ");
		updLabelFirstName.setStyle("-fx-font-size: 14px; -fx-font-family: 'Courier New'; -fx-font-weight: 200 ");
		updLabelFirstName.setLayoutX(UPD_PANE_LABEL_FIELDS_X_POSITION1);
		updLabelFirstName.setLayoutY(updatePlayerLabel.getLayoutY() + 30);

		// Creation of a TextField for First Name:
		updTextFirstName = new TextField();
		updTextFirstName.setStyle("-fx-font-size: 14px; -fx-font-family: 'Courier New'");
		updTextFirstName.setLayoutX(UPD_PANE_TEXT_FIELDS_X_POSITION1);
		updTextFirstName.setLayoutY(updatePlayerLabel.getLayoutY() + 30);
		updTextFirstName.setPrefWidth(UPD_PANE_TEXT_FIELDS_PREF_WIDTH);
		updTextFirstName.setTooltip(new Tooltip("Please update your First Name here"));

		// Creation of a Label for Last Name:
		Label updLabelLastName = new Label("Last Name: ");
		updLabelLastName.setStyle("-fx-font-size: 14px; -fx-font-family: 'Courier New'; -fx-font-weight: 200 ");
		updLabelLastName.setLayoutX(UPD_PANE_LABEL_FIELDS_X_POSITION1);
		updLabelLastName.setLayoutY(updLabelFirstName.getLayoutY() + 30);

		// Creation of a TextField for Last Name:
		updTextLastName = new TextField();
		updTextLastName.setStyle("-fx-font-size: 14px; -fx-font-family: 'Courier New'");
		updTextLastName.setLayoutX(UPD_PANE_TEXT_FIELDS_X_POSITION1);
		updTextLastName.setLayoutY(updLabelFirstName.getLayoutY() + 30);
		updTextLastName.setPrefWidth(UPD_PANE_TEXT_FIELDS_PREF_WIDTH);
		updTextLastName.setTooltip(new Tooltip("Please update your Last Name here"));

		// Creation of a Label for Address:
		Label updLabelAddress = new Label("Address: ");
		updLabelAddress.setStyle("-fx-font-size: 14px; -fx-font-family: 'Courier New'; -fx-font-weight: 200 ");
		updLabelAddress.setLayoutX(UPD_PANE_LABEL_FIELDS_X_POSITION1);
		updLabelAddress.setLayoutY(updTextLastName.getLayoutY() + 30);

		// Creation of a TextField for Address:
		updTextAddress = new TextField();
		updTextAddress.setStyle("-fx-font-size: 14px; -fx-font-family: 'Courier New'");
		updTextAddress.setLayoutX(UPD_PANE_TEXT_FIELDS_X_POSITION1);
		updTextAddress.setLayoutY(updTextLastName.getLayoutY() + 30);
		updTextAddress.setPrefWidth(UPD_PANE_TEXT_FIELDS_PREF_WIDTH);
		updTextAddress.setTooltip(new Tooltip("Please update your Address here"));

		// Creation of a Label for Postal Code:
		Label updLabelPostalCode = new Label("Postal Code: ");
		updLabelPostalCode.setStyle("-fx-font-size: 14px; -fx-font-family: 'Courier New'; -fx-font-weight: 200 ");
		updLabelPostalCode.setLayoutX(UPD_PANE_LABEL_FIELDS_X_POSITION1);
		updLabelPostalCode.setLayoutY(updLabelAddress.getLayoutY() + 30);

		// Creation of a TextField for Postal Code:
		updTextPostalCode = new TextField();
		updTextPostalCode.setStyle("-fx-font-size: 14px; -fx-font-family: 'Courier New'");
		updTextPostalCode.setLayoutX(UPD_PANE_TEXT_FIELDS_X_POSITION1);
		updTextPostalCode.setLayoutY(updLabelAddress.getLayoutY() + 30);
		updTextPostalCode.setPrefWidth(UPD_PANE_TEXT_FIELDS_PREF_WIDTH);
		updTextPostalCode.setTooltip(new Tooltip("Please update your Postal Code here"));

		// Creation of a Label for Province:
		Label updLabelProvince = new Label("Province: ");
		updLabelProvince.setStyle("-fx-font-size: 14px; -fx-font-family: 'Courier New'; -fx-font-weight: 200 ");
		updLabelProvince.setLayoutX(UPD_PANE_LABEL_FIELDS_X_POSITION1);
		updLabelProvince.setLayoutY(updLabelPostalCode.getLayoutY() + 30);

		// Creation of a ComboBox for Province:
		updProvinceComboBox = new ComboBox(provinceList);
		updProvinceComboBox.setStyle("-fx-font-size: 14px; -fx-font-family: 'Courier New'");
		updProvinceComboBox.setLayoutX(UPD_PANE_TEXT_FIELDS_X_POSITION1);
		updProvinceComboBox.setLayoutY(updLabelPostalCode.getLayoutY() + 30);
		updProvinceComboBox.setPrefWidth(UPD_PANE_TEXT_FIELDS_PREF_WIDTH);
		updProvinceComboBox.setTooltip(new Tooltip("Please update your Province here"));

		// Creation of a Label for Phone Number:
		Label updLabelPhoneNumber = new Label("Phone Number: ");
		updLabelPhoneNumber.setStyle("-fx-font-size: 14px; -fx-font-family: 'Courier New'; -fx-font-weight: 200 ");
		updLabelPhoneNumber.setLayoutX(UPD_PANE_LABEL_FIELDS_X_POSITION1);
		updLabelPhoneNumber.setLayoutY(updLabelProvince.getLayoutY() + 30);

		// Creation of a TextField for Phone Number:
		updTextPhoneNumber = new TextField();
		updTextPhoneNumber.setStyle("-fx-font-size: 14px; -fx-font-family: 'Courier New'");
		updTextPhoneNumber.setLayoutX(UPD_PANE_TEXT_FIELDS_X_POSITION1);
		updTextPhoneNumber.setLayoutY(updLabelProvince.getLayoutY() + 30);
		updTextPhoneNumber.setPrefWidth(UPD_PANE_TEXT_FIELDS_PREF_WIDTH);
		updTextPhoneNumber.setTooltip(new Tooltip("Please update your Phone Number here"));

		// Creation of a Label to act as a title for the game update:
		Label updatePlayedGameLabel = new Label("UPDATE YOUR PLAYED GAME...");
		updatePlayedGameLabel.setStyle(
				"-fx-font-size: 14px; -fx-font-weight: bold;" + "-fx-font-family: 'Copperplate Gothic Light'; ");
		updatePlayedGameLabel.setLayoutX(UPD_PANE_LABEL_FIELDS_X_POSITION2);
		updatePlayedGameLabel.setLayoutY(60);

		// Creation of a Label for game title:
		Label updGameLabel = new Label("Game Title: ");
		updGameLabel.setStyle("-fx-font-size: 14px; -fx-font-family: 'Courier New'; -fx-font-weight: 200 ");
		updGameLabel.setLayoutX(UPD_PANE_LABEL_FIELDS_X_POSITION2);
		updGameLabel.setLayoutY(updatePlayedGameLabel.getLayoutY() + 35);

		// Creation of a ComboBox for player id:
		updGameComboBox = new ComboBox(fillGameTitleComboBox());
		updGameComboBox.setStyle("-fx-font-size: 14px; -fx-font-family: 'Courier New'");
		updGameComboBox.setLayoutX(UPD_PANE_TEXT_FIELDS_X_POSITION2);
		updGameComboBox.setLayoutY(updatePlayedGameLabel.getLayoutY() + 30);
		updGameComboBox.setPrefWidth(UPD_PANE_TEXT_FIELDS_PREF_WIDTH);
		updGameComboBox.setTooltip(new Tooltip("Please select your game here."));

		// Creation of a Label for Playing Date:
		Label updLabelPlayingDate = new Label("Playing Date: ");
		updLabelPlayingDate.setStyle("-fx-font-size: 14px; -fx-font-family: 'Courier New'; -fx-font-weight: 200 ");
		updLabelPlayingDate.setLayoutX(UPD_PANE_LABEL_FIELDS_X_POSITION2);
		updLabelPlayingDate.setLayoutY(updGameLabel.getLayoutY() + 30);

		// Creation of a TextField for Playing Date:
		updTextPlayingDate = new TextField("");
		updTextPlayingDate.setStyle("-fx-font-size: 14px; -fx-font-family: 'Courier New'");
		updTextPlayingDate.setLayoutX(UPD_PANE_TEXT_FIELDS_X_POSITION2);
		updTextPlayingDate.setLayoutY(updGameLabel.getLayoutY() + 30);
		updTextPlayingDate.setPrefWidth(UPD_PANE_TEXT_FIELDS_PREF_WIDTH);
		updTextPlayingDate.setTooltip(new Tooltip("Please update the Playing Date here"));

		// Creation of a Label for Game Score:
		Label labelGameScore = new Label("Score: ");
		labelGameScore.setStyle("-fx-font-size: 14px; -fx-font-family: 'Courier New'; -fx-font-weight: 200 ");
		labelGameScore.setLayoutX(UPD_PANE_LABEL_FIELDS_X_POSITION2);
		labelGameScore.setLayoutY(updTextPlayingDate.getLayoutY() + 30);

		// Creation of a TextField for Game Score:
		updTextGameScore = new TextField();
		updTextGameScore.setStyle("-fx-font-size: 14px; -fx-font-family: 'Courier New'");
		updTextGameScore.setLayoutX(UPD_PANE_TEXT_FIELDS_X_POSITION2);
		updTextGameScore.setLayoutY(updTextPlayingDate.getLayoutY() + 30);
		updTextGameScore.setPrefWidth(UPD_PANE_TEXT_FIELDS_PREF_WIDTH);
		updTextGameScore.setTooltip(new Tooltip("Please update your Score here"));

		// Creation of a button to update the players:
		Button updatePlayersBtn = new Button("Upd Player");
		updatePlayersBtn.setStyle("-fx-font-size: 14px; -fx-font-family: 'Courier New';"
				+ " -fx-font-weight: 600; -fx-text-alignment: center; -fx-border-radius: 10;"
				+ " -fx-background-radius: 10; -fx-border-color: #223322; -fx-base: #90ee90;");
		updatePlayersBtn.setLayoutX(UPD_PANE_LABEL_FIELDS_X_POSITION2 + 3);
		updatePlayersBtn.setLayoutY(labelGameScore.getLayoutY() + 40);

		// Creation of a listener for the button to update the players and played games:
		updatePlayersBtn.setOnAction(event -> updatePlayersBtnHandler());

		// Creation of a button to update the played games:
		Button insertNewPlayerAndGameBtn = new Button("Played Game");
		insertNewPlayerAndGameBtn.setStyle("-fx-font-size: 14px; -fx-font-family: 'Courier New';"
				+ " -fx-font-weight: 600; -fx-text-alignment: center; -fx-border-radius: 10;"
				+ " -fx-background-radius: 10; -fx-border-color: #223322; -fx-base: #a0ee00;");
		insertNewPlayerAndGameBtn.setLayoutX(UPD_PANE_LABEL_FIELDS_X_POSITION2 + 123);
		insertNewPlayerAndGameBtn.setLayoutY(labelGameScore.getLayoutY() + 40);

		// Creation of a listener for the button to update the players and played games:
		insertNewPlayerAndGameBtn.setOnAction(event -> insertNewPlayerAndGameBtnHandler());

		// Creation of a button to clear update form:
		Button clearUpdPaneBtn = new Button("Clear form");
		clearUpdPaneBtn.setStyle("-fx-font-size: 14px; -fx-font-family: 'Courier New';"
				+ " -fx-font-weight: 400; -fx-text-alignment: center; -fx-border-radius: 10;"
				+ " -fx-background-radius: 10; -fx-border-color: #223322; -fx-base: #90eeee;");
		clearUpdPaneBtn.setLayoutX(UPD_PANE_LABEL_FIELDS_X_POSITION2 + 250);
		clearUpdPaneBtn.setLayoutY(labelGameScore.getLayoutY() + 40);

		// Creation of a listener for the button to clear the update form:
		clearUpdPaneBtn.setOnAction(event -> clearUpdPaneBtnHandler());

		// Creation of a TextArea to display the result:
		updatedResult = new TextArea("Result...");
		updatedResult.setPrefSize(370, 7);
		updatedResult.setStyle("-fx-font-size: 14px; -fx-font-family: 'Courier New';"
				+ " -fx-text-alignment: center; -fx-border-color: #223322;"
				+ " -fx-base: #eecccc; -fx-font-color: rgb(0,0,0); ");
		updatedResult.setLayoutX(UPD_PANE_LABEL_FIELDS_X_POSITION2 + 3);
		updatedResult.setLayoutY(updatePlayersBtn.getLayoutY() + 50);

		// Creation of the update pane:
		Pane updatePane = new Pane();
		updatePane.setStyle("-fx-background-color: #ddffdd; -fx-border-radius: 25;"
				+ " -fx-background-radius: 25; -fx-border-color: rgb(28, 132, 203);");
		updatePane.setPrefSize(UPD_PANE_PREF_WIDTH, UPD_PANE_PREF_HEIGHT);
		updatePane.setLayoutX(PANE_PLAYER_PREF_WIDTH + 20);
		updatePane.setLayoutY(PANE_PREF_HEIGHT / 4 - 37);

		// Addition of all elements for the update pane into it:
		updatePane.getChildren().add(updatePaneLabel);
		updatePane.getChildren().add(labelPlayerId);
		updatePane.getChildren().add(playerIdComboBox);
		updatePane.getChildren().add(updatePlayerLabel);
		updatePane.getChildren().add(updLabelFirstName);
		updatePane.getChildren().add(updTextFirstName);
		updatePane.getChildren().add(updLabelLastName);
		updatePane.getChildren().add(updTextLastName);
		updatePane.getChildren().add(updLabelAddress);
		updatePane.getChildren().add(updTextAddress);
		updatePane.getChildren().add(updLabelPostalCode);
		updatePane.getChildren().add(updTextPostalCode);
		updatePane.getChildren().add(updLabelProvince);
		updatePane.getChildren().add(updProvinceComboBox);
		updatePane.getChildren().add(updLabelPhoneNumber);
		updatePane.getChildren().add(updTextPhoneNumber);
		updatePane.getChildren().add(updatePlayedGameLabel);
		updatePane.getChildren().add(updGameLabel);
		updatePane.getChildren().add(updGameComboBox);
		updatePane.getChildren().add(updLabelPlayingDate);
		updatePane.getChildren().add(updTextPlayingDate);
		updatePane.getChildren().add(labelGameScore);
		updatePane.getChildren().add(updTextGameScore);
		updatePane.getChildren().add(updatePlayersBtn);
		updatePane.getChildren().add(insertNewPlayerAndGameBtn);
		updatePane.getChildren().add(clearUpdPaneBtn);
		updatePane.getChildren().add(updatedResult);

		/*
		 * ***************** * * Results Table * * ***************** *
		 */

		// Creation of a Label for the result:
		Label generalResultLbl = new Label("Show complete info for player:");
		generalResultLbl.setStyle(
				"-fx-font-size: 16px; -fx-font-weight: bold;" + "-fx-font-family: 'Copperplate Gothic Light'; ");
		generalResultLbl.setLayoutX(460);
		generalResultLbl.setLayoutY(460);

		// Creation of a ComboBox for player id:
		playerIdComboBoxFinal = new ComboBox(fillPlayerIdNameComboBox());
		playerIdComboBoxFinal.setStyle("-fx-font-size: 14px; -fx-font-family: 'Courier New'");
		playerIdComboBoxFinal.setLayoutX(760);
		playerIdComboBoxFinal.setLayoutY(457);
		playerIdComboBoxFinal.setPrefWidth(270);
		playerIdComboBoxFinal.setTooltip(new Tooltip("Please pick up a Player here."));
		playerIdComboBoxFinal.getSelectionModel().selectedItemProperty().addListener((options, oldValue, newValue) -> {
			fillGeneralResultLbl();
		});

		final Label labelTbl = new Label("PLAYER x GAMES");

		table.setEditable(true);

		table.setStyle(
				"-fx-font-size: 14px; -fx-font-family: 'Courier New'; -fx-border-radius: 7; -fx-background-radius: 7");

		TableColumn playerFirstNameCol = new TableColumn("First Name");
		playerFirstNameCol.setMinWidth(93);
		playerFirstNameCol.setCellValueFactory(new PropertyValueFactory<Player, String>("firstName"));

		TableColumn playerLastNameCol = new TableColumn("Last Name");
		playerLastNameCol.setMinWidth(92);
		playerLastNameCol.setCellValueFactory(new PropertyValueFactory<Player, String>("lastName"));

		TableColumn playerAddressCol = new TableColumn("Address");
		playerAddressCol.setMinWidth(50);
		playerAddressCol.setCellValueFactory(new PropertyValueFactory<Player, String>("address"));

		TableColumn playerPostalCodeCol = new TableColumn("Postal C");
		playerPostalCodeCol.setMinWidth(80);
		playerPostalCodeCol.setCellValueFactory(new PropertyValueFactory<Player, String>("postalCode"));

		TableColumn playerProvinceCol = new TableColumn("Prov.");
		playerProvinceCol.setMinWidth(51);
		playerProvinceCol.setCellValueFactory(new PropertyValueFactory<Player, String>("province"));

		TableColumn playerPhoneCol = new TableColumn("Phone");
		playerPhoneCol.setMinWidth(80);
		playerPhoneCol.setCellValueFactory(new PropertyValueFactory<Player, String>("phone"));

		TableColumn gameTitleCol = new TableColumn("Game Title");
		gameTitleCol.setMinWidth(93);
		gameTitleCol.setCellValueFactory(new PropertyValueFactory<Player, String>("gameTitle"));

		TableColumn gamePlayedDateCol = new TableColumn("Played Date");
		gamePlayedDateCol.setMinWidth(105);
		gamePlayedDateCol.setCellValueFactory(new PropertyValueFactory<Player, String>("gamePlayedDate"));

		TableColumn gameScoreCol = new TableColumn("Score");
		gameScoreCol.setMinWidth(90);
		gameScoreCol.setCellValueFactory(new PropertyValueFactory<Player, String>("gameScore"));

		table.setItems(data);
		table.getColumns().addAll(playerFirstNameCol, playerLastNameCol, playerAddressCol, playerPostalCodeCol,
				playerProvinceCol, playerPhoneCol, gameTitleCol, gamePlayedDateCol, gameScoreCol);

		final VBox vbox = new VBox();
		vbox.setSpacing(5);
		vbox.setStyle("-fx-background-color: #ddddee; -fx-border-color: rgb(28, 132, 203);");
		vbox.getChildren().addAll(labelTbl, table);
		vbox.setLayoutX(460);
		vbox.setLayoutY(500);
		vbox.setPrefSize(765, 190);

		/*
		 * ************************************** * * Adding all panes into the mainPane
		 * * * ************************************** *
		 */
		mainPane.getChildren().addAll(gamesLogoPane, newPlayerInfoPane, newGameInfoPane, updatePane, generalResultLbl,
				playerIdComboBoxFinal, vbox);

		/*
		 * ****************************** * * Setting up mainScene Scene * *
		 * ****************************** *
		 */
		Scene mainScene = new Scene(mainPane);

		/*
		 * *************************** * * Setting up primaryStage * *
		 * *************************** *
		 */
		// Setting up primaryStage:
		primaryStage.setTitle("PLAYERS X GAMES REGISTRATION FORM");
		primaryStage.setResizable(false);
		primaryStage.setScene(mainScene);
		primaryStage.show();
	}

	/*
	 * **************************** * * METHODS TO HANDLE EVENTS * *
	 * **************************** *
	 */

	/*
	 * ********************* * * Insert New Player * * ********************* *
	 */
	// submitNewPlayerBtnHandler method:
	private void submitNewPlayerBtnHandler() {
		insertNewPlayerInfo();
		selectNewPlayerInfo();
	}

	// insertNewPlayerInfo method:
	private void insertNewPlayerInfo() {
		boolean alreadyExistingUser = false;
		boolean atLeastOneTextFieldIsNull = false;
		Connection con = null;
		Statement stmt = null;
		ResultSet rs = null;
		try {
			// Establishing the connection to database:
			Class.forName(DRIVER);
			con = DriverManager.getConnection(DATABASE_URL);
			stmt = con.createStatement();
			rs = stmt.executeQuery(SELECT_PLAYER);
			// Important checks from Player table, that is: PK, already existing player and
			// any field left unfilled:
			lastPlayerPK = 0;
			String fullNameGUI;
			String fullNameDB;
			while (rs.next()) {
				// First, getting the last existing PK in Player table:
				lastPlayerPK = rs.getInt("player_id");
				// Second, verifying if the user already exists:
				fullNameDB = (rs.getString("first_name") + rs.getString("last_name")).trim();
				fullNameGUI = (textFirstName.getText() + textLastName.getText()).trim();
				if (fullNameGUI.equals(fullNameDB)) {
					System.out.printf("%n%s%n%n", "User already exists in DB, this player cannot be inserted again!!!");
					newPlayerResult.setText("Player already exists in DB...\nThis player cannot be inserted again!!!");
					alreadyExistingUser = true;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			// Closing the connection to DB:
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (Exception e) {
				}
			}
			if (stmt != null) {
				try {
					stmt.close();
				} catch (Exception e) {
				}
			}
			if (con != null) {
				try {
					con.close();
				} catch (Exception e) {
				}
			}
		}
		// Third, verifying if any text field was left unfilled:
		if (textFirstName.getText().equals("") || textLastName.getText().equals("") || textAddress.getText().equals("")
				|| textPostalCode.getText().equals("") || textPhoneNumber.getText().equals("")) {
			atLeastOneTextFieldIsNull = true;
			System.out.printf("%n%s%n%n", "At least one field is empty: please fill all fields!!!");
			newPlayerResult.setText("At least one field is empty...\nPlease fill all fields!!!");
		}
		// Finally insert the player if he/she does not already exist in DB
		// AND if all TextFields are filled (not null):
		if (!alreadyExistingUser && !atLeastOneTextFieldIsNull) {
			lastPlayerPK++;
			try {
				// Establishing the connection to database:
				Class.forName(DRIVER);
				con = DriverManager.getConnection(DATABASE_URL);
				pst = con.prepareStatement(INSERT_PLAYER);
				pst.setInt(1, lastPlayerPK);
				pst.setString(2, textFirstName.getText());
				pst.setString(3, textLastName.getText());
				pst.setString(4, textAddress.getText());
				pst.setString(5, textPostalCode.getText());
				pst.setString(6, String.valueOf(provinceComboBox.getValue()));
				pst.setString(7, textPhoneNumber.getText());
				pst.executeUpdate();
			} catch (SQLException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
				// Closing the connection to DB:
			} finally {
				System.out.println("Player inserted with player_id = " + lastPlayerPK + "!");
				newPlayerResult.setText("Player inserted with player_id = " + lastPlayerPK + "!");
				try {
					pst.close();
					con.close();
				} catch (Exception e) {
				}
			}
		}
	}

	/*
	 * ****************** * * Insert New Game * * ****************** *
	 */
	// submitNewGameBtnHandler method:
	private void submitNewGameBtnHandler() {
		insertNewGameInfo();
		selectNewGameInfo();
	}

	// insertNewGameInfo method:
	private void insertNewGameInfo() {
		boolean alreadyExistingGame = false;
		boolean gameTitleIsNull = false;
		Connection con = null;
		Statement stmt = null;
		ResultSet rs = null;
		try {
			// Establishing the connection to database:
			Class.forName(DRIVER);
			con = DriverManager.getConnection(DATABASE_URL);
			stmt = con.createStatement();
			rs = stmt.executeQuery(SELECT_GAME);
			// Important checks from game table, that is: PK, already existing game and
			// field left unfilled:
			lastGamePK = 0;
			String gameTitleGUI;
			String gameTitleDB;
			while (rs.next()) {
				// First, getting the last existing PK in game table:
				lastGamePK = rs.getInt("game_id");
				// Second, verifying if the game already exists:
				gameTitleDB = (rs.getString("game_title")).trim();
				gameTitleGUI = (textGameTitle.getText()).trim();
				if (gameTitleGUI.equals(gameTitleDB)) {
					System.out.printf("%n%s%n%n", "Game already exists in DB, this game cannot be inserted again!!!");
					newGameResult.setText("Game already exists in DB...\nThis game cannot be inserted again!!!");
					alreadyExistingGame = true;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			// Closing the connection to DB:
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (Exception e) {
				}
			}
			if (stmt != null) {
				try {
					stmt.close();
				} catch (Exception e) {
				}
			}
			if (con != null) {
				try {
					con.close();
				} catch (Exception e) {
				}
			}
		}
		// Third, verifying if the game's title field was left unfilled:
		if (textGameTitle.getText().equals("")) {
			gameTitleIsNull = true;
			System.out.printf("%n%s%n%n", "Please fill the game's title field!!!");
			newGameResult.setText("The game's title field is empty...\nPlease fill it first!!!");
		}
		// Finally insert the game if it does not already exist in DB
		// AND if the title field are filled (not null):
		if (!alreadyExistingGame && !gameTitleIsNull) {
			lastGamePK++;
			try {
				// Establishing the connection to database:
				Class.forName(DRIVER);
				con = DriverManager.getConnection(DATABASE_URL);
				pst = con.prepareStatement(INSERT_GAME);
				pst.setInt(1, lastGamePK);
				pst.setString(2, textGameTitle.getText());
				pst.executeUpdate();
			} catch (SQLException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
				// Closing the connection to DB:
			} finally {
				System.out.println("Game inserted with game_id = " + lastGamePK + "!");
				newGameResult.setText("Game inserted with game_id = " + lastGamePK + "!");
				try {
					pst.close();
					con.close();
				} catch (Exception e) {
				}
			}
		}
	}

	/*
	 * *************************** * * Insert New PlayerAndGame * *
	 * *************************** *
	 */
	// insertNewPlayerAndGameBtnHandler method:
	private void insertNewPlayerAndGameBtnHandler() {
		Connection con = null;
		Statement stmt = null;
		ResultSet rs = null;
		int currentPlayerKey = (playerIdComboBox.getSelectionModel().getSelectedIndex()) + 1;
		int currentGameId = (updGameComboBox.getSelectionModel().getSelectedIndex()) + 1;
		try {
			// Establishing the connection to database:
			Class.forName(DRIVER);
			con = DriverManager.getConnection(DATABASE_URL);
			stmt = con.createStatement();
			rs = stmt.executeQuery(SELECT_PLAYERANDGAME);
			// Getting the lastPlayerAndGamePK:
			lastPlayerAndGamePK = 0;
			while (rs.next()) {
				lastPlayerAndGamePK = rs.getInt("player_game_id");
			}
		} catch (Exception e) {
			e.printStackTrace();
			// Closing the connection to DB:
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (Exception e) {
				}
			}
			if (stmt != null) {
				try {
					stmt.close();
				} catch (Exception e) {
				}
			}
			if (con != null) {
				try {
					con.close();
				} catch (Exception e) {
				}
			}
		}
		// Finally insert the PlayerAndGame
		if (String.valueOf(updGameComboBox.getValue()) != "" && updTextPlayingDate.getText() != ""
				&& updTextGameScore.getText() != "") {
			lastPlayerAndGamePK++;
			try {
				// Establishing the connection to database:
				Class.forName(DRIVER);
				con = DriverManager.getConnection(DATABASE_URL);
				pst = con.prepareStatement(INSERT_PLAYERANDGAME);
				pst.setInt(1, lastPlayerAndGamePK);
				pst.setInt(2, currentGameId);
				pst.setInt(3, currentPlayerKey);
				pst.setString(4, updTextPlayingDate.getText());
				pst.setString(5, updTextGameScore.getText());
				pst.executeUpdate();
			} catch (SQLException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
				// Closing the connection to DB:
			} finally {
				System.out.println("PlayerAndGame inserted with player_game_id = " + lastPlayerAndGamePK + "!");
				updatedResult.setText("PlayerAndGame inserted with player_game_id = " + lastPlayerAndGamePK + "!");
				try {
					pst.close();
					con.close();
				} catch (Exception e) {
				}
			}
		}
	}

	/*
	 * ************************* * * Select New Player Info * *
	 * ************************* *
	 */
	// selectNewPlayerInfo method:
	private void selectNewPlayerInfo() {
		Connection con = null;
		Statement stmt = null;
		ResultSet rs = null;
		try {
			// Establishing the connection to database:
			Class.forName(DRIVER);
			con = DriverManager.getConnection(DATABASE_URL);
			stmt = con.createStatement();
			rs = stmt.executeQuery(SELECT_PLAYER);
			System.out.println("Current table of Players:\n");
			// Returning Player data:
			while (rs.next()) {
				System.out.printf("%-3s%-10s%-10s%-30s%-10s%-5s%-15s%n", rs.getInt("player_id"),
						rs.getString("first_name"), rs.getString("last_name"), rs.getString("address"),
						rs.getString("postal_code"), rs.getString("province"), rs.getString("phone_number"));
			}
		} catch (Exception e) {
			e.printStackTrace();
			// Closing the connection to DB:
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (Exception e) {
				}
			}
			if (stmt != null) {
				try {
					stmt.close();
				} catch (Exception e) {
				}
			}
			if (con != null) {
				try {
					con.close();
				} catch (Exception e) {
				}
			}
		}
	}

	/*
	 * *********************** * * Select New Game Info * * ***********************
	 * *
	 */
	// selectNewGameInfo method:
	private void selectNewGameInfo() {

		Connection con = null;
		Statement stmt = null;
		ResultSet rs = null;
		try {
			// Establishing the connection to database:

			Class.forName(DRIVER);
			con = DriverManager.getConnection(DATABASE_URL);
			stmt = con.createStatement();
			rs = stmt.executeQuery(SELECT_GAME);

			System.out.println("Current table of games:\n");

			// Returning game data:
			while (rs.next()) {
				System.out.printf("%-3s%-10s%n", rs.getInt("game_id"), rs.getString("game_title"));
			}
		} catch (Exception e) {
			e.printStackTrace();

			// Closing the connection to DB:
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (Exception e) {
				}
			}
			if (stmt != null) {
				try {
					stmt.close();
				} catch (Exception e) {
				}
			}
			if (con != null) {
				try {
					con.close();
				} catch (Exception e) {
				}
			}
		}
	}

	/*
	 * **************************** * * clearNewPlayerBtnHandler * *
	 * **************************** *
	 */
	// clearNewPlayerBtnHandler method:
	private void clearNewPlayerBtnHandler() {
		textFirstName.setText("");
		textLastName.setText("");
		textAddress.setText("");
		textPostalCode.setText("");
		provinceComboBox.getSelectionModel().clearSelection();
		textPhoneNumber.setText("");
		newPlayerResult.setText("Result...");
	}

	/*
	 * ************************** * * clearNewGameBtnHandler * *
	 * ************************** *
	 */
	// clearNewGameBtnHandler method:
	private void clearNewGameBtnHandler() {

		textGameTitle.setText("");
		newGameResult.setText("Result...");
	}

	/*
	 * ************************** * * clearUpdPaneBtnHandler * *
	 * ************************** *
	 */
	// clearUpdPaneBtnHandler method:
	private void clearUpdPaneBtnHandler() {

		updTextFirstName.setText("");
		updTextLastName.setText("");
		updTextAddress.setText("");
		updTextPostalCode.setText("");
		updProvinceComboBox.getSelectionModel().clearSelection();
		updTextPhoneNumber.setText("");
		updGameComboBox.getSelectionModel().clearSelection();
		updTextGameScore.setText("");
		updTextPlayingDate.setText("");
		updatedResult.setText("Result...");
	}

	/*
	 * **************************** * * fillPlayerIdNameComboBox * *
	 * **************************** *
	 */
	// fillPlayerIdNameComboBox method:
	private ObservableList<String> fillPlayerIdNameComboBox() {
		Connection con = null;
		Statement stmt = null;
		ResultSet rs = null;
		ObservableList<String> playersIds = FXCollections.observableArrayList();
		try {
			// Establishing the connection to database:
			Class.forName(DRIVER);
			con = DriverManager.getConnection(DATABASE_URL);
			stmt = con.createStatement();
			rs = stmt.executeQuery(SELECT_PLAYER);
			while (rs.next()) {
				// Getting all player_id PK existing in Player table:
				playersIds.addAll("Id " + String.valueOf(rs.getInt("player_id")) + ": " + (rs.getString("first_name"))
						+ " " + (rs.getString("last_name")));
			}
			return playersIds;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
			// Closing the connection to DB:
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (Exception e) {
				}
			}
			if (stmt != null) {
				try {
					stmt.close();
				} catch (Exception e) {
				}
			}
			if (con != null) {
				try {
					con.close();
				} catch (Exception e) {
				}
			}
		}
	}

	/*
	 * ******************************** * * fillGameTitleComboBox method * *
	 * ******************************** *
	 */
	// fillGameTitleComboBox method:
	private ObservableList<String> fillGameTitleComboBox() {
		Connection con = null;
		Statement stmt = null;
		ResultSet rs = null;
		ObservableList<String> gameIds = FXCollections.observableArrayList();
		try {
			// Establishing the connection to database:
			Class.forName(DRIVER);
			con = DriverManager.getConnection(DATABASE_URL);
			stmt = con.createStatement();
			rs = stmt.executeQuery(SELECT_GAME);
			while (rs.next()) {

				// Getting all player_id PK existing in Player table:
				gameIds.addAll("Id " + String.valueOf(rs.getInt("game_id")) + ": " + (rs.getString("game_title")));
			}
			return gameIds;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
			// Closing the connection to DB:
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (Exception e) {
				}
			}
			if (stmt != null) {
				try {
					stmt.close();
				} catch (Exception e) {
				}
			}
			if (con != null) {
				try {
					con.close();
				} catch (Exception e) {
				}
			}
		}
	}

	/*
	 * *************************** * * updatePlayersBtnHandler * *
	 * *************************** *
	 */
	// updatePlayersBtnHandler method:
	private void updatePlayersBtnHandler() {

		int currentPlayerKey = playerIdComboBox.getSelectionModel().getSelectedIndex();
		currentPlayerKey++;
		System.out.println(currentPlayerKey);
		Connection con = null;
		try {
			Class.forName(DRIVER);
			con = DriverManager.getConnection(DATABASE_URL);
			con.setAutoCommit(false);
			PreparedStatement stmt = con.prepareStatement(
					"UPDATE Player SET first_name = ?, last_name = ?, address = ?, postal_code = ?, province = ?, phone_number = ? WHERE player_id = "
							+ currentPlayerKey);
			stmt.setString(1, updTextFirstName.getText());
			stmt.setString(2, updTextLastName.getText());
			stmt.setString(3, updTextAddress.getText());
			stmt.setString(4, updTextPostalCode.getText());
			stmt.setString(5, String.valueOf(updProvinceComboBox.getValue()));
			stmt.setString(6, updTextPhoneNumber.getText());
			stmt.executeUpdate();
			con.commit();
			stmt.close();
			con.close();
			updatedResult.setText("The player was successfully updated!");
			playerIdComboBox.getSelectionModel().clearSelection();
		} catch (Exception e) {
			System.err.print(e.getClass().getName() + ": " + e.getMessage());
		}
	}

	/*
	 * ************************** * * updatePlayerTextFields * *
	 * ************************** *
	 */
	// updatePlayerTextFields method:
	private void updatePlayerTextFields() {
		int currentKey = playerIdComboBox.getSelectionModel().getSelectedIndex();
		currentKey++;
		System.out.println(currentKey);
		Connection con = null;
		Statement stmt = null;
		ResultSet rs = null;
		try {
			// Establishing the connection to database:
			Class.forName(DRIVER);
			con = DriverManager.getConnection(DATABASE_URL);
			stmt = con.createStatement();
			rs = stmt.executeQuery("SELECT * FROM player WHERE player_id =" + currentKey);
			while (rs.next()) {
				updTextFirstName.setText(String.valueOf(rs.getString("first_name")));
				updTextLastName.setText(String.valueOf(rs.getString("last_name")));
				updTextAddress.setText(String.valueOf(rs.getString("address")));
				updTextPostalCode.setText(String.valueOf(rs.getString("postal_code")));
				updTextPhoneNumber.setText(String.valueOf(rs.getString("phone_number")));
				updProvinceComboBox.getSelectionModel().select(String.valueOf(rs.getString("province")));
			}
		} catch (Exception e) {
			e.printStackTrace();
			// Closing the connection to DB:
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (Exception e) {
				}
			}
			if (stmt != null) {
				try {
					stmt.close();
				} catch (Exception e) {
				}
			}
			if (con != null) {
				try {
					con.close();
				} catch (Exception e) {
				}
			}
		}
	}

	/*
	 * ******************************* * * fillGeneralResultLbl method * *
	 * ******************************* *
	 */
	// fillGeneralResultLbl method:
	private void fillGeneralResultLbl() {
		Connection con = null;
		Statement stmt = null;
		ResultSet rs = null;
		int currentKey = playerIdComboBoxFinal.getSelectionModel().getSelectedIndex() + 1;
		String fn = "";
		String ln = "";
		String add = "";
		String pc = "";
		String pn = "";
		String prov = "";
		try {
			// Establishing the connection to database:
			Class.forName(DRIVER);
			con = DriverManager.getConnection(DATABASE_URL);
			stmt = con.createStatement();
			rs = stmt.executeQuery("SELECT * FROM player WHERE player_id =" + currentKey);
			while (rs.next()) {
				fn = rs.getString("first_name");
				ln = rs.getString("last_name");
				add = rs.getString("address");
				pc = rs.getString("postal_code");
				prov = rs.getString("province");
				pn = rs.getString("phone_number");
			}
		} catch (Exception e) {
			e.printStackTrace();
			// Closing the connection to DB:
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (Exception e) {
				}
			}
			if (stmt != null) {
				try {
					stmt.close();
				} catch (Exception e) {
				}
			}
			if (con != null) {
				try {
					con.close();
				} catch (Exception e) {
				}
			}
		}
		con = null;
		stmt = null;
		rs = null;
		int counter = 0;
		LinkedList<String> gTitleLst = new LinkedList<String>();
		LinkedList<String> playDateLst = new LinkedList<String>();
		LinkedList<String> scoreLst = new LinkedList<String>();
		try {
			// Establishing the connection to database:
			Class.forName(DRIVER);
			con = DriverManager.getConnection(DATABASE_URL);
			stmt = con.createStatement();
			rs = stmt.executeQuery(
					"SELECT game.game_title, playerandgame.playing_date, playerandgame.score FROM ((game INNER JOIN playerandgame ON game.game_id = playerandgame.game_id) INNER JOIN player ON playerandgame.player_id = player.player_id) WHERE player.player_id = "
							+ currentKey);
			while (rs.next()) {
				gTitleLst.add(String.valueOf(rs.getString("game_title")));
				playDateLst.add(String.valueOf(rs.getString("playing_date")));
				scoreLst.add(String.valueOf(rs.getString("score")));
				counter++;
			}
		} catch (Exception e) {
			e.printStackTrace();
			// Closing the connection to DB:
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (Exception e) {
				}
			}
			if (stmt != null) {
				try {
					stmt.close();
				} catch (Exception e) {
				}
			}
			if (con != null) {
				try {
					con.close();
				} catch (Exception e) {
				}
			}
		}

		Player[] objectPlayer = new Player[counter];

		for (int i = 0; i < counter; i++) {
			objectPlayer[i] = new Player(fn, ln, add, pc, prov, pn, gTitleLst.get(i), playDateLst.get(i),
					scoreLst.get(i));

			data = FXCollections.observableArrayList(objectPlayer);
		}

		table.setItems(data);
	}

	/*
	 * *************** * * Main method * * *************** *
	 */
	public static void main(String[] args) {
		Application.launch(args);
		// launch(args);
	}

}
