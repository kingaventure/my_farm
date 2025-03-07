import javafx.animation.*;
import javafx.application.Platform;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Random;

public class Animal {
    private Stock stock;

    public Animal(Stock stock) {
        this.stock = stock;
    }

    protected void showAnimalOptions(ImageView imageView, Button plantButton, ProgressBar progressBar, Stage colorStage, Rectangle rect) {
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
            if (choice.contains("Poulet") && stock.getWheat() > 0) {
                stock.addChickens(-1);
                stock.addWheat(-1);
                imageView.setImage(new Image("/img/chick_yn.png"));
                startAnimalTimer(imageView ,plantButton, progressBar, "chicken", colorStage, rect);
            } else if (choice.contains("Vache") && stock.getCorn() > 0) {
                stock.addCows(-1);
                stock.addCorn(-1);
                imageView.setImage(new Image("/img/cow_yn.png"));
                startAnimalTimer(imageView, plantButton, progressBar, "cow", colorStage, rect);
            } else if (choice.contains("Mouton") && stock.getRice() > 0) {
                stock.addSheep(-1);
                stock.addRice(-1);
                imageView.setImage(new Image("/img/mout_yn.png"));
                startAnimalTimer(imageView, plantButton, progressBar, "sheep", colorStage, rect);
            } else {
                showAlert("Vous n'avez pas assez de ressources pour élever " + choice);
                colorStage.close();
            }
        });
    }

    private void startAnimalTimer(ImageView imageView, Button plantButton, ProgressBar progressBar, String type, Stage stage, Rectangle rect) {
        int growTime;
        switch (type) {
            case "chicken":
                growTime = 40;
                break;
            case "cow":
                growTime = 50;
                break;
            case "sheep":
                growTime = 60;
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
                        stock.addEggs(random.nextInt(3) + 1);
                        break;
                    case "cow":
                        stock.addMilk(random.nextInt(4) + 1);
                        break;
                    case "sheep":
                        stock.addWool(random.nextInt(8) + 1);
                        break;
                }
            });
        });

        progressBar.setProgress(0);
        plantButton.setDisable(true);

        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
            double progress = progressBar.getProgress() + 1.0 / growTime;
            progressBar.setProgress(progress);
            if (progress >= 0.74 && progress < 0.75) {
                switch (type) {
                    case "chicken":
                        imageView.setImage(new Image("/img/chick_ad.png"));
                        break;
                    case "cow":
                        imageView.setImage(new Image("/img/cow_ad.png"));
                        break;
                    case "sheep":
                        imageView.setImage(new Image("/img/mout_ad.png"));
                        break;
                }
            }
        }));
        timeline.setCycleCount(growTime);
        timeline.play();
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Informations");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}