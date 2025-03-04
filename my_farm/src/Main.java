import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

import java.io.*;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public class Main extends Application {

    private static int ROWS = 40;
    private static int COLUMNS = 40;
    private static int RECT_SIZE = 20;
    private static String SAVE_FILE = "grid_state.txt";
    private Rectangle[][] rectangles = new Rectangle[ROWS][COLUMNS];
    private int coins = 1000;
    private Label coinLabel = new Label("Pièces: " + coins);
    private Button saveButton = new Button("Sauvegarder");
    private Button houseButton = new Button("Maison");
    private Button marketButton = new Button("Marché");
    private Stock stock = new Stock();

    @Override
    public void start(Stage primaryStage) {
        GridPane gridPane = new GridPane();

        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLUMNS; col++) {
                Rectangle rect = new Rectangle(RECT_SIZE, RECT_SIZE);
                rect.setFill(Color.LIGHTGRAY);
                rect.setStroke(Color.BLACK);

                rect.setOnMouseClicked(event -> {
                    if (event.getButton() == MouseButton.PRIMARY) {
                        if (rect.getFill().equals(Color.GREEN) || rect.getFill().equals(Color.PINK)) {
                            if (confirmSale()) {
                                rect.setFill(Color.LIGHTGRAY);
                                coins += 100;
                            }
                        } else {
                            if (coins >= 100) {
                                String choice = chooseParcelType();
                                if (choice != null) {
                                    if (choice.equals("Champ")) {
                                        rect.setFill(Color.GREEN);
                                    } else if (choice.equals("Enclos")) {
                                        rect.setFill(Color.PINK);
                                    }
                                    coins -= 100;
                                }
                            } else {
                                showAlert("Vous n'avez pas assez de pièce pour acheter cet parcelle.");
                            }
                        }
                        updateCoinLabel();
                    } else if (event.getButton() == MouseButton.SECONDARY) {
                        showColorModal(rect);
                    }
                });

                rectangles[row][col] = rect;
                gridPane.add(rect, col, row);
            }
        }

        loadGridState();
        updateCoinLabel();

        Label titleLabel = new Label("My Farm");
        titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");
        HBox titleBox = new HBox(titleLabel);
        titleBox.setAlignment(Pos.CENTER);

        coinLabel.setStyle("-fx-background-color: yellow; -fx-text-fill: black; -fx-padding: 5px;");

        HBox coinBox = new HBox(coinLabel);
        coinBox.setAlignment(Pos.CENTER);
        HBox houseBox = new HBox(houseButton);
        houseBox.setAlignment(Pos.CENTER);
        HBox marketBox = new HBox(marketButton);
        marketBox.setAlignment(Pos.CENTER);
        HBox saveBox = new HBox(saveButton);
        saveBox.setAlignment(Pos.CENTER);
        VBox menuBox = new VBox(coinBox, houseBox, saveBox, marketBox);
        menuBox.setAlignment(Pos.TOP_LEFT);
        menuBox.setSpacing(10);

        BorderPane root = new BorderPane();
        root.setTop(titleBox);
        root.setLeft(menuBox);
        root.setCenter(gridPane);

        Scene scene = new Scene(root);
        scene.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.S) {
                saveGridState();
            }
        });

        saveButton.setOnAction(event -> saveGridState());
        marketButton.setOnAction(event -> openMarket());
        houseButton.setOnAction(event -> showStock());

        primaryStage.setScene(scene);
        primaryStage.setTitle("MyFarm Julien");
        primaryStage.show();
    }

    private void saveGridState() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(SAVE_FILE))) {
            writer.println(coins);
            writer.println(stock.getWheatSeeds());
            writer.println(stock.getCornSeeds());
            writer.println(stock.getRiceSeeds());
            writer.println(stock.getChickens());
            writer.println(stock.getCows());
            writer.println(stock.getSheep());
            for (int row = 0; row < ROWS; row++) {
                for (int col = 0; col < COLUMNS; col++) {
                    Color color = (Color) rectangles[row][col].getFill();
                    if (color.equals(Color.GREEN)) {
                        writer.print("2");
                    } else if (color.equals(Color.PINK)) {
                        writer.print("3");
                    } else if (color.equals(Color.BLUE)) {
                        writer.print("1");
                    } else {
                        writer.print("0");
                    }
                }
                writer.println();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadGridState() {
        File file = new File(SAVE_FILE);
        if (!file.exists()) {
            return;
        }

        try (Scanner scanner = new Scanner(file)) {
            if (scanner.hasNextInt()) {
                coins = scanner.nextInt();
                scanner.nextLine();
            }
            if (scanner.hasNextInt()) {
                stock.addWheatSeeds(scanner.nextInt());
                scanner.nextLine();
            }
            if (scanner.hasNextInt()) {
                stock.addCornSeeds(scanner.nextInt());
                scanner.nextLine();
            }
            if (scanner.hasNextInt()) {
                stock.addRiceSeeds(scanner.nextInt());
                scanner.nextLine();
            }
            if (scanner.hasNextInt()) {
                stock.addChickens(scanner.nextInt());
                scanner.nextLine();
            }
            if (scanner.hasNextInt()) {
                stock.addCows(scanner.nextInt());
                scanner.nextLine();
            }
            if (scanner.hasNextInt()) {
                stock.addSheep(scanner.nextInt());
                scanner.nextLine();
            }
            for (int row = 0; row < ROWS; row++) {
                String line = scanner.nextLine();
                for (int col = 0; col < COLUMNS; col++) {
                    char ch = line.charAt(col);
                    if (ch == '2') {
                        rectangles[row][col].setFill(Color.GREEN);
                    } else if (ch == '3') {
                        rectangles[row][col].setFill(Color.PINK);
                    } else if (ch == '1') {
                        rectangles[row][col].setFill(Color.BLUE);
                    } else {
                        rectangles[row][col].setFill(Color.LIGHTGRAY);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void updateCoinLabel() {
        coinLabel.setText("Pièces: " + coins);
    }

    private void showAlert(String message) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Informations");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private boolean confirmSale() {
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle("Confirmation vente");
        alert.setHeaderText(null);
        alert.setContentText("Voulez vous vraiment vendre cette parcelle ?");
        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == ButtonType.OK;
    }

    private String chooseParcelType() {
        List<String> choices = Arrays.asList("Champ", "Enclos");
        ChoiceDialog<String> dialog = new ChoiceDialog<>("Champ", choices);
        dialog.setTitle("Choix du type de parcelle");
        dialog.setHeaderText(null);
        dialog.setContentText("Choissiez le type de parcelle:");
        Optional<String> result = dialog.showAndWait();
        return result.orElse(null);
    }

    private void showColorModal(Rectangle rect) {
        Color color = (Color) rect.getFill();
        if (color.equals(Color.LIGHTGRAY)) {
            showAlert("Il n'y a rien ici.");
        } else {
            Stage colorStage = new Stage();
            BorderPane pane = new BorderPane();
            pane.setStyle("-fx-background-color: " + toRgbString(color) + ";");
            Scene scene = new Scene(pane, 200, 200);
            Button plantButton = new Button("");
            if( toRgbString(color).equals(toRgbString(Color.GREEN)) ) {
                plantButton = new Button("Planter");
            } else if( toRgbString(color).equals(toRgbString(Color.PINK)) ) {
                plantButton = new Button("Elever");
            }

            plantButton.setOnAction(e -> {
                if( toRgbString(color).equals(toRgbString(Color.GREEN)) ) {
                    showPlantOptions();
                } else if( toRgbString(color).equals(toRgbString(Color.PINK)) ) {
                    showAlert("Vous avez élevé un animal.");
                }
            });
            ProgressBar progressBar = new ProgressBar();

            VBox vbox = new VBox(plantButton, progressBar);
            vbox.setAlignment(Pos.CENTER);
            vbox.setSpacing(10);
            pane.setCenter(vbox);

            colorStage.setScene(scene);
            colorStage.setTitle("Zoom de parcelle");
            colorStage.show();
        }
    }

    private String toRgbString(Color color) {
        int r = (int) (color.getRed() * 255);
        int g = (int) (color.getGreen() * 255);
        int b = (int) (color.getBlue() * 255);
        return String.format("rgb(%d, %d, %d)", r, g, b);
    }

    private void openMarket() {
        ListView<String> listView = new ListView<>();
        listView.getItems().addAll(
                "Graines de blé - 50 pièces",
                "Graines de maïs - 60 pièces",
                "Graines de riz - 70 pièces",
                "Poulet - 200 pièces",
                "Vache - 300 pièces",
                "Mouton - 250 pièces"
        );
        listView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        Button buyButton = new Button("Acheter");
        buyButton.setOnAction(event -> {
            ObservableList<String> selectedItems = listView.getSelectionModel().getSelectedItems();
            int totalCost = 0;
            for (String item : selectedItems) {
                if (item.contains("Graines de blé") && coins >= 50) {
                    stock.addWheatSeeds(1);
                    totalCost += 50;
                } else if (item.contains("Graines de maïs") && coins >= 60) {
                    stock.addCornSeeds(1);
                    totalCost += 60;
                } else if (item.contains("Graines de riz") && coins >= 70) {
                    stock.addRiceSeeds(1);
                    totalCost += 70;
                } else if (item.contains("Poulet") && coins >= 200) {
                    stock.addChickens(1);
                    totalCost += 200;
                } else if (item.contains("Vache") && coins >= 300) {
                    stock.addCows(1);
                    totalCost += 300;
                } else if (item.contains("Mouton") && coins >= 250) {
                    stock.addSheep(1);
                    totalCost += 250;
                } else {
                    showAlert("Vous n'avez pas assez de pièces pour acheter " + item);
                    return;
                }
            }
            if (coins >= totalCost) {
                coins -= totalCost;
                updateCoinLabel();
            } else {
                showAlert("Vous n'avez pas assez de pièces pour acheter les articles sélectionnés.");
            }
        });

        VBox vbox = new VBox(listView, buyButton);
        Scene scene = new Scene(vbox, 300, 400);
        Stage marketStage = new Stage();
        marketStage.setScene(scene);
        marketStage.setTitle("Marché");
        marketStage.show();
    }

    private void showPlantOptions() {
        List<String> choices = Arrays.asList(
                "Graines de blé (" + stock.getWheatSeeds() + ")",
                "Graines de maïs (" + stock.getCornSeeds() + ")",
                "Graines de riz (" + stock.getRiceSeeds() + ")"
        );

        ChoiceDialog<String> dialog = new ChoiceDialog<>(choices.get(0), choices);
        dialog.setTitle("Plantation");
        dialog.setHeaderText("Choisissez le type de graine à planter:");
        dialog.setContentText("Graine:");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(choice -> {
            if (choice.contains("Graines de blé") && stock.getWheatSeeds() > 0) {
                stock.addWheatSeeds(-1);
                // Logic to plant wheat seeds
            } else if (choice.contains("Graines de maïs") && stock.getCornSeeds() > 0) {
                stock.addCornSeeds(-1);
                // Logic to plant corn seeds
            } else if (choice.contains("Graines de riz") && stock.getRiceSeeds() > 0) {
                stock.addRiceSeeds(-1);
                // Logic to plant rice seeds
            } else {
                showAlert("Vous n'avez pas assez de graines pour planter " + choice);
            }
        });
    }

    private void showStock() {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Stock");
        alert.setHeaderText(null);
        alert.setContentText("Graines : \n\nGraines de blé : " + stock.getWheatSeeds() + "\nGraines de riz : " + stock.getRiceSeeds() + "\nGraines de maïs : " + stock.getCornSeeds() + "\n\nAnimaux : \n\nPoulets : " + stock.getChickens() + "\nVaches : " + stock.getCows() + "\nMoutons : " + stock.getSheep());
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}