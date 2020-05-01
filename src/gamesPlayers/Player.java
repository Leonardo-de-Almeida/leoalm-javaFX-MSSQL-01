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

import javafx.beans.property.SimpleStringProperty;

// Player class:
public class Player {

	// Instance fields:
	private final SimpleStringProperty firstName;
	private final SimpleStringProperty lastName;
	private final SimpleStringProperty address;
	private final SimpleStringProperty postalCode;
	private final SimpleStringProperty province;
	private final SimpleStringProperty phone;
	private final SimpleStringProperty gameTitle;
	private final SimpleStringProperty gamePlayedDate;
	private final SimpleStringProperty gameScore;

	// Constructor:
	public Player(String firstName, String lastName, String address, String postalCode, String province, String phone,
			String gameTitle, String gamePlayedDate, String gameScore) {
		this.firstName = new SimpleStringProperty(firstName);
		this.lastName = new SimpleStringProperty(lastName);
		this.address = new SimpleStringProperty(address);
		this.postalCode = new SimpleStringProperty(postalCode);
		this.province = new SimpleStringProperty(province);
		this.phone = new SimpleStringProperty(phone);
		this.gameTitle = new SimpleStringProperty(gameTitle);
		this.gamePlayedDate = new SimpleStringProperty(gamePlayedDate);
		this.gameScore = new SimpleStringProperty(gameScore);
	}

	// Getters and setters:
	public String getFirstName() {
		return firstName.get();
	}

	public SimpleStringProperty firstNameProperty() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName.set(firstName);
	}

	public String getLastName() {
		return lastName.get();
	}

	public SimpleStringProperty lastNameProperty() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName.set(lastName);
	}

	public String getAddress() {
		return address.get();
	}

	public SimpleStringProperty addressProperty() {
		return address;
	}

	public void setAddress(String address) {
		this.address.set(address);
	}

	public String getPostalCode() {
		return postalCode.get();
	}

	public SimpleStringProperty postalCodeProperty() {
		return postalCode;
	}

	public void setPostalCode(String postalCode) {
		this.postalCode.set(postalCode);
	}

	public String getProvince() {
		return province.get();
	}

	public SimpleStringProperty provinceProperty() {
		return province;
	}

	public void setProvince(String province) {
		this.province.set(province);
	}

	public String getPhone() {
		return phone.get();
	}

	public SimpleStringProperty phoneProperty() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone.set(phone);
	}

	public String getGameTitle() {
		return gameTitle.get();
	}

	public SimpleStringProperty gameTitleProperty() {
		return gameTitle;
	}

	public void setGameTitle(String gameTitle) {
		this.gameTitle.set(gameTitle);
	}

	public String getGamePlayedDate() {
		return gamePlayedDate.get();
	}

	public SimpleStringProperty gamePlayedDateProperty() {
		return gamePlayedDate;
	}

	public void setGamePlayedDate(String gamePlayedDate) {
		this.gamePlayedDate.set(gamePlayedDate);
	}

	public String getGameScore() {
		return gameScore.get();
	}

	public SimpleStringProperty gameScoreProperty() {
		return gameScore;
	}

	public void setGameScore(String gameScore) {
		this.gameScore.set(gameScore);
	}
}
