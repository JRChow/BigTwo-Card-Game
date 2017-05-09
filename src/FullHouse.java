/**
 * The FullHouse class is a subclass of the Hand class, 
 * and is used to model a hand of full house in a Big Two card game.
 * @author Zhou Jingran
 *
 */
public class FullHouse extends Hand {
	/**
	 * A constructor for building a hand of full house and assign the owner
	 * @param player the player who plays this hand of full house
	 * @param cards the cards that compose the hand of full house
	 */
    public FullHouse(CardGamePlayer player, CardList cards) {
        super(player, cards);
    }

    @Override
    /**
     * A method for getting the top card of the hand of full house
     * @return the top card of the hand of full house
     */
    public Card getTopCard() {
        if (this.isValid()) {
            this.sort();
            Card topCard;
            int start;
            int end;
            if (this.getCard(0).getRank() == this.getCard(2).getRank()) {
                start = 1;
                end = 3;
                topCard = this.getCard(0);
            } else {
                start = 3;
                end = 5;
                topCard = this.getCard(2);
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

    /**
     * An override method for checking if the FullHouse can beat another hand of cards
     * @param hand the competing hand of this hand
     * @return true if the FullHouse can beat the competing hand
     */
    @Override
    public boolean beats(Hand hand) {
        if (hand.getType() == "Straight" || hand.getType() == "Flush") {
            return true;
        }
        if (hand.getType() == "FullHouse") {
            if (this.getTopCard().compareTo(hand.getTopCard()) == 1) {
                return true;
            }
        }
        return false;
    }

    /**
     *  A method for checking if this is a valid hand of full house
     *  @return true if this is a valid hand of full house, false otherwise 
     */
    public boolean isValid() {
        if (this.size() == 5) {
            this.sort();
            if (this.getCard(0).getRank() == this.getCard(2).getRank()
                    && this.getCard(2).getRank() != this.getCard(3).getRank()
                    && this.getCard(3).getRank() == this.getCard(4).getRank()) {
                return true;
            }
            return this.getCard(0).getRank() == this.getCard(1).getRank()
                    && this.getCard(1).getRank() != this.getCard(2).getRank()
                    && this.getCard(2).getRank() == this.getCard(4).getRank();
        }
        return false;
    }

    /**
     * A method for returning a string specifying the type is Full House
     * @return the string that specifies the type is Full House
     */
    public String getType() {return "FullHouse";}
}
