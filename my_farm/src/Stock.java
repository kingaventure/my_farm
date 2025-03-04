public class Stock {
    private int wheatSeeds;
    private int cornSeeds;
    private int riceSeeds;
    private int chickens;
    private int cows;
    private int sheep;
    private int wheat;
    private int corn;
    private int rice;

    public Stock() {
        this.wheatSeeds = 0;
        this.cornSeeds = 0;
        this.riceSeeds = 0;
        this.chickens = 0;
        this.cows = 0;
        this.sheep = 0;
    }

    public int getWheat() {
        return wheat;
    }

    public void addWheat(int amount) {
        this.wheat += amount;
    }

    public int getCorn() {
        return corn;
    }

    public void addCorn(int amount) {
        this.corn += amount;
    }

    public int getRice() {
        return rice;
    }

    public void addRice(int amount) {
        this.rice += amount;
    }

    public int getWheatSeeds() {
        return wheatSeeds;
    }

    public void addWheatSeeds(int amount) {
        this.wheatSeeds += amount;
    }

    public int getCornSeeds() {
        return cornSeeds;
    }

    public void addCornSeeds(int amount) {
        this.cornSeeds += amount;
    }

    public int getRiceSeeds() {
        return riceSeeds;
    }

    public void addRiceSeeds(int amount) {
        this.riceSeeds += amount;
    }

    public int getChickens() {
        return chickens;
    }

    public void addChickens(int amount) {
        this.chickens += amount;
    }

    public int getCows() {
        return cows;
    }

    public void addCows(int amount) {
        this.cows += amount;
    }

    public int getSheep() {
        return sheep;
    }

    public void addSheep(int amount) {
        this.sheep += amount;
    }
}
