import javafx.application.*;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

import java.util.*;

public class Main extends Application {

    private static int ROWS = 40;
    private static int COLUMNS = 40;
    private static int RECT_SIZE = 20;
    private Rectangle[][] rectangles = new Rectangle[ROWS][COLUMNS];
    private int coins = 1000;
    private Label coinLabel = new Label("Pièces: " + coins);
    private Button saveButton = new Button("Sauvegarder");
    private Button houseButton = new Button("Grange");
    private Button marketButton = new Button("Marché");
    private Button infoButton = new Button("Information");
    private Stock stock = new Stock();
    private Market market;
    private Save saveManager;
    private Animal animal;
    private Plant plant;

    @Override
    public void start(Stage primaryStage) {
        market = new Market(coins, stock, coinLabel);
        saveManager = new Save(coins, stock, rectangles);
        animal = new Animal(stock);
        plant = new Plant(stock, rectangles);

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

        saveManager.loadGridState();
        coins = saveManager.getCoins();
        updateCoinLabel();

        Label titleLabel = new Label("My Farm");
        titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");
        HBox titleBox = new HBox(titleLabel);

        titleBox.setAlignment(Pos.CENTER);
        infoButton.setBackground(Background.fill(Color.GREEN));
        infoButton.setTextFill(Color.WHITE);
        coinLabel.setBackground(Background.fill(Color.YELLOW));
        coinLabel.setTextFill(Color.BLACK);
        saveButton.setBackground(Background.fill(Color.BLUE));
        saveButton.setTextFill(Color.WHITE);
        houseButton.setBackground(Background.fill(Color.RED));
        houseButton.setTextFill(Color.WHITE);
        marketButton.setBackground(Background.fill(Color.ORANGE));
        marketButton.setTextFill(Color.WHITE);

        HBox coinBox = new HBox(coinLabel);
        coinBox.setAlignment(Pos.CENTER);
        HBox houseBox = new HBox(houseButton);
        houseBox.setAlignment(Pos.CENTER);
        HBox marketBox = new HBox(marketButton);
        marketBox.setAlignment(Pos.CENTER);
        HBox saveBox = new HBox(saveButton);
        saveBox.setAlignment(Pos.CENTER);
        HBox infoBox = new HBox(infoButton);
        infoBox.setAlignment(Pos.CENTER);
        VBox menuBox = new VBox(coinBox, houseBox, saveBox, marketBox, infoBox);
        menuBox.setAlignment(Pos.TOP_LEFT);
        menuBox.setSpacing(10);

        BorderPane root = new BorderPane();
        root.setTop(titleBox);
        root.setLeft(menuBox);
        root.setCenter(gridPane);
        root.setBackground(Background.fill(Color.GRAY));
        Scene scene = new Scene(root);
        scene.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.S) {
                saveManager.saveGridState();
            }
        });

        saveButton.setOnAction(event -> {
            saveManager.setCoins(coins);
            saveManager.saveGridState();
        });
        marketButton.setOnAction(event -> market.openMarket());
        houseButton.setOnAction(event -> showStock());
        infoButton.setOnAction(event -> showInformation());

        primaryStage.setScene(scene);
        primaryStage.setTitle("MyFarm Julien");
        primaryStage.show();
    }

    private void showColorModal(Rectangle rect) {
        Color color = (Color) rect.getFill();
        if (color.equals(Color.LIGHTGRAY)) {
            showAlert("Il n'y a rien ici.");
        } else {
            Stage colorStage = new Stage();
            colorStage.setOnCloseRequest(event -> {
                if (colorStage.isShowing()) {
                    event.consume();
                }
            });

            BorderPane pane = new BorderPane();
            pane.setStyle("-fx-background-color: " + toRgbString(color) + ";");
            Scene scene = new Scene(pane, 200, 200);
            Button plantButton;
            if (toRgbString(color).equals(toRgbString(Color.GREEN))) {
                plantButton = new Button("Planter");
            } else if (toRgbString(color).equals(toRgbString(Color.PINK))) {
                plantButton = new Button("Elever");
            } else {
                plantButton = new Button("");
            }
            ProgressBar progressBar = new ProgressBar();

            Image image = new Image("/img/test.png");
            ImageView imageView = new ImageView();
            imageView.setImage(image);
            imageView.setFitWidth(100);
            imageView.setPreserveRatio(true);
            imageView.setSmooth(true);
            imageView.setCache(true);

            plantButton.setOnAction(e -> {
                if (toRgbString(color).equals(toRgbString(Color.GREEN))) {
                    plant.showPlantOptions(imageView, plantButton, progressBar, colorStage, rect); // Use Plant instance
                } else if (toRgbString(color).equals(toRgbString(Color.PINK))) {
                    animal.showAnimalOptions(plantButton, progressBar, colorStage, rect); // Use Animal instance
                }
            });

            VBox vbox = new VBox(imageView, plantButton, progressBar);
            vbox.setAlignment(Pos.CENTER);
            vbox.setSpacing(10);
            pane.setCenter(vbox);

            colorStage.setScene(scene);
            colorStage.setTitle("Zoom de parcelle");
            colorStage.show();
        }
    }


    private void showInformation() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setHeaderText(null);
        alert.setContentText("Bienvenue dans MyFarm Julien. Vous pouvez acheter des parcelles de terrain en cliquant dessus avec le bouton gauche de la souris. Vous pouvez également vendre des parcelles en cliquant dessus avec le bouton droit de la souris. Pour sauvegarder l'état de la ferme, appuyez sur la touche S, pour commencer à planter ou élever presser clique droit sur une parcelle déja acheter. Si vous plantez une graine sur une parcelle adjacente à une autre parcelle de champ, vous obtiendrez un bonus de récolte. Vous pouvez également acheter des graines, des animaux et vendre des produits sur le marché. Bonne chance!");
        alert.showAndWait();
    }


    private void updateCoinLabel() {
        coinLabel.setText("Pièces: " + coins);
    }


    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Informations");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }


    private boolean confirmSale() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
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


    private String toRgbString(Color color) {
        int r = (int) (color.getRed() * 255);
        int g = (int) (color.getGreen() * 255);
        int b = (int) (color.getBlue() * 255);
        return String.format("rgb(%d, %d, %d)", r, g, b);
    }


    private void showStock() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Stock");
        alert.setHeaderText(null);
        alert.setContentText("Graines : \n\nGraines de blé : " + stock.getWheatSeeds() + "\nGraines de riz : " + stock.getRiceSeeds() + "\nGraines de maïs : " + stock.getCornSeeds() + "\n\nAnimaux : \n\nPoulets : " + stock.getChickens() + "\nVaches : " + stock.getCows() + "\nMoutons : " + stock.getSheep() + "\n\nProduits : \n\nBlé : " + stock.getWheat() + "\nRiz : " + stock.getRice() + "\nMaïs : " + stock.getCorn() + "\nOeufs : " + stock.getEggs() + "\nLait : " + stock.getMilk() + "\nLaine : " + stock.getWool());
        alert.showAndWait();
    }


    public static void main(String[] args) {
        launch(args);
    }

}