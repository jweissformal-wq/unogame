package unogame;

import java.util.Collections;
import java.util.LinkedList;
import java.util.Stack;

public class UnoDriver {

    private static final Stack<String> deck = new java.util.Stack<>(); //create a stack for the deck
    
    public static final String[] colors = {"Red", "Yellow", "Blue", "Green"}; //all possible cards
    public static final String[] actionCardType = {"Skip", "Reverse", "+2"};
    public static final String[] wildCardType = {"Regular Wild", "Wild +4"};
    public static String[] cards = new String[108];//a temp holder of all cards

    public static int cardI = 0;//current cards index

    public static LinkedList<String> cpu1Cards = new LinkedList<>(); //linked lists for all of the hands
    public static LinkedList<String> cpu2Cards = new LinkedList<>();
    public static LinkedList<String> cpu3Cards = new LinkedList<>();
    public static LinkedList<String> playerCards = new LinkedList<>();
    public static String table;

    public static void main(String[] args) {//main method
        
        UnoMethods unoMethods = new UnoMethods(); //uno methods object to make thhe game
        
        cardI = UnoMethods.makeNumberCards(colors, cards, cardI); //make all possible cards using the methods from the uno methods class
        cardI = UnoMethods.makeActionCards(colors, cards, actionCardType, cardI); //also cardI is updated based on how many cards were filled from each method
        cardI = UnoMethods.makeWildCards(colors, cards, wildCardType, cardI);

        for (int i = 0; i < cards.length; i++) { //add all cards to the deck
            deck.push(cards[i]);
        }

        Collections.shuffle(deck);

        // distribute cards to players
        for (int i = 0; i <= 3; i++) {
            for (int j = 0; j < 7; j++) {
                switch (i) {
                    case 0:
                        cpu1Cards.add(deck.pop()); //.pop because you take a away from deck, .add because it is added to the hand
                        break;
                    case 1:
                        cpu2Cards.add(deck.pop());
                        break;
                    case 2:
                        cpu3Cards.add(deck.pop());
                        break;
                    case 3:
                        playerCards.add(deck.pop());
                        break;
                }
            }
        }

        table = deck.pop(); //a starting card
        int starter = (int) (Math.random() * 3);
        UnoMethods.game(table, starter, deck, cpu1Cards, cpu2Cards, cpu3Cards, playerCards); //given all information needed for the deck and hands, start the game
    }
}
