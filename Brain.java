import api.BankCache;
import api.Config;
import api.Methods;
import data.Locations;
import data.Supply;
import org.rspeer.runetek.adapter.scene.Player;
import org.rspeer.runetek.api.Game;
import org.rspeer.runetek.api.Worlds;
import org.rspeer.runetek.api.scene.Players;
import org.rspeer.runetek.event.listeners.*;
import org.rspeer.runetek.event.types.*;
import org.rspeer.script.ScriptMeta;
import org.rspeer.script.task.Task;
import org.rspeer.script.task.TaskScript;
import org.rspeer.ui.Log;
import tasks.*;

import java.awt.*;
import java.io.IOException;


@ScriptMeta(developer = "Neer", name = "[AirOrbeer]", desc = "[Will Enchant Air Orbs For Fat Profit]")
public class Brain extends TaskScript implements RenderListener, ItemTableListener, TargetListener, DeathListener {
    private final Task[] Queue = {new Banking(), new Safety(), new Restock(), new EnchantOrb(), new Walker()};
    private final int STAFF_OF_AIR_ID = 1381;

    private static void DrawString(Graphics graphics, String string, int x, int y, Color color, Color shadow) {
        if (shadow != null) {
            graphics.setColor(shadow);
            graphics.drawString(string, x + 1, y - 1);
            graphics.drawString(string, x - 1, y - 1);
            graphics.drawString(string, x - 1, y);
            graphics.drawString(string, x + 1, y + 1);
            graphics.drawString(string, x - 1, y + 1);
            graphics.drawString(string, x + 1, y);
        }
        if (color != null) {
            graphics.setColor(color);
            graphics.drawString(string, x, y);
        }
        graphics.setColor(Color.WHITE);
    }

    private static void DrawString(Graphics graphics, String string, int x, int y) {
        DrawString(graphics, string, x, y, Color.WHITE, Color.BLACK);
    }

    @Override
    public void onStart() {
        submit(Queue);
        Game.getEventDispatcher().register((ItemTableListener) BankCache::notify);
        Config.startTimer();
        Config.addBadWorld(301);
        Config.setTask("Starting up");
        try {
            Methods.getPrices();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.fine("AirOrbeer Started, have fun!");
    }

    @Override
    public void onStop() {
        Game.getEventDispatcher().deregister((ItemTableListener) BankCache::notify);
    }

    @Override
    public void notify(RenderEvent renderEvent) {
        int x = 5;
        int y = 35;
        int taskY = 335;
        int profit = Config.getOrbsMade() * 750;
        int orbsPerHour = (int) Config.getClock().getHourlyRate(Config.getOrbsMade());
        Graphics2D g = (Graphics2D) renderEvent.getSource();
        DrawString(g, "Runtime: " + Config.getTimeElapsed(), x, y);
        DrawString(g, "Orbs made: [" + Config.getOrbsMade() + "] " + orbsPerHour + " /h", x, y += 13);
        DrawString(g, "Profit: [" + profit + "] " + (int) Config.getClock().getHourlyRate(profit) + " /h", x, y += 13);
        DrawString(g, "Deaths: " + Config.getDeathCount(), x, y += 13);
        DrawString(g, "Attackers: " + Config.getAttackers(), x, y += 13);
        DrawString(g, "Bad world count: " + Config.getBadWorlds().size(), x, y += 13);
        if (Game.isLoggedIn()) {
            if (Methods.getWildyLevel() > 0) {
                for (Player aPlayer : Players.getLoaded(Methods::canAttackUs)) {
                    if (aPlayer != Players.getLocal() && !aPlayer.getAppearance().isEquipped(STAFF_OF_AIR_ID)) {
                        aPlayer.getPosition().outline(g);
                        DrawString(g, "Bad man", aPlayer.getPosition().toScreen().getX(), aPlayer.getPosition().toScreen().getY());
                    }
                }
            }
            DrawString(g, "Current world: " + Worlds.getCurrent(), x, y += 13);
        }
        DrawString(g, "Task: " + Config.getCurrentTask(), x, taskY);
    }

    @Override
    public void notify(ItemTableEvent itemTableEvent) {
        if (Locations.AIR_ORB_ALTAR.getLocationPosition().distance() == 0) {
            if (itemTableEvent.getId() == Supply.AIRORB.getItemID()) {
                Config.addOrbsMade(1);
            }
        }
    }


    @Override
    public void notify(TargetEvent targetEvent) {
        /*
        if (targetEvent.getSource().getId() == 0 && targetEvent.getTarget() == Players.getLocal()) {
            if (Methods.canAttackUs(targetEvent.getTarget())) {
                if (Players.getLocal().isHealthBarVisible() && Config.isSafe()) {
                    if (!Config.getBadWorlds().contains(Worlds.getCurrent())) {
                        Config.addBadWorld(Worlds.getCurrent());
                    }
                    Config.setSafe(false);
                }
                Config.setAttackers(Config.getAttackers() + 1);
                Log.severe("Got a mother fucker attacking us.");
            } else {
                Log.fine("HAHAHA Moron tried attacking us but he can't");
            }
        }
         */
    }

    @Override
    public void notify(DeathEvent deathEvent) {
        if (deathEvent.getSource().equals(Players.getLocal())) {
            Config.setDeathCount(Config.getDeathCount() + 1);
        }
    }
}
