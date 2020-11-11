import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import java.lang.reflect.Method;

public class Tests {


    //CardDeck class
    //Tests the addition and removal of a card from the card deck
    @Test
    public void CardDeckTests() {
        List<Card> testCards = new ArrayList<Card>();
        testCards.add(new Card(1));
        testCards.add(new Card(2));
        testCards.add(new Card(3));
        testCards.add(new Card(4));
        CardDeck testDeck = new CardDeck(1, testCards);
        Assert.assertEquals(1, testDeck.cards.get(0).cardNum);
        Assert.assertEquals(4,testDeck.cards.size());
        testDeck.addCardToDeck(new Card(5));
        Assert.assertEquals(5, testDeck.cards.size());
        Assert.assertEquals(5, testDeck.cards.get(4).cardNum);
        Card removedCard = testDeck.removeCardFromDeck();
        Assert.assertEquals(1, removedCard.cardNum);
        Assert.assertEquals(4, testDeck.cards.size());
    }
    //Card class
    //Tests the card object so it can be constructed as expected
    @Test
    public void CardTests() {
        Card testCard = new Card(1);
        Assert.assertEquals(1, testCard.cardNum);
    }
    //Monitor class
    //Tests if the monitor class is constructed correctly and then can be changed when someone has won
    @Test
    public void MonitorTests() {
        Monitor testMonitor = new Monitor();
        Assert.assertEquals(false, testMonitor.isWon);
        Assert.assertEquals(0, testMonitor.winner);
        testMonitor.addWinner(1);
        Assert.assertEquals(true, testMonitor.isWon);
        Assert.assertEquals(1, testMonitor.winner);
    }
    //To be used in subsequent tests
    public Player TestPlayer(){
        List<Card> testCards = new ArrayList<Card>();
        testCards.add(new Card(1));
        testCards.add(new Card(2));
        testCards.add(new Card(3));
        testCards.add(new Card(4));
        CardDeck testDeck = new CardDeck(1, testCards);
        List<Card> testHand = new ArrayList<>();
        testHand.add(new Card(1));
        testHand.add(new Card(1));
        testHand.add(new Card(1));
        testHand.add(new Card(1));
        Monitor testMonitor = new Monitor();
        Player player = new Player(1, testDeck, testDeck, testMonitor);
        for (Card card: testHand) {
            player.addCard(card);
        }
        return player;
    }

    private Player player = TestPlayer();
    private Method m;
    //Player class
    //Checks if all elements are equal method
    @Before
    public void PlayerCheckIfAllElementsAreEqualSetUp() throws Exception {
        m = player.getClass().getDeclaredMethod("checkIfAllElementsAreEqual");
        m.setAccessible(true);
    }
    @Test
    public void PlayerCheckIfAllElementsAreEqualTests() throws Exception {
        Assert.assertEquals(true, m.invoke(player));
    }
    //Checks if a player has won method
    @Before
    public void PlayerIsWonSetUp() throws Exception {
        m = player.getClass().getDeclaredMethod("isWon");
        m.setAccessible(true);
    }
    @Test
    public void PlayerIsWonTests() throws Exception {
        Assert.assertEquals(true, m.invoke(player));
    }
    //Checks the return hand method
    @Before
    public void PlayerReturnHandSetUp() throws Exception {
        m = player.getClass().getDeclaredMethod("returnHand");
        m.setAccessible(true);
    }
    @Test
    public void PlayerReturnHandTests() throws Exception {
        List<Card> expectedHand = new ArrayList<Card>();
        expectedHand.add(new Card(1));
        expectedHand.add(new Card(1));
        expectedHand.add(new Card(1));
        expectedHand.add(new Card(1));
        Assert.assertNotNull(m.invoke(player));
    }
    //Tests is the thread can start properly
    @Test
    public void PlayerRunTests() {
        Player player = TestPlayer();
        player.run();
        Assert.assertEquals(true, Thread.currentThread().isAlive());
    }
    //Main
    //Tests if a pack is created correctly
    @Test
    public void WriteFileTests() throws IOException {
        CardGame.writeFile(4, "testPack.txt");
        new File("testPack.txt").isFile();
    }
    //Tests if a file is read correctly
    @Test
    public void ReadFileTest() {
        Assert.assertNull(CardGame.ReadFile("errorPack.txt"));
    }
    //Tests if the Create Deck Nums function works correctly 
    @Test
    public void CreateDeckNumsTests() {
        List<List<Card>> listsOFDeckNums = CardGame.CreateDecksNums("testPack.txt", 4);
        Assert.assertEquals(8, listsOFDeckNums.size());
    }
}

