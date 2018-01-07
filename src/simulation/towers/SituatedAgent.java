package simulation.towers;

import madkit.kernel.AbstractAgent;
import madkit.kernel.Agent;
import madkit.kernel.Message;

import java.awt.*;
import java.util.Random;

public class SituatedAgent extends Agent {

    /**
     * The agent's environment. Here it is just used to know its boundaries. It will be automatically set by the environment
     * agent itself: No need to instantiate anything here.
     */
    protected EnvironmentAgent environment;

    /**
     * agent's position
     */
    protected Dimension location = new Dimension();
    protected Dimension target = new Dimension();

    protected Color color;
    protected int id;
    public boolean running = false;
    private String name;

    public SituatedAgent(Dimension location, Dimension target, String name) {
        this.location.setSize(location);
        this.target.setSize(target);
        this.name = name;
    }

    /**
     * initialize my role and fields
     */
    @Override
    protected void activate() {
        requestRole(TowerSimulation.MY_COMMUNITY, TowerSimulation.SIMU_GROUP, TowerSimulation.AGENT_ROLE);

        Random rand = new Random();
        float r = rand.nextFloat();
        float g = rand.nextFloat();
        float b = rand.nextFloat();
        color = new Color(r, g, b);
    }

    protected void nextStep()
    {
        setRunning(true);
    }

    @Override
    protected void live() {
        while(true)
        {
            if(isRunning())
            {
                setRunning(false);
                doLive();
            }
            else
            {
                pause(500);
            }
            pause((int)(100+Math.random()*50));
        }
    }

    protected void doLive() {
        getLogger().info("i live");
        AgressionMessage message = receiveMessages();

        if(message != null)
        {
            flee(message);
        }
        else
        {
            if(!isSatisfied())
            {
                satisfy();
            }
        }
    }

    private void satisfy()
    {
        getLogger().info("try satisfy");
        if(canBeSatisfied())
        {
            doSatisfaction();
        }
        else
        {
            satisfactionAgression();
        }
    }

    private void flee(AgressionMessage message) {
        getLogger().info("got aggressed " + message);
        int place = findPlaceForFleeing(message.getConstraint());
        getLogger().info("want flee to " + place);
        boolean flee = false;
        int nbTry = 0;

        while(!flee && nbTry < 5)
        {
            flee = fleeAgression();
            nbTry++;
        }

        if(flee)
        {
            doFlee(message, place);
        }
    }

    private AgressionMessage receiveMessages() {
        Message lastMessage = purgeMailbox();

        if(lastMessage != null && lastMessage instanceof AgressionMessage)
        {
            return (AgressionMessage)lastMessage;
        }

        return null;
    }

    private int calcDistance(Dimension dim) {
        int diffX = Math.abs(location.width - dim.width);
        int diffY = Math.abs(location.height - dim.height);

        return diffX + diffY;
    }

    public Dimension getLocation() {
        return location;
    }

    public Dimension getTarget() {
        return target;
    }

    public Color getColor() {
        return color;
    }

    public int getId() {
        return id;
    }

    // CONDITIONS

    private boolean isSatisfied()
    {
        return location.equals(target);
    }

    private boolean canBeSatisfied()
    {
        return target.width != location.width && canMove();
    }

    private boolean canMove() {
        getLogger().info("verif if can move");
        return patchIsEmpty(new Dimension(this.location.width, this.location.height+1));
    }

    private boolean patchIsEmpty(Dimension dim)
    {
        return environment.getAgentAt(dim, this) == null;
    }

    // COMPORTEMENTS

    private void doSatisfaction()
    {
        getLogger().info("can satisfy");
        moveOn(target.width);
    }

    private void satisfactionAgression()
    {
        getLogger().info("satisfaction agression");

        int location = findPlaceForSatisfaction();
        int constraint = findConstraintForSatisfaction();
        tryAggressOrMove(location, constraint);
    }

    private void doFlee(AgressionMessage message, int location)
    {
        getLogger().info("flee to " + location);
        if(moveOn(location))
            sendReply(message, new Message());
    }

    private boolean fleeAgression()
    {
        getLogger().info("try flee");
        if(canFleeOn())
        {
            getLogger().info("can flee");
            return true;
        }
        else
        {
            getLogger().info("aggress before flee");
            return aggress(findConstraintForFleeAgression());
        }
    }

    private boolean canFleeOn() {
        getLogger().info("verif if can flee");
        return canMove();
    }

    private void tryAggressOrMove(int location, int constraint) {
        if(aggress(constraint))
            moveOn(location);
    }

    private boolean aggress(int constraint)
    {
        return aggress(new Dimension(location.width, location.height+1), constraint);
    }

    private boolean aggress(Dimension location, int constraint)
    {
        getLogger().info("try aggress");
        return aggress(environment.getAgentAt(location, this), location, constraint);
    }

    private boolean aggress(AbstractAgent agent, Dimension location,  int constraint)
    {
        if(agent != null)
        {
            getLogger().info("agresss at position " + location + "with constraint " + constraint);
            Message ack = this.sendMessageAndWaitForReply(agent.getAgentAddressIn(TowerSimulation.MY_COMMUNITY, TowerSimulation.SIMU_GROUP, TowerSimulation.AGENT_ROLE), new AgressionMessage(constraint), 100);

            return ack != null;
        }

        getLogger().info("anyone to aggress, you can move");

        return true;
    }

    private boolean canMoveOn(Dimension dimension)
    {
        if(calcDistance(dimension) <= 1 && environment.getAgentAt(dimension, this) == null)
        {
            return true;
        }
        return false;
    }

    private boolean moveOn(int column)
    {
        getLogger().info("try move to "+column);
        Dimension loc = environment.moveAgentTo(this, column);
        if(loc != null)
        {
            getLogger().info("move to "+column);

            this.location.setSize(loc);
            return true;
        }
        getLogger().info("fail move to "+column);

        return false;
    }

    // CHOIX DES DEPLACEMENTS

    private int findPlaceForSatisfaction()
    {
        if(location.width == target.width)
            return findPlaceForFleeing(target.width);
        else
            return target.width;
    }

    private int findPlaceForFleeing(int constraint)
    {
//        for(int i = 0; i < environment.getDimension().width; i++)
//        {
//            if(i != constraint && i != location.width)
//            {
//                return i;
//            }
//        }

        if(environment.getTopDime(target.width).equals(target))
            return target.width;

        return environment.getEmptiestColumn(name, location.width, constraint);
//        return 0;
    }

    private int findConstraintForFleeAgression()
    {
        return this.location.width;
    }

    private int findConstraintForSatisfaction()
    {
        return target.width;
    }

    public synchronized boolean isRunning() {
        return running;
    }

    public synchronized void setRunning(boolean running) {
        this.running = running;
    }

    public String getName() {
        if(this.name == null) {
            this.name = this.getClass().getSimpleName() + "-" + this.id;
        }

        return this.name;
    }
}