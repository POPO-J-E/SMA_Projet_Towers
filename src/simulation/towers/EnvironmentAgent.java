package simulation.towers;

import madkit.kernel.AbstractAgent;
import madkit.kernel.Watcher;
import madkit.simulation.probe.PropertyProbe;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * This agent is used to model a quite simple environment. Nothing in it; It just defines its boundaries and uses a
 * {@link PropertyProbe} to set the agents' environment field so that they can use the environment's methods once they
 * enter the artificial society.
 */
public class EnvironmentAgent extends Watcher {

    /**
     * environment's boundaries
     */
    private Dimension dimension;

    private List<AbstractAgent> agents;
    private final SituatedAgent[][] map;

	/**
     * so that the agents can perceive my dimension
     */
    public Dimension getDimension() {
	return dimension;
    }

	public EnvironmentAgent(Dimension dimension) {
		this.dimension = dimension;
		map = new SituatedAgent[dimension.width][dimension.height];
	}

	public EnvironmentAgent(int width, int height) {
		this(new Dimension(width, height));
	}

	@Override
    protected void activate() {
		agents = new ArrayList<>();

		// 1 : request my role so that the viewer can probe me
		requestRole(TowerSimulation.MY_COMMUNITY, TowerSimulation.SIMU_GROUP, TowerSimulation.ENV_ROLE);

		// 2 : this probe is used to initialize the agents' environment field
		addProbe(new AgentsProbe(TowerSimulation.MY_COMMUNITY, TowerSimulation.SIMU_GROUP, TowerSimulation.AGENT_ROLE, "environment"));

   }

	public int getEmptiestColumn(String caller, int current, int constraint) {
    	getLogger().info("request for best col from " + caller);
    	int bestCol = -1;
    	int bestNumber = -1;

    	synchronized (map) {
			for (int i = 0; i < dimension.width; i++) {
				if (i != current && i != constraint) {
					for (int j = 0; j < dimension.height; j++) {
						if (map[i][j] == null && j > bestNumber) {
							bestNumber = j;
							bestCol = i;
						}
					}
				}
			}
		}

		return bestCol;
	}

	class AgentsProbe extends PropertyProbe<AbstractAgent, EnvironmentAgent> {

		public AgentsProbe(String community, String group, String role, String fieldName) {
			super(community, group, role, fieldName);
		}

		@Override
		protected void adding(AbstractAgent agent) {
			super.adding(agent);
			setPropertyValue(agent, EnvironmentAgent.this);
			agents.add(agent);

			if(agent instanceof SituatedAgent)
			{
				SituatedAgent situatedAgent = (SituatedAgent)agent;
				Dimension dimension = situatedAgent.getLocation();
				setAgentAt(situatedAgent, dimension);
			}
		}
    }

    public  AbstractAgent getAgentAt(Dimension location, AbstractAgent agentAsker)
	{
		SituatedAgent agent;

		synchronized (map) {
			agent = map[location.width][location.height];
			if (agent == null || agent == agentAsker)
				return null;
		}

		return agent;
	}

	public Dimension moveAgentTo(SituatedAgent agent, int column)
	{
		Dimension agentLoc = agent.getLocation();
		Dimension dim = null;

		synchronized (map) {
			if (getAgentAt(new Dimension(agentLoc.width, agentLoc.height + 1), agent) == null) {
				Dimension top = getTopColumn(column);
				setAgentAt(null, agent.getLocation());
				setAgentAt(agent, top);

				dim = top;
			}
		}

		return dim;
	}

	public Dimension getTopDime(int column)
	{
		Dimension dim = null;
		synchronized (map)
		{
			dim = getTopColumn(column);
		}
		return dim;
	}

	private Dimension getTopColumn(int column)
	{
		Dimension top = null;
		int i = 0;

		while (top == null)
		{
			Dimension dim = new Dimension(column, i);
			if(map[dim.width][dim.height] == null)
			{
				top = dim;
			}
			i++;
		}

		return top;
	}

	private void setAgentAt(SituatedAgent agent, Dimension location)
	{
		synchronized (map) {
			map[(int) location.getWidth()][(int) location.getHeight()] = agent;
		}
	}
}