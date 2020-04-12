package api;

import org.rspeer.runetek.api.commons.StopWatch;

import java.util.ArrayList;
import java.util.List;

public class Config {
    private static String currentTask;
    private static StopWatch timeElapsed;
    private static int orbsMade;
    private static boolean restockTime;
    private static int attackers;
    private static boolean Safe = true;
    private static int deathCount;
    private static List<Integer> badWorlds = new ArrayList<>();


    public static void setDeathCount(int deathCount) {
        Config.deathCount = deathCount;
    }

    public static void setSafe(boolean safe) {
        Config.Safe = safe;
    }

    public static void addBadWorld(int world) {
        badWorlds.add(world);
    }

    public static void startTimer() {
        timeElapsed = StopWatch.start();
    }

    public static String getTimeElapsed() {
        return timeElapsed == null ? null : timeElapsed.toElapsedString();
    }

    public static void setTask(String task) {
        currentTask = task;
    }

    public static void addOrbsMade(int count) {
        orbsMade += count;
    }

    public static void setRestockTime(boolean restockTime) {
        Config.restockTime = restockTime;
    }

    public static void setAttackers(int attackers) {
        Config.attackers = attackers;
    }

    public static boolean isRestockTime() {
        return restockTime;
    }

    public static boolean isSafe() {
        return Safe;
    }

    public static List<Integer> getBadWorlds() {
        return badWorlds;
    }

    public static int getAttackers() {
        return attackers;
    }

    public static int getOrbsMade() {
        return orbsMade;
    }

    public static int getDeathCount() {
        return deathCount;
    }

    public static String getCurrentTask() {
        return currentTask;
    }

    public static StopWatch getClock() {
        return timeElapsed;
    }
}
