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
	//TODO: check if correct
	public boolean isOneOfNeighborsHoneyPot(){
		return availableNodes.size() > Parameters.MAX_NEIGHBORS;
	}



	@Override
	protected void result(Node lastNode) {
		// TODO Auto-generated method stub

	}
}
