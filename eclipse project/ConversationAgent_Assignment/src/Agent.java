

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.Set;
import edu.mit.jwi.*;
import edu.mit.jwi.item.*;

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
	Boolean visited = false;
	int previousRandomResponse = 17;
	boolean outputSynonymsToConsole = true;
	


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
	public Agent(String name) throws IOException {
		this.agentName = name;
		this.userInput = new ArrayList<String>();
		this.agentResponses = new ArrayList<String>();
		this.conversationPath = new ArrayList<String>();
		
		/*
		File dictDir = new File("src\\dict");	
		IDictionary dict = new Dictionary(dictDir);
		dict.open();	*/
		
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
	public void execute(ConversationGraph graph, String newUserInput) {
		
		//Output user's Input to GUI
		ConversationMain.updateTextArea("(You): " + newUserInput + "\n");
		
		// pass and store user input to handleInput
		addUserInput(newUserInput);
		String nextNode = handleInput(graph, getUserInput());
		
		// If userInput was viable, Traverse to node returned by handleInput():		
		if(nextNode != null) {
			conversationPath.add(nextNode);
			visited = false;
		}
		
		// Check if agent is at the end node; if so, reset back to the root node.
		if (graph.get(getCurrentNode()).getID().equals(END_NODE)) {
			conversationPath.add(ROOT_NODE);
		}
		
		// Retrieve a response from the current node, store it in response history, then output to user:		
		if(!visited) {
			String response = selectResponse(graph.get(getCurrentNode()));
			addResponse(response);
			speak(response);
			visited = true;
		}

		isContinueNode(graph);

	}
	
	
	public void isContinueNode(ConversationGraph graph) {
		if (peekKeywords(graph).keySet().contains(CONTINUE_NODE)) {
			
			String nextNode = handleInput(graph, null);
			conversationPath.add(nextNode);
			
			String response = selectResponse(graph.get(getCurrentNode()));
			addResponse(response);
			speak(response);
			
			isContinueNode(graph);	
		} 
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
	public String handleInput(ConversationGraph graph, String userInput) {
	
		
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
			
			ArrayList<String> synonyms = getSynonyms(keyword);
			
			if(compareWords(synonyms, userInput)) {
				keywordCounter++;
				// Store nodeIDs of any matched keywords into list:
				matchedNodes.add(peekKeywords(graph).get(keyword));
				break;
			}
			
			
			/*
			for(int i = 0; i < synonyms.size(); i++) {
				if (userInput.indexOf(synonyms.get(i)) >= 0) {
					keywordCounter++;
					// Store nodeIDs of any matched keywords into list:
					matchedNodes.add(peekKeywords(graph).get(keyword));
					break;
				}
				
			}*/
			
			/*
			if (userInput.indexOf(keyword) >= 0) {
				keywordCounter++;
				// Store nodeIDs of any matched keywords into list:
				matchedNodes.add(peekKeywords(graph).get(keyword));
			}*/
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
			/*
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
			}*/
			
			/*I kept the content from what was here before, but for my purposes I felt it was entirely unnecessary, and was
			 * based on assumed features we would implement that are kind of useless.*/
			return matchedNodes.get(0);

		} else if (keywordCounter < 1) { // No keywords found, thus no linked nodeIDs.
			return handleNoKeyword();
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
		ConversationMain.updateTextArea(this.agentName + ": " + response + "\n");
		
		//System.out.println(this.agentName + ": " + response);
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
	/*I commented out this section because I felt it was useless.*/
	/*
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
	}*/

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
	public String handleNoKeyword() {
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
		
		visited = true;
		return null;
		
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
	
	/**
	 * Returns an arraylist of synonyms based off of the given string. 
	 * Will only find synonyms of first word if there is more than one word sent.
	 * 
	 * @param rootWord
	 * @return list of the keyword and all synonyms found in WordNet
	 */
	public ArrayList<String> getSynonyms(String keyword){
		ArrayList<String> synonyms = new ArrayList<String>();
		String trimmedkeyword = keyword.trim();
		synonyms.add(trimmedkeyword);
		
		File dictDir = new File("src\\dict");
		IDictionary dict = new Dictionary(dictDir);
		try {
			dict.open();
		} catch (IOException e) {
			System.out.println("Not sure why this broke, but it's when you .open() the Dictionary");
			e.printStackTrace();
		}
		
		if(dict.isOpen()) {
			
			//This checks the word in WordNet with all POSs to add all definitions to the synonym list
			for(int i = 0; i < 4; i++) {
				if(i == 0) {
					IIndexWord idxWord = dict.getIndexWord(keyword, POS.NOUN);
					if(idxWord != null) {
						ArrayList<String> tempArrayList = makePartialSynonymList(idxWord, dict);
						synonyms.addAll(tempArrayList);
					}
				}
				if(i == 1) {
					IIndexWord idxWord = dict.getIndexWord(keyword, POS.VERB);
					if(idxWord != null) {
						ArrayList<String> tempArrayList = makePartialSynonymList(idxWord, dict);
						synonyms.addAll(tempArrayList);
					}
				}
				if(i == 2) {
					IIndexWord idxWord = dict.getIndexWord(keyword, POS.ADJECTIVE);
					if(idxWord != null) {
						ArrayList<String> tempArrayList = makePartialSynonymList(idxWord, dict);
						synonyms.addAll(tempArrayList);
					}
				}
				if(i == 3) {
					IIndexWord idxWord = dict.getIndexWord(keyword, POS.ADVERB);
					if(idxWord != null) {
						ArrayList<String> tempArrayList = makePartialSynonymList(idxWord, dict);
						synonyms.addAll(tempArrayList);
					}
				}
			}
			
			
			/*creates the IIndexWord for the keyword. If it doesn't match any part of speech for some weird
			 reason then we just don't add synonyms*/
			/*
			IIndexWord idxWord = dict.getIndexWord(keyword, POS.NOUN);
			if(idxWord == null) {
				 idxWord = dict.getIndexWord(keyword, POS.VERB);
				 if(idxWord == null) {
					 idxWord = dict.getIndexWord(keyword, POS.ADJECTIVE);
					 if(idxWord == null) {
						 idxWord = dict.getIndexWord(keyword, POS.ADVERB);
					 }
				 }
			}
			//getSynonyms
			if(idxWord != null) {
				for(IWordID wordID : idxWord.getWordIDs()) {
					IWord word = dict.getWord(wordID);
					ISynset synset = word.getSynset();
					for(IWord w : synset.getWords()) {
						synonyms.add(w.getLemma());
						
						if(outputSynonymsToConsole) {
							System.out.println("Lemma = " + w.getLemma());						
						}
					}
				}
			}*/
		}
		
		return synonyms;
	}
	/**
	 * 
	 * @param idxWord a IIndexWord you want an array of synonyms for
	 * @param dict the IDictionary file you want to pull synonyms from
	 * @return ArrayList of words that are synonyms
	 */
	public ArrayList<String> makePartialSynonymList(IIndexWord idxWord, IDictionary dict){
		ArrayList<String> partialSynonymList = new ArrayList<String>();
		
		if(idxWord != null) {
			for(IWordID wordID : idxWord.getWordIDs()) {
				IWord word = dict.getWord(wordID);
				ISynset synset = word.getSynset();
				for(IWord w : synset.getWords()) {
					partialSynonymList.add(w.getLemma());
					
					if(outputSynonymsToConsole) {
						System.out.println("Lemma = " + w.getLemma());						
					}
				}
			}
		}
		return partialSynonymList;
	}
	
	
	public boolean compareWords(ArrayList<String> synonyms, String userInput) {
		
		boolean match = false;
		ArrayList<String> userInputAsArray = toWordArray(userInput);
		
		for (int x = 0; x < synonyms.size(); x++) {
			if(match == true) {
				break;
			}
			char[] curSynonym = synonyms.get(x).toCharArray();
			int charCount=0;
			
			for (int z = 0; z < userInputAsArray.size(); z++) {
				if(match == true) {
					break;
				}
				char[] curInputWord = userInputAsArray.get(z).toCharArray();

				if(curInputWord.length == curSynonym.length) {
					for(int c = 0; c < curSynonym.length; c++) {
						if(curInputWord[c] == curSynonym[c]) {
							charCount++;
						}
						else if(checkSwappedCharacter(curSynonym, curInputWord, c)) {
							charCount++;
						}
						if(charCount >= curSynonym.length) {
							match = true;
						}
					}	
				}
				else if(curInputWord.length == curSynonym.length+1) {	
					for(int c = 0; c < curSynonym.length; c++) {
						if(curInputWord[c] == curSynonym[c]) {
							charCount++;
						}
						else if(checkExtraCharacter(curSynonym, curInputWord, c)) {
							charCount++;
							c++;
						}
						else if(checkSwappedCharacter(curSynonym, curInputWord, c)) {
							charCount++;
						}
						if(charCount >= curSynonym.length){
							match = true;
							break;
						}
					}
				}
				else if(curInputWord.length == curSynonym.length-1) {		
					for(int c = 0; c < curInputWord.length; c++) {
						if(curInputWord[c] == curSynonym[c]) {
							charCount++;
						}
						else if(checkMissingCharacter(curSynonym, curInputWord, c)) {
							charCount++;
						}
						else if(checkSwappedCharacter(curSynonym, curInputWord, c)) {
							charCount++;
						}
						if(charCount >= curSynonym.length-1){
							match = true;
							break;
						}
					}
				}
			}
			charCount = 0;
		}
		return match;
	}
	
	/**
	 * returns true if previous or next character in index is equal to the current on in curInputWord
	 * @param curSynonym -- current synonym we are testing
	 * @param curInputWord -- current user word we are testing
	 * @param c -- current point in the index
	 * @return
	 */
	
	private boolean checkSwappedCharacter(char[] curSynonym, char[] curInputWord, int c) {
		
		/*check if current character is same as next character
		 * check if current character is same as previous character
		 * We know they aren't the same in both because otherwise we wouldn't be here
		 * 
		 * */
		
		
		if(c!=0) {
			if(curInputWord[c] == curSynonym[c-1]) {
				return true;
			}
		}
		if(c < curSynonym.length-2) {
			if(curInputWord[c] == curSynonym[c+1]) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Determines if curInputWord has an extra character by comparing it to the next two
	 * 
	 * @param curSynonym
	 * @param curInputWord
	 * @param c
	 * @return
	 */
	private boolean checkExtraCharacter(char[] curSynonym, char[] curInputWord, int c) {
		if(c < curSynonym.length-3 && c < curInputWord.length-3) {
			if(curInputWord[c] == curSynonym[c+1]) {
				if(curInputWord[c+1] == curSynonym[c+2]) {
					return true;
				}
			}	
		}
		return false;
	}

	/**
	 * 
	 * @param curSynonym current Synonym we're working with
	 * @param curInputWord Current word to test
	 * @param c the current index
	 * @return true if current index of curInputWord equals the next character in curSynonym
	 */
	private boolean checkMissingCharacter(char[] curSynonym, char[] curInputWord, int c) {
		
		if(curInputWord[c] == curSynonym[c+1]) {
			return true;
		}
		return false;
	}

	/** takes a string and cuts words into substrings, then adds those to an arraylist
	 * The split is determined based off of the whitespace
	 * 
	 * @param A string that you want to cut into words
	 * @return an arraylist of the words
	 */
	public ArrayList<String> toWordArray(String input){
		ArrayList<String> words = new ArrayList<String>();
		String[] splitString = (input.trim()).split(" ");
		
		for (int q = 0; q < splitString.length; q++) {
			words.add(splitString[q]);
		}
		return words;
	}

}
