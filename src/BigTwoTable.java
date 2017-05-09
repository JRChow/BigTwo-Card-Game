import javax.swing.*;
import javax.swing.text.DefaultCaret;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;


/**
 * The BigTwoTable class is used to build a GUI for the Big Two card game and handle all user actions.
 * @author Zhou Jingran
 */
public class BigTwoTable implements CardGameTable {
    private BigTwoClient client; // a card game associates with this table.
    private boolean[] selected; // a boolean array indicating which cards are being selected.
    private int activePlayer; // an integer specifying the index of the active player.
    private JFrame frame; // the main window of the application.
    private JPanel bigTwoPanel; // a panel for showing the cards of each player and the cards played on the table.
    private JButton playButton; //  a “Play” button for the active player to play the selected cards.
    private JButton passButton; //  a “Pass” button for the active player to pass his/her turn to the next player.
    private JTextArea msgArea; // a text area for showing the current game status as well as end of game messages.
    private JTextArea chatArea; // NEW showing the chat messages sent by the players
    private JTextField inputField; // NEW for players to send out chat messages
    private Image[][] cardImages; // a 2D array storing the images for the faces of the cards
    private Image cardBackImage; // an image for the backs of the cards.
    private Image[] avatars; // an array storing the images for the avatars.

    /**
     * A constructor for creating a BigTwoTable
     * @param game a reference to a card game associates with this table
     */
    public BigTwoTable(BigTwoClient client) {
        this.client = client;

        selected = new boolean[13];

        for (int i = 0; i < 13; i++) {
            selected[i] = false;
        }

        activePlayer = client.getPlayerID();


        frame = new JFrame();

        avatars = new Image[4];
        avatars[0] = new ImageIcon("jerome.png").getImage();
        avatars[1] = new ImageIcon("snowy.png").getImage();
        avatars[2] = new ImageIcon("jiang.png").getImage();
        avatars[3] = new ImageIcon("hitler.png").getImage();

        cardImages = new Image[4][13];
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 13; j++) {
                String name = i + "_" + j + ".gif";
                cardImages[i][j] = new ImageIcon(name).getImage();
            }
        }
        cardBackImage = new ImageIcon("back.png").getImage();

        playButton = new JButton("Play");
        playButton.addActionListener(new PlayButtonListener());
        passButton = new JButton("Pass");
        passButton.addActionListener(new PassButtonListener());



        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JMenuBar menuBar = new JMenuBar();
        JMenu menu = new JMenu("Menu");
        JMenuItem connectMenuItem = new JMenuItem("Connect");
        connectMenuItem.addActionListener(new ConnectMenuItemListener());
        JMenuItem quitMenuItem = new JMenuItem("Quit");
        quitMenuItem.addActionListener(new QuitMenuItemListener());
        menu.add(connectMenuItem);
        menu.add(quitMenuItem);
        menuBar.add(menu);
        frame.setJMenuBar(menuBar);

        JPanel lowerPanel = new JPanel();
        lowerPanel.setBackground(Color.lightGray);
        lowerPanel.setLayout(new BoxLayout(lowerPanel, BoxLayout.LINE_AXIS));

        // button panel
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(Color.lightGray);
        buttonPanel.add(playButton);
        buttonPanel.add(passButton);

        // message input panel
        JLabel inputLabel = new JLabel("Message:");
        inputField = new JTextField(37);
        inputField.addActionListener(new InputFieldListener());
        JPanel inputPanel = new JPanel();
        inputPanel.setBackground(Color.lightGray);
        inputPanel.add(inputLabel);
        inputPanel.add(inputField);


        Dimension minSize = new Dimension(0, 0);
        Dimension prefSize = new Dimension(1, 40);
        Dimension maxSize = new Dimension(Short.MAX_VALUE, 100);

        lowerPanel.add(new Box.Filler(minSize, prefSize, maxSize));
        lowerPanel.add(buttonPanel);
        lowerPanel.add(new Box.Filler(minSize, prefSize, maxSize));
        lowerPanel.add(inputPanel);

        frame.add(lowerPanel, BorderLayout.SOUTH);

        msgArea = new JTextArea(10, 40);
        chatArea = new JTextArea(10, 40);
        msgArea.setEditable(false);
        chatArea.setEditable(false);
        JScrollPane scroller = new JScrollPane(msgArea);
        msgArea.setLineWrap(true);
        scroller.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        scroller.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scroller.setViewportView(msgArea);

        JScrollPane chatScroller = new JScrollPane(chatArea);
        chatArea.setLineWrap(true);
        chatScroller.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        chatScroller.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        chatScroller.setViewportView(chatArea);

        JPanel textAreaPanel = new JPanel();
        textAreaPanel.setBackground(Color.LIGHT_GRAY);
        textAreaPanel.setLayout(new BoxLayout(textAreaPanel, BoxLayout.Y_AXIS));
        textAreaPanel.add(scroller);
        textAreaPanel.add(chatScroller);

        frame.add(textAreaPanel, BorderLayout.EAST);

        bigTwoPanel = new BigTwoPanel();
        frame.add(bigTwoPanel, BorderLayout.CENTER);

        frame.setSize(1100, 730);
        frame.setVisible(true);

        repaint();
    }

    /**
     *  A method for the user to input name and server IP address
     */
    public void inputNameAndIP() {
        String name = JOptionPane.showInputDialog(frame, "What's your name?");
        client.setPlayerName(name);

        String serverIP = JOptionPane.showInputDialog(frame, "Server IP Address:", "127.0.0.1");
        client.setServerIP(serverIP);
    }


    /**
     * A method for printing chat messages
     * @param chatMsg a string representing one chat message
     */
    public void printChatMsg(String chatMsg) {
        chatArea.append(chatMsg + "\n");
        chatArea.setCaretPosition(chatArea.getDocument().getLength());
    }

    /**
     * A method for setting the index of the active player
     * @param activePlayer the index of the active player
     */
    public void setActivePlayer(int activePlayer) {
        this.activePlayer = activePlayer;
    }


    /**
     *  A method for getting an array of indices of the cards selected.
     * @return an array of indices of the cards
     */
    public int[] getSelected() {
        if (selected.length > 0) {
            int counter = 0;
            for (int i = 0; i < selected.length; i++) {
                if (selected[i]) {
                    counter ++;
                }
            }
            int[] indexSelected = new int[counter];
            counter = 0;
            for (int i = 0; i < selected.length; i++) {
                if (selected[i]) {
                    indexSelected[counter] = i;
                    counter ++;
                }
            }
            return indexSelected;
        }
        return null;
    }

    /**
     * A method for resetting the list of selected cards.
     */
    public void resetSelected() {
        for (int i = 0; i < selected.length; i++) {
            selected[i] = false;
        }
    }

    /**
     * A method for printing end-of-game messages
     */
    public void paintEndOfGame() {
        if (client.endOfGame()) {
            String endMessage = "";
            for (int i = 0; i < client.getPlayerList().size(); i++) {
                if (client.getPlayerList().get(i).getCardsInHand().size() != 0) {
                    if (i == client.getPlayerID()) {
                        endMessage += "You have ";
                    } else {
                        endMessage += client.getPlayerList().get(i).getName() + " has ";
                    }
                    endMessage += client.getPlayerList().get(i).getCardsInHand().size() + " cards.\n";
                } else {
                    if (i == client.getPlayerID()) {
                        endMessage += "You win!\n";
                    } else {
                        endMessage += client.getPlayerList().get(i).getName() + " wins.\n";
                    }
                }
            }
            JOptionPane.showMessageDialog(frame, endMessage, "Game Ends", JOptionPane.INFORMATION_MESSAGE);
            reset();
            client.sendMessage(new CardGameMessage(4, -1, null));
        }
    }


    /**
     * A method for repainting the GUI.
     */
    public void repaint() {
        frame.repaint();

        if (client.getPlayerID() == client.getCurrentIdx()) {
            enable();
        } else {
            disable();
        }
    }

    /**
     * A method for printing the specified string to the message area of the GUI.
     * @param msg the string to be printed to the message area of the card game
     */
    public void printMsg(String msg) {
        msgArea.append(msg + "\n");
        msgArea.setCaretPosition(msgArea.getDocument().getLength());
    }

    /**
     * A method for clearing the message area of the GUI.
     */
    public void clearMsgArea() {
        msgArea.setText("");
    }

    /**
     * A method for resetting the GUI.
     */
    public void reset() {
        resetSelected();
        clearMsgArea();
    }

    /**
     * A method for enabling user interactions with the GUI.
     */
    public void enable() {
        bigTwoPanel.setEnabled(true);
        playButton.setEnabled(true);
        passButton.setEnabled(true);
    }

    /**
     * A method for disabling user interactions with the GUI.
     */
    public void disable() {
        bigTwoPanel.setEnabled(false);
        playButton.setEnabled(false);
        passButton.setEnabled(false);
    }

    /**
     * A customized JPanel for displaying cards, players and other game information.
     */
    class BigTwoPanel extends JPanel implements MouseListener {
        private int columnSpace = 130; // space between columns
        private int rowSpace = 30; // space between rows

        /**
         * A constructor for registering the class with itself as a MouseListener
         */
        public BigTwoPanel() {
            this.addMouseListener(this);
        }

        /**
         * A method for checking which card is selected by the mouse click.
         * @param e an event triggered by a mouseClick
         */
        @Override
        public void mouseClicked(MouseEvent e) {
            if (bigTwoPanel.isEnabled()) {
                CardGamePlayer currentPlayer = client.getPlayerList().get(activePlayer);

                int y = (e.getY()-15)/columnSpace;
                if (y == activePlayer && e.getY() <= 127+columnSpace*y && e.getX() >= 100 && e.getX() <= 100+rowSpace*(currentPlayer.getNumOfCards()-1)+73) {
                    int x;
                    if (e.getX() >= 130+rowSpace*(currentPlayer.getNumOfCards()-1)) {
                        x = currentPlayer.getNumOfCards()-1;
                    } else {
                        x = (e.getX()-100)/rowSpace;
                    }

                    boolean empty = false;
                    if ( (e.getY() > 112+columnSpace*y && selected[x] && !selected[x-1>=0?x-1:0] && e.getX() < 130+rowSpace*(currentPlayer.getNumOfCards()-1))
                            || (e.getY() > 15+columnSpace*y && e.getY() <= 30+columnSpace*y && selected[x-1>=0?x-1:0] && !selected[x] && e.getX() < 130+rowSpace*(currentPlayer.getNumOfCards()-1))
                            || (x == currentPlayer.getNumOfCards()-1 && e.getX() >= 130+rowSpace*x && e.getX() <= 143+rowSpace*x && e.getY() >= 112+columnSpace*y && selected[x] && !selected[x-1>=0?x-1:0])
                            || (x == currentPlayer.getNumOfCards()-1 && e.getX() >= 130+rowSpace*x && e.getX() <= 143+rowSpace*x && e.getY() <= 30 +columnSpace*y && !selected[x]) && selected[x-1>=0?x-1:0]) {
                        x = (x - 1 > 0 ? x - 1 : 0);
    //                    msgArea.append("case x-1 \n");
                    } else if ( (e.getX() <= 113+rowSpace*x && e.getY() <= 30+columnSpace*y && selected[x-2>=0?x-2:0] && !selected[x-1>=0?x-1:0] && !selected[x])
                            || (e.getX() <= 113+rowSpace*x && e.getY() >= 112+columnSpace*y && selected[x] && selected[x-1>=0?x-1:0] && !selected[x-2>=0?x-2:0]) ) {
                        x = (x-2>=0?x-2:0);
    //                    msgArea.append("case x-2");
                    } else if ( (e.getY() > 15+columnSpace*y && e.getY() <= 30+columnSpace*y && !selected[x] && !selected[x-1>=0?x-1:0])
                            || (e.getY() > 112+columnSpace*y && selected[x] && selected[x-1>=0?x-1:0])
                            || (x == currentPlayer.getNumOfCards()-1 && selected[x] && e.getY() >= 112+columnSpace*y && ( (e.getX() >= 130+rowSpace*x && e.getX() <= 143+rowSpace*x && !selected[x-1>=0?x-1:0])||(e.getX() > 143+rowSpace*x) ))
                            || (x == currentPlayer.getNumOfCards()-1 && !selected[x] && e.getY() <= 30 + columnSpace*y && ((e.getX() >= 143+rowSpace*x)||(e.getX() <= 143+rowSpace*x && e.getX() >=130+rowSpace*x && !selected[x-1>=0?x-1:0]) ) ) ) {
                        empty = true;
    //                    msgArea.append("case empty");
                    }
                    if (!empty) {
                        selected[x] = !selected[x];
                    }
                    this.repaint();
                }
            }
        }

        /**
         * A dummy method.
         * @param e an event triggered by pressing the mouse
         */
        @Override
        public void mousePressed(MouseEvent e) {}

        /**
         * A dummy method.
         * @param e an event triggered by releasing the mouse
         */
        @Override
        public void mouseReleased(MouseEvent e) {}

        /**
         * A dummy method.
         * @param e an event triggered when the mouse enters a certain area
         */
        @Override
        public void mouseEntered(MouseEvent e) {}

        /**
         * A dummy method.
         * @param e an event triggered when the mouse exits a certain area
         */
        @Override
        public void mouseExited(MouseEvent e) {}

        /**
         * A method for drawing the card game table
         * @param g graphics
         */
        @Override
        public void paintComponent(Graphics g) {
//            printMsg("paintComponent() is called!");
            //draw background picture
            Image bgImg = new ImageIcon("AnimusBright.jpg").getImage();
            g.drawImage(bgImg, 0, 0, null);

            // draw lines
            for (int i = 0; i < 4; i++) {
                g.drawLine(0, 132 + columnSpace * i, frame.getWidth(), 132 + columnSpace * i);
            }

            // draw icon and names
            for (int i = 0; i < 4; i++) {
                if (client.getNumOfPlayers() > 0 && client.getPlayerList().get(i).getName() != null && client.getPlayerList().get(i).getName().length() > 0) {
                    g.drawImage(avatars[i], 0, 30 + columnSpace * i, this);
                    if (i == client.getPlayerID()) {
                        Color color = i == client.getCurrentIdx()? Color.BLUE : Color.RED;
                        g.setColor(color);
                        g.drawString("You", 12, 120 + columnSpace * i);
                    } else {
                        Color color = i == client.getCurrentIdx()? Color.BLUE : Color.BLACK;
                        g.setColor(color);
                        g.drawString(client.getPlayerList().get(i).getName(), 12, 120 + columnSpace * i);
                    }
                    g.setColor(Color.black);
                }
            }

            // draw cards
            for (int i = 0; i < 4 && client.getNumOfPlayers() > 0; i++) {
                CardGamePlayer currentPlayer = client.getPlayerList().get(i);
                if (currentPlayer.getName() != null && currentPlayer.getName().length() > 0) {
                    for (int j = 0; j < currentPlayer.getCardsInHand().size(); j++) {
                        Image currentCard = cardImages[currentPlayer.getCardsInHand().getCard(j).getSuit()][currentPlayer.getCardsInHand().getCard(j).getRank()];
                        if (i == activePlayer) {
                            if (selected[j]) {
                                g.drawImage(currentCard, 100 + rowSpace * j, 15 + columnSpace * i, this);
                            } else {
                                g.drawImage(currentCard, 100 + rowSpace * j, 30 + columnSpace * i, this);
                            }
                        } else {
                            g.drawImage(cardBackImage, 100 + rowSpace * j, 30 + columnSpace * i, this);
                        }
                    }
                }
            }

            g.drawString("Played by", 3, 540);
            ArrayList<Hand> hands = client.getHandsOnTable();
            if (hands != null && hands.size() > 0) {
                int handsPlayerIdx = -1;
                String handsPlayerName = hands.get(hands.size()-1).getPlayer().getName();
                for (int x = 0; x < client.getPlayerList().size(); x++) {
                    if (client.getPlayerList().get(x).getName() == handsPlayerName) {
                        handsPlayerIdx = x;
                        break;
                    }
                }
                g.drawImage(avatars[handsPlayerIdx], 0, 547, this);
                if (handsPlayerIdx == client.getPlayerID()) {
                    handsPlayerName = "You";
                }
                g.drawString(handsPlayerName, 9, 633);
                for (int i = 0; i < hands.get(hands.size()-1).size(); i++) {
                    Image cardOnTable = cardImages[hands.get(hands.size()-1).getCard(i).getSuit()][hands.get(hands.size()-1).getCard(i).getRank()];
                    g.drawImage(cardOnTable, 100 + rowSpace * i, 535, this);
                }
            }
        }
    }

    /**
     * The ActionListener of the button 'Play'.
     */
    class PlayButtonListener implements ActionListener {

        /**
         * A method for checking and then resetting the selected cards.
         * @param e an event triggered by clicking the button 'Play'
         */
        @Override
        public void actionPerformed(ActionEvent e) {
            if (getSelected().length > 0) {
                client.makeMove(client.getPlayerID(), getSelected());
                resetSelected();
            }
        }
    }

    /**
     * The ActionListener of the button 'Pass'.
     */
    class PassButtonListener implements ActionListener {

        /**
         * A method for resetting the selected cards and check if the player can pass.
         * @param e an event triggered by clicking the button 'Pass'.
         */
        @Override
        public void actionPerformed(ActionEvent e) {
            resetSelected();
//            client.checkMove(client.getPlayerID(), getSelected());
            client.makeMove(client.getPlayerID(), getSelected());
        }
    }

    /**
     * The ActionListener of the menu item 'Connect'.
     */
    class ConnectMenuItemListener implements ActionListener {

        /**
         * A method for creating a new random deck of cards to restart the game, reset the active player, and enable GUI.
         * @param e an event triggered by clicking the menu item 'Connect'
         */
        @Override
        public void actionPerformed(ActionEvent e) {
            if (!client.isConnected()) {
                client.makeConnection();
            } else {
                printMsg("You are already connected to the server.\n");
            }
        }
    }

    /**
     * The ActionListener of the menu item 'Quit'.
     */
    class QuitMenuItemListener implements ActionListener {

        /**
         * A method for quitting the game.
         * @param e an event triggered by clicking the menu item 'Quit'.
         */
        @Override
        public void actionPerformed(ActionEvent e) {
            System.exit(0);
        }
    }

    /**
     * The ActionListener of the input text field.
     */
    class InputFieldListener implements ActionListener {

        /**
         * A method for sending chat messages to the server and reset the input text field.
         * @param e an event triggered by hit 'Enter' in the input text field
         */
        @Override
        public void actionPerformed(ActionEvent e) {
            if (inputField.getText().length() > 0) {
                client.sendMessage(new CardGameMessage(7, -1, inputField.getText()));
                inputField.setText("");
            }
        }
    }
}