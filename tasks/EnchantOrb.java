package tasks;

import api.Config;
import api.Methods;
import data.Locations;
import org.rspeer.runetek.adapter.scene.Player;
import org.rspeer.runetek.adapter.scene.SceneObject;
import org.rspeer.runetek.api.Game;
import org.rspeer.runetek.api.Worlds;
import org.rspeer.runetek.api.commons.Time;
import org.rspeer.runetek.api.commons.math.Random;
import org.rspeer.runetek.api.component.Production;
import org.rspeer.runetek.api.component.tab.Equipment;
import org.rspeer.runetek.api.component.tab.Inventory;
import org.rspeer.runetek.api.component.tab.Magic;
import org.rspeer.runetek.api.component.tab.Spell;
import org.rspeer.runetek.api.input.menu.ActionOpcodes;
import org.rspeer.runetek.api.scene.Players;
import org.rspeer.runetek.api.scene.SceneObjects;
import org.rspeer.runetek.event.listeners.HitsplatListener;
import org.rspeer.runetek.event.types.HitsplatEvent;
import org.rspeer.script.task.Task;
import org.rspeer.ui.Log;

public class EnchantOrb extends Task implements HitsplatListener {
    private static int spellOnObject = ActionOpcodes.SPELL_ON_OBJECT;

    @Override
    public boolean validate() {
        if (Game.isLoggedIn()) {
            if (Inventory.contains("Unpowered orb")
                    && Inventory.contains("Cosmic rune")
                    && Equipment.contains("Staff of air")
                    && Equipment.contains(i -> i.getName().contains("Amulet of glory("))) {
                return Locations.AIR_ORB_ALTAR.getLocationPosition().distance() == 0;
            }
        }
        return false;
    }

    @Override
    public int execute() {
        Config.setTask("Enchanting orbs");
        checkPK();
        enchantOrb();
        return Random.nextInt(50, 100);
    }

    private void checkPK() {
        Player[] people = Players.getLoaded(x -> Methods.canAttackUs(x) && x.getTarget() != null && x.getTarget().equals(Players.getLocal()));
        if (people.length > 0 && Players.getLocal().isHealthBarVisible()) {
            if (!Config.getBadWorlds().contains(Worlds.getCurrent())) {
                Config.addBadWorld(Worlds.getCurrent());
            }
            Config.setSafe(false);
        }
    }

    private void enchantOrb() {
        SceneObject obelisk = SceneObjects.getNearest("Obelisk of Air");
        if (obelisk != null) {
            int orbCount = Inventory.getCount("Air orb");
            if (Production.isOpen()) {
                if (Production.initiate()) {
                    Time.sleepUntil(() -> Inventory.getCount("Air orb") > orbCount, 3000);
                }
            }
            if (Players.getLocal().getAnimation() == -1 || Players.getLocal().getAnimationFrame() >= 5) {
                if (!Magic.isSpellSelected()) {
                    Magic.cast(Spell.Modern.CHARGE_AIR_ORB);
                    Time.sleepUntil(Magic::isSpellSelected, 1200);
                } else {
                    if (obelisk.interact(spellOnObject)) {
                        Time.sleepUntil(Production::isOpen, 1200);
                    }
                }
            }
        }
    }

    @Override
    public void notify(HitsplatEvent hitsplatEvent) {
        if(hitsplatEvent.getSource().equals(Players.getLocal())){
            Config.setSafe(false);
        }
    }
}
