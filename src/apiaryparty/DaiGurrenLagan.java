package apiaryparty;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@SuppressWarnings("Duplicates")
public class DaiGurrenLagan extends Defender {
	Random r;
	boolean isHoneyPotViable;
	boolean isFirewallViable;
	int costSpentInFirewalls;
	int costSpentHoneyCombs;


	public DaiGurrenLagan(String graphFile) {
		super("DaiGurrenLagan", graphFile);
	}

	@Override
	public void initialize() {
		r = new Random();
		isHoneyPotViable = isHoneyPotViable();
		isFirewallViable = isFirewallViable();
		costSpentInFirewalls = 0;
		costSpentHoneyCombs = 0;
	}

	@Override
	public void actionResult(boolean actionSuccess) {

	}

	@Override
	public DefenderAction makeAction() {
		/**
		 *  1. Protect DB nodes by removing all the possible edges through firewall if it is a viable move,
		 *  until you have consumed 1/4 of the budget
		 *  while( dbNode.neighborAmount >= MIN_NEIGHBORS && Parameters.DEFENDER_BUDGET/4 >= budgetSpentOnHoneyCombs)
		 *  put a firewall
		 *  2. Protect network through honeycombs if it is a viable move until you have consumed
		 *  choose one of the highest valued nodes and lowest valued nodes alternating
		 *  1/4 of the budget
		 *  3. Protect valuable targets (personal or secure devices) with mid or mid-low security until you
		 *  have spent the rest of the budget
		 */
		if (isFirewallViable) {
			for (int dbNodeID : getDBNodeIDs()) {
				Node dbNode = net.getNode(dbNodeID);
				if (dbNode.getNeighborAmount() >= Parameters.MIN_NEIGHBORS || !doneProtectingDbs(costSpentInFirewalls)) {
					costSpentInFirewalls += Parameters.FIREWALL_RATE;
					//Insert a firewall between DB and one of its neighboors
					return new DefenderAction(dbNode.getNodeID(), dbNode.neighbor.get(0).getNodeID());
				}
			}
		} else if (isHoneyPotViable) {
			if (!doneAddingHoneyCombs(costSpentHoneyCombs)) {
				costSpentHoneyCombs += Parameters.HONEYPOT_RATE;
				Random r = new Random();
				int honeyNode = r.nextInt(net.getAvailableNodes().size());
				return new DefenderAction(DefenderActionType.HONEYPOT, honeyNode);
			}
		} else {
			if (getBudget() <= Parameters.STRENGTHEN_RATE || getBudget() < Parameters.STRENGTHEN_RATE) {
				for (int valuableNodeID : getValueableNodeIDs()) {
					Node valuableNode = net.getNode(valuableNodeID);
					if (valuableNode.getSv() <= 3*Parameters.ATTACK_RATE/4 )
						return new DefenderAction(DefenderActionType.STRENGTHEN, valuableNode.getNodeID());
				}
			}
		}
		return new DefenderAction(DefenderActionType.END_TURN);
	}


	private List<Integer> getDBNodeIDs() {
		List<Integer> dbNodeIds = new ArrayList<>();
		for (Node n : net.getNodes()) {
			if (n.isDatabase())
				dbNodeIds.add(n.getNodeID());
		}
		return dbNodeIds;
	}
	private List<Integer> getValueableNodeIDs() {
		List<Integer> valuableNodeIDs = new ArrayList<>();
		for (Node n : net.getNodes()) {
			if (n.getSv() >= Parameters.ATTACK_RATE/2)
				valuableNodeIDs.add(n.getNodeID());
		}
		return valuableNodeIDs;
	}

	private boolean isHoneyPotViable() {
		return (Parameters.DEFENDER_BUDGET / 4) >= Parameters.HONEYPOT_RATE;
	}

	private boolean isFirewallViable() {
		return (Parameters.DEFENDER_BUDGET / 4) >= Parameters.FIREWALL_RATE;
	}

	private boolean doneProtectingDbs(int costSpentInFirewalls) {
		return Parameters.DEFENDER_BUDGET / 4 == costSpentInFirewalls || (Parameters.DEFENDER_BUDGET / 4) % costSpentInFirewalls < Parameters.FIREWALL_RATE;
	}

	private boolean doneAddingHoneyCombs(int costSpentInHoneyCombs) {
		return Parameters.DEFENDER_BUDGET / 4 == costSpentInHoneyCombs || (Parameters.DEFENDER_BUDGET / 4) % costSpentInHoneyCombs < Parameters.HONEYPOT_RATE;
	}
}
