package api;

import org.rspeer.runetek.api.scene.Players;

public class CharStates {
    public static boolean isAnimating(){
        return Players.getLocal().isAnimating();
    }
    public static  boolean isMoving(){
        return Players.getLocal().isMoving();
    }
}
