package simulation.towers;

import madkit.kernel.Message;

/**
 * Created by kifkif on 10/10/2017.
 */
public class AgressionMessage extends Message {
    protected int constraint;

    public AgressionMessage(int constraint) {
        this.constraint = constraint;
    }

    public int getConstraint() {
        return constraint;
    }

    @Override
    public String toString() {
        return "AgressionMessage{" +
                "constraint=" + constraint +
                '}';
    }
}
