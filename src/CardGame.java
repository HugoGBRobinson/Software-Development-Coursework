import java.io.*;
import java.util.*;

public class CardGame{
    public static void main(String[] args) throws IOException {
        //needs number of players
        Scanner scanInt = new Scanner(System.in);
        Scanner scanString = new Scanner(System.in);
        System.out.println("Please enter then number of players");
        int numberOfPlayers = scanInt.nextInt();
        writeFile(numberOfPlayers);
        //Location of valid pack
        List<List<Card>> listsOFDeckNums = null;
        while (true){
            System.out.println("Please enter location of pack to load");
            String location = scanString.nextLine();
            listsOFDeckNums = CreateDecksNums(location, numberOfPlayers);
            if (listsOFDeckNums != null){break;}
            System.out.println("The file was not read correctly or does not exist, please enter it again");
        }
        
        //Distribute 4 cards to each player, round robin
        CardDeck decksOfCards[] = new CardDeck[numberOfPlayers]; //Needs looking at as to wether it should be of type Card or Deck of cards?
        Player threads[] = new Player[numberOfPlayers];
        Monitor monitor = new Monitor();
        for (int i = 0; i < numberOfPlayers; i++) {
            decksOfCards[i] = new CardDeck(i);
        }
        //Round robin dealing
        for (int i = 0; i < numberOfPlayers; i++) {
            for (int j = 0; j < numberOfPlayers; j++) {
                decksOfCards[j].addCardToDeck(listsOFDeckNums.get(i).remove(0));
            }
        }
        //Creates player objects and puts them in a list
        for (int i = 0; i < numberOfPlayers; i++) {
            if (i == threads.length-1){threads[i] = new Player(i+1, decksOfCards[i], decksOfCards[0],monitor);}
            else {threads[i] = new Player(i+1, decksOfCards[i], decksOfCards[i+1],monitor);}
        }
        //Round robin dealing
        for (int i = numberOfPlayers; i < numberOfPlayers * 2; i++) {
            for (int j = 0; j < numberOfPlayers; j++) {
                threads[j].addCard(listsOFDeckNums.get(i).remove(0));
            }
        }
        //Makes and starts the threads
        for (int i = 0; i < threads.length; i++) {
            Thread player = new Thread(threads[i]);
            System.out.println("Starting Thread "+ i);
            player.start();
        }
    }
    public static List<List<Card>> CreateDecksNums(String fileName, int numOfDecks){
        List<Integer> pack = ReadFile(fileName);
        if (pack == null){return null;}
        List<List<Card>> listOFDeckNums = new ArrayList<List<Card>>();
        for (int i = 0; i < numOfDecks*2; i++) {
            List<Card> deckNums = new ArrayList<Card>();
            for (int x = 0; x < 4; x++) {
                deckNums.add(new Card(pack.remove(0)));
            }
            listOFDeckNums.add(deckNums);
        }
        return  listOFDeckNums;
    }
    //Writes to a file newline list of random ints
    public static void writeFile(int num) throws IOException {
        File myObj = new File("path");
        myObj.createNewFile();
        BufferedWriter writer = new BufferedWriter(new FileWriter("inputPack.txt"));
        Random random = new Random();
        for (int i = 0; i < (8*num); i++) {
            writer.write(String.valueOf(random.nextInt(10)));
            writer.newLine();
        }
        writer.close();
    }
    //For tests
    public static void writeFile(int num, String fileName) throws IOException {
        File myObj = new File("path");
        myObj.createNewFile();
        BufferedWriter writer = new BufferedWriter(new FileWriter(fileName));
        Random random = new Random();
        for (int i = 0; i < (8*num); i++) {
            writer.write(String.valueOf(random.nextInt(10)));
            writer.newLine();
        }
        writer.close();
    }

    //Reads in file from user input filename
    public static List<Integer> ReadFile(String fileName) {
        List<Integer> list = new ArrayList<Integer>();
            try {
                Scanner scanner = new Scanner(new FileInputStream(fileName));
                while (scanner.hasNextLine()) {
                    list.add(Integer.parseInt(scanner.nextLine()));
                }
                scanner.close();
            } catch (FileNotFoundException e) {
                System.out.println("Cannot find file, please try again");
                return null;
            }
        return list;
    }
}

//Player Class that all threads run off
class Player implements Runnable {
    //For thread safety the instance methods here must be synchronised
    int num;
    List<Card> cards = new ArrayList<Card>();
    CardDeck leftDeck;
    CardDeck rightDeck;
    Monitor monitor;

    public Player(int Num, CardDeck leftDeck, CardDeck rightDeck, Monitor monitor) {
        num = Num;
        this.leftDeck = leftDeck;
        this.rightDeck = rightDeck;
        this.monitor = monitor;
    }
    protected void addCard(Card card){
        cards.add(card);
    }

    //Checks if all the elements in a list are equal to help with isWon function
    private boolean checkIfAllElementsAreEqual(){
        for (Card card : cards) {
            for (int i = 0; i < 4; i++) {
                if (card.cardNum != cards.get(i).cardNum)
                    return false;
            }
        }
        return true;
    }
    //Checks if the player has won
    private boolean isWon(){
        if (checkIfAllElementsAreEqual() == true){return true;}
        return false;
    }
    //Returns the players current hand
    private List<Integer> returnHand() {
        List<Integer> hand = new ArrayList<Integer>();
        for (Card card: cards) {
            hand.add(card.cardNum);
        }
        return hand;
    }

    @Override
    public void run(){
        File playerFile = new File("player"+num+"_output.txt");
        File deckFile = new File("deck"+num+"_output.txt");

        try {
            FileWriter fw = new FileWriter(playerFile);
            PrintWriter pw = new PrintWriter(fw);
            pw.println("Player " + num + " initial hand is " + returnHand().toString());

            synchronized (monitor) {
                while (true) {
                    if (isWon()) {
                        System.out.println("Player "+num + " wins");
                        pw.println("Player "+num + " wins");
                        monitor.addWinner(num);
                        break;
                    }
                    if (monitor.isWon == true) {
                        pw.println("Player " + monitor.winner + " has informed player " + num + " that they have won");
                        break;
                    }
                    try {
                        Card newCard = leftDeck.removeCardFromDeck();
                        cards.add(newCard);
                        pw.println("player " + num + " draws a " + newCard.cardNum + " from deck " + leftDeck.deckNum);

                    } catch (IndexOutOfBoundsException e) {
                        try {
                            monitor.wait();
                        } catch (InterruptedException ex) {
                            ex.printStackTrace();
                        }
                    }
                    if (cards.size() == 5) {
                        Card cardToRemove = cards.remove(ChooseCardToRemove());
                        rightDeck.addCardToDeck(cardToRemove);
                        pw.println("player " + num + " discards a " + cardToRemove.cardNum + " to deck " + rightDeck.deckNum);
                        pw.println("player " + num + " current hand is " + returnHand().toString());
                        monitor.notifyAll();

                    }
                    if (isWon()) {
                        pw.println("player " + num + " wins ");
                        pw.println("player " + num + " exits ");
                        pw.println("player " + num + " final hand is: " + returnHand().toString());
                        System.out.println("Player "+num + " wins");
                        monitor.addWinner(num);
                        break;
                    }
                    if (monitor.isWon == true) {
                        pw.println("Player " + monitor.winner + " has informed player " + num + " that " + monitor.winner + " has won");
                        pw.println("player " + num + " exits ");
                        pw.println("player " + num + " final hand is: " + (returnHand().toString()));
                        break;
                    }
                }

            }

            pw.close();
            FileWriter fw2 = new FileWriter(deckFile);
            PrintWriter pw2 = new PrintWriter(fw2);
            pw2.println("deck"+num+" contents:"+leftDeck.toString());
            pw2.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    //Is the game logic for each player
    private int ChooseCardToRemove(){
        int count = 0;
        for (Card card: this.cards) {
            if (card.cardNum != this.num){return count;}
            else {count = count + 1;}
        }
        return count;
    }

}

//Monitor class that allows the threads to share information such as:
//When a card has been added to an empty card deck
//Has someone won
//Who has won
class Monitor{
    boolean isWon;
    int winner;
    public Monitor(){
        isWon = false;
    }

    public void addWinner(int newWinner){
        winner = newWinner;
        isWon = true;}
}

//Thread safe card object
class Card{
    int cardNum;
    public Card(int cardNum){this.cardNum = cardNum;}
}

//Card deck object that holds 4 cards that allows for:
//The addition of a card to the deck
//The removal of a card from the deck
//Converting the deck to a string
//An overloaded constructor for testing
class CardDeck{
    int deckNum;
    List<Card> cards = new ArrayList<Card>();
    public CardDeck(int deckNum){this.deckNum = deckNum;}
    //Overloaded for tests
    public CardDeck(int deckNum, List<Card> cards){this.deckNum = deckNum; this.cards = cards;}
    public void addCardToDeck(Card card){cards.add(card);}
    public Card removeCardFromDeck(){return cards.remove(0);}
    public String toString(){
        String returnStr="";
        for (Card card: this.cards){
            returnStr = returnStr+card.cardNum+" ";
        }
        return returnStr;
    }
}