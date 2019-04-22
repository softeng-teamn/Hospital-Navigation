package database;

import map.Edge;

import java.util.ArrayList;
import java.util.List;

class EdgeDatabase {
    private final DatabaseService databaseService;

    public EdgeDatabase(DatabaseService databaseService) {
        this.databaseService = databaseService;
    }

    /**
     * insert an edge. The method will fail and return false if the two nodes it points to
     * do not already exist in the database.
     *
     * @param e the edge to insert
     * @return true if the insert succeeds and false if otherwise
     */
    boolean insertEdge(Edge e) {
        String insertStatement = ("INSERT INTO EDGE VALUES(?,?,?)");
        String node1ID = e.getNode1().getNodeID();
        String node2ID = e.getNode2().getNodeID();

        boolean successful = databaseService.executeInsert(insertStatement, e.getEdgeID(), node1ID, node2ID);
        if (successful) databaseService.executeEdgeCallbacks();
        return successful;
    }

    /**
     * get an edge. This also pulls out the nodes that edge connects.
     *
     * @param edgeID the ID of the edge to retrieve
     * @return the edge corresponding to the given ID
     */
    Edge getEdge(String edgeID) {
        String query = "SELECT e.*, n1.nodeID as n1nodeID, n1.xcoord as n1xcoord, n1.ycoord as n1ycoord, n1.floor as n1floor, n1.building as n1building, n1.nodeType as n1nodeType, n1.longName as n1longName, n1.shortName as n1shortName, n1.isClosed as n1isClosed, n2.nodeID as n2nodeID, n2.xcoord as n2xcoord, n2.ycoord as n2ycoord, n2.floor as n2floor, n2.building as n2building, n2.nodeType as n2nodeType, n2.longName as n2longName, n2.shortName as n2shortName, n2.isClosed as n2isClosed FROM EDGE e Join NODE n1 on e.NODE1 = n1.NODEID Join NODE n2 on e.NODE2 = n2.NODEID WHERE (EDGEID = ?)";
        return (Edge) databaseService.executeGetById(query, Edge.class, edgeID);
    }

    /**
     * updates an edge with new node IDs.
     *
     * @param e the edge to update
     * @return true or false based on whether the insert succeeded or not
     */
    boolean updateEdge(Edge e) {
        String query = "UPDATE EDGE SET edgeID=?, NODE1=?, NODE2=? WHERE(EDGEID = ?)";
        boolean successful = databaseService.executeUpdate(query, e.getEdgeID(), e.getNode1().getNodeID(), e.getNode2().getNodeID(), e.getEdgeID());
        if (successful) databaseService.executeEdgeCallbacks();
        return successful;
    }

    /**
     * Deletes an edge from the database.
     *
     * @param e edge to delete from the database
     * @return true or false based on whether the insert succeeded or not
     */
    boolean deleteEdge(Edge e) {
        String query = "DELETE FROM EDGE WHERE (edgeID = ?)";
        boolean successful = databaseService.executeUpdate(query, e.getEdgeID());
        if (successful) databaseService.executeEdgeCallbacks();
        return successful;
    }

    /**
     * Retrieves every edge from the database.
     *
     * @return An ArrayList of every edge in the database.
     */
    ArrayList<Edge> getAllEdges() {
        String query = "Select e.*, n1.nodeID as n1nodeID, n1.xcoord as n1xcoord, n1.ycoord as n1ycoord, n1.floor as n1floor, n1.building as n1building, n1.nodeType as n1nodeType, n1.longName as n1longName, n1.shortName as n1shortName, n1.isClosed as n1isClosed, n2.nodeID as n2nodeID, n2.xcoord as n2xcoord, n2.ycoord as n2ycoord, n2.floor as n2floor, n2.building as n2building, n2.nodeType as n2nodeType, n2.longName as n2longName, n2.shortName as n2shortName, n2.isClosed as n2isClosed FROM EDGE e Join NODE n1 on e.NODE1 = n1.NODEID Join NODE n2 on e.NODE2 = n2.NODEID";
        return (ArrayList<Edge>) (List<?>) databaseService.executeGetMultiple(query, Edge.class, new Object[]{});
    }

    /**
     * Retrieves all edges connected to the given node
     *
     * @param nodeId The node to retrieve edges from
     * @return All edges connected to the given node.
     */
    ArrayList<Edge> getAllEdgesWithNode(String nodeId) {
        String query = "Select e.*, n1.nodeID as n1nodeID, n1.xcoord as n1xcoord, n1.ycoord as n1ycoord, n1.floor as n1floor, n1.building as n1building, n1.nodeType as n1nodeType, n1.longName as n1longName, n1.shortName as n1shortName, n1.isClosed as n1isClosed, n2.nodeID as n2nodeID, n2.xcoord as n2xcoord, n2.ycoord as n2ycoord, n2.floor as n2floor, n2.building as n2building, n2.nodeType as n2nodeType, n2.longName as n2longName, n2.shortName as n2shortName, n2.isClosed as n2isClosed FROM EDGE e Join NODE n1 on e.NODE1 = n1.NODEID Join NODE n2 on e.NODE2 = n2.NODEID Where (n1.NODEID = ? or n2.NODEID = ?)";
        return (ArrayList<Edge>) (List<?>) databaseService.executeGetMultiple(query, Edge.class, nodeId, nodeId);
    }
}