package tasks;

import api.BankCache;
import api.Config;
import api.ExGrandExchange;
import api.Methods;
import com.dax.walker.DaxWalker;
import com.dax.walker.Server;
import data.Locations;
import data.Supply;
import org.rspeer.runetek.adapter.scene.Npc;
import org.rspeer.runetek.api.Game;
import org.rspeer.runetek.api.commons.Time;
import org.rspeer.runetek.api.commons.math.Random;
import org.rspeer.runetek.api.component.Bank;
import org.rspeer.runetek.api.component.GrandExchange;
import org.rspeer.runetek.api.component.GrandExchangeSetup;
import org.rspeer.runetek.api.component.tab.Inventory;
import org.rspeer.runetek.api.scene.Npcs;
import org.rspeer.runetek.providers.RSGrandExchangeOffer;
import org.rspeer.script.task.Task;
import org.rspeer.ui.Log;

import java.io.IOException;

public class Restock extends Task {
    private DaxWalker daxWalker = new DaxWalker(new Server("sub_DPjXXzL5DeSiPf", "PUBLIC-KEY"));

    @Override
    public boolean validate() {
        return Game.isLoggedIn() && Config.isRestockTime() ;
    }

    @Override
    public int execute() {
        Methods.setSupplyCount(); // Sets supply count
        Methods.calcBuyCount();
        Config.setTask("Restocking supplies");
        if (!Methods.pricesGot()) {
            Log.fine("Fetching prices");
            try {
                Methods.getPrices();
                return Random.nextInt(100);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (Locations.GRAND_EXCHANGE.getLocationPosition().distance() > 5) {
            daxWalker.walkTo(Locations.GRAND_EXCHANGE.getLocationPosition());
            return Random.nextInt(600, 800);
        }
        if (GrandExchange.isOpen() && GrandExchange.collectAll(false)) {
            return Random.nextInt(600, 800);
        }

        if (BankCache.hasFetched() && Methods.pricesGot() && !GrandExchange.isOpen()) {
            Methods.calcBuyCount();
            Log.info("Calculating buy counts..");
            for(Supply aSupply : Supply.values()){
                if(aSupply.getBuyCount() > 0){
                    Log.info("Should buy: " + aSupply.getItemName() + " Amt: " + aSupply.getBuyCount() + " Because we have " + aSupply.getCurrentCount() + " and we need " + aSupply.getCountWanted());
                }
            }
        }

        if (BankCache.hasFetched()) {
            if (Supply.AIRORB.getCurrentCount() > 0) {
                if (getAirOrbs()) {
                    return Random.nextInt(600, 800);
                }
            }
            if (Supply.UNCHARGEDGLORY.getCurrentCount() > 0) {
                if (getAllUncharged()) {
                    return Random.nextInt(600, 800);
                }
            }
            // If we have shit to sell open GE and sell.
            if (Inventory.contains("Air orb") || Inventory.getCount("Amulet of glory") > 1) {
                if (!GrandExchange.isOpen() && openGE()) {
                    return Random.nextInt(600, 800);
                } else {
                    ExGrandExchange.sell(Supply.AIRORB.getItemID() + 1, 0, Supply.AIRORB.minusTenPercent(), false);
                    ExGrandExchange.sell(Supply.UNCHARGEDGLORY.getItemID() + 1, 0, Supply.UNCHARGEDGLORY.minusTenPercent(), false);
                }
            } else {
                if (Methods.invContainsSupplies()) {
                    if (!Bank.isOpen() && openBank()) {
                        return Random.nextInt(600, 800);
                    }
                    if (Bank.isOpen()) {
                        Bank.depositAllExcept("Coins");
                        return Random.nextInt(600, 800);
                    }
                }
                if (Inventory.getCount(true, Supply.COINS.getItemID()) > Methods.getCoinRequired()) {
                    if (!GrandExchange.isOpen()) {
                        openGE();
                    } else {
                        Methods.calcBuyCount();
                        // BUY STUFF STARTS HERE!!!!!!!!!!!
                        if (Supply.AMULETOFGLORY.getBuyCount() > 0) {
                            if (buyItem(Supply.AMULETOFGLORY.getItemID(), Supply.AMULETOFGLORY.getBuyCount(), Supply.AMULETOFGLORY.plusFifteenPercent())) {
                                Supply.AMULETOFGLORY.setBuyCount(0);
                            } else {
                                return Random.nextInt(600, 800);
                            }
                        } else if (Supply.UNPOWEREDORB.getBuyCount() > 0) {
                            if (buyItem(Supply.UNPOWEREDORB.getItemID(), Supply.UNPOWEREDORB.getBuyCount(), 150)) {
                                Supply.UNPOWEREDORB.setBuyCount(0);
                            } else {
                                return Random.nextInt(600, 800);
                            }
                        } else if (Supply.COSMICRUNE.getBuyCount() > 0) {
                            if (buyItem(Supply.COSMICRUNE.getItemID(), Supply.COSMICRUNE.getBuyCount(), Supply.COSMICRUNE.getPrice() + 30)) {
                                Supply.COSMICRUNE.setBuyCount(0);
                            } else {
                                return Random.nextInt(600, 800);
                            }
                        } else if (Supply.STAMINAPOTION.getBuyCount() > 0) {
                            if (buyItem(Supply.STAMINAPOTION.getItemID(), Supply.STAMINAPOTION.getBuyCount(), Supply.STAMINAPOTION.plusFifteenPercent())) {
                                Supply.STAMINAPOTION.setBuyCount(0);
                            } else {
                                return Random.nextInt(600, 800);
                            }
                        } else if (Supply.JUGOFWINE.getBuyCount() > 0) {
                            if (buyItem(Supply.JUGOFWINE.getItemID(), Supply.JUGOFWINE.getBuyCount(), 7)) {
                                Supply.JUGOFWINE.setBuyCount(0);
                            } else {
                                return Random.nextInt(600, 800);
                            }
                        } else if (Supply.STAFFOFAIR.getBuyCount() > 0) {
                            if (buyItem(Supply.STAFFOFAIR.getItemID(), Supply.STAFFOFAIR.getBuyCount(), Supply.STAFFOFAIR.plusFifteenPercent())) {
                                Supply.STAFFOFAIR.setBuyCount(0);
                            } else {
                                return Random.nextInt(600, 800);
                            }
                        } else {
                            Config.setRestockTime(false);
                        }
                    }
                } else {
                    if (!Bank.isOpen() && openBank()) {
                        return Random.nextInt(300, 600);
                    } else {
                        Bank.withdrawAll(Supply.COINS.getItemID());
                        return Random.nextInt(1000, 1600);
                    }
                }
            }


        } else {
            if (!Bank.isOpen() && openBank()) {
                return Random.nextInt(600, 800);
            }
        }
        return Random.nextInt(1200, 1800);
    }

    private boolean buyItem(int id, int quanity, int price) {
        if (GrandExchange.getFirst(o -> o.getItemId() == id) != null) {
            Log.info("We have offer already");
            return true;
        }
        if (GrandExchange.getView() != GrandExchange.View.BUY_OFFER) {
            GrandExchange.createOffer(RSGrandExchangeOffer.Type.BUY);
            Time.sleepUntil(() -> GrandExchange.getView() != GrandExchange.View.BUY_OFFER, 3000);
        } else {
            if (GrandExchangeSetup.getItem() == null) {
                GrandExchangeSetup.setItem(id);
            } else if (GrandExchangeSetup.getQuantity() != quanity) {
                GrandExchangeSetup.setQuantity(quanity);
            } else if (GrandExchangeSetup.getPricePerItem() != price) {
                GrandExchangeSetup.setPrice(price);
            } else {
                GrandExchangeSetup.confirm();
                return true;
            }
        }
        return false;
    }

    private boolean openBank() {
        Npc banker = Npcs.getNearest(n -> n.containsAction("Bank"));
        if (banker != null) {
            return banker.interact("Bank");
        }
        return false;
    }

    private boolean openGE() {
        Npc clerk = Npcs.getNearest(n -> n.containsAction("Exchange"));
        if (clerk != null) {
            return clerk.interact("Exchange");
        }
        return false;
    }

    private boolean getAirOrbs() {
        if (!Inventory.contains(i -> i.getName().contains("Air orb") && i.isNoted())) {
            if (!Bank.isOpen() && openBank()) {
                Time.sleepUntil(Bank::isOpen, 3000);
            } else {
                if (Bank.getWithdrawMode() != Bank.WithdrawMode.NOTE) {
                    if (Bank.setWithdrawMode(Bank.WithdrawMode.NOTE)) {
                        Time.sleepUntil(() -> Bank.getWithdrawMode() == Bank.WithdrawMode.NOTE, 3000);
                    }
                } else {
                    return Bank.withdrawAll("Air orb");
                }
            }
        }
        return false;
    }

    private boolean getAllUncharged() {
        if (!Inventory.contains("Amulet of glory")) {
            if (!Bank.isOpen() && openBank()) {
                Time.sleepUntil(Bank::isOpen, 3000);
            } else {
                if (Bank.getWithdrawMode() != Bank.WithdrawMode.NOTE) {
                    if (Bank.setWithdrawMode(Bank.WithdrawMode.NOTE)) {
                        Time.sleepUntil(() -> Bank.getWithdrawMode() == Bank.WithdrawMode.NOTE, 3000);
                    }
                } else {
                    return Bank.withdrawAll("Amulet of glory");
                }
            }
        }
        return false;
    }
}
