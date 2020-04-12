package api;

import data.Supply;
import org.rspeer.runetek.adapter.component.InterfaceComponent;
import org.rspeer.runetek.adapter.component.Item;
import org.rspeer.runetek.adapter.scene.PathingEntity;
import org.rspeer.runetek.api.commons.Time;
import org.rspeer.runetek.api.component.Interfaces;
import org.rspeer.runetek.api.component.tab.Inventory;
import org.rspeer.runetek.api.scene.Players;
import org.rspeer.ui.Log;

import java.io.IOException;
import java.util.function.Predicate;

public class Methods {

    private static Predicate<Item> gloryPred = item -> item.getName().contains("Amulet of glory(");
    private static int oneDoseID = 12631;
    private static int twoDoseID = 12629;
    private static int threeDoseID = 12627;
    private static int fourDoseID = 12625;

    public static void setSupplyCount() {
        if(BankCache.hasFetched()) {
            for (Supply aSupply : Supply.values()) {
                if (aSupply.getItemName().contains("Stamina")) {
                    aSupply.setCurrentCount(getStaminaDoseCount());
                } else {
                    aSupply.setCurrentCount(BankCache.getCount(true, aSupply.getItemID()));
                    Time.sleep(50);
                }
            }
        }
    }

    public static int getWildyLevel() {
        final InterfaceComponent level = Interfaces.getFirst(90, a -> a.getText().contains("Level: "));
        return level == null ? 0 : Integer.parseInt(level.getText().replace("Level: ", ""));
    }


    public static boolean canAttackUs(PathingEntity targetter){
        int tlevel = targetter.getCombatLevel();
        int mylevel = Players.getLocal().getCombatLevel();
        int lvlmax = mylevel +getWildyLevel();
        int lvlmin = mylevel -getWildyLevel();
        return tlevel <= lvlmax && tlevel >= lvlmin;
    }

    public static void calcBuyCount() {
        if(BankCache.hasFetched()) {
            for (Supply aSupply : Supply.values()) {
                if (aSupply.equals(Supply.STAMINAPOTION)) {
                    int getCount = aSupply.getCountWanted() - aSupply.getCurrentCount();
                    aSupply.setBuyCount(getCount / 4);
                } else {
                    aSupply.setBuyCount(aSupply.getCountWanted() - aSupply.getCurrentCount());
                }
            }
        }
    }

    public static void getPrices() throws IOException {
        for (Supply aSupply : Supply.values()) {
            if (aSupply.getPrice() == 0) {
                aSupply.setPrice(ExPriceCheck.getOSBuddyPrice(aSupply.getItemID()));
                Time.sleep(50, 100);
            }
        }
        Log.fine("Prices set");
    }

    public static boolean pricesGot() {
        for (Supply aSupply : Supply.values()) {
            if (!aSupply.getItemName().equals("Coins") && aSupply.getPrice() == 0) {
                return false;
            }
        }
        return true;
    }

    public static int getCoinRequired() {
        int total = 0;
        for (Supply aSupply : Supply.values()) {
            if (aSupply.getBuyCount() > 0) {
                total += aSupply.getBuyCount() * aSupply.getPrice();
            }
        }
        return total;
    }


    public static boolean invContainsSupplies() {
        for (Supply aSupply : Supply.values()) {
            if (!aSupply.equals(Supply.COINS)) {
                if (Inventory.contains(aSupply.getItemID())) {
                    return true;
                }
            }
        }
        return Inventory.getFirst(Item::isNoted) != null;
    }

    private static int getStaminaDoseCount() {
        int total;
        int oneDoseCount = BankCache.getCount(true,oneDoseID);
        int twoDoseCount = BankCache.getCount(true,twoDoseID) * 2;
        int threeDoseCount = BankCache.getCount(true,threeDoseID) * 3;
        int fourDoseCount = BankCache.getCount(true,fourDoseID) * 4;
        total = oneDoseCount + twoDoseCount + threeDoseCount + fourDoseCount;
        return total;
    }
}
