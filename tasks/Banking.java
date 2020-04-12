package tasks;

import api.CharStates;
import api.Config;
import api.Methods;
import com.dax.walker.DaxWalker;
import com.dax.walker.Server;
import data.Locations;
import org.rspeer.runetek.adapter.component.Item;
import org.rspeer.runetek.adapter.scene.SceneObject;
import org.rspeer.runetek.api.Game;
import org.rspeer.runetek.api.commons.Time;
import org.rspeer.runetek.api.commons.math.Random;
import org.rspeer.runetek.api.component.Bank;
import org.rspeer.runetek.api.component.tab.Equipment;
import org.rspeer.runetek.api.component.tab.Inventory;
import org.rspeer.runetek.api.component.tab.Tab;
import org.rspeer.runetek.api.component.tab.Tabs;
import org.rspeer.runetek.api.local.Health;
import org.rspeer.runetek.api.movement.Movement;
import org.rspeer.runetek.api.movement.position.Position;
import org.rspeer.runetek.api.scene.SceneObjects;
import org.rspeer.script.task.Task;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class Banking extends Task {
    private DaxWalker daxWalker = new DaxWalker(new Server("sub_DPjXXzL5DeSiPf", "PUBLIC-KEY"));
    private Predicate<Item> stamPred = item -> item.getName().contains("Stamina potion");
    private Predicate<Item> gloryPred = item -> item.getName().contains("Amulet of glory(");
    private Position goodBank = new Position(3095, 3491, 0);

    @Override
    public boolean validate() {
        if (Game.isLoggedIn()) {
            if (Config.isRestockTime()) {
                return false;
            }
            if(Locations.EDGEVILLE_BANK.getLocationPosition().distance() < 20
            && (!Movement.isStaminaEnhancementActive() || Health.getPercent() < 95)){
                return true;
            }
            return !Inventory.contains("Unpowered orb")
                    || Inventory.getCount(true, "Cosmic rune") < neededCosmics()
                    || !Equipment.contains("Staff of air")
                    || !Equipment.contains(gloryPred);
        }
        return false;
    }

    @Override
    public int execute() {
        Config.setTask("Banking for supplies");
        Methods.setSupplyCount();
        if (Locations.EDGEVILLE_BANK.getLocationPosition().distance() > 20 && Equipment.contains(gloryPred)) {
            teleportEdge();
        } else if (!Equipment.contains(gloryPred) && Locations.EDGEVILLE_BANK.getLocationPosition().distance() > 10) {
            daxWalker.walkTo(Locations.EDGEVILLE_BANK.getLocationPosition());
        } else {
            if (hasRequireds()) {
                drinkAndWield();
                return Random.nextInt(600, 800);
            } else {
                if (!Bank.isOpen()) {
                    openBank();
                } else {
                    // Get amulet of glory and equip it. ( CONVERT TO METHOD ) ?
                    if (Inventory.contains("Air orb")) {
                        if (Bank.depositInventory()) {
                            return Random.nextInt(600, 1000);
                        }
                    }
                    if (hasEnoughSupply()) {
                        withdrawRequireds();
                        if (hasRequireds()) {
                            return Random.nextInt(600, 800);
                        }

                        if (Inventory.contains("Jug") || Inventory.contains("Amulet of glory") || Inventory.contains(stamPred) || Inventory.contains("Vial") || Inventory.contains("Coins")) {
                            if (Bank.depositInventory()) {
                                return Random.nextInt(100, 200);
                            }
                        }

                        // If we have enough Unpowered orb and cosmic runes for one trip then withdraw else set the global variable restock time to true.
                        if (Bank.getCount("Unpowered orb") >= 27 && Bank.getCount("Cosmic rune") >= 81) {
                            if (Inventory.getCount(true, "Cosmic rune") < 81 && Bank.withdraw("Cosmic rune", 81 - Inventory.getCount(true, "Cosmic rune"))) {
                                Time.sleepUntil(() -> Inventory.contains("Cosmic rune"), 3000);
                            }
                            if (Inventory.contains("Cosmic rune") && !Inventory.contains("Unpowered orb") && Bank.withdrawAll("Unpowered orb")) {
                                Time.sleepUntil(() -> Inventory.contains("Unpowered orb"), 3000);
                            }
                        }
                    } else {
                        Config.setRestockTime(true); // If we do not have enough supplies its restock time.
                    }
                }
            }
        }
        return Random.nextInt(300, 600);
    }

    private List<String> getRequireds() {
        List<String> theItems = new ArrayList<>();

        if (!Inventory.contains("Staff of air") && !Equipment.contains("Staff of air")) {
            theItems.add("Staff of air");
        }
        if (!Movement.isStaminaEnhancementActive() && !Inventory.contains(stamPred)) {
            theItems.add("Stamina potion");
        }
        if (!Equipment.contains(gloryPred) && !Inventory.contains(gloryPred)) {
            theItems.add("Amulet of glory(");
        }
        if (Health.getPercent() < 95 && !Inventory.contains("Jug of wine")) {
            theItems.add("Jug of wine");
        }
        return theItems;
    }


    private boolean hasEnoughSupply() {
        if (Bank.getCount("Unpowered orb") >= 27) {
            if (Bank.getCount("Cosmic rune") >= 81) {
                if (Bank.getCount("Staff of air") > 0) {
                    if (Bank.getCount("Jug of wine") > 0) {
                        if (Bank.getCount(gloryPred) > 0) {
                            return Bank.getCount(stamPred) > 0;
                        }
                    }
                }
            }
        }
        return false;
    }

    private void withdrawRequireds() {
        if (getRequireds().size() > 0 && Inventory.getFreeSlots() < getRequireds().size()) {
            Bank.depositInventory();
            Time.sleepUntil(Inventory::isEmpty, 1500);
        }
        int healthLost = Health.getLevel() - Health.getCurrent();
        for (String aString : getRequireds()) {
            if (aString.equals("Jug of wine") && healthLost > 11) {
                if (Bank.withdraw(aString, 1)) {
                    Time.sleepUntil(() -> Inventory.contains(i -> i.getName().contains(aString)), 1500);
                }
                if (Bank.withdraw(aString, 1)) {
                    Time.sleepUntil(() -> Inventory.contains(i -> i.getName().contains(aString)), 1500);
                }
            } else {
                if (Bank.withdraw(i -> i.getName().contains(aString), 1)) {
                    Time.sleepUntil(() -> Inventory.contains(i -> i.getName().contains(aString)), 1500);
                }
            }
        }
    }

    private void drinkAndWield() {
        Item wine = Inventory.getFirst("Jug of wine");
        if (wine != null && Health.getPercent() < 100) {
            if (wine.interact("Drink")) {
                Time.sleep(610, 800);
            }
        }

        if (Inventory.contains(stamPred) && !Movement.isStaminaEnhancementActive()) {
            Item stamPot = Inventory.getFirst(stamPred);
            if (stamPot != null) {
                stamPot.interact("Drink");
                Time.sleep(610, 800);
            }
        }

        if (Inventory.contains(gloryPred)) {
            Item myNecklace = Inventory.getFirst(gloryPred);
            if (myNecklace != null) {
                if (myNecklace.interact("Wear")) {
                    Time.sleep(610, 800);
                }
            }
        }

        if (Inventory.contains("Staff of air")) {
            Item myStaff = Inventory.getFirst("Staff of air");
            if (myStaff != null) {
                if (myStaff.interact("Wield")) {
                    Time.sleep(610, 800);
                }
            }
        }
    }

    private void teleportEdge() {
        if (!Tabs.isOpen(Tab.EQUIPMENT)) {
            if (Tabs.open(Tab.EQUIPMENT)) {
                Time.sleepUntil(() -> Tabs.isOpen(Tab.EQUIPMENT), 3000);
            }
        } else {
            if (!CharStates.isAnimating() && Equipment.interact(gloryPred, "Edgeville")) {
                Time.sleepUntil(() -> !CharStates.isAnimating(), 3000);
            }
        }
    }

    private boolean hasRequireds() {
        if (Inventory.contains("Staff of air") || Inventory.contains(gloryPred) || (Inventory.contains("Jug of wine") && Health.getPercent() <= 95)) {
            return true;
        }
        return !Movement.isStaminaEnhancementActive() && Inventory.contains(stamPred);
    }

    private void openBank() {
        SceneObject Bankomat = SceneObjects.getNearest(o -> o.containsAction("Bank") && o.getPosition().equals(goodBank));
        if (Bankomat != null) {
            if (Bankomat.interact("Bank")) {
                Time.sleepUntil(Bank::isOpen, 3000);
            }
        }
    }

    private int neededCosmics() {
        int unpoweredCount = Inventory.getCount("Unpowered orb");
        return unpoweredCount * 3;
    }
}
