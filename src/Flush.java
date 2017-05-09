/**
 * The Flush class is a subclass of the Hand class, 
 * and is used to model a hand of flush in a Big Two card game.
 * @author Zhou Jingran
 *
 */
public class Flush extends Hand {
	/**
	 * A constructor for building a hand of flush and setting the owner
	 * @param player the player who plays this flush
	 * @param cards the cards that compose the flush
	 */
    public Flush(CardGamePlayer player, CardList cards) {
        super(player, cards);
    }

    /**
     * An override method for checking if the flush can beat another hand of cards
     * @param hand the competing hand of this hand
     * @return true if the hand can beat the competing hand
     */
    @Override
    public boolean beats(Hand hand) {
        if (hand.getType() == "Straight") {
            return true;
        }
        if (hand.getType() == "Flush") {
            if (this.getTopCard().getSuit() > hand.getTopCard().getSuit()) {
                return true;
            }
            if (this.getTopCard().getSuit() == hand.getTopCard().getSuit()) {
                if (this.getTopCard().compareTo(hand.getTopCard()) == 1) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * A method for checking if the hand of flush is valid
     * @return true if the flush is valid, false otherwise
     */
    public boolean isValid() {
        if (this.size() == 5) {
            for (int i = 1; i < 5; i++) {
                if (this.getCard(0).getSuit() !=
                        this.getCard(i).getSuit()) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }
    
    /**
     * A method for returning a string specifying the type is Flush
     * @return a string that tells the type is Flush
     */
    public String getType() {return "Flush";}
}
