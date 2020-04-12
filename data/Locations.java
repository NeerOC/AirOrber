package data;

import org.rspeer.runetek.api.movement.position.Position;

public enum Locations {
    AIR_ORB_ALTAR(new Position(3088, 3570, 0)),
    EDGEVILLE_BANK(new Position(3089, 3490, 0)),
    GRAND_EXCHANGE(new Position(3164,3487, 0));


    private Position locationPosition;

    Locations(Position locationPosition){
        this.locationPosition = locationPosition;
    }

    public Position getLocationPosition() {
        return locationPosition;
    }
}
