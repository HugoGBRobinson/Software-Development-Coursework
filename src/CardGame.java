import java.util.*;
import java.io.File;
import java.io.FileNotFoundException;

public class CardGame extends Thread {

    int num;
    boolean waiting;

    public CardGame(int Num, boolean Waiting) {
        num = Num;
        waiting = Waiting;
    }

    public synchronized void run() {

        Player player = new Player(num);
        while (waiting == true) {
            try {
                this.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.println("Hello from a thread! " + num);
        waiting = true;
        notifyAll();

        try {
            this.wait();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        //Player instance
        //Card's Instances
        //Checking (Out to external func)
        //Winning?
        //Restart thread


    }

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
        Moniter moniter = new Moniter(1); //Moniter for threads
        Card decksOfCards[] = new Card[4]; //Needs looking at as to wether it should be of type Card or Deck of cards?
        Player threads[] = new Player[numberOfPlayers];
        //Creates player objects and puts them in a list
        for (int i = 0; i < numberOfPlayers; i++) {
            threads[i] = new Player(i+1);
        }
        //Once player objects are created this then populates them with required data
        for (int i = 0; i < numberOfPlayers; i++) {
            if (i==0){threads[i].addWaitandNotifiers(threads[threads.length-1],threads[i+1],moniter);}
            else if (i == threads.length-1){threads[i].addWaitandNotifiers(threads[i-1],threads[0],moniter);}
            else {threads[i].addWaitandNotifiers(threads[i-1],threads[i+1],moniter);}
        }
        //Makes and starts the threads
        for (int i = threads.length -1; i > -1; i--) {
            Thread player = new Thread(threads[i]);
            System.out.println("Starting Thread "+ i);
            player.start();
        }
    }
    //For testing
    public static List<Integer> ReadFileFake(int num){
        Random random = new Random();
        List<Integer> pack = new ArrayList<Integer>();
        for (int i = 0; i < (8*num); i++) {
            pack.add(random.nextInt(15));
        }
        return  pack;
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

class Player implements Runnable{
    //For thread saftey the instance methods here must be syncronised
    int num;
    Player playerWaitingFor;
    Player playerNotifing;
    List<Integer> cards;
    Moniter moniter;

    public Player(int Num) {
        num = Num;
    }
    public void addWaitandNotifiers(Player playerWaitingFor, Player playerNotifing, Moniter moniter){
        this.playerWaitingFor = playerWaitingFor;
        this.playerNotifing = playerNotifing;
        this.moniter = moniter;
    }

    private synchronized void isWaiting() throws InterruptedException {
        //Syncronised on an external moniter too allow data transfer between threads and wait/notify to work
        synchronized (moniter){
            while (true){
                if (moniter.player == num){
                    moniter.changePlayer(playerNotifing.num);
                    System.out.println("Player " + num + " Running");
                    //Once notified then wait
                    moniter.notify();
                    moniter.wait();
                }
                else{
                    //Not sure why but this print statment needs to be here to let the one above show for more than 1 thread
                    System.out.println(num + " is waiting");
                    moniter.wait();
                }
            }
        }
    }


    private void addCard(int card){
        cards.add(card);
    }

    private void isWon(){
        //Not implimented
    }

    @Override
    public void run(){
        try {
            isWaiting();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    //Methods : Check for winning hand, draw card from left (their num) discard to right (num + 1, or 1)
    //Must discard non prefered cards (Player 1 will discard card 2)

}

class Card{
    //For thread safty the instance methods here must be syncronised

}

class CardDeck{

}

class Moniter{
    int player;
    public Moniter(int player){
        this.player = player;
    }

    public void changePlayer(int player){this.player = player;}
}
