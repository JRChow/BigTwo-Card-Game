/**
 * The Hand class is a subclass of the CardList class, and is used to model a hand of cards.
 * @author Zhou Jingran
 *
 */
abstract class Hand extends CardList {
    private CardGamePlayer player; // the player who plays this hand

    /**
     * A constructor for building a hand with the specified player and list of cards.
     * @param player 
     * 				the player who plays this hand
     * @param cards 
     * 				the cards that compose this hand
     */
    public Hand(CardGamePlayer player, CardList cards) {
        this.player = player;
        for (int i = 0; i < cards.size(); i++) {
            this.addCard(cards.getCard(i));
        }
    }

    /**
     * A method for retrieving the player of this hand.
     * @return the player of this hand
     */
    public CardGamePlayer getPlayer() {return player;}

    /**
     * A method for retrieving the top card of this hand.
     * @return the top card of this hand
     */
    public Card getTopCard() {
        Card topCard = this.getCard(0);
        for (int i = 1; i < this.size(); i++) {
            if (this.getCard(i).compareTo(topCard) == 1) {
                topCard = this.getCard(i);
            }
        }
        return topCard;
    }

    /**
     * A method for checking if this hand beats a specified hand.
     * @param hand
     * 			the competing hand of this hand
     * @return true if this hand beats the specified hand, false otherwise
     */
    public boolean beats(Hand hand) {
        if (this.getType() == hand.getType()) {
            if (this.getTopCard().compareTo(hand.getTopCard()) == 1) {
                return true;
            }
        }
        return false;
    }

    /**
     * A method for checking if this is a valid hand.
     * @return true if this is a valid hand, false otherwise
     */
    abstract boolean isValid();
    
    /**
     * A method for returning a string specifying the type of this hand.
     * @return a string specifying the type of this hand
     */
    abstract String getType();
}
