package apiaryparty;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class KimTheDestroyerOfPlanets extends Attacker {

    private final static String attackerName = "KimTheDestroyerOfPlanets";

    public Random r;

     int nodesOfInterest = -1;

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
     *
     * is database
     *
     *
     */

    public KimTheDestroyerOfPlanets(String defenderName, String graphFile) {
        super(attackerName, defenderName, graphFile);
    }

    public KimTheDestroyerOfPlanets() {
        super(attackerName);
    }

    protected void initialize() {
        r = new Random();
        //nodesOfInterest = 0;
    }

    @Override
    public AttackerAction makeAction() {
        Node node = new Node();

        if (availableNodes.size() == 0)
            return new AttackerAction(AttackerActionType.INVALID, 0);

        if (getDBNodeIds().size() == 0 ) { //There are no DB nodes available
            List<Integer> dbNodeIDs = getDBNodeIds();
        }
        else {
            availableNodes.sor
        }

        int nodeID = availableNodes.get(r.nextInt(availableNodes.size())).getNodeID();
        //TODO: decide which node to attack before evaluating for honeypot
        Node target = net.getNode();
        if (honeyPotExists())
            for (Node n : target.getNeighborList()) {
                if (areNodesSimilar(target, n))
                    return new AttackerAction(AttackerActionType.PROBE_HONEYPOT, target.getNodeID());
            }
        else
            return new AttackerAction(AttackerActionType.ATTACK, target.getNodeID());

        if (Parameters.STRENGTHEN_RATE >= (Parameters.DEFENDER_BUDGET) / 4) {
            //TODO
            return new AttackerAction(AttackerActionType.ATTACK, target.getNodeID());
        } else {
            //  if (isProbingViable()) {
            if (highestSV(target) > 20) {
                if (nodesOfInterest == -1 && isProbingViable()) {
                    nodesOfInterest = (highestSV(target));
                    return new AttackerAction(AttackerActionType.PROBE_POINTS, highestSV(target));
                } else {
                    //for (int interestID : nodesOfInterest)
                    return new AttackerAction(AttackerActionType.SUPERATTACK, nodesOfInterest);
                }
            }

            //  }
        }
        if (isProbingInexpensive()) {
            return new AttackerAction(AttackerActionType.PROBE_POINTS, highestSV(target));
        }

        return new AttackerAction(AttackerActionType.END_TURN, 0);
    }





//		return new AttackerAction(AttackerActionType.ATTACK, nodeID);

//        for (int neighborID : getDBNodeIds()) {
//            //Node dbNode = net.getNode(dbNodeID);
//            if (honeyPotExists() && areNodesSimilar(node.getNeighbor(neighborID), node.getNeighbor(neighborID))) {
//
//            }
//        }


        //isHoneyProbingViable()) {
        //return new AttackerAction(AttackerActionType.PROBE_HONEYPOT, dbNodeID);
    //}



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


    private List<Integer> getDBNodeIds() {
        List<Integer> dbNodeIds = new ArrayList<>();
        for (Node n : net.getNodes()) {
            if (n.isDatabase())
                dbNodeIds.add(n.getNodeID());
        }
        return dbNodeIds;
    }

    public int highestSV(Node current) {
        int currentSV = current.getSv();
        int currentNode = current.getNodeID();
        if(currentSV < current.neighbor.get(0).getSv()) {
            currentNode = current.neighbor.get(0).getNodeID();
            return currentNode;
        }
        else
            return currentNode;

    }

    private ArrayList<Node> getNeighborList(Node current) {
        return current.getNeighborList();
    }

    public boolean areNodesSimilar(Node source, Node neighbor) {
        double division = source.getSv() / neighbor.getSv();
        double percentage = 0.00;
        percentage = division > 1 ? division - 1 : 1 - division;

        //threshold
        return percentage <= 0.25;


    }

    @Override
    protected void result(Node lastNode) {
        // TODO Auto-generated method stub

    }


}

