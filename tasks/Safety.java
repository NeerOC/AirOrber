package tasks;

import api.Config;
import com.dax.walker.DaxWalker;
import com.dax.walker.Server;
import data.Locations;
import org.rspeer.runetek.adapter.component.Item;
import org.rspeer.runetek.api.Game;
import org.rspeer.runetek.api.Worlds;
import org.rspeer.runetek.api.commons.Time;
import org.rspeer.runetek.api.commons.math.Random;
import org.rspeer.runetek.api.component.Bank;
import org.rspeer.runetek.api.component.WorldHopper;
import org.rspeer.runetek.api.component.tab.Equipment;
import org.rspeer.runetek.api.component.tab.Inventory;
import org.rspeer.runetek.api.component.tab.Tab;
import org.rspeer.runetek.api.component.tab.Tabs;
import org.rspeer.runetek.api.local.Health;
import org.rspeer.runetek.api.movement.Movement;
import org.rspeer.runetek.providers.RSWorld;
import org.rspeer.script.task.Task;

import java.util.function.Predicate;

public class Safety extends Task {
    private static final Predicate<RSWorld> SAFE_WORLD = x -> x.isMembers() && !x.isDeadman() && !x.isPVP()
            && !x.isSeasonDeadman() && !x.isSkillTotal() && !x.isTournament() && !Config.getBadWorlds().contains(x.getId()) && (x.getLocation() == RSWorld.LOCATION_UK || x.getLocation() == RSWorld.LOCATION_DE);
    private DaxWalker daxWalker = new DaxWalker(new Server("sub_DPjXXzL5DeSiPf", "PUBLIC-KEY"));

    @Override
    public boolean validate() {
        return Game.isLoggedIn() && !Config.isSafe();
    }

    @Override
    public int execute() {
        Config.setTask("Avoiding Pkers");
        if (Config.getBadWorlds().contains(Worlds.getCurrent())) {
            if (Locations.EDGEVILLE_BANK.getLocationPosition().distance() > 20) {
                if (Inventory.contains(i -> i.getName().contains("Amulet of glory("))) {
                    Item neck = Inventory.getFirst(i -> i.getName().contains("Amulet of glory("));
                    if (neck != null) {
                        if (neck.interact("Wear")) {
                            return 50;
                        }
                    }
                }
                if (Equipment.contains(i -> i.getName().contains("Amulet of glory("))) {
                    if (!Tabs.isOpen(Tab.EQUIPMENT)) {
                        if(Tabs.open(Tab.EQUIPMENT)){
                            return 50;
                        }
                    } else {
                        if (Equipment.contains(i -> i.getName().contains("Amulet of glory")) && Locations.EDGEVILLE_BANK.getLocationPosition().distance() > 20) {
                            if (Equipment.interact(i -> i.getName().contains("Amulet of glory"), "Edgeville")) {
                                return Random.nextInt(1200, 1800);
                            }
                        }
                    }
                } else {
                    daxWalker.walkTo(Locations.EDGEVILLE_BANK.getLocationPosition());
                }
            } else {
                if (Health.getPercent() >= 95 && Movement.isStaminaEnhancementActive()) {
                    if(Bank.isOpen()){
                        if(Bank.close()){
                            return Random.nextInt(600,800);
                        }
                    }
                    if (!WorldHopper.isOpen()) {
                        WorldHopper.open();
                    } else {
                        if (WorldHopper.hopTo(SAFE_WORLD)) {
                            Time.sleepUntil(() -> Game.getState() != Game.STATE_HOPPING_WORLD, 3000);
                        }
                    }
                }
            }
        } else {
            Config.setSafe(true);
        }
        return Random.nextInt(50, 100);
    }
}
