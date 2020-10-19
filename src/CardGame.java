public class CardGame extends Thread {

    int num;
    boolean waiting;
    public CardGame(int Num, boolean Waiting){
        num = Num;
        waiting = Waiting;
    }

    public synchronized void run () {

            Player player = new Player(num);
            while (waiting == true){
                try {
                    this.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        System.out.println ("Hello from a thread! " + num);
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

        CardGame game1 = new CardGame(1, false);
        CardGame game2 = new CardGame(2, true);

        game1.start();
        game2.start();

    }
}

class Player{
    int num;
    public Player(int Num){
        num = Num;
    }
}

class Card{

}
