import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.Random;

public class Market {
    private int coins;
    private Stock stock;
    private Label coinLabel;

    public Market(int coins, Stock stock, Label coinLabel) {
        this.coins = coins;
        this.stock = stock;
        this.coinLabel = coinLabel;
    }

    public void openMarket() {
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
        updateMarketList(listView, wheatSeedPrice, cornSeedPrice, riceSeedPrice, chickenPrice, cowPrice, sheepPrice, wheatPrice, ricePrice, cornPrice, eggPrice, milkPrice, woolPrice);

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
                } else if (item.contains("Lait")) {
                    stock.addMilk(1);
                    totalCost += milkPrice;
                } else if (item.contains("Laine")) {
                    stock.addWool(1);
                    totalCost += woolPrice;
                } else {
                    showAlert("Vous n'avez pas assez de pièces pour acheter " + item);
                    return;
                }
            }
            if (coins >= totalCost) {
                coins -= totalCost;
                updateCoinLabel();
                updateMarketList(listView, wheatSeedPrice, cornSeedPrice, riceSeedPrice, chickenPrice, cowPrice, sheepPrice, wheatPrice, ricePrice, cornPrice, eggPrice, milkPrice, woolPrice);
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
                } else if (item.contains("Lait") && stock.getMilk() > 0) {
                    stock.addMilk(-1);
                    totalGain += milkPrice;
                } else if (item.contains("Laine") && stock.getWool() > 0) {
                    stock.addWool(-1);
                    totalGain += woolPrice;
                } else {
                    showAlert("Vous n'avez pas assez de stock pour vendre " + item);
                    return;
                }
            }
            coins += totalGain;
            updateCoinLabel();
            updateMarketList(listView, wheatSeedPrice, cornSeedPrice, riceSeedPrice, chickenPrice, cowPrice, sheepPrice, wheatPrice, ricePrice, cornPrice, eggPrice, milkPrice, woolPrice);
        });

        VBox vbox = new VBox(listView, buyButton, sellButton);
        Scene scene = new Scene(vbox, 300, 400);
        Stage marketStage = new Stage();
        marketStage.setScene(scene);
        marketStage.setTitle("Marché");
        marketStage.show();
    }

    private void updateMarketList(ListView<String> listView, int wheatSeedPrice, int cornSeedPrice, int riceSeedPrice, int chickenPrice, int cowPrice, int sheepPrice, int wheatPrice, int ricePrice, int cornPrice, int eggPrice, int milkPrice, int woolPrice) {
        listView.getItems().clear();
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
                "Oeufs (" + stock.getEggs() + ") - " + eggPrice + " pièces",
                "Lait (" + stock.getMilk() + ") - " + milkPrice + " pièces",
                "Laine (" + stock.getWool() + ") - " + woolPrice + " pièces"
        );
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
}