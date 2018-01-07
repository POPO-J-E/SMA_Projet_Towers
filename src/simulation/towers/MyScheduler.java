package simulation.towers;

import madkit.kernel.AbstractAgent;
import madkit.kernel.Scheduler;
import madkit.simulation.activator.GenericBehaviorActivator;

/**
 * 
 * 
 * #jws simulation.towers.TowerSimulation jws#
 * 
 * Nothing really new here, except that we define an additional Activator which is used to schedule the display.
 * Especially, this is about calling the "observe" method of agents having the role of viewer in the organization
 */

public class MyScheduler extends Scheduler {

    protected GenericBehaviorActivator<AbstractAgent> agents;
    protected GenericBehaviorActivator<AbstractAgent> viewers;

    @Override
    protected void activate() {

	// 1 : request my role
	requestRole(TowerSimulation.MY_COMMUNITY, TowerSimulation.SIMU_GROUP, TowerSimulation.SCH_ROLE);

	// 3 : initialize the activators
	// by default, they are activated once each in the order they have been added
	agents = new GenericBehaviorActivator<AbstractAgent>(TowerSimulation.MY_COMMUNITY, TowerSimulation.SIMU_GROUP, TowerSimulation.AGENT_ROLE, "nextStep");
	addActivator(agents);
	viewers = new GenericBehaviorActivator<AbstractAgent>(TowerSimulation.MY_COMMUNITY, TowerSimulation.SIMU_GROUP, TowerSimulation.VIEWER_ROLE, "observe");
	addActivator(viewers);

	setDelay(20);

//	 4 : let us start the simulation automatically
//	setSimulationState(SimulationState.RUNNING);
    }
}