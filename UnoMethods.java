package unogame;

import java.util.LinkedList; //import all needed java.util or java.io
import java.util.Scanner;
import java.util.Stack;
import java.util.Iterator;
import java.util.InputMismatchException;
import java.io.FileNotFoundException;
import java.io.PrintWriter; //for txt file results

public class UnoMethods { //create an uno methods class to hold all possible methods
    public static boolean skip = false; //holds if player was skipped
    public static int direction = 1; //direction after reverse applied
    public static String newColor = null; //all color info for wilds
    public static int color = 5;
    public static int numToAdd = 0;//num to add after a + number card

    // Make number cards
    public static int makeNumberCards(String[] colors, String[] cards, int cardI) { //loop through all possible numbers twice
        for (int cardNum = 0; cardNum <= 9; cardNum++) {//create all number cards
            for (int j = 0; j < colors.length; j++) {
                if (cardNum == 0) {
                    cards[cardI] = colors[j] + " " + cardNum; //add to a temporary string all number cards
                    cardI += 1;
                } else {
                    for (int w = 1; w <= 2; w++) {//2nd iteration
                        cards[cardI] = colors[j] + " " + cardNum;
                        cardI += 1;
                    }
                }
            }
        }
        return cardI;
    }

    // Make action cards (Skip, Reverse, +2)
    public static int makeActionCards(String[] colors, String[] cards, String[] actionCardType, int cardI) { //create action cards like +2, reverse, and skip
        for (int i = 0; i < colors.length; i++) { //loop through all colors
            for (int j = 0; j < actionCardType.length; j++) { //all possible action cards
                for (int w = 1; w <= 2; w++) {//do it twice
                    cards[cardI] = colors[i] + " " + actionCardType[j];//add to temporary array
                    cardI += 1;
                }
            }
        }
        return cardI;
    }

    // Make wild cards (+4, Wild)
    public static int makeWildCards(String[] colors, String[] cards, String[] wildCardType, int cardI) {//create the wild cards
        for (int j = 0; j < wildCardType.length; j++) {//create all the wild cards including the +4 ones stored in the wild card type array
            for (int w = 1; w <= 4; w++) {
                cards[cardI] = wildCardType[j];
                cardI += 1;
            }
        }
        return cardI;
    }

    // Main game method
    public static void game(String table, int player, Stack<String> deck, LinkedList<String> cpu1Cards, //create the game itself (game logic)
                            LinkedList<String> cpu2Cards, LinkedList<String> cpu3Cards, LinkedList<String> playerCards) {
        try (PrintWriter writer = new PrintWriter("game_results.txt")) {//a try catch statement to close print writer after code executes
            while (cpu1Cards.size() > 0 && cpu2Cards.size() > 0 && cpu3Cards.size() > 0 && playerCards.size() > 0) { //while all players have more than one card
                // Print current player and table card
                String logMessage = "Current player: " + player + " | Table card: " + table; //a given player and table card are given and printed
                logToFile(writer, logMessage); //logs what happened into the text file
                
                // Loop through the players in order
                switch (player) {//depending on player make it their turn
                    case 0:
                        logToFile(writer, "CPU1's turn...");
                        table = cpu1Play(cpu1Cards, deck, table);//cpu1 turn
                        break;
                    case 1:
                        logToFile(writer, "CPU2's turn...");
                        table = cpu2Play(cpu2Cards, deck, table);//cpu2 turn
                        break;
                    case 2:
                        logToFile(writer, "CPU3's turn...");
                        table = cpu3Play(cpu3Cards, deck, table);//cpu3 turn
                        break;
                    case 3:
                        logToFile(writer, "Player's turn...");
                        table = playerPlay(playerCards, deck, table);//player turn
                        break;
                }

                // if skip is true, the current player should be skipped
                if (skip) {
                    skip = false;
                    player = (player + direction + 4) % 4; // Skip this turn and move to next player
                    continue;
                }

                //update the player turn based on the direction (1 for forward, -1 for backward)
                player = (player + direction + 4) % 4; // ensures player is between 0-3 (inclusive)
            }

            logToFile(writer, "Game over!"); //logs to file that the game has ended

        } catch (FileNotFoundException e) { //if the file to add to wasn't found...
            System.out.println("An error occurred while writing to the file."); //say it wasn't found
            e.printStackTrace();//print what happened
        }
    }

    // select wild card color method
    public static String selectedColor(int color) {
        switch (color) { //depending on entered value, the color is selected accordingly
            case 0: return "Red";
            case 1: return "Green";
            case 2: return "Blue";
            case 3: return "Yellow";
            default: return "Red"; // Default to Red in case of an invalid input
        }
    }

    
    public static void logToFile(PrintWriter writer, String message) {
        writer.println(message);  // printwriter says what happened based on the message
    }

    // CPU1 Play (note to self, repetitive, so we needed a seperate method to reference, I just like having seperate methods to reference different cpus)
    public static String cpu1Play(LinkedList<String> cpu1Cards, Stack<String> deck, String table) { //references the cpuplay method
        return cpuPlay(cpu1Cards, deck, table); //returns a played card
    }

    // CPU2 Play
    public static String cpu2Play(LinkedList<String> cpu2Cards, Stack<String> deck, String table) {
        return cpuPlay(cpu2Cards, deck, table);//returns a played card
    }

    // CPU3 Play
    public static String cpu3Play(LinkedList<String> cpu3Cards, Stack<String> deck, String table) {
        return cpuPlay(cpu3Cards, deck, table);//returns a played card
    }

    // generic CPU play function
    public static String cpuPlay(LinkedList<String> cpuCards, Stack<String> deck, String table) {
        Iterator<String> iterator = cpuCards.iterator(); //iterates through the cards
        while (iterator.hasNext()) { //while there are more cards to look through...
            String card = iterator.next();//the card is the next card
            if (!deck.isEmpty()) { //if the deck also isn't empty...
                if (!table.contains("Wild")) { //if there are no wild cards on the table...
                    if (!card.contains("Wild") && !card.contains("+2")) { //if the given card isnt a wild or +2 card
                        String[] tableParts = table.split(" "); //split the table card to search for card num (or action) and color
                        if (card.contains(tableParts[0]) || card.contains(tableParts[1])) { //add the card to the table and remove it from your hand if playable
                            table = card;
                            iterator.remove();
                            return table;
                        }
                    }
                } else if (table.contains("Wild")) { //otherwise if the table has a wild
                    if (card.contains(newColor) || card.contains("Wild")) { //it must be in correspondence to set color
                        table = card;
                        iterator.remove();
                        if (card.contains("Wild")) { //if the cad you have is a wild, select a random color
                            color = (int) (Math.random() * 4); // Select random color
                            newColor = selectedColor(color);
                        }
                        return table; //return played card
                    }
                }
            }
        }
        if (!deck.isEmpty()) { //if the deck is not empty
            cpuCards.add(deck.pop());  // draw a card from the deck if not empty
        }
        return table; //return played card
    }

    // Player's turn
    public static String playerPlay(LinkedList<String> playerCards, Stack<String> deck, String table) { //player played card method
        Scanner s = new Scanner(System.in); // a scanner to input
        if (deck.isEmpty()) { //if the deck is empty
            System.out.println("The deck ran out of cards");//say we ran out cards
            System.exit(0);// close the program
        }

        // Skip the player's turn if skip flag is set
        if (skip) { //if you are skipped
            skip = false; //set it so now everyone else wont get skipped
            System.out.println("You have been skipped!");
            return table; //keep table val
        }

        System.out.println("Current table card: " + table);//other wise the state current card on the table
        boolean playable = false;  //state default play value as false
        System.out.println("Your cards:");
        for (int i = 0; i < playerCards.size(); i++) { //print all current cards
            System.out.println((i + 1) + ": " + playerCards.get(i));
        }

        // Check if any cards are playable
        for (int i = 0; i < playerCards.size(); i++) {
            if (table.contains("Wild") && playerCards.get(i).contains(newColor)) { //loop through and check playbility based on if the card on the table is a wild, color, number, or you have a wild 
                playable = true; //you can play
                break;
            } else if (!table.contains("Wild")) {
                String[] tableParts = table.split(" ");
                if (playerCards.get(i).contains(tableParts[0]) || playerCards.get(i).contains(tableParts[1])) {
                    playable = true; //you can play
                    break;
                }
            } else if (playerCards.get(i).contains("Wild")) {
                playable = true; //you can play
                break;
            }
        }

        if (playable) { //if you can play...
            int cardIndex = 0;
            boolean invalid = true;
            while (invalid) { //while you are inputing false values
                System.out.print("Choose a card to play (enter the index of the card): "); //ask to input a card
                try {
                    cardIndex = s.nextInt(); // selected card
                    if (cardIndex < 1 || cardIndex > playerCards.size()) { //if doesn't fit in the list of values
                        System.out.println("Invalid card index! Please try again.");//reenter a value
                        continue;
                    }
                    s.nextLine(); // newline after nextInt
                    invalid = false; //now invalid is set to false to escape
                } catch (InputMismatchException e) {
                    System.out.println("Invalid input! Please enter a number."); //if you didnt input a number then restart input
                    s.nextLine();  // Clear the invalid input
                }
            }

            // handle Wild card color selection
            if (playerCards.get(cardIndex - 1).contains("Wild")) {//if played card is a wild
                System.out.println("Which of the 4 colors would you like to be the color? (Red, Green, Blue, Yellow)"); //ask what color they want
                newColor = s.nextLine();//get the color
                table = playerCards.get(cardIndex - 1);//set table to played card
                playerCards.remove(cardIndex - 1); //take out this card
            } else { //if not a wild...
                table = playerCards.get(cardIndex - 1);//set table to played card
                playerCards.remove(cardIndex - 1);//take out this card
            }
        } else {//if you can not play anymore...
            System.out.println("You have no playable cards.");
            System.out.print("Do you want to draw a card? (y/n): ");//ask them to draw
            String drawChoice = s.nextLine();
            if (drawChoice.equalsIgnoreCase("y")) {//if they say yes..
                if (!deck.isEmpty()) {
                    playerCards.add(deck.pop());  // draw a card from the deck if not an empty deck
                    System.out.println("You drew a card!");
                } else {
                    System.out.println("No cards left in the deck to draw."); //otherwise say you can't draw
                }
            }
        }
        return table;//return played card
    }
}
