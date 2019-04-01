import java.util.ArrayList;

/**
 * This object represents one fragment (or node) of a conversation. The ConversationNode is used 
 * for storing the agent's possible replies to the user given a particular conversation-state. 
 * The nodes also define the possible flow of the conversation as each ConversationNode stores 
 * which nodes may come before or after itself in a conversation (referred to as parents and children).
 * <br><br>
 * NOTE: There is no debugging output in this class on this branch (master) to keep things clean. 
 * To view such pre-built debugging traces, view this class from the (dev) branch from the remote 
 * repository.
 */
public class ConversationNode {

	// the name of the node
	private String ID;

	// list of keywords for the node
	private ArrayList<String> keywords;
	// list of possible responses for the node
	private ArrayList<String> responses;

	// lists of IDs of the parents and children for the node
	private ArrayList<String> parentIDs;
	private ArrayList<String> childIDs;

	/**
	 * Constructor
	 * 
	 * @param id: the unique identifier of the node
	 */
	public ConversationNode(String id) {
		this.ID = id;
		this.keywords = new ArrayList<String>();
		this.responses = new ArrayList<String>();
		this.parentIDs = new ArrayList<String>();
		this.childIDs = new ArrayList<String>();

	}

	/**
	 * Unused constructor with all attributes of the node as arguments.
	 * 
	 * @param id
	 * @param keywords
	 * @param responses
	 * @param parentIDs
	 * @param childIDs
	 */
	public ConversationNode(String id, ArrayList<String> keywords, ArrayList<String> responses,
			ArrayList<String> parentIDs, ArrayList<String> childIDs) {
		this.ID = id;
		this.keywords = keywords;
		this.responses = responses;
		this.parentIDs = parentIDs;
		this.childIDs = childIDs;
	}

	// Getters and setters
	// And also 'adders' for the list attributes.
	public String getID() {
		return this.ID;
	}

	public ArrayList<String> getKeywords() {
		return this.keywords;
	}

	public ArrayList<String> getChildren() {
		return this.childIDs;
	}

	public ArrayList<String> getParents() {
		return this.parentIDs;
	}

	public ArrayList<String> getResponses() {
		return this.responses;
	}

	public void setKeywords(ArrayList<String> keys) {
		for (String key : keys) {
			this.keywords.add(key);
		}
	}

	public void addKeyword(String key) {
		this.keywords.add(key);
	}

	public void setResponses(ArrayList<String> reps) {
		for (String rep : reps) {
			this.responses.add(rep);
		}
	}

	public void addResponse(String rep) {
		this.responses.add(rep);
	}

	public void setParents(ArrayList<String> parents) {
		for (String parent : parents) {
			this.parentIDs.add(parent);
		}
	}

	public void addChild(String child) {
		this.childIDs.add(child);
	}

	public void addParent(String par) {
		this.parentIDs.add(par);
	}

	public void setChildren(ArrayList<String> children) {
		for (String child : children) {
			this.childIDs.add(child);
		}
	}

}
