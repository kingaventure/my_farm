import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;

public class Save {
    private static final String SAVE_FILE = "grid_state.txt";
    private int coins;
    private Stock stock;
    private static final int ROWS = 40;
    private static final int COLUMNS = 40;
    private Rectangle[][] rectangles;

    public Save(int coins, Stock stock, Rectangle[][] rectangles) {
        this.coins = coins;
        this.stock = stock;
        this.rectangles = rectangles;
    }

    public void saveGridState() {
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

    public void loadGridState() {
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

    public int getCoins() {
        return coins;
    }

    public void setCoins(int coins) {
        this.coins = coins;
    }
}