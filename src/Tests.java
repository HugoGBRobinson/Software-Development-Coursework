import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import java.lang.reflect.Method;

public class Tests {


    //CardDeck class
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
    @Test
    public void CardTests() {
        Card testCard = new Card(1);
        Assert.assertEquals(1, testCard.cardNum);
    }
    //Monitor class
    @Test
    public void MonitorTests() {
        Monitor testMonitor = new Monitor();
        Assert.assertEquals(false, testMonitor.isWon);
        Assert.assertEquals(0, testMonitor.winner);
        testMonitor.addWinner(1);
        Assert.assertEquals(true, testMonitor.isWon);
        Assert.assertEquals(1, testMonitor.winner);
    }
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
        return new Player(1, testDeck, testDeck, testHand, testMonitor);
    }

    private Player player = TestPlayer();
    private Method m;
    //Player class
    @Before
    public void PlayerCheckIfAllElementsAreEqualSetUp() throws Exception {
        m = player.getClass().getDeclaredMethod("checkIfAllElementsAreEqual");
        m.setAccessible(true);
    }
    @Test
    public void PlayerCheckIfAllElementsAreEqualTests() throws Exception {
        Assert.assertEquals(true, m.invoke(player));
    }
    @Before
    public void PlayerIsWonSetUp() throws Exception {
        m = player.getClass().getDeclaredMethod("isWon");
        m.setAccessible(true);
    }
    @Test
    public void PlayerIsWonTests() throws Exception {
        Assert.assertEquals(true, m.invoke(player));
    }
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
    @Test
    public void PlayerRunTests() {
        Player player = TestPlayer();
        player.run();
        Assert.assertEquals(true, Thread.currentThread().isAlive());
    }
    //Main
    @Test
    public void WriteFileTests() throws IOException {
        CardGame.writeFile(4, "testPack.txt");
        Assert.assertEquals(true, new File("testPack.txt").isFile());
    }
    @Test
    public void CreateDeckNumsTests() {
        List<List<Card>> listsOFDeckNums = CardGame.CreateDecksNums("testPack.txt", 4);
        Assert.assertEquals(8, listsOFDeckNums.size());
    }
}

