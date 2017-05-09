/**
 * The Pair class is a subclass of the Hand class, 
 * and is used to model a hand of pair in a Big Two card game.
 * @author Zhou Jingran
 *
 */
public class Pair extends Hand {
	/**
	 * A constructor for building a hand of pair using the given cards and assign the owner of this pair
	 * @param player the player who plays this hand of pair
	 * @param cards the cards that compose this hand of pair
	 */
    public Pair(CardGamePlayer player, CardList cards) {
        super(player, cards);
    }

    /**
     * A method for checking if this is a valid hand of pair.
     * @return true if this is a valid hand of pair, false otherwise
     */
    public boolean isValid() {
        return this.size() == 2 &&
                this.getCard(0).getRank() == this.getCard(1).getRank();
    }

    /**
     * A method for returning a string specifying the type of this hand of Pair.
     * @return a string specifying the type of this hand of pair
     */
    public String getType() {
        return "Pair";
    }
}
