import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Random;

public class Plant {
    private Stock stock;
    private static final int ROWS = 40;
    private static final int COLUMNS = 40;
    private Rectangle[][] rectangles;

    public Plant(Stock stock, Rectangle[][] rectangles) {
        this.stock = stock;
        this.rectangles = rectangles;
    }

    protected void showPlantOptions(ImageView imageView, Button button, ProgressBar progressBar, Stage colorStage, Rectangle rect) {
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
                        stock.addWheat((random.nextInt(3) + 1) * numberOfCropTouching);
                        break;
                    case "Maïs":
                        stock.addCorn((random.nextInt(5) + 1) * numberOfCropTouching);
                        break;
                    case "Riz":
                        stock.addRice((random.nextInt(8) + 1) * numberOfCropTouching);
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

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Informations");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}