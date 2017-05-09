/**
 * The Straight class is a subclass of the Hand class, 
 * and is used to model a hand of straight in a Big Two card game.
 * @author Zhou Jingran
 *
 */
public class Straight extends Hand {
	
	/**
	 * A constructor for building a hand of straight using the given cards and assign the owner of this straight
	 * @param player the player who plays this hand of straight
	 * @param cards the cards that compose this hand of straight
	 */
    public Straight(CardGamePlayer player, CardList cards) {
        super(player, cards);
    }

    /**
     * A method for checking if this is a valid hand of straight.
     * @return true if this is a valid hand of straight, false otherwise
     */
    public boolean isValid() {
        if (this.size() == 5) {
            this.sort();
            for (int i = 1; i < 5; i++) {
                if ((this.getCard(0).getRank() + 11) % 13 + i
                        != (this.getCard(i).getRank() + 11) % 13) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    /**
     * A method for returning a string specifying the type of this hand of straight.
     * @return a string specifying the type of this hand of straight
     */
    public String getType() {
        return "Straight";
    }
}
