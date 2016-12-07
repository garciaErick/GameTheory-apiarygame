package apiaryparty;

import java.util.*;


public class KimTheDestroyerOfPlanets extends Attacker {

	private final static String attackerName = "KimTheDestroyerOfPlanets";

	public Random r;
	boolean probedDB;
	boolean probedHighValueNode;
	int costSpentInSuperAttacks;

	/**
	 * Constructor
	 * @param defenderName defender's name
	 * @param graphFile graph to read
	 */

	/**
	 * 1) Compare number of actual nodes with nodes in parameters
	 * if number of actual nodes > nodes in parameters there is probably honeypots
	 * if probe honeypot is viable (1/4 attacker budget)
	 * probe honeypot around highest SV values
	 * 2) Retrieve Strengthening node rate
	 * if strengthening cost is higher than 1/4 of defender budget
	 * safe to assume PV is around SV and will consider attack if PV is high
	 * else
	 * invest in probe PV is viable
	 * 3) Attack highest PV within available nodes to reach DB
	 * if SV +20
	 * if probe PV is viable
	 * probe PV if true super attack
	 * <p>
	 * GOAL: gain maximum points as possible without falling in honeypot. attempt to get DB.
	 * <p>
	 * is database
	 */

	public KimTheDestroyerOfPlanets(String defenderName, String graphFile) {
		super(attackerName, defenderName, graphFile);
	}

	public KimTheDestroyerOfPlanets() {
		super(attackerName);
	}

	protected void initialize() {
		r = new Random();
		probedDB = false;
		probedHighValueNode = false;
		costSpentInSuperAttacks = 0;
	}

	@Override
	public AttackerAction makeAction() {

		while (getDBNodeIds().size() != 0 && isSuperAttackViable() && doneDoingSuperAttacks(costSpentInSuperAttacks)) {
			if (honeyPotExists() && !probedDB) {
				probedDB = true;
				return new AttackerAction(AttackerActionType.PROBE_HONEYPOT, getDBNodeIds().get(0));
			} else if (!net.getNode(getDBNodeIds().get(0)).isHoneyPot()) {
				probedDB = false;
				costSpentInSuperAttacks += Parameters.SUPERATTACK_RATE;
				return new AttackerAction(AttackerActionType.SUPERATTACK, getDBNodeIds().get(0));
			} else //if it is a honeypot then we will remove it from the list and continue
				getDBNodeIds().remove(0);
		}
		/**
		 * attack highest value node that is above 1/2 of 20 and less than 3/4 of 20
		 * because if its higher than 3/4 we might need a super attack or might
		 * not be worth it
		 **/
		//TODO: check if sorting is correct
		List<Node> availableNodesSorted = sortNodesDescending(availableNodes);

		for (Node n : availableNodesSorted) {
			if (3 * Parameters.ATTACK_ROLL / 4 >= n.getSv() && n.getSv() >= Parameters.ATTACK_ROLL / 2 && !n.isCaptured()) {
				System.out.println("Is captured: " + n.isCaptured());
				return new AttackerAction(AttackerActionType.ATTACK, n.getNodeID());
			}
		}

		for (Node n : availableNodesSorted) {
			if (n.getSv() <= Parameters.ATTACK_ROLL / 2)
				return new AttackerAction(AttackerActionType.ATTACK, n.getNodeID());
		}

		for (Node n : availableNodesSorted) {
			if (3 * Parameters.ATTACK_ROLL / 4 <= n.getSv() && doneDoingSuperAttacks(costSpentInSuperAttacks)) {
				//TODO: also probe for points because maybe it was strengthened and then do a super attack
				for (Node neighbor : n.getNeighborList()) {
					if (areNodesSimilar(n, neighbor) && isHoneyProbingViable() && !probedHighValueNode && honeyPotExists()) {
						probedHighValueNode = true;
						return new AttackerAction(AttackerActionType.PROBE_HONEYPOT, n.getNodeID());
					} else if (n.isHoneyPot() && honeyPotExists()) {
						probedHighValueNode = false;
						availableNodesSorted.remove(n);
					} else {
						//TODO: check if we should or if we have enough budget for a super attack else do a regular attack
						probedHighValueNode = false;
						return new AttackerAction(AttackerActionType.SUPERATTACK, n.getNodeID());
					}
				}
			}
		}
		return new AttackerAction(AttackerActionType.END_TURN, 0);
	}

	public boolean honeyPotExists() {
		return (net.getSize() > Parameters.NUMBER_OF_NODES);
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

	private boolean isSuperAttackViable() {
		return (Parameters.ATTACKER_BUDGET / 4) >= Parameters.SUPERATTACK_RATE;
	}

	private boolean doneDoingSuperAttacks(int costSpentInSuperAttacks) {
		return costSpentInSuperAttacks != 0 && (Parameters.ATTACKER_BUDGET / 4 == costSpentInSuperAttacks || (Parameters.ATTACKER_BUDGET / 4) % costSpentInSuperAttacks < Parameters.SUPERATTACK_RATE);

	}

	private List<Integer> getDBNodeIds() {
		List<Integer> dbNodeIds = new ArrayList<>();
		for (Node n : net.getNodes())
			if (n.isDatabase())
				dbNodeIds.add(n.getNodeID());
		return dbNodeIds;
	}

	public int highestSV(Node current) {
		int currentSV = current.getSv();
		int currentNodeID = current.getNodeID();
		if (currentSV < current.neighbor.get(0).getSv()) {
			currentNodeID = current.neighbor.get(0).getNodeID();
			return currentNodeID;
		} else
			return currentNodeID;

	}

	private ArrayList<Node> getNeighborList(Node current) {
		return current.getNeighborList();
	}

	private boolean areNodesSimilar(Node source, Node neighbor) {
		double division = source.getSv() / neighbor.getSv();
		double percentage = 0.00;
		percentage = division > 1 ? division - 1 : 1 - division;
		return percentage <= 0.25; //threshold
	}

	//TODO: check if its correct :P
	private List<Node> sortNodesDescending(List<Node> nodeList) {
		Collections.sort(nodeList, (n1, n2) -> {
			Integer sv1 = n1.getSv();
			Integer sv2 = n2.getSv();
			return sv1.compareTo(sv2);
		});
		return nodeList;
	}

	@Override
	protected void result(Node lastNode) {
		// TODO Auto-generated method stub
	}
}

