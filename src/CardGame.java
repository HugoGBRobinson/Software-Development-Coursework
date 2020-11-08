import java.util.*;
import java.io.File;
import java.io.FileNotFoundException;

public class CardGame{

        //Player instance
        //Card's Instances
        //Checking (Out to external func)
        //Winning?
        //Restart thread

    public static void main(String[] args) throws InterruptedException {
        //needs number of players
        Scanner scan = new Scanner(System.in);
        System.out.println("Please enter then number of players");
        int numberOfPlayers = scan.nextInt();
        Player players[] = new Player[numberOfPlayers];
        //Location of valid pack
        //System.out.println("Please enter location of pack to load");
        //String location = scan.nextLine();
        List<Integer> pack = ReadFileFake(numberOfPlayers);
        //Distribute 4 cards to each player, round robin

        //Fill decks
        //Start threads for players
        CardDeck decksOfCards[] = new CardDeck[numberOfPlayers]; //Needs looking at as to wether it should be of type Card or Deck of cards?
        Player threads[] = new Player[numberOfPlayers];
        List<List<Card>> listsOFDeckNums = CreateDecksNums(numberOfPlayers);
        Monitor monitor = new Monitor();
        //Needs to be changed into a round robbin
        for (int i = 0; i < numberOfPlayers; i++) {
            decksOfCards[i] = new CardDeck(i, listsOFDeckNums.remove(0));
        }
        //Creates player objects and puts them in a list
        for (int i = 0; i < numberOfPlayers; i++) {
            if (i == threads.length-1){threads[i] = new Player(i+1, decksOfCards[i], decksOfCards[0], listsOFDeckNums.remove(0),monitor);}
            else {threads[i] = new Player(i+1, decksOfCards[i], decksOfCards[i+1], listsOFDeckNums.remove(0),monitor);}
        }
        //Makes and starts the threads
        for (int i = 0; i < threads.length; i++) {
            Thread player = new Thread(threads[i]);
            System.out.println("Starting Thread "+ i);
            player.start();
        }
    }
    public static List<List<Card>> CreateDecksNums(int numOfDecks){
        List<Integer> pack = ReadFileFake(numOfDecks);
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
    //For testing
    public static List<Integer> ReadFileFake(int num){
        Random random = new Random();
        List<Integer> pack = new ArrayList<Integer>();
        for (int i = 0; i < (8*num); i++) {
            pack.add(random.nextInt(10));
        }
        return pack;
    }
    //Not needed yet
    public static Queue<String> ReadFile(String fileName) {
        Queue<String> queue = new LinkedList<String>();
        try {
            File myObj = new File(fileName);
            Scanner myReader = new Scanner(myObj);
            while (myReader.hasNextLine()) {
                queue.add(myReader.nextLine());
            }
            myReader.close();
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
        return queue;
    }
}

class Player implements Runnable {
    //For thread saftey the instance methods here must be syncronised
    int num;
    List<Card> cards;
    CardDeck leftDeck;
    CardDeck rightDeck;
    Monitor monitor;

    public Player(int Num, CardDeck leftDeck, CardDeck rightDeck, List<Card> cards, Monitor monitor) {
        num = Num;
        this.leftDeck = leftDeck;
        this.rightDeck = rightDeck;
        this.cards = cards;
        this.monitor = monitor;
    }
        private void addCard(Card card){
        cards.add(card);
    }

    private boolean checkIfAllElementsAreEqual(){
        for (Card card : cards) {
            for (int i = 0; i < 4; i++) {
                if (card.cardNum != cards.get(i).cardNum)
                    return false;
            }
        }
        return true;

    }

    private boolean isWon(){
        if (checkIfAllElementsAreEqual() == true){return true;}
        return false;
    }

    private List<Integer> returnHand() {
        List<Integer> hand = new ArrayList<Integer>();
        for (Card card: cards) {
            hand.add(card.cardNum);
        }
        return hand;
    }

    @Override
    public void run(){
        System.out.println("player "+ num + " current hand is " + returnHand().toString());
        synchronized (monitor){
                while (true){
                    if (isWon()){System.out.println(num + " has won"); monitor.addWinner(num);break;}
                    if (monitor.isWon == true){
                        System.out.println("Player " + monitor.winner + " has informed player " + num + " that they have won");
                        break;
                    }
                    try {
                        Card newCard = leftDeck.removeCardFromDeck();
                        cards.add(newCard);
                        System.out.println("player "+ num + " draws a " + newCard.cardNum + " from deck " + leftDeck.deckNum);

                    } catch (IndexOutOfBoundsException e){
                        try {
                            monitor.wait();
                        } catch (InterruptedException ex) {
                            ex.printStackTrace();
                        }
                    }
                    if (cards.size() == 5){
                        Card cardToRemove = cards.remove(0);
                        rightDeck.addCardToDeck(cardToRemove);
                        System.out.println("player "+ num + " discards a " + cardToRemove.cardNum + " to deck " + rightDeck.deckNum);
                        monitor.notifyAll();

                    }
                    System.out.println("player "+ num + " current hand is " + returnHand().toString());
                    if (isWon())
                    {
                        System.out.println("player " + num + " wins ");
                        System.out.println("player " + num + " exits ");
                        System.out.println("player " + num + " final hand is: "+ returnHand().toString());
                        monitor.addWinner(num);
                        break;
                    }
                    if (monitor.isWon == true){
                        System.out.println("Player " + monitor.winner + " has informed player " + num + " that "+ monitor.winner + " has won");
                        System.out.println("player " + num + " exits ");
                        System.out.println("player " + num + " final hand is: "+ returnHand().toString());
                        break;
                    }
                }

        }

    }

    //Methods : Check for winning hand, draw card from left (their num) discard to right (num + 1, or 1)
    //Must discard non prefered cards (Player 1 will discard card 2)

}

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

class Card{
    int cardNum;
    public Card(int cardNum){this.cardNum = cardNum;}
    //For thread safty the instance methods here must be syncronised

}

class CardDeck{
    int deckNum;
    List<Card> cards = new ArrayList<Card>();
    public CardDeck(int deckNum, List<Card> cards){this.deckNum = deckNum; this.cards = cards;}
    public void addCardToDeck(Card card){cards.add(card); System.out.println("Deck "+ deckNum + " is adding the card " + card.cardNum);}
    public Card removeCardFromDeck(){ System.out.println("Deck "+ deckNum + " is removing a card"); return cards.remove(0);}
}