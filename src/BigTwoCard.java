/**
 * The BigTwoCard class is a subclass of the Card class, and is used to model a card used in a Big Two card game.
 * @author Zhou Jingran
 *
 */
public class BigTwoCard extends Card {
	/**
	 * A constructor for building a card with the specified suit and rank
	 * @param suit 
	 * 			an integer between 0 and 3 specifying the suit of the card
	 * @param rank 
	 * 			an integer between 0 and 12 specifying the rank of the card
	 */
    public BigTwoCard(int suit, int rank) {
        super(suit, rank);
    }

    /**
     *  A method for comparing this card with the specified card for order.
     *  @param card 
     *  			the object to be compared with this object
     *  @return negative, zero, positive integer value if this card is less than, equal to, greater than the specified card
     */
    public int compareTo(Card card) {
        Card tempThis = new Card(this.getSuit(), (this.getRank()+11)%13);
        Card tempCard = new Card(card.getSuit(), (card.getRank()+11)%13);
        return tempThis.compareTo(tempCard);
    }
}
