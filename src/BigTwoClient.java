import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

/**
 * The BigTwoClient class is used to model a Big Two card game.
 * @author Zhou Jingran
 *
 */
public class BigTwoClient implements CardGame, NetworkGame {
    private int numOfPlayers = 0; // an integer specifying the number of players
    private Deck deck; // a deck of cards
    private ArrayList<CardGamePlayer> playerList; // a list of players
    private ArrayList<Hand> handsOnTable; // a list of hands played on the table
    private int playerID; // an integer specifying the player index of the local player
    private String playerName; // a string specifying the name of the local player
    private String serverIP; // a string specifying the IP address of the game server
    private int serverPort; // an integer specifying the TCP port of the game server
    private Socket sock; // a socket connection to the game server
    private ObjectOutputStream oos; // an ObjectOutputStream for sending messages to the server
    private int currentIdx; // the index of the current player
    private BigTwoTable table; // a Big Two table which builds the GUI for the game and handles all user actions.
    private boolean connected = false; // a boolean value indicating the connection status

    /**
     * A constructor for creating a Big Two card game.
     */
    public BigTwoClient() {
        // create 4 players and add them to the list of players
        playerList = new ArrayList<CardGamePlayer>();
        for (int i = 0; i < 4; i++) {
            playerList.add(new CardGamePlayer());
        }
        // create a BigTwoTable for GUI and user actions
        table = new BigTwoTable(this);

        handsOnTable = new ArrayList<Hand>();
        serverPort = 2396;

        // make a connection to the game server
        makeConnection();
    }

    /**
     * A method for checking if the client is already connected to the server.
     * @return true if the client is connected to the server
     */
    public boolean isConnected() {
        return connected;
    }

    /**
     * A method for getting the number of players.
     * @return the number of players
     */
    public int getNumOfPlayers() {
        return numOfPlayers;
    }

    /**
     *  A method for retrieving the deck of cards being used.
     * @return the deck of cards being used
     */
    public Deck getDeck() {return deck;}

    /**
     * A method for retrieving the list of players.
     * @return the list of players
     */
    public ArrayList<CardGamePlayer> getPlayerList() {return playerList;}

    /**
     * A method for retrieving the list of hands played on the table.
     * @return the list of hands played on the table
     */
    public ArrayList<Hand> getHandsOnTable() {return handsOnTable;}

    /**
     *  A method for retrieving the index of the current player.
     * @return the index of the current player
     */
    public int getCurrentIdx() {return currentIdx;}

    /**
     * A method for starting the game with a (shuffled) deck of cards supplied as the argument. It implements the Big Two game logics.
     * @param deck
     * 		a shuffled deck of cards
     */
    public void start(Deck deck) {
        this.deck = deck;
        // remove all the cards from the players as well as from the table
        for (CardGamePlayer p : playerList) {
            p.removeAllCards();
        }
        handsOnTable = new ArrayList<Hand>();

        // distribute the cards to the players
        for (int i = 0; i < this.deck.size(); i++) {
            playerList.get(i/13).addCard(getDeck().getCard(i));
        }

        // identify the player who holds the 3 of Diamonds
        // and set the activePlayer to the playerID
        for (int i = 0; i < playerList.size(); i++) {
            playerList.get(i).sortCardsInHand();
            if (playerList.get(i).getCardsInHand().contains(new BigTwoCard(0, 2))) {
                currentIdx = i;
                table.setActivePlayer(playerID); // only show cards of the local player
            }
        }
        table.repaint();
        table.printMsg(playerList.get(currentIdx).getName() + "'s turn: ");
    }

    /**
     * A method for making a move by a player with the specified playerID using the cards specified by the list of indices.
     * @param playerID the playerID of the player who makes the move
     * @param cardIdx the cards specified by the list of indices
     */
    @Override
    public void makeMove(int playerID, int[] cardIdx) {
        CardGameMessage makeMoveMsg = new CardGameMessage(6, -1, cardIdx); // MOVE type
        sendMessage(makeMoveMsg);
    }

    /**
     * A method for checking the move made by the current player given the list of the indices of the cards selected by him/her.
     * @param cardIdx the list of the indices of the cards selected by the current
     */
    @Override
    public void checkMove(int playerID, int[] cardIdx) {
        // form a hand
        CardList cardsSelected = new CardList();
        CardGamePlayer currentPlayer = playerList.get(playerID);
        for (int i = 0; cardIdx.length != 0 && i < cardIdx.length; i++) {
            cardsSelected.addCard(playerList.get(getCurrentIdx()).getCardsInHand().getCard(cardIdx[i]));
        }
        // check if is legal move
        boolean isLegalMove = false;
        if (handsOnTable.size() == 0) {
            // first player
            if (cardsSelected.contains(new BigTwoCard(0, 2)) &&
                    composeHand(currentPlayer, cardsSelected) != null) {
                isLegalMove = true;
            }
        } else {
            // not the first player
            if (cardIdx.length != 0) {
                if ((composeHand(currentPlayer, cardsSelected) != null) &&
                        (handsOnTable.get(handsOnTable.size()-1).getPlayer() == currentPlayer ||
                                composeHand(currentPlayer, cardsSelected).beats(handsOnTable.get(handsOnTable.size()-1)))) {
                    isLegalMove = true;
                }
            } else {
                if (handsOnTable.get(handsOnTable.size()-1).getPlayer() != currentPlayer) {
                    // PASS
                    isLegalMove = true;
                }
            }
        }
        //  remove from player, add to table
        if (isLegalMove) {
            if (cardIdx.length != 0) {
                currentPlayer.removeCards(cardsSelected);
                handsOnTable.add(composeHand(currentPlayer, cardsSelected));
                table.printMsg("{" + handsOnTable.get(handsOnTable.size() - 1).getType() + "} ");
                table.printMsg(handsOnTable.get(handsOnTable.size()-1).toString());
            } else {
                table.printMsg("{Pass}");
            }

            if (endOfGame()) {
                // if end of game, disable GUI and display the result
                table.repaint();
                table.disable();
                table.paintEndOfGame();
            } else {
                // if not end of game, next player
                currentIdx = (getCurrentIdx() + 1) % 4;
                table.printMsg(playerList.get(currentIdx).getName() + "'s turn:");
            }
        } else {
            // if not legal move
            // display message
            // new input
            table.printMsg("Not a legal move!!!");
            table.resetSelected();
        }
        table.repaint();
    }

    /**
     *  A method for checking if the game ends.
     * @return true if the game ends
     */
    public boolean endOfGame() {
        if (numOfPlayers == 4 && !(playerList.get(0).getCardsInHand().size() == 0
                && playerList.get(1).getCardsInHand().size() == 0
                && playerList.get(2).getCardsInHand().size() == 0
                && playerList.get(3).getCardsInHand().size() == 0)) {
            return playerList.get(0).getCardsInHand().size() == 0
                    || playerList.get(1).getCardsInHand().size() == 0
                    || playerList.get(2).getCardsInHand().size() == 0
                    || playerList.get(3).getCardsInHand().size() == 0;
        }
        return false;
    }

    /**
     * A method for returning a valid hand from the specified list of cards of the player.
     * @param player the owner of the list of cards (the hand)
     * @param cards a list of cards that will compose a hand
     * @return a valid hand or null if no valid hand can be composed
     */
    public static Hand composeHand(CardGamePlayer player, CardList cards) {
        if (cards.size() == 1) {
            Single single = new Single(player, cards);
            if (single.isValid()) {
                return single;
            }
        }
        if (cards.size() == 2) {
            Pair pair = new Pair(player, cards);
            if (pair.isValid()) {
                return pair;
            }
        }
        if (cards.size() == 3) {
            Triple triple = new Triple(player ,cards);
            if (triple.isValid()) {
                return triple;
            }
        }
        if (cards.size() == 5) {
            Quad quad = new Quad(player, cards);
            if (quad.isValid()) {
                return quad;
            }
            FullHouse fullHouse = new FullHouse(player, cards);
            if (fullHouse.isValid()) {
                return fullHouse;
            }
            StraightFlush straightFlush = new StraightFlush(player, cards);
            if (straightFlush.isValid()) {
                return straightFlush;
            }
            Straight straight = new Straight(player, cards);
            if (straight.isValid()) {
                return straight;
            }
            Flush flush = new Flush(player, cards);
            if (flush.isValid()) {
                return flush;
            }
        }
        return null;
    }

    /**
     * A method for getting the playerID (i.e., index) of the local player.
     * @return the playerID (i.e., index) of the local player
     */
    @Override
    public int getPlayerID() {
        return playerID;
    }

    /**
     * A method for setting the playerID (i.e., index) of the local player.
     * @param playerID the playerID (i.e., index) of the local player
     */
    @Override
    public void setPlayerID(int playerID) {
        this.playerID = playerID;
    }

    /**
     * A method for getting the name of the local player.
     * @return the name of the local player
     */
    @Override
    public String getPlayerName() {
        return playerName;
    }

    /**
     * A method for setting the name of the local player.
     * @param playerName the name of the local player
     */
    @Override
    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    /**
     * A method for getting the IP address of the game server.
     * @return the IP address of the game server
     */
    @Override
    public String getServerIP() {
        return serverIP;
    }

    /**
     * A method for setting the IP address of the game server.
     * @param serverIP the IP address of the game server
     */
    @Override
    public void setServerIP(String serverIP) {
        this.serverIP = serverIP;
    }

    /**
     * A method for getting the TCP port of the game server.
     * @return the TCP port of the game server
     */
    @Override
    public int getServerPort() {
        return serverPort;
    }

    /**
     * A method for setting the TCP port of the game server.
     * @param serverPort the TCP port of the game server
     */
    @Override
    public void setServerPort(int serverPort) {
        this.serverPort = serverPort;
    }

    /**
     * A method for making a socket connection with the game server.
     */
    @Override
    public void makeConnection() {
        table.inputNameAndIP();

        // make a socket connection with the game server
        try {
            sock = new Socket(serverIP, serverPort);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // create an ObjectOutputStream for sending messages to the game server
        try {
            oos = new ObjectOutputStream(sock.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }

        // create a thread for receiving messages from the game server
        Thread receiveThread = new Thread(new ServerHandler());
        receiveThread.start();

        // send a message of the type JOIN to the game server
        sendMessage(new CardGameMessage(1, -1, playerName));

        // send a message of the type READY to the game server
        sendMessage(new CardGameMessage(4, -1, null));

        connected = true;
    }

    /**
     * A method for parsing the messages received from the game server
     * @param message the message received from the game server
     */
    @Override
    public void parseMessage(GameMessage message) {
        // REFER TO THE GENERAL BEHAVIOUR OF THE CLIENT
        switch (message.getType()) {
            case CardGameMessage.PLAYER_LIST:
                table.printMsg("Connected to server at " + serverIP);
                // set the playerID of the local player
                setPlayerID(message.getPlayerID());
                // update the names in the play list
                String[] nameList = (String[]) message.getData();
                numOfPlayers = 0;
                for (int i = 0; i < playerList.size(); i++) {
                    playerList.get(i).setName(nameList[i]);
                    if (playerList.get(i).getName() != null) {numOfPlayers ++;}
                }
                break;
            case CardGameMessage.JOIN:
                // add a new player to the player list by updating his/her name
                playerList.get(message.getPlayerID()).setName((String) message.getData());
                numOfPlayers ++;
                break;
            case CardGameMessage.FULL:
                // display a message in the text area of the BigTwoTable that the server is full and cannot join the game
                table.printMsg("Server is FULL! You shall not pass.");
                table.printMsg("Lose connection to the server.");
                connected = false;
                break;
            case CardGameMessage.QUIT:
                playerList.get(message.getPlayerID()).setName("");
                // if a game is in progress, stop the game and send a message type ready to the server.
                if (!endOfGame()) {
                    table.disable();
                    sendMessage(new CardGameMessage(4, -1, null));
                }
                numOfPlayers --;
                break;
            case CardGameMessage.READY:
                table.printMsg("Player " + message.getPlayerID() + " - " + playerList.get(message.getPlayerID()).getName() + " is ready.");
                break;
            case CardGameMessage.START:
                table.reset();
                table.printMsg("All players are ready. Game starts.");
                start((Deck) message.getData());
                break;
            case CardGameMessage.MOVE:
                // check the move made by the specified player
                checkMove(message.getPlayerID(), (int[]) message.getData());
                break;
            case CardGameMessage.MSG:
                table.printChatMsg((String) message.getData());
                break;
            default:
                table.printMsg("Wrong message type: " + message.getType());
                break;
        }
        table.repaint();
    }

    /**
     * A method for sending the specified message to the game server
     * @param message the specified
     */
    @Override
    public void sendMessage(GameMessage message) {
        try {
            oos.writeObject(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * A class for receiving and parsing messages from the server.
     */
    class ServerHandler implements Runnable {
        private ObjectInputStream inputStream;

        /**
         * The constructor for building a new ServerHandler class
         */
        ServerHandler() {
            try {
                inputStream = new ObjectInputStream(sock.getInputStream());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        /**
         * A method for receiving and parsing messages from the server
         */
        @Override
        public synchronized void run() {
            CardGameMessage message;
            try {
                while ((message = (CardGameMessage) inputStream.readObject()) != null) {
                    parseMessage(message);
                }
            } catch (Exception e) {
                e.printStackTrace();
                connected = false;
                table.reset();
                table.printMsg("Lost connection with the server.");
                makeConnection();
            }
        }
    }

    /**
     * A method for starting a Big Two card game.
     * @param args string arguments
     */
    public static void main(String[] args) {
        BigTwoClient bigTwoClient = new BigTwoClient();
    }
}
