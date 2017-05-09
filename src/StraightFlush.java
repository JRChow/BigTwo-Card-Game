/**
 * The StraightFlush class is a subclass of the Hand class, 
 * and is used to model a hand of straight flush in a Big Two card game.
 * @author Zhou Jingran
 *
 */
public class StraightFlush extends Hand {
	/**
	 * A constructor for building a hand of straight flush using the given cards and assign the owner of this straight flush
	 * @param player the player who plays this hand of straight flush 
	 * @param cards the cards that compose this hand of straight flush
	 */
    public StraightFlush(CardGamePlayer player, CardList cards) {
        super(player, cards);
    }

    @Override
    public boolean beats(Hand hand) {
        if (hand.getType() == "Straight" || hand.getType() == "Flush" || hand.getType() == "FullHouse" || hand.getType() == "Quad") {
            return true;
        }
        return super.beats(hand);
    }

    /**
     * A method for checking if this is a valid hand of straight flush.
     * @return true if this is a valid hand of straight flush, false otherwise
     */
    public boolean isValid() {
        if (this.size() == 5) {
            this.sort();
            for (int i = 0; i < 4; i++) {
                if (this.getCard(i).getSuit() != this.getCard(i+1).getSuit()) {return false;}
                if ((this.getCard(i).getRank()+11)%13 + 1 != (this.getCard(i+1).getRank()+11)%13) {return false;}
            }
            return true;
        }
        return false;
    }

    /**
     * A method for returning a string specifying the type of this hand of straight flush.
     * @return a string specifying the type of this hand of straight flush
     */
    public String getType() {return "StraightFlush";}
}
