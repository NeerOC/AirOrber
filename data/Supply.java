package data;

public enum Supply {
    UNCHARGEDGLORY("Amulet of glory", 1704,0,0, 0, 0),
    AIRORB("Air orb", 573,0,0, 0,0),
    STAFFOFAIR("Staff of air", 1381,10,0, 0,0),
    STAMINAPOTION("Stamina Potion", 12625,80,0, 0,0),
    AMULETOFGLORY("Amulet of glory(6)", 11978,14,0, 0,0),
    UNPOWEREDORB("Unpowered orb", 567,2000,0, 0,0),
    JUGOFWINE("Jug of wine", 1993, 500,0, 0,0),
    COSMICRUNE("Cosmic rune", 564,6000,0, 0,0),
    COINS("Coins", 995, 0, 0, 0,0);

    private String itemName;
    private int countWanted;
    private int itemID;
    private int currentCount;
    private int price;
    private int buyCount;

    Supply(String itemName, int itemID, int countWanted, int currentCount, int price, int buyCount){
        this.itemName = itemName;
        this.itemID = itemID;
        this.countWanted = countWanted;
        this.currentCount = currentCount;
        this.price = price;
        this.buyCount = buyCount;
    }

    public void setCurrentCount(int currentCount) {
        this.currentCount = currentCount;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public int getNotedID() { return itemID + 1; }

    public int getBuyCount() {
        return buyCount;
    }

    public void setBuyCount(int buyCount) {
        this.buyCount = buyCount;
    }

    public int getCurrentCount() {
        return currentCount;
    }

    public int getPrice() {
        return price;
    }

    public String getItemName() {
        return itemName;
    }

    public int getCountWanted() {
        return countWanted;
    }

    public int getItemID() {
        return itemID;
    }

    public int plusTenPercent(){
        double tenPercentPlus = getPrice() * 1.10;
        return (int) tenPercentPlus;
    }

    public int plusFifteenPercent(){
        double tenPercentPlus = getPrice() * 1.15;
        return (int) tenPercentPlus;
    }

    public int plusTwentyPercent(){
        double tenPercentPlus = getPrice() * 1.20;
        return (int) tenPercentPlus;
    }

    public int plusTwentyFivePercent(){
        double tenPercentPlus = getPrice() * 1.25;
        return (int) tenPercentPlus;
    }


    public int minusTenPercent(){
        double tenPercentOff = getPrice() * 0.90;
        return (int) tenPercentOff;
    }
}
