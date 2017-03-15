package soccer;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.*;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class GameTest {

    private Game game;
    private String team1 = "Metallist";
    private String team2 = "Arsenal";
    private String player = "Messi";

    @Before
    public void setUp() {
        game = new Game(team1, team2);
    }

    @Test
    public void testIsFinished() {
        assertFalse(game.isFinished());
    }

    @Test
    public void testEmptyScore() {
        assertEquals(0, game.numberOfAttempt(team1));
        assertEquals(0, game.numberOfAttempt(team2));
    }

    @Test
    public void testFirstKick() {
        game.kick(player, team1, 0);
        assertEquals(1, game.numberOfAttempt(team1));
    }

    @Test
    public void testTeamScoreGoal() {
        game.kick(player, team1, 1);
        assertEquals(1, game.getScore(team1));
    }

    @Test
    public void testResultString() {
        game.kick(player, team1, 1);
        game.kick(player, team2, 1);
        assertEquals("(1)" + team1 + ":" + team2 + "(1)", game.result());
    }

    @Test
    public void testScore50() {
        for (int i = 0; i < 5; i++)
            game.kick(player, team1, 1);
        for (int i = 0; i < 5; i++)
            game.kick(player, team2, 0);
        assertTrue(game.isFinished());
    }

    @Test
    public void testScore10() {
        game.kick(player, team1, 1);
        for (int i = 0; i < 4; i++)
            game.kick(player, team1, 0);
        for (int i = 0; i < 5; i++)
            game.kick(player, team2, 0);
        assertTrue(game.isFinished());
    }

    @Test
    public void testScore01() {
        game.kick(player, team2, 1);
        for (int i = 0; i < 4; i++)
            game.kick(player, team2, 0);
        for (int i = 0; i < 5; i++)
            game.kick(player, team1, 0);
        assertTrue(game.isFinished());
    }

    @Test
    public void testScore67() {
        for (int i = 0; i < 6; i++)
            game.kick(player, team1, 1);
        game.kick(player, team1, 0);
        for (int i = 0; i < 7; i++)
            game.kick(player, team2, 1);
        assertTrue(game.isFinished());
        assertEquals("(6)" + team1 + ":" + team2 + "(7)", game.result());
    }

    @Test
    public void testPlayerKick() {
        game = spy(Game.class);
        game.setTeams(team1, team2);
        int[] result = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
        when(game.loadLastTen(player)).thenReturn(result);
        assertArrayEquals(result, game.kick(player, team2, 1));
    }

    @Test
    public void testResultBeforeSevenSeries() {
        int series = 7;
        for (int i = 0; i < series; i++)
            game.kick(player, team1, 1);
        for (int i = 0; i < series; i++)
            game.kick(player, team2, 1);
        assertEquals("(7)" + team1 + ":" + team2 + "(7)", game.result());
    }

    @Test
    public void testResultAfterSevenSeries() {
        game = spy(Game.class);
        game.setTeams(team1, team2);
        when(game.costFailure(team1)).thenReturn(500000000);
        int series = 8;
        for (int i = 0; i < series - 1; i++)
            game.kick(player, team1, 1);
        game.kick(player, team1, 0);
        for (int i = 0; i < series; i++)
            game.kick(player, team2, 1);
        assertEquals("(7)" + team1 + "[500000000]:" + team2 + "(8)", game.result());
    }

}