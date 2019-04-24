package edu.wpi.cs3733d19.teamN.database;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import edu.wpi.cs3733d19.teamN.map.Node;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

class NodeDatabase {
    private final DatabaseService databaseService;

    NodeDatabase(DatabaseService databaseService) {
        this.databaseService = databaseService;
    }

    /**
     * Attempt to insert a node into the database. Will not succeed if n.nodeID is not unique
     *
     * @param n A {@link Node} to insert into the database
     * @return true if the node is successfully inserted, false otherwise.
     */
    boolean insertNode(Node n) {
        String nodeStatement = ("INSERT INTO NODE VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?)");
        boolean successful = databaseService.executeInsert(nodeStatement, n.getNodeID(), n.getXcoord(), n.getYcoord(), n.getFloor(), n.getBuilding(), n.getNodeType(), n.getLongName(), n.getShortName(), n.isClosed());
        if (successful) databaseService.executeNodeCallbacks();
        return successful;
    }

    /**
     * Update the database entry for a given node
     *
     * @param n A {@link Node} to update. The node must have a valid ID
     * @return true if the update is successful, false otherwise
     */
    boolean updateNode(Node n) {
        String query = "UPDATE NODE SET xcoord=?, ycoord=?, floor=?, building=?, nodeType=?, longName=?, shortName=?, isClosed=? WHERE (nodeID = ?)";
        boolean successful = databaseService.executeUpdate(query, n.getXcoord(), n.getYcoord(), n.getFloor(), n.getBuilding(), n.getNodeType(),
                n.getLongName(), n.getShortName(), n.isClosed(), n.getNodeID());
        if (successful) databaseService.executeNodeCallbacks();
        return successful;
    }

    /**
     * Delete a node if it exists
     *
     * @param n A {@link Node} to delete. n.nodeId must not be null
     * @return true if a record is deleted, false otherwise
     */
    boolean deleteNode(Node n) {
        String query = "DELETE FROM NODE WHERE (nodeID = ?)";
        boolean successful = databaseService.executeUpdate(query, n.getNodeID());
        if (successful) databaseService.executeNodeCallbacks();
        return successful;
    }

    /**
     * retrieves the given node from the database
     *
     * @param nodeID the ID of the node to be retrieved
     * @return a node with the given ID
     */
    Node getNode(String nodeID) {
        String query = "SELECT * FROM NODE WHERE (NODEID = ?)";
        return (Node) databaseService.executeGetById(query, Node.class, nodeID);
    }

    /**
     * Takes a list of nodes and adds all of them to the database.
     *
     * @param nodes A list of nodes to add to the database
     * @return true if the insertion is successful, and false if otherwise
     */
    boolean insertAllNodes(List<Node> nodes) {
        String nodeStatement = ("INSERT INTO NODE VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?)");
        PreparedStatement insertStatement = null;

        // Track the status of the insert
        boolean insertStatus = false;

        try {
            // Prep the statement
            insertStatement = databaseService.getConnection().prepareStatement(nodeStatement);

            for (int i = 0; i <= nodes.size() / 1000; i++) {
                for (int j = (i * 1000); j < i * 1000 + 1000 && j < nodes.size(); j++) {
                    Node n = nodes.get(j);
                    databaseService.prepareStatement(insertStatement, n.getNodeID(), n.getXcoord(), n.getYcoord(), n.getFloor(), n.getBuilding(), n.getNodeType(), n.getLongName(), n.getShortName(), n.isClosed());
                    insertStatement.addBatch();
                }
                // Execute
                insertStatement.executeBatch();

                databaseService.executeNodeCallbacks();

                // If we made it this far, we're successful!
                insertStatus = true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            databaseService.closeStatement(insertStatement);
        }
        return insertStatus;
    }

    /**
     * Returns all nodes in the database.
     *
     * @return list of all nodes in the database
     */
    ArrayList<Node> getAllNodes() {
        String query = "Select * FROM NODE";
        return (ArrayList<Node>) (List<?>) databaseService.executeGetMultiple(query, Node.class, new Object[]{});
    }

    /**
     * get nodes filtered by specific getType
     *
     * @param filterOut the parameter to exclude specific nodes by
     * @return an arraylist of nodes that do not include the specified parameter
     */
    @SuppressFBWarnings(value = "SQL_PREPARED_STATEMENT_GENERATED_FROM_NONCONSTANT_STRING", justification = "Not a security issue - just add question marks based on number of types to filter out.")
    ArrayList<Node> getNodesFilteredByType(String... filterOut) {
        String query = "Select * from NODE where NODETYPE not in (";
        StringBuilder builtQuery = new StringBuilder();
        builtQuery.append(query);
        for (int i = 0; i < filterOut.length; i++) {
            builtQuery.append("?,");
        }
        builtQuery.deleteCharAt(builtQuery.lastIndexOf(","));
        builtQuery.append(")");

        return (ArrayList<Node>) (List<?>) databaseService.executeGetMultiple(builtQuery.toString(), Node.class, (Object[]) filterOut);
    }

    /**
     * get all nodes from the specified floor
     *
     * @param floor the floor to retrieve all nodes from
     * @return an arraylist of all nodes on the given floor.
     */
    ArrayList<Node> getNodesByFloor(String floor) {
        String query = "Select * FROM NODE WHERE NODE.FLOOR = ?";
        return (ArrayList<Node>) (List<?>) databaseService.executeGetMultiple(query, Node.class, floor);
    }

    int getNumNodeTypeByFloor(String nodeType, String floor) {
        PreparedStatement stmt = null;
        ResultSet res = null;
        try {
            stmt = databaseService.getConnection().prepareStatement("SELECT COUNT (*) AS TOTAL FROM NODE WHERE (floor=? AND nodeType=?)");
            databaseService.prepareStatement(stmt, floor, nodeType);

            // execute the query
            res = stmt.executeQuery();
            int num = -1;
            while (res.next()) {
                num = res.getInt("TOTAL");
            }
            stmt.close();
            res.close();
            return num;
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        } finally {
            databaseService.closeAll(stmt, res);
        }
    }

    /**
     * returns a list of nodes that are connected to the given node
     *
     * @param n the node to retrieve all nodes connected to from
     * @return A list of all nodes connected to the given node.
     */
    ArrayList<Node> getNodesConnectedTo(Node n) {
        String nodeID = n.getNodeID();
        String query = "SELECT NODE.NodeID, NODE.xcoord, NODE.ycoord, NODE.floor, NODE.building, NODE.nodeType, NODE.longName, NODE.shortName, NODE.isClosed FROM NODE INNER JOIN EDGE ON (NODE.NodeID = EDGE.node1 AND EDGE.node2 = ?) OR (NODE.NodeID = EDGE.node2 AND EDGE.Node1 = ?) WHERE NODE.isClosed = false";

        return (ArrayList<Node>) (List<?>) databaseService.executeGetMultiple(query, Node.class, nodeID, nodeID);
    }

    ArrayList<Node> getNodesByFloorByType(String floor, String type){
        String query = "Select * FROM NODE WHERE NODE.FLOOR = ? AND NODE.TYPE = ?";
        return (ArrayList<Node>) (List<?>) databaseService.executeGetMultiple(query, Node.class, floor, type);
    }
}