package tasks;

import api.Config;
import api.Methods;
import data.Locations;
import com.dax.walker.DaxWalker;
import com.dax.walker.Server;
import org.rspeer.runetek.adapter.component.InterfaceComponent;
import org.rspeer.runetek.api.Game;
import org.rspeer.runetek.api.commons.math.Random;
import org.rspeer.runetek.api.component.InterfaceOptions;
import org.rspeer.runetek.api.component.Interfaces;
import org.rspeer.runetek.api.component.tab.Combat;
import org.rspeer.runetek.api.component.tab.Equipment;
import org.rspeer.runetek.api.component.tab.Inventory;
import org.rspeer.script.task.Task;
import org.rspeer.ui.Log;

public class Walker extends Task {
    private DaxWalker daxWalker = new DaxWalker(new Server("sub_DPjXXzL5DeSiPf", "PUBLIC-KEY"));

    @Override
    public boolean validate() {
        if (Game.isLoggedIn()) {
            if (Inventory.contains("Unpowered orb")
                    && Inventory.getCount(true, "Cosmic rune") >= neededCosmics()
                    && Equipment.contains("Staff of air")
                    && Equipment.contains(i -> i.getName().contains("Amulet of glory("))) {
                return Locations.AIR_ORB_ALTAR.getLocationPosition().distance() > 0;
            }
        }
        return false;
    }

    @Override
    public int execute() {
        Methods.setSupplyCount();
        Config.setTask("Walking to Air Obelisk");
        if(Combat.isAutoRetaliateOn()){
            if(Combat.toggleAutoRetaliate(false)){
                Log.fine("Toggled auto retaliate off");
                return 150;
            }
        }
        if(InterfaceOptions.isAcceptingAid()){
            InterfaceComponent acceptAidButton = Interfaces.get(c->c.containsAction("Toggle Accept Aid"))[0];
            if(acceptAidButton!= null && acceptAidButton.interact(a-> true)){
                Log.fine("Toggled accept aid button");
            }
        }
        daxWalker.walkTo(Locations.AIR_ORB_ALTAR.getLocationPosition());
        return Random.nextInt(600, 1200);
    }

    private int neededCosmics() {
        int unpoweredCount = Inventory.getCount("Unpowered orb");
        return unpoweredCount * 3;
    }
}
