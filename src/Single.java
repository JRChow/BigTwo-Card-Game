/**
 * The Single class is a subclass of the Hand class, 
 * and is used to model a hand of single in a Big Two card game.
 * @author Zhou Jingran
 *
 */
public class Single extends Hand {

	/**
	 * A constructor for building a hand of single using the given cards and assign the owner of this single
	 * @param player the player who plays this hand of single
	 * @param cards the cards that compose this hand of single
	 */
    public Single(CardGamePlayer player, CardList cards) {
        super(player, cards);
    }

    /**
     * A method for checking if this is a valid hand of single.
     * @return true if this is a valid hand of single, false otherwise
     */
    public boolean isValid() {
        return this.size() == 1;
    }

    /**
     * A method for returning a string specifying the type of this hand of single.
     * @return a string specifying the type of this hand of single
     */
    public String getType() {
        return "Single";
    }
}
