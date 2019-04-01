import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Scanner;

public class ConversationMain {
	final static String FILENAME = "DialogueGraph.txt";
	final static String INITIALIZATION_NODE = "Greeting";

	public static ConversationGraph conversation; // Stores the conversation tree of possible replies
	public static Agent agent; // Stores session info
	public static int steps = 0; // turn counter

	/**
	 * This function initializes the ConversationGraph object by calling the
	 * ConverSationGraph.parseConversationNodes() function. This function takes as
	 * an argument a Scanner object pointed at the initialization file. We
	 * initialize the Scanner object within a try-catch block to handle running the
	 * program within the IDE and within the project built as a jar file. <br>
	 * <br>
	 * This function terminates by setting the INITIALIZATION_NODE to be the root of
	 * the current conversation.
	 * 
	 * @throws IOException
	 * @throws URISyntaxException
	 */
	public static void initializeConversation() throws IOException, URISyntaxException {

		Scanner filein;
		InputStream in;

		try {
			/*
			 * The try block is to handle opening the file within the IDE. When a class file
			 * from the project is executed we can treat the data as a text file.
			 */
			URL dialoguePath = ConversationMain.class.getResource(FILENAME);
			File file = new File(dialoguePath.toURI());
			filein = new Scanner(file);
		} catch (IllegalArgumentException e) {
			/*
			 * This catch is to handle when the program is executed as a compiled jar file.
			 * We must treat the data initialization file as a Resource in the jar. We
			 * access the text as an InputStream and point the Scanner to that stream
			 * object.
			 */
			in = ConversationMain.class.getClassLoader().getResourceAsStream(FILENAME);
			filein = new Scanner(in);
		}

		conversation.parseConversationNodes(filein);
		filein.close();
		agent.conversationPath.add(INITIALIZATION_NODE);
	}

	/**
	 * The program simulates a conversational agent through console output that will
	 * send messages to a user, receive their input, and decide what to do with that
	 * input.
	 * 
	 * @param args
	 * @throws IOException
	 * @throws URISyntaxException
	 */
	public static void main(String[] args) {
		Scanner scanner = new Scanner(System.in);

		conversation = new ConversationGraph();
		String names[] = { "Bob", "Tom", "Bill", "Sarah", "Alex", "Jessie", "Calvin", "Janet" };
		Math.random();
		int rand = (int) (Math.random() * names.length);
		agent = new Agent(names[rand]);
		
		System.out.println("WELCOME to Electronics Emporium! One of our customer service representatives will be with your shortly...\n");

		try {
			initializeConversation();
			boolean loop = true;
			while (loop) {
				agent.execute(conversation, scanner);
			}
			agent.speak("Thank you very much for using our servce. Take care and have a fantastic day!");
			scanner.close();
		} catch (IOException e) {
			System.out.println(
					"*** CRITICAL ERROR: The data file for the conversational agent cannot be found. Aborting program.");
		} catch (URISyntaxException e) {
			System.out.println(
					"*** CRITICAL ERROR: The data file for the conversational agent cannot be found. Aborting program.");

		}
	}

	/**
	 * Used only as a demonstration of how to use the Agent and ConversationNode
	 * classes.
	 * 
	 * @param node
	 * @param input
	 * @return
	 */
	@Deprecated
	public static String getResponse(ConversationNode node, String input) {
		String temp;
		if (node.getID().equals("Root")) {
			temp = node.getResponses().get(0);
			temp = temp.replace("*name*", agent.agentName);
			agent.conversationPath.add("Reply");
			return temp;
		} else if (node.getID().equals("Reply")) {
			agent.userInfo.put("name", input);
			temp = node.getResponses().get(0);
			temp = temp.replace("*user_name*", agent.userInfo.get("name"));
			return temp;
		} else {
			// Default response selection, outputs a random response if there are multiple.
			int numResponses = node.getResponses().size();
			int selectedResponse = 0;
			if (numResponses > 1) {
				selectedResponse = (int) Math.random() * numResponses;
			}
			temp = node.getResponses().get(selectedResponse);
		}

		if (temp.isEmpty()) {
			return "ERROR";
		} else
			return temp;
	}

	/**
	 * Used only as an example to illustrate simple data storage within a
	 * ConversationGraph by manually initializing a couple ConversationNode objects
	 */
	@Deprecated
	public static void exampleInit() {

		// Create root node with id "Root"
		ConversationNode root_node = new ConversationNode("Root");

		// Add a response for the Root node
		root_node.addResponse("Hello my name is *name*. What is your name?");

		// adds the Root node to the conversation tree
		conversation.add(root_node);

		// Create a new step for the conversation
		ConversationNode reply_node = new ConversationNode("Reply");

		// Add a response for the Reply node
		reply_node.addResponse("Nice to meet you *user_name*.");

		// Define Reply's parent node to be the Root node
		reply_node.addParent("Root");
		conversation.get("Root").addChild("Reply");

		// add the Reply node to the conversation tree
		conversation.add(reply_node);
	}

}
