package simulation.towers;

import java.awt.*;

public class DistanceDimension {
    private Dimension dimension;
    private int distance;

    public DistanceDimension(Dimension dimension, int distance) {
        this.dimension = dimension;
        this.distance = distance;
    }

    public Dimension getDimension() {
        return dimension;
    }

    public void setDimension(Dimension dimension) {
        this.dimension = dimension;
    }

    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }
}
