/**
 * The Triple class is a subclass of the Hand class, 
 * and is used to model a hand of triple in a Big Two card game.
 * @author Zhou Jingran
 *
 */
public class Triple extends Hand {

	/**
	 * A constructor for building a hand of triple using the given cards and assign the owner of this triple
	 * @param player the player who plays this hand of triple
	 * @param cards the cards that compose this hand of triple
	 */
    public Triple(CardGamePlayer player, CardList cards) {
        super(player, cards);
    }

    /**
     * A method for checking if this is a valid hand of triple.
     * @return true if this is a valid hand of triple, false otherwise
     */
    public boolean isValid() {
        return this.size() == 3
                && this.getCard(0).getRank() == this.getCard(1).getRank()
                && this.getCard(1).getRank() == this.getCard(2).getRank();
    }

    /**
     * A method for returning a string specifying the type of this hand of triple.
     * @return a string specifying the type of this hand of triple
     */
    public String getType() {
        return "Triple";
    }
}
