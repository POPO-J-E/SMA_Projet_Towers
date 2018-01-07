package simulation.towers;

import madkit.kernel.AbstractAgent;

import java.awt.*;
import java.util.ArrayList;
import java.util.Random;
import java.util.List;

/**
 * 
 * 
 * #jws simulation.towers.TowerSimulation jws#
 * 
 * It is time to display something !! The only purpose of this class is to show an example of what could be a launching
 * sequence. The display work is done in {@link Viewer}
 */
public class TowerSimulation extends AbstractAgent {

    // Organizational constants
    public static final String MY_COMMUNITY = "simu";
    public static final String SIMU_GROUP = "simu";
    public static final String AGENT_ROLE = "agent";
    public static final String ENV_ROLE = "environment";
    public static final String SCH_ROLE = "scheduler";
    public static final String VIEWER_ROLE = "viewer";
    public static final int AGENT_NUMBER = 5;
    public static final int WIDTH = 3;
    public static final int HEIGHT = AGENT_NUMBER+1;

    @Override
    protected void activate() {
        // 1 : create the simulation group
        createGroup(MY_COMMUNITY, SIMU_GROUP);

        // 2 : create the environment
        EnvironmentAgent environment = new EnvironmentAgent(WIDTH, HEIGHT);
        launchAgent(environment);

        Random rand = new Random();

        String[] towers = {"A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V"};

        List<Dimension> targets = new ArrayList<>(towers.length);

        for (int i = 0; i < AGENT_NUMBER; i++) {
            targets.add(new Dimension(2, i));
        }

        // 4 : launch some simulated agents
        for (int i = 0; i < AGENT_NUMBER; i++) {
            Dimension start = new Dimension(0, i);
            Dimension target = targets.remove(rand.nextInt(targets.size()));
            AbstractAgent agent1 = new SituatedAgent(start, target, towers[i]);
            launchAgent(agent1);
        }

        // 5 : create the scheduler
        MyScheduler scheduler = new MyScheduler();
        launchAgent(scheduler, true);

        // 3 : create the viewer
        Viewer viewer = new Viewer();
        launchAgent(viewer, true);
    }

    public static void main(String[] args) {
	executeThisAgent(1, false); // no gui for me
    }
}
