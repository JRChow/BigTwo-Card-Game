/**
 * The Quad class is a subclass of the Hand class, 
 * and is used to model a hand of quad in a Big Two card game.
 * @author Zhou Jingran
 *
 */
public class Quad extends Hand {
	/**
	 * A constructor for building a hand of quad using the given cards and assign the owner of this quad
	 * @param player the player who plays this hand of quad
	 * @param cards the cards that compose this hand of quad
	 */
    public Quad(CardGamePlayer player, CardList cards) {
        super(player, cards);
    }

    @Override
    /**
     * A method for retrieving the top card of this hand of quad.
     * @return the top card of this hand of quad
     */
    public Card getTopCard() {
        if (this.isValid()) {
            this.sort();
            Card topCard;
            int start;
            int end;
            if (this.getCard(0).getRank() != this.getCard(1).getRank()) {
                start = 2;
                end = 5;
                topCard = this.getCard(1);
            } else {
                start = 1;
                end = 4;
                topCard = this.getCard(0);
            }
            for (int i = start; i < end; i++) {
                if (this.getCard(i).getRank() > topCard.getRank()) {
                    topCard = this.getCard(i);
                }
            }
            return topCard;
        }
        return null;
    }

    @Override
    public boolean beats(Hand hand) {
        if (hand.getType() == "Straight" || hand.getType() == "Flush" || hand.getType() == "FullHouse") {
            return true;
        }
        return super.beats(hand);
    }
    /**
     * A method for checking if this is a valid hand of quad.
     * @return true if this is a valid hand of quad, false otherwise
     */
    public boolean isValid() {
        if (this.size() == 5) {
            this.sort();
            if (this.getCard(0).getRank() != this.getCard(1).getRank()
                    && this.getCard(1).getRank() == this.getCard(4).getRank()) {
                return true;
            }
            return this.getCard(0).getRank() == this.getCard(3).getRank()
                    && this.getCard(3).getRank() != this.getCard(4).getRank();
        }
        return false;
    }

    /**
     * A method for returning a string specifying the type of this hand of quad.
     * @return a string specifying the type of this hand of quad
     */
    public String getType() {return "Quad";}
}
