# Project Plan: A Conversational Agent

### Apurva Narayan

### COSC 310

### University of British Columbia, Okanagan Campus

- Nate Charlesworth
- Christopher Cheung
- Connor Kingsmill
- Felipe Portela
- Daniel Walls

 The purpose of this project is to implement a pseudo-intelligent conversational agent (a program) that will respond to user input (text input from a human through the client). As true understanding of human input and intelligent response by a software agent is beyond the scope of our area of study, the program will therefore attempt to mimic intelligence through pattern matching and keyword recognition: hence the 'pseudo-intelligence' moniker assigned to the agent. The goal of the agent&#39;s is to have reasonable replies to the user&#39;s input and respond generated sentences to the user. For our implementation of the agent we assigned it the specific role of a call centre agent, focusing on being able to help with troubleshooting issues, handling customer complaints, assisting with product returns, etcetera. This specific role was decided once again to contain the scope of the project.

## Technical Overview:

 Our strategy to accomplish the goals of this project is to use three types of Objects we created. These object types will be implemented as Java classes.

  1. **Agent**
  
     The Agent object represents the conversation agent. This object is used to store information about the current conversation. Additionally the Agent object has functions which enable the agent to process input and select what it will output. These include functions to determine if the user input matches any relevant keywords, selecting and formatting a reply when keywords are matched, and choosing how the agent replies when it is unable to understand the input.
  2. **ConversationNode**
  
      This object represents one fragment (or node) of a conversation. The ConversationNode is used for storing the agent's possible replies to the user given a particular conversation-state. The nodes also define the possible flow of the conversation as each ConversationNode stores which nodes may come before or after itself in a conversation (referred to as parents and children).
  3. **ConversationGraph**
  
      The ConversationGraph object is used as a data structure for storing the ConversationNodes that could exist in a conversation. Because the nodes have specific possible parents and children, the ConversationGraph is essentially a directed graph. The ConversationGraph type also has functions which allow traversal along the nodes and accessing data from nodes in the graph. Finally the graph object can parse a .txt file with a specific format to construct ConversationNodes and add them to the graph.

### Understanding the Objects
Below is a simple example to illustrate these objects in a conversation. It is an excerpt of a conversation between a customer and a waiter at a restaurant. The square brackets conatin a description of how the next to reply could choose what to say.

  ```
  Waiter: "Hello, my name is Brian and I'll be your server tonight. How are you doing today?"
  [The waiter asks an opening question, the cutomer chooses his answer based on how he is *doing today*]
  Cust.: "I'm doing well, thanks Brian."
  [Because the customer answered that he is *well*, the waiter continues to the next question he planned to ask.]
  Waiter: "Can I get you anything to start with? Maybe something to drink?"
  [Now the customer chooses what he would like to *drink*.]
  Cust.: "I'll have a beer please."
  [Because the customer mentioned *beer* the waiter will need him to be more specific, but the waiter will use the category of beer to get a specific answer.]
  Waiter: Is there a specific beer you were looking for? Would you like to hear what we have on tap?
  ...
  ```
To understand the object types we defined for this project, consider the customer and the waiter to be Agents. Both of them keep track of what the other says in the conversation and they store some information the other shares. For example, the customer noted the waiter's name when he told it to him. Each of them decides their reply based on some **keywords** that the other said, shown above between asterisks (\*) in the square brackets. For instance, when the waiter asked if the customer was interested in what's on tap because the customer mentioned **beer**.

Next we consider ConversationNodes. To understand them think of each verbal exchange between the two as a ConversationNode. We can see that the possible replies one could say can be grouped together based on some keywords that could trigger one to say it. For example, the customer said *"I'll have a beer please"* when asked about drinks, but another time he might reply "I'll have a stout" or "I'll just have water", and so on. We can also see that each of these nodes has an ordering to them which keeps the flow of the conversation. The waiter always starts with an introduction and the first thing he asks about is drinks. If we think of the waiter as an Agent, the first ConversationNode of his conversation might be *'introduction'* followed by *'ask about drinks'*.

Finally we have the ConversationGraph, which represents ***everything that could be said in this conversation***. Of course, the number of possible prompts and replies between two intelligent people is infinite, but our aim is a 'pseudo-intelligent' agent. Instead of using context to determine what a person could say in a conversation, we chose a specific role for the agent. That role allows us to control the scope of the conversation and the ConversationGraph allows us to build, store, and access a modular conversation.


With this system it is easy to improve the conversation agent's ability to mimic a person holding a real conversation. The ConversationGraph can be continually expanded with new ConversationNodes, each node having many possible children and different keywords leading to unique responses. The method that the Agent selects individual replies based on keywords and other patterns can be improved, variation could be added to the responses, and lifelike reactions to input that the Agent is unable to interpret can be programmed.



## Development Branches:

### DialogueGraph: (FINISHED)

Dedicated to creating the conversation graph that the conversation agent will use. Visual representations of conversation graphs will use UML class diagrams made in Astah. The data for the conversation graph will be contained within a .txt file which will be parsed by the program.

#### Visual Representation via Astah UML:

UML classes will represent individual nodes on the conversation graph. The name of that UML class will represent the nodeID. Attributes of the UML class will represent the attributes of that particular conversation node, consisting of: parentIDs, keywords, and responses. To create a new class diagram:
- Open Astah.
- Navigate to File -> New. Then, Diagram -> Class Diagram.

A blank class diagram window should appear on the right. To create a new UML class to represent a conversation node:

- Select the 'Class' icon from the top-left of the toolbar of the newly-opened class diagram window.
- With the 'Class' tool selected, click anywhere in the class diagram window to add the "node".
- The 'Name' field of the UML class will represent the nodeID.

The UML class representing the conversation node will initially be emply. To add attributes:

- Select the UML class with your mouse. A window will open at the bottom-left of the program. Select the 'Attribute' tab if it isn't already selected.
- Select the 'Add' button at the bottom-left toolbar of the newly-opened window.
- An attribute will be added to the UML class. Fill it out accordingly.
- Repeat the previous steps to add any number of "nodes" to the conversation graph as necessary.

To connect the "nodes" with an "edge" to create a graph:

- Hover your mouse to the bottom of the parent node. A small triangle will appear: click it.
- A small window will appear. Select the line segment you'd like to represent the edge, and connect it to the top of the child node.

You are free to move the nodes around, and the connections will remain intact, regardless of position. When saving Astah UML diagrams, be sure to place them in the /docs directory within the project directory. Each Astah UML diagram will represent the entire conversation graph for a particular agent personality.

#### Data File Containing Conversation Graph Data:

While Astah UML is useful for a visual representation of the conversation graph during design, the actual data that will need to be parsed will be contained within a .txt file. These files should share the same name as their Astah UML diagrams, for example: exampleGraph.asta and exampleGraph.txt. The format for these data files must adhere to the following structure:

```
nodeID:
parentIDs:
keywords:
responses:
##
```

The ## line signifies we are done parsing the current node and moving to the next node. An example of two nodes utilizing this format:

```
nodeID: "root"
parentIDs: null
keywords: null
responses: "Hello my name is *name*. What is your name?"
##
nodeID: "reply"
parentIDs: "root"
keywords: "name"
responses: "Nice to meet you *user_name*." , "I hope I can help you today *user_name*."
##
```

Currently, variables in responses (such as username, bot name, etc.) will be surrounded by asterisks to be parsed by IOParsing. Do note that **parent nodes must appear above child nodes in the data file**, otherwise GraphGeneration will have an exception. The location of a child node in respect to its parent nodes in the data file does not matter aside from it **needing to be below the parent node**. Please ensure you follow this format.

### GraphGeneration: (FINISHED)

This branch is focused on populating a ConversationGraph data structure with ConversationNodes parsed from data files. The data files for storing the ConversationNodes are text files following the format explained above. The ConversationGraph is generated at program launch and contains all conversation paths that the conversation agent can traverse depending on the user's input.

### IOParsing (FINISHED)

Dedicated to taking user input and searching the ConversationGraph to find the appropriate response from the conversation agent. This handles the interaction between the user and the conversation agent, traversing the conversation graph appropriately based on these interactions.

### UserInterface (Dropped)

The interface for which the conversation agent and user will interact. Whether a simple command-line interaction, or a more robust GUI will be utilized, has yet to be determined.

NOTE: This feature branch has been cancelled. The user interface will be command-line input and output.
## Project SDLC: Agile: eXtreme Programming (XP)

 We have chosen XP as the SDLC for this project as its scope is very development-oriented, which lends itself well to the XP paradigm. As our team-size is quite small, XP also seems relevant as there is less inherent micro-management and a focus on collaboration from the entire team to produce the best software possible. Since XP adapts a test-driven development (TDD) philosophy, we find this appealing as the success of the project will largely depend on whether the agent responds correctly to user input; by ensuring complete testing coverage through TDD, this will provide us the desired result. Further, as our team is likely to have different coding style, structure, and naming practices, an iterative refactoring process is beneficial to ensure our software remains uniform and understandable for both those developing the software itself or viewing the source later. As the requirements of the project are likely to change throughout the development process as we become more familiar with the design and implementation necessary for a functional conversational agent, something our team has no experience with, the XP approach seems most applicable as it allows for easy incorporation of changes when our requirements undoubtedly change throughout the process of the project.

## Phases:

### Planning:

  1. Project outline
  2. Generate user stories (emulate them as our customer (professor) will not be providing them for this project)
  3. Requirements gathering for development iterations from user stories (3.2 Testing requirements) (3.3 Software requirements)
  4. Agree on system naming standards for classes, methods, variables, and uniformity of code style and structure, as well as documentation uniformity
  5. Formalize repository structure and create main iteration branches
  6. Outline task breakdown structure
  7. Form programming pairs and sign-up for task iterations

### Design:

  1. Define iteration features with simplest design
  2. Generate CRC cards for initial object identification

### Development:

  1. Test-driven development (1.2 Design tests for feature iterations) (1.3 Implement code to fulfill all iteration tests)
  2. Ensure required documentation is complete
  3. Release code and merge to repository
  4. Refactor repository code
  5. Repeat development phase for each feature iteration until all are implemented

### Review:

  1. Emulate customer acceptance tests through peer-review of implemented feature iterations
  2. Revise necessary project requirements
  3. Re-iterate through process of design-development-review cycle for any revised or additional requirements
  4. Finalize project report
  5. Release software


--------------------------------------------------------------------------------------------------------------------------------
A3 Information (End of ReadMe Information) (5 Points):

Features I implemented:
 - Simple GUI (5 Points)
 - Extra Topic (2 Points)
 - Canned Responses (3 Points)
 - Spelling Mistake Tests (5 Points)
 - Language Toolkit -- Synonyms via Princeton's WordNet and MIT's JWI api (10 Points)
 
### Simple GUI as an Executable (5 Points)

I have written a GUI for the program. Simply run the executable and the program will load as a GUI. Press the "Enter" button, or press enter on your keyboard while you can actively type in the box. Implementing the GUI meant rewriting the code from being Scanner based to event based.

Benefit of a nice GUI via an Executable File is that you don't need to install Eclipse or another IDE to run the program. 

### Extra Topic (2 Points)

I wrote an extra topic. You can see it in the Astah map file as "BotInfo", or go down the "Bot" branch.

I felt that if we had written this program for others to see that it would be nice for people to know a little about the team. That, and I couldn't think of much else the bot could deal with directly with a user from A2.
### Canned Responses (3 Points)

We already had 5 canned responses, so I added a 6th one and made sure that you will never get the same canned response twice in a row. If ever the bot is confused, you'll know for sure.

The lack of repetition in the bot's confused responses makes it feel more real, improving the conversation.

### Spell Check (5 Points)

Checks for switched characters, and 1 extra or missing character. If you hit enough characters in the right order, the program assumes you meant the current keyword and continues down that conversation. It does mean some weird situations, like "ees" counting as "yes," however it's an improvement over our previous system. Before, we tested to see if your characters had an index in the sentence. With larger words this was fine, but if you had typed in "Arnold" and the keyword was "no", the program assumed you meant no. That's not an issue anymore.

So not only does it fix issues with a previous system, it also can understand simple spelling mistakes, so users don't need to waste time trying to fix typos, if they happen.

### Language Toolkit -- Synonym recognition (10 Points)

Using the Princeton Wordnet (https://wordnet.princeton.edu/), and the MIT JWI (https://projects.csail.mit.edu/jwi/), I have coded the program to check for synonyms of the keywords and see if they match the user input. 

It allows for the user to have more variety in their speech. So instead of using "Order", you could say "I wanted help ordering a product", which the language toolkit enables. The conversation feels much more natural.

## Limitations of Program:
- User input containing many key words may trigger incorrect pattern response.
- Currently the agent handles conflicts where user input may trigger traversal to different nodes, but not in an intelligent manner. The Agent will jump to the first keyword matched
- I don't like that you need to have the dict folder in the same directory as "A3 Chatbot.jar" when running the executable, but it's the only way I could find to make it work.

## Level 0 DFD

## Level 1 DFD



## Sample Output


## 5 Features to Extract into an API
  1. GUI -- Someone could extract the setupGUI() function from ConversationMain and use it for their own GUI, so long as they code their program as being event based rather than Scanner based.
  2. Spell Checking -- compareWords() and related functions could be cut into an API with minimal changes.
  3. Conversation Graph/Nodes -- One could take the classes ConversationGraph and ConversationNodes to be used with a  DialogueGraph.txt, and they could write their own functions for accessing them.
  4. Implementation of JWI/Wordnet -- Someone could copy my implementation of JWI/Wordnet to use it their own way.
  5. Agent/Bot -- If someone were to alter DialogueGraph but leave everything else the same, they could write text for their own bot. So the program as a whole works as an API that way.



## Installation:

 1. Download the '/built program/A3 Chat Bot' folder from this repository
 2. Make sure you have the jar executable and the dict folder in the same folder
 3. Run 'A3 Chatbot.jar' to open the conversation agent as a Java program

### **NOTE:**
You must have Java installed on your machine or you will be unable to open the program. If you have issues running the program please check that the path to your Java installation is set.

#### Steps to test your Java installation on Windows:
 1. Open 'cmd.exe'
 2. Type "where java" and press the Enter key
    - This should return a filepath to the Java installation
    - Example command and desired output:  
    ```
    C:\Users\example>where java⏎
    C:\Program Files\Java\jdk1.8.0_181\bin\java.exe
    ```
 3. If you receive an error, you must either:
    - Install Java (https://www.java.com/en/download/)    ***or***
    - If you have Java installed: Configure the system PATH variable

For help setting the Java PATH system variable:
 - https://www.java.com/en/download/help/path.xml
