package apiaryparty;

import java.util.Random;


public class KimTheDestroyerOfPlanets extends Attacker {

	private final static String attackerName = "KimTheDestroyerOfPlanets";

	public Random r;

	/**
	 * Constructor
	 * @param defenderName defender's name
	 * @param graphFile graph to read
	 */

	/**
	 *
	 * @param defenderName
	 * @param graphFile
	 * 1) Compare number of actual nodes with nodes in parameters
	 * 		if number of actual nodes > nodes in parameters there is probably honeypots
	 * 			if probe honeypot is viable
	 * 				probe honeypot around highest SV values
	 * 2) Retrieve Strengthening node rate
	 * 		if strengthening cost is higher
	 */

	public KimTheDestroyerOfPlanets(String defenderName, String graphFile) {
		super(attackerName, defenderName, graphFile);
	}

	public KimTheDestroyerOfPlanets(){
		super(attackerName);
	}

	protected void initialize(){
		r = new Random();
	}

	@Override
	public AttackerAction makeAction() {
		if(availableNodes.size()==0)
			return new AttackerAction(AttackerActionType.INVALID,0);
		int nodeID = availableNodes.get(r.nextInt(availableNodes.size())).getNodeID();
		return new AttackerAction(AttackerActionType.ATTACK, nodeID);
	}

	public boolean honeyPotExists() {
		return net.getSize() > Parameters.NUMBER_OF_NODES;
	}

	private boolean isProbingViable() {
		return (Parameters.ATTACKER_BUDGET / 4) >= Parameters.PROBE_POINTS_RATE;
	}

	private boolean isProbingInexpensive() {
		return (Parameters.ATTACKER_BUDGET / 14) <= Parameters.PROBE_POINTS_RATE;
	}

	private boolean isHoneyProbingViable() {
		return (Parameters.ATTACKER_BUDGET / 4) >= Parameters.PROBE_HONEY_RATE;
	}

	private boolean isHoneyProbingInexpensive() {
		return (Parameters.ATTACKER_BUDGET / 14) <= Parameters.PROBE_HONEY_RATE;
	}


	@Override
	protected void result(Node lastNode) {
		// TODO Auto-generated method stub

	}
}
