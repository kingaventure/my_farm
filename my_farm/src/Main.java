import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import java.util.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Alert;
import javafx.scene.layout.BorderPane;

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

    @Override
    public void start(Stage primaryStage) {
        market = new Market(coins, stock);
        saveManager = new Save(coins, stock, rectangles);
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

    private void showInformation() {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setHeaderText(null);
        alert.setContentText("Bienvenue dans MyFarm Julien. Vous pouvez acheter des parcelles de terrain en cliquant dessus avec le bouton gauche de la souris. Vous pouvez également vendre des parcelles en cliquant dessus avec le bouton droit de la souris. Pour sauvegarder l'état de la ferme, appuyez sur la touche S, pour commencer à planter ou élever presser clique droit sur une parcelle déja acheter. Si vous plantez une graine sur une parcelle adjacente à une autre parcelle de champ, vous obtiendrez un bonus de récolte. Vous pouvez également acheter des graines, des animaux et vendre des produits sur le marché. Bonne chance!");
        alert.showAndWait();
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
            colorStage.setOnCloseRequest(event -> {
                if (colorStage.isShowing()) {
                    event.consume();
                }
            });

            BorderPane pane = new BorderPane();
            pane.setStyle("-fx-background-color: " + toRgbString(color) + ";");
            Scene scene = new Scene(pane, 200, 200);
            final Button plantButton;
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
                    showPlantOptions(imageView, plantButton, progressBar, colorStage, rect);
                } else if (toRgbString(color).equals(toRgbString(Color.PINK))) {
                    showAnimalOptions(plantButton, progressBar, colorStage, rect);
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


    private void showPlantOptions(ImageView imageView, Button button, ProgressBar progressBar, Stage colorStage, Rectangle rect) {
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
                imageView.setImage(new Image("/img/sprut.png"));
                startPlantingTimer(imageView, button, progressBar, "Blé", colorStage, rect);
            } else if (choice.contains("Graines de maïs") && stock.getCornSeeds() > 0) {
                stock.addCornSeeds(-1);
                imageView.setImage(new Image("/img/sprut.png"));
                startPlantingTimer(imageView, button, progressBar, "Maïs", colorStage, rect);
            } else if (choice.contains("Graines de riz") && stock.getRiceSeeds() > 0) {
                stock.addRiceSeeds(-1);
                imageView.setImage(new Image("/img/sprut.png"));
                startPlantingTimer(imageView, button, progressBar, "Riz", colorStage, rect);
            } else {
                showAlert("Vous n'avez pas assez de graines pour planter " + choice);
                colorStage.close();
            }
        });
    }


    private void showAnimalOptions(Button plantButton, ProgressBar progressBar, Stage colorStage, Rectangle rect) {
        List<String> choices = Arrays.asList(
                "Poulet (" + stock.getChickens() + ")" + " - 1 blé",
                "Vache (" + stock.getCows() + ") - 1 maïs",
                "Mouton (" + stock.getSheep() + ") - 1 riz"
        );

        ChoiceDialog<String> dialog = new ChoiceDialog<>(choices.get(0), choices);
        dialog.setTitle("Elevage");
        dialog.setHeaderText("Choisissez le type d'animal à élever:");
        dialog.setContentText("Animal:");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(choice -> {
            if (choice.contains("Poulet") && stock.getChickens() > 0 && stock.getWheat() > 0) {
                stock.addChickens(-1);
                stock.addWheat(-1);
                startAnimalTimer(plantButton, progressBar, "chicken", colorStage, rect);
            } else if (choice.contains("Vache") && stock.getCows() > 0 && stock.getCorn() > 0) {
                stock.addCows(-1);
                stock.addCorn(-1);
                startAnimalTimer(plantButton, progressBar, "cow", colorStage, rect);
            } else if (choice.contains("Mouton") && stock.getSheep() > 0 && stock.getRice() > 0) {
                stock.addSheep(-1);
                stock.addRice(-1);
                startAnimalTimer(plantButton, progressBar, "sheep", colorStage, rect);
            } else {
                showAlert("Vous n'avez pas assez d'animaux ou ressource pour élever " + choice);
                colorStage.close();
            }
        });
    }


    private void startPlantingTimer(ImageView imageView, Button button, ProgressBar progressBar, String seedType, Stage colorStage, Rectangle rect) {
        int growTime;
        switch (seedType) {
            case "Blé":
                growTime = 20;
                break;
            case "Maïs":
                growTime = 50;
                break;
            case "Riz":
                growTime = 100;
                break;
            default:
                throw new IllegalArgumentException("Unknown seed type: " + seedType);
        }

        Color originalColor = (Color) rect.getFill();
        rect.setFill(Color.YELLOW);

        TimerManager timerManager = new TimerManager(growTime);
        timerManager.start(() -> {
            Platform.runLater(() -> {
                progressBar.setProgress(1.0);
                button.setDisable(false);

                rect.setFill(originalColor);
                colorStage.close();
                showAlert("La plantation de " + seedType + " est terminée!");
                Random random = new Random();
                int numberOfCropTouching = calculateBonusTouchingCrop(rect);

                switch (seedType) {
                    case "Blé":
                        stock.addWheat((random.nextInt(3)+ 1) * numberOfCropTouching);
                        break;
                    case "Maïs":
                        stock.addCorn((random.nextInt(5)+ 1) * numberOfCropTouching);
                        break;
                    case "Riz":
                        stock.addRice((random.nextInt(8)+ 1) * numberOfCropTouching);
                        break;
                }
            });
        });

        progressBar.setProgress(0);
        button.setDisable(true);
        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
            double progress = progressBar.getProgress() + 1.0 / growTime;
            progressBar.setProgress(progress);

            if (progress >= 0.25 && progress < 0.26) {
                switch (seedType) {
                    case "Blé":
                        imageView.setImage(new Image("/img/wheat_half.png"));
                        break;
                    case "Maïs":
                        imageView.setImage(new Image("/img/corn_half.png"));
                        break;
                    case "Riz":
                        imageView.setImage(new Image("/img/rice_half.png"));
                        break;
                }
            } else if (progress >= 0.75 && progress < 0.76) {
                switch (seedType) {
                    case "Blé":
                        imageView.setImage(new Image("/img/wheat_hole.png"));
                        break;
                    case "Maïs":
                        imageView.setImage(new Image("/img/corn_hole.png"));
                        break;
                    case "Riz":
                        imageView.setImage(new Image("/img/rice_hole.png"));
                        break;
                }
            }
        }));
        timeline.setCycleCount(growTime);
        timeline.play();
    }


    private void startAnimalTimer(Button plantButton, ProgressBar progressBar, String type, Stage stage, Rectangle rect) {
        int growTime;
        switch (type) {
            case "chicken":
                growTime = 100;
                break;
            case "cow":
                growTime = 120;
                break;
            case "sheep":
                growTime = 80;
                break;
            default:
                throw new IllegalArgumentException("Unknown animal type: " + type);
        }

        Color originalColor = (Color) rect.getFill();
        rect.setFill(Color.YELLOW);

        TimerManager timerManager = new TimerManager(growTime);
        timerManager.start(() -> {
            Platform.runLater(() -> {
                progressBar.setProgress(1.0);
                plantButton.setDisable(false);
                rect.setFill(originalColor);
                stage.close();
                showAlert("L'élevage de " + type + " est terminé!");
                Random random = new Random();
                switch (type) {
                    case "chicken":
                        stock.addEggs( random.nextInt(3)+ 1);
                        break;
                    case "cow":
                        stock.addMilk( random.nextInt(4)+ 1);
                        break;
                    case "sheep":
                        stock.addWool( random.nextInt(8)+ 1);
                        break;
                }
            });
        });

        progressBar.setProgress(0);
        plantButton.setDisable(true);

        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
            double progress = progressBar.getProgress() + 1.0 / growTime;
            progressBar.setProgress(progress);
        }));
        timeline.setCycleCount(growTime);
        timeline.play();
    }


    private String toRgbString(Color color) {
        int r = (int) (color.getRed() * 255);
        int g = (int) (color.getGreen() * 255);
        int b = (int) (color.getBlue() * 255);
        return String.format("rgb(%d, %d, %d)", r, g, b);
    }


    private int getCropTouching(Rectangle rect) {
        int cropTouching = 0;
        Color targetColor = (Color) rect.getFill();
        int row = -1, col = -1;

        for (int r = 0; r < ROWS; r++) {
            for (int c = 0; c < COLUMNS; c++) {
                if (rectangles[r][c] == rect) {
                    row = r;
                    col = c;
                    break;
                }
            }
            if (row != -1) break;
        }

        int[][] directions = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};
        for (int[] dir : directions) {
            int newRow = row + dir[0];
            int newCol = col + dir[1];
            if (newRow >= 0 && newRow < ROWS && newCol >= 0 && newCol < COLUMNS) {
                if (rectangles[newRow][newCol].getFill().equals(targetColor)) {
                    cropTouching++;
                }
            }
        }

        return cropTouching;
    }


    private int calculateBonusTouchingCrop(Rectangle rect) {
        int bonus = 0;
        int cropTouching = getCropTouching(rect);
        if (cropTouching > 4) {
            bonus = 10;
        } else if (cropTouching < 4 && cropTouching > 1) {
            bonus = 5;
        } else if (cropTouching == 1) {
            bonus = 3;
        } else {
            bonus = 1;
        }
        return bonus;
    }


    private void showStock() {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Stock");
        alert.setHeaderText(null);
        alert.setContentText("Graines : \n\nGraines de blé : " + stock.getWheatSeeds() + "\nGraines de riz : " + stock.getRiceSeeds() + "\nGraines de maïs : " + stock.getCornSeeds() + "\n\nAnimaux : \n\nPoulets : " + stock.getChickens() + "\nVaches : " + stock.getCows() + "\nMoutons : " + stock.getSheep() + "\n\nProduits : \n\nBlé : " + stock.getWheat() + "\nRiz : " + stock.getRice() + "\nMaïs : " + stock.getCorn() + "\nOeufs : " + stock.getEggs() + "\nLait : " + stock.getMilk() + "\nLaine : " + stock.getWool());
        alert.showAndWait();
    }


    public static void main(String[] args) {
        launch(args);
    }

}