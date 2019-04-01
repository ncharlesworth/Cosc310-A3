import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import java.util.Set;

/**
 * The ConversationGraph object is used as a data structure for storing the ConversationNodes 
 * that could exist in a conversation. Because the nodes have specific possible parents and 
 * children, the ConversationGraph is essentially a directed graph. The ConversationGraph type
 *  also has functions which allow traversal along the nodes and accessing data from nodes in 
 *  the graph. Finally the graph object can parse a .txt file with a specific format to construct 
 *  ConversationNodes and add them to the graph.
 * <br><br>
 * NOTE: There is no debugging output in this class on this branch (master) to keep things clean. 
 * To view such pre-built debugging traces, view this class from the (dev) branch from the remote 
 * repository.
 */
public class ConversationGraph {

	/**
	 * The nodes are stored in a hash map key<String>: the ID of the node
	 * value<ConversationNode>: the node
	 */
	private HashMap<String, ConversationNode> graph;

	/*
	 * Constructor
	 */
	public ConversationGraph() {
		graph = new HashMap<String, ConversationNode>();
	}

	/**
	 * Function to add a node to the ConversationGraph
	 * 
	 * @param node
	 * @return
	 */
	public boolean add(ConversationNode node) {
		String key = node.getID();
		this.graph.put(key, node);
		return true;
	}

	/**
	 * Method to get a node from the graph. Returns the ConversationNode associated
	 * with the given ID.
	 * 
	 * @param id
	 * @return
	 */
	public ConversationNode get(String id) {
		return graph.get(id);
	}

	/**
	 * Return a set of all nodeIDs (keys) in the graph.
	 * 
	 * @return
	 */
	public Set<String> getKeys() {
		return graph.keySet();
	}

	/**
	 * Method to get all keywords from all children of a node. The output is a hash
	 * map with key: a keyword value: the ID of the child with that keyword
	 * 
	 * @param id
	 * @return
	 */
	public HashMap<String, String> getChildKeywords(String id) {
		HashMap<String, String> keyword_child = new HashMap<String, String>();
		ConversationNode node = this.get(id);
		ArrayList<String> children = node.getChildren();
		for (String childID : children) {
			ConversationNode child = this.get(childID);
			ArrayList<String> childKeys = child.getKeywords();
			for (String keyword : childKeys) {
				keyword_child.put(keyword.toLowerCase(), childID);
			}
		}
		return keyword_child;
	}

	/**
	 * Parses all ConversationNodes from a Scanner object and adds them to this
	 * ConversationGraph The format for ConversationNodes as text to be parsed is
	 * explained in the README.
	 * 
	 * @param sc
	 */
	public void parseConversationNodes(Scanner sc) {
		String currentLine;
		ConversationNode temp_node = null;
		int openQuote;
		int closeQuote;

		while (sc.hasNext()) {
			currentLine = sc.nextLine();

			if (currentLine.toLowerCase().startsWith("nodeID:".toLowerCase())) {
				openQuote = currentLine.indexOf('\"');
				closeQuote = currentLine.indexOf('\"', openQuote + 1);
				String nodeID = currentLine.substring(openQuote + 1, closeQuote);
				temp_node = new ConversationNode(nodeID);
			} else if (currentLine.toLowerCase().startsWith("parentIDs:".toLowerCase())) {
				openQuote = currentLine.indexOf('\"');
				closeQuote = currentLine.indexOf('\"', openQuote + 1);
				String parentID;
				int lastQuote = currentLine.lastIndexOf('\"');

				do {
					if (closeQuote == -1 && openQuote == -1 && lastQuote == -1) {
						break;
					} else {
						parentID = currentLine.substring(openQuote + 1, closeQuote);
						temp_node.addParent(parentID);

						/**
						 * Check if the ConversationGraph is empty. If it is, no nodes exist and thus
						 * they cannot possibly have children. If true, skip adding child nodes.
						 */
						if (this.graph.isEmpty()) {
							closeQuote++;
						} else {
							this.get(parentID).addChild(temp_node.getID());
							// List the children of the parent node that this node was just added to as a child:
							if (closeQuote != lastQuote) {
								openQuote = currentLine.indexOf('\"', closeQuote + 1);
								closeQuote = currentLine.indexOf('\"', openQuote + 1);
							} else if (closeQuote == lastQuote) {
								closeQuote++;
							}
						}
					}
				} while (closeQuote <= lastQuote);

			} else if (currentLine.toLowerCase().startsWith("keywords:".toLowerCase())) {
				String keyword;
				openQuote = currentLine.indexOf('\"');
				closeQuote = currentLine.indexOf('\"', openQuote + 1);
				int lastQuote = currentLine.lastIndexOf('\"');

				do {
					if (closeQuote == -1 && openQuote == -1 && lastQuote == -1) {
						break;
					} else {
						keyword = currentLine.substring(openQuote + 1, closeQuote);
						temp_node.addKeyword(keyword);
						if (closeQuote != lastQuote) {
							openQuote = currentLine.indexOf('\"', closeQuote + 1);
							closeQuote = currentLine.indexOf('\"', openQuote + 1);
						} else if (closeQuote == lastQuote) {
							closeQuote++;
						}
					}
				} while (closeQuote <= lastQuote);
				
				// List all keywords of the newly-added node:
			} else if (currentLine.toLowerCase().startsWith("responses:".toLowerCase())) {
				String response;
				openQuote = currentLine.indexOf('\"');
				closeQuote = currentLine.indexOf('\"', openQuote + 1);
				int lastQuote = currentLine.lastIndexOf('\"');

				do {
					if (closeQuote == -1 && openQuote == -1 && lastQuote == -1) {
						break;
					} else {
						response = currentLine.substring(openQuote + 1, closeQuote);
						temp_node.addResponse(response);
						if (closeQuote != lastQuote) {
							openQuote = currentLine.indexOf('\"', closeQuote + 1);
							closeQuote = currentLine.indexOf('\"', openQuote + 1);
						} else if (closeQuote == lastQuote) {
							closeQuote++;
						}
					}
				} while (closeQuote <= lastQuote);
				
				// List all responses of the newly-added node:
			} else if (currentLine.contains("##")) {
				this.add(temp_node);
				graph.get(temp_node.getID()).getID();
				temp_node = null;
			}
		}
	}

}
