import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;

import java.io.*;
import java.util.*;

public class Main extends Application {

    private static int ROWS = 40;
    private static int COLUMNS = 40;
    private static int RECT_SIZE = 20;
    private static String SAVE_FILE = "grid_state.txt";
    private Rectangle[][] rectangles = new Rectangle[ROWS][COLUMNS];
    private int coins = 1000;
    private Label coinLabel = new Label("Pièces: " + coins);
    private Button saveButton = new Button("Sauvegarder");
    private Button houseButton = new Button("Grange");
    private Button marketButton = new Button("Marché");
    private Button infoButton = new Button("Information");
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
                saveGridState();
            }
        });

        saveButton.setOnAction(event -> saveGridState());
        marketButton.setOnAction(event -> openMarket());
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

    private void saveGridState() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(SAVE_FILE))) {
            writer.println(coins);
            writer.println(stock.getWheatSeeds());
            writer.println(stock.getCornSeeds());
            writer.println(stock.getRiceSeeds());
            writer.println(stock.getChickens());
            writer.println(stock.getCows());
            writer.println(stock.getSheep());
            writer.println(stock.getWheat());
            writer.println(stock.getCorn());
            writer.println(stock.getRice());
            writer.println(stock.getEggs());
            writer.println(stock.getMilk());
            writer.println(stock.getWool());
            for (int row = 0; row < ROWS; row++) {
                for (int col = 0; col < COLUMNS; col++) {
                    Color color = (Color) rectangles[row][col].getFill();
                    if (color.equals(Color.GREEN)) {
                        writer.print("1");
                    } else if (color.equals(Color.PINK)) {
                        writer.print("2");
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
            if (scanner.hasNextInt()) {
                stock.addWheat(scanner.nextInt());
                scanner.nextLine();
            }
            if (scanner.hasNextInt()) {
                stock.addCorn(scanner.nextInt());
                scanner.nextLine();
            }
            if (scanner.hasNextInt()) {
                stock.addRice(scanner.nextInt());
                scanner.nextLine();
            }
            if (scanner.hasNextInt()) {
                stock.addEggs(scanner.nextInt());
                scanner.nextLine();
            }
            if (scanner.hasNextInt()) {
                stock.addMilk(scanner.nextInt());
                scanner.nextLine();
            }
            if (scanner.hasNextInt()) {
                stock.addWool(scanner.nextInt());
                scanner.nextLine();
            }
            for (int row = 0; row < ROWS; row++) {
                String line = scanner.nextLine();
                for (int col = 0; col < COLUMNS; col++) {
                    char ch = line.charAt(col);
                    if (ch == '1') {
                        rectangles[row][col].setFill(Color.GREEN);
                    } else if (ch == '2') {
                        rectangles[row][col].setFill(Color.PINK);
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
            plantButton.setOnAction(e -> {
                if (toRgbString(color).equals(toRgbString(Color.GREEN))) {
                    showPlantOptions(plantButton, progressBar, colorStage, rect);
                } else if (toRgbString(color).equals(toRgbString(Color.PINK))) {
                    showAnimalOptions(plantButton, progressBar, colorStage, rect);
                }
            });



            VBox vbox = new VBox(plantButton, progressBar);
            vbox.setAlignment(Pos.CENTER);
            vbox.setSpacing(10);
            pane.setCenter(vbox);

            colorStage.setScene(scene);
            colorStage.setTitle("Zoom de parcelle");
            colorStage.show();
        }
    }


    private void showPlantOptions(Button button, ProgressBar progressBar, Stage colorStage, Rectangle rect) {
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
                startPlantingTimer(button, progressBar, "Blé", colorStage, rect);
            } else if (choice.contains("Graines de maïs") && stock.getCornSeeds() > 0) {
                stock.addCornSeeds(-1);
                startPlantingTimer(button, progressBar, "Maïs", colorStage, rect);
            } else if (choice.contains("Graines de riz") && stock.getRiceSeeds() > 0) {
                stock.addRiceSeeds(-1);
                startPlantingTimer(button, progressBar, "Riz", colorStage, rect);
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


    private void startPlantingTimer(Button button, ProgressBar progressBar, String seedType, Stage colorStage, Rectangle rect) {
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


    private void openMarket() {
        Random random = new Random();
        int wheatSeedPrice = 50 + (random.nextBoolean() ? 10 : -10);
        int cornSeedPrice = 60 + (random.nextBoolean() ? 10 : -10);
        int riceSeedPrice = 70 + (random.nextBoolean() ? 10 : -10);
        int chickenPrice = 100 + random.nextInt(101);
        int cowPrice = 200 + random.nextInt(201);
        int sheepPrice = 150 + random.nextInt(151);
        int wheatPrice = 50 + random.nextInt(51);
        int ricePrice = 60 + random.nextInt(61);
        int cornPrice = 70 + random.nextInt(71);
        int eggPrice = 60 + random.nextInt(11);
        int milkPrice = 70 + random.nextInt(21);
        int woolPrice = 80 + random.nextInt(31);

        ListView<String> listView = new ListView<>();
        listView.getItems().addAll(
                "Graines de blé (" + stock.getWheatSeeds() + ") - " + wheatSeedPrice + " pièces",
                "Graines de maïs (" + stock.getCornSeeds() + ") - " + cornSeedPrice + " pièces",
                "Graines de riz (" + stock.getRiceSeeds() + ") - " + riceSeedPrice + " pièces",
                "Poulet (" + stock.getChickens() + ") - " + chickenPrice + " pièces",
                "Vache (" + stock.getCows() + ") - " + cowPrice + " pièces",
                "Mouton (" + stock.getSheep() + ") - " + sheepPrice + " pièces",
                "Blé (" + stock.getWheat() + ") - " + wheatPrice + " pièces",
                "Riz (" + stock.getRice() + ") - " + ricePrice + " pièces",
                "Maïs (" + stock.getCorn() + ") - " + cornPrice + " pièces",
                "Oeufs (" + stock.getEggs() + ") -" + eggPrice + " pièces",
                "Lait (" + stock.getMilk() + ") -" + milkPrice + " pièces",
                "Laine (" + stock.getWool() + ") -" + woolPrice + " pièces"
        );
        listView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        Button buyButton = new Button("Acheter");
        buyButton.setOnAction(event -> {
            ObservableList<String> selectedItems = listView.getSelectionModel().getSelectedItems();
            int totalCost = 0;
            for (String item : selectedItems) {
                if (item.contains("Graines de blé") && coins >= wheatSeedPrice) {
                    stock.addWheatSeeds(1);
                    totalCost += wheatSeedPrice;
                } else if (item.contains("Graines de maïs") && coins >= cornSeedPrice) {
                    stock.addCornSeeds(1);
                    totalCost += cornSeedPrice;
                } else if (item.contains("Graines de riz") && coins >= riceSeedPrice) {
                    stock.addRiceSeeds(1);
                    totalCost += riceSeedPrice;
                } else if (item.contains("Poulet") && coins >= chickenPrice) {
                    stock.addChickens(1);
                    totalCost += chickenPrice;
                } else if (item.contains("Vache") && coins >= cowPrice) {
                    stock.addCows(1);
                    totalCost += cowPrice;
                } else if (item.contains("Mouton") && coins >= sheepPrice) {
                    stock.addSheep(1);
                    totalCost += sheepPrice;
                } else if (item.contains("Blé") && coins >= wheatPrice) {
                    stock.addWheat(1);
                    totalCost += wheatPrice;
                } else if (item.contains("Riz") && coins >= ricePrice) {
                    stock.addRice(1);
                    totalCost += ricePrice;
                } else if (item.contains("Maïs") && coins >= cornPrice) {
                    stock.addCorn(1);
                    totalCost += cornPrice;
                } else if (item.contains("Oeufs")) {
                    stock.addEggs(1);
                    totalCost += eggPrice;
                }
                else if (item.contains("Lait")) {
                    stock.addMilk(1);
                    totalCost += milkPrice;
                }
                else if (item.contains("Laine")) {
                    stock.addWool(1);
                    totalCost += woolPrice;
                }
                else {
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

        Button sellButton = new Button("Vendre");
        sellButton.setOnAction(event -> {
            ObservableList<String> selectedItems = listView.getSelectionModel().getSelectedItems();
            int totalGain = 0;
            for (String item : selectedItems) {
                if (item.contains("Graines de blé") && stock.getWheatSeeds() > 0) {
                    stock.addWheatSeeds(-1);
                    totalGain += wheatSeedPrice;
                } else if (item.contains("Graines de maïs") && stock.getCornSeeds() > 0) {
                    stock.addCornSeeds(-1);
                    totalGain += cornSeedPrice;
                } else if (item.contains("Graines de riz") && stock.getRiceSeeds() > 0) {
                    stock.addRiceSeeds(-1);
                    totalGain += riceSeedPrice;
                } else if (item.contains("Poulet") && stock.getChickens() > 0) {
                    stock.addChickens(-1);
                    totalGain += chickenPrice;
                } else if (item.contains("Vache") && stock.getCows() > 0) {
                    stock.addCows(-1);
                    totalGain += cowPrice;
                } else if (item.contains("Mouton") && stock.getSheep() > 0) {
                    stock.addSheep(-1);
                    totalGain += sheepPrice;
                } else if (item.contains("Blé") && stock.getWheat() > 0) {
                    stock.addWheat(-1);
                    totalGain += wheatPrice;
                } else if (item.contains("Riz") && stock.getRice() > 0) {
                    stock.addRice(-1);
                    totalGain += ricePrice;
                } else if (item.contains("Maïs") && stock.getCorn() > 0) {
                    stock.addCorn(-1);
                    totalGain += cornPrice;
                } else if (item.contains("Oeufs") && stock.getEggs() > 0) {
                    stock.addEggs(-1);
                    totalGain += eggPrice;
                    } else if ( item.contains("Lait") && stock.getMilk() > 0) {
                    stock.addMilk(-1);
                    totalGain += milkPrice;
                    } else if ( item.contains("Laine") && stock.getWool() > 0) {
                    stock.addWool(-1);
                    totalGain += woolPrice;
                } else {
                    showAlert("Vous n'avez pas assez de stock pour vendre " + item);
                    return;
                }
            }
            coins += totalGain;
            updateCoinLabel();
        });

        VBox vbox = new VBox(listView, buyButton, sellButton);
        Scene scene = new Scene(vbox, 300, 400);
        Stage marketStage = new Stage();
        marketStage.setScene(scene);
        marketStage.setTitle("Marché");
        marketStage.show();
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