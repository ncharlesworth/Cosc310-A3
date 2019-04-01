import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.Scanner;
import java.util.Set;

/**
 * The Agent object represents the conversation agent. This object is used to store information 
 * about the current conversation. Additionally the Agent object has functions which enable the 
 * agent to process input and select what it will output. These include functions to determine 
 * if the user input matches any relevant keywords, selecting and formatting a reply when keywords 
 * are matched, and choosing how the agent replies when it is unable to understand the input.
 * <br><br>
 * NOTE: There is no debugging output in this class on this branch (master) to keep things clean. 
 * To view such pre-built debugging traces, view this class from the (dev) branch from the remote 
 * repository.
 */
public class Agent {

	String agentName; // Name of agent in current session
	ArrayList<String> conversationPath; // List of ConversationNode IDs corresponding to the current conversation, in
										// order
	ArrayList<String> userInput; // List of strings corresponding to user input
	ArrayList<String> agentResponses; // List out messages that have been replied
	HashMap<String, String> userInfo = new HashMap<String, String>(); // Used for storing user information such as name
																		// and email
																		// if user name is Bob then ->
																		// userInfo.put("name", "Bob")

	int previousRandomResponse = 17;

	/**
	 * Variable names for graph traversal behavior. Any node that is CONTINUE_NODE
	 * as its keyword will skip asking for the user's input. This is generally used
	 * when a conversation topic has changed, and it would be awkward to ask for
	 * additional user input before transition to the next node. A node with the
	 * NULL_NODE keyword requires a user's input (such as when asking for the user's
	 * name, their email, or other similar information), but the agent will always
	 * traverse to that node regardless of what the user input is. END_NOTE is the
	 * very last conversation node in the graph, which will identify to the agent
	 * when traverse back to the main conversation root in case the user requires
	 * additional service, and ROOT_NODE is the ID of that specific node.
	 */
	final String CONTINUE_NODE = "continue";
	final String NULL_NODE = "null";
	final String END_NODE = "BotReview2";
	final String ROOT_NODE = "Root";

	// Constructor
	public Agent(String name) {
		this.agentName = name;
		this.userInput = new ArrayList<String>();
		this.agentResponses = new ArrayList<String>();
		this.conversationPath = new ArrayList<String>();
	}

	public String getAgetName() {
		return agentName;
	}

	/**
	 * Agent behavior method. This method will need to be looped in the main method to have an ongoing
	 * conversation, otherwise this method will only handle one interaction between the agent and user.
	 * <br><br>
	 * The agent will find where it is located on the conversation graph, detect what type of behavior
	 * it is expected to follow for that node, receive user input if it is necessary, and pass that
	 * input to inputHandler() to make a decision based on that input. The agent will then traverse to
	 * the next node of the conversation graph that it has deemed appropriate.
	 * @param graph The ConversationGraph being used. 
	 * @param scanner The System.in scanner instantiated in the main program method.
	 */
	public void execute(ConversationGraph graph, Scanner scanner) {
		// Check if agent is at the end node; if so, reset back to the root node.
		if (graph.get(getCurrentNode()).getID().equals(END_NODE)) {
			conversationPath.add(ROOT_NODE);
		}

		// Retrieve a response from the current node, store it in response history, then output to user:
		String response = selectResponse(graph.get(getCurrentNode()));
		addResponse(response);
		speak(response);

		// Receive input from the user if it is required, ignoring cases that contain only whitespace.
		String userInput = "";
		if (!(peekKeywords(graph).keySet().contains(CONTINUE_NODE))) {
			userInput = scanner.nextLine();
			while (!(userInput.trim().length() > 0)) {
				userInput = scanner.nextLine();
			}
		}

		// If next node is a CONTINUE_NODE, pass null to handleInput(); otherwise, pass and store user input.
		String nextNode;
		if (peekKeywords(graph).keySet().contains(CONTINUE_NODE)) {
			nextNode = handleInput(graph, null, scanner);
		} else {
			addUserInput(userInput);
			nextNode = handleInput(graph, getUserInput(), scanner);
		}

		// Traverse to node returned by handleInput():
		conversationPath.add(nextNode);
	}

	/**
	 * Handles the user input and decides what conversation path to take by matching
	 * user input with potential conversation node keywords. This method strictly
	 * handles what to do with user input; i.e: what conversation node to traverse
	 * to given the input it receives from the user. This method should be called
	 * from an agent object via the execute() method.
	 * 
	 * @param graph     The ConversationGraph being used.
	 * @param userInput Input from the user.
	 * @param scanner   The System.in scanner instantiated in the main program
	 *                  method.
	 * @return nodeID to traverse to.
	 */
	public String handleInput(ConversationGraph graph, String userInput, Scanner scanner) {
		Set<String> keywords = peekKeywords(graph).keySet();
		ArrayList<String> matchedNodes = new ArrayList<String>();
		int keywordCounter = 0;

		if (keywords.contains(CONTINUE_NODE)) {
			matchedNodes.add(peekKeywords(graph).get(CONTINUE_NODE));
			return matchedNodes.get(0);
		} else if (keywords.contains(NULL_NODE)) {
			matchedNodes.add(peekKeywords(graph).get(NULL_NODE));
			return matchedNodes.get(0);
		}

		// Check if the userInput contains a keyword of a child node:
		for (String keyword : keywords) {
			if (userInput.indexOf(keyword) >= 0) {
				keywordCounter++;
				// Store nodeIDs of any matched keywords into list:
				matchedNodes.add(peekKeywords(graph).get(keyword));
			}
		}
		/**
		 * Check how many keywords were matched from user input. If only one keyword is
		 * matched, then we know exactly what nodeID we need to traverse to. If no
		 * keywords are matched, then we need to ask the user for new input to clarify
		 * what they want. If more than one keyword is matched, then we need to find if
		 * all those keywords are linked to the same nodeID or not. If they are, then
		 * there is no ambiguity and we know exactly what nodeID to traverse to. If
		 * keyword matches are linked to a set of different nodeIDs (we have mismatched
		 * nodes), then we need to find out what node the user actually intended to get
		 * to.
		 */
		if (keywordCounter > 1) {
			String firstMatchedNode = matchedNodes.get(0);
			int mismatchedNodes = 0;
			for (String nodeID : matchedNodes) {
				if (!firstMatchedNode.equals(nodeID)) {
					mismatchedNodes++;
				}
			}
			if (mismatchedNodes == 0) { // Exactly one nodeID found linked to multiple found keywords.
				return matchedNodes.get(0);
			} else { // More than one nodeID found linked to keywords.
				return handleMultiChildren(graph, userInput, scanner);
			}
		} else if (keywordCounter < 1) { // No keywords found, thus no linked nodeIDs.
			return handleNoKeyword(graph, userInput, scanner);
		} else { // Optimal case: exactly one keyword found that is linked to exactly one nodeID.
			return matchedNodes.get(0);
		}

	}

	/**
	 * Output the agent's response to the user.
	 * <br><br>
	 * Replaces the *name* with the current agent name.
	 * 
	 * @param response String containing the response the agent will say to the
	 *                 user.
	 */
	public void speak(String response) {
		response = response.replace("*name*", this.agentName);
		System.out.println(this.agentName + ": " + response);
	}

	/**
	 * Method to handle what the agent will do when keywords found in multiple
	 * children.
	 * <br><br>
	 * NOTE: This feature is not fully implemented. If the function is called the agent will simply ask the user 
	 * which keyword they meant by going through them all in order. A heuristic evaluation function would
	 * need to be implemented to make this process more organic, which is beyond the scope of the current
	 * iteration of this project.
	 * @param graph A ConversationGraph.
	 * @param userInput Input from the user containing conflicting keywords.
	 * @param scanner   The System.in scanner instantiated in the main program
	 *                  method.
	 * @return The nodeID that was determined to be where the agent should traverse
	 *         to.
	 */
	public String handleMultiChildren(ConversationGraph graph, String userInput, Scanner scanner) {
		String userReply;
		String agentReply = "";
		Set<String> keywords = peekKeywords(graph).keySet();
		ArrayList<String> matchedNodes = new ArrayList<String>();
		ArrayList<String> matchedKeys = new ArrayList<String>();
		HashMap<String, String> matches = new HashMap<String, String>();
		int keywordCounter = 0;
		// Check if the userInput contains a keyword of a child node:
		for (String keyword : keywords) {
			if (userInput.indexOf(keyword) >= 0) {
				// Store nodeIDs of any matched keywords into list:
				matchedNodes.add(peekKeywords(graph).get(keyword));
				matchedKeys.add(keyword);
				matches.put(matchedKeys.get(keywordCounter), matchedNodes.get(keywordCounter));
				keywordCounter++;
			}
		}
		for (String match : matches.keySet()) {
			speak("Did you mean " + match + "?");
			userReply = scanner.nextLine();
			if (userReply.toLowerCase().contains("yes") || userReply.toLowerCase().contains("yeah")
					|| userReply.toLowerCase().contains("correct") || userReply.toLowerCase().contains("ok")) {
				agentReply = matches.get(match);
				break;
			}
		}
		if (agentReply.isEmpty()) {
			return handleNoKeyword(graph, userInput, scanner);
		} else
			return agentReply;
	}

	/**
	 * Method to handle what the agent will do when the user's input contains no
	 * keywords for child nodes. Takes a new response from the user and passes it
	 * back to handleInput() to be be parsed.
	 * 
	 * @param graph     A ConversationGraph.
	 * @param userInput Input from the user that does not contain any relevant
	 *                  keywords.
	 * @param scanner   The System.in scanner instantiated in the main program
	 *                  method.
	 * @return The nodeID the agent will revert to.
	 */
	public String handleNoKeyword(ConversationGraph graph, String userInput, Scanner scanner) {
		ArrayList<String> responseList = new ArrayList<String>();
		responseList.add("I'm not sure I understand. Can you rephrase that for me?");
		responseList.add("Sorry, I don't quite understand what you're saying.");
		responseList.add("I'm sorry, I'm a bit confused by what you're asking.");
		responseList.add("Would you be able to rephrase that for me? I'm a little confused with what you are asking.");
		responseList.add("Perhaps you could be a bit more specific, I don't quite understand what you mean.");
		responseList.add("Could you say that like you're speaking to a child? I don't understand for some reason.");
		int random = new Random().nextInt(responseList.size());
		while(previousRandomResponse==random) {
			random = new Random().nextInt(responseList.size());
		}
		previousRandomResponse=random;
		String response = responseList.get(random);
		speak(response);
		addUserInput(scanner.nextLine());
		return handleInput(graph, getUserInput(), scanner);
	}

	/**
	 * Find what ConversationNode the agent is currently at within the
	 * ConversationGraph.
	 * 
	 * @return nodeID contained within the last element of conversationPath
	 */
	public String getCurrentNode() throws IndexOutOfBoundsException {
		return conversationPath.get(conversationPath.size() - 1);
	}

	/**
	 * Retrieves the last input received from the user, located at the end of the
	 * userInput list.
	 * 
	 * @return The last input received from the user
	 */
	public String getUserInput() throws IndexOutOfBoundsException {
		return userInput.get(userInput.size() - 1).toLowerCase();
	}

	/**
	 * Finds all keywords leading away from the current node the agent is located
	 * at, as well as what nodeIDs those particular keywords are associated with.
	 * 
	 * @param graph The ConversationGraph being used.
	 * @return A map of key-value pairs where each key is a keyword and its
	 *         associated value is the nodeID where that particular keyword is
	 *         stored at.
	 */
	public HashMap<String, String> peekKeywords(ConversationGraph graph) {
		String currentNode = this.getCurrentNode();
		return graph.getChildKeywords(currentNode);
	}

	/**
	 * Retrieves a user's input from an amount of turns prior to the current one in
	 * the form of an offset.
	 * 
	 * @param offset amount of turns prior to the latest (offset 1 would indicate
	 *               the previous turn)
	 * @return The user input received from the user at the particular offset
	 * @throws IndexOutOfBoundsException
	 */
	public String getInputOffset(int offset) throws IndexOutOfBoundsException {
		return userInput.get(userInput.size() - (offset + 1));
	}

	/**
	 * Add a response to the response history of the agent.
	 * 
	 * @param response The response the agent just made.
	 */
	public void addResponse(String response) {
		this.agentResponses.add(response);
	}

	/**
	 * Add input from the user to the userInput history of the agent.
	 * 
	 * @param userInput The last input received from the user.
	 */
	public void addUserInput(String userInput) {
		this.userInput.add(userInput);
	}

	/**
	 * Returns a random response from the list of available responses at the given
	 * node.
	 * 
	 * @param node The ConversationNode from which the response is retrieved.
	 * @return A random response from that node.
	 */
	public String selectResponse(ConversationNode node) {
		int random = new Random().nextInt(node.getResponses().size());
		return node.getResponses().get(random);

	}

}
