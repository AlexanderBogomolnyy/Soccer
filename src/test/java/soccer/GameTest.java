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

    @Test(expected = IllegalArgumentException.class)
    public void testIllegalTeamIdentifier() {
        game.kick(player, 2, true);
    }

    @Test
    public void testEmptyScore() {
        assertEquals(0, game.numberOfAttempt(Game.FIRST_TEAM));
        assertEquals(0, game.numberOfAttempt(Game.SECOND_TEAM));
    }

    @Test
    public void testFirstKick() {
        game.kick(player, Game.FIRST_TEAM, false);
        assertEquals(1, game.numberOfAttempt(Game.FIRST_TEAM));
    }

    @Test
    public void testTeamScoreGoal() {
        game.kick(player, Game.SECOND_TEAM, true);
        assertEquals(1, game.getScore(Game.SECOND_TEAM));
    }

    @Test(expected = IllegalStateException.class)
    public void testExceptionOneTeamShootTwiceInARowFirstTeamAtTheBegin(){
        game.kick(player, Game.FIRST_TEAM, true);
        game.kick(player, Game.FIRST_TEAM, true);
    }

    @Test(expected = IllegalStateException.class)
    public void testExceptionOneTeamShootTwiceInARowSecondTeamAtTheBegin(){
        game.kick(player, Game.SECOND_TEAM, true);
        game.kick(player, Game.SECOND_TEAM, true);
    }

    @Test(expected = IllegalStateException.class)
    public void testExceptionOneTeamShootTwiceInARowFirstTeamAtTheMiddle(){
        game.kick(player, Game.FIRST_TEAM, true);
        game.kick(player, Game.SECOND_TEAM, true);
        game.kick(player, Game.FIRST_TEAM, false);
        game.kick(player, Game.FIRST_TEAM, true);
    }

    @Test(expected = IllegalStateException.class)
    public void testExceptionOneTeamShootTwiceInARowSecondTeamAtTheMiddle(){
        game.kick(player, Game.SECOND_TEAM, true);
        game.kick(player, Game.FIRST_TEAM, true);
        game.kick(player, Game.SECOND_TEAM, false);
        game.kick(player, Game.SECOND_TEAM, true);
    }

    @Test
    public void testResultString() {
        game.kick(player, Game.FIRST_TEAM, true);
        game.kick(player, Game.SECOND_TEAM, true);
        assertEquals("(1)" + team1 + ":" + team2 + "(1)", game.getGameResult());
    }

    @Test(expected = IllegalStateException.class)
    public void testImpossibleScore50() {
        for (int i = 0; i < 5; i++) {
            game.kick(player, Game.FIRST_TEAM, true);
            game.kick(player, Game.SECOND_TEAM, false);
        }
    }

    @Test
    public void testScore30() {
        for (int i = 0; i < 3; i++) {
            game.kick(player, Game.FIRST_TEAM, true);
            game.kick(player, Game.SECOND_TEAM, false);
        }
        assertTrue(game.isFinished());
    }

    @Test
    public void testScore10() {
        game.kick(player, Game.FIRST_TEAM, true);
        for (int i = 0; i < 4; i++) {
            game.kick(player, Game.SECOND_TEAM, false);
            game.kick(player, Game.FIRST_TEAM, false);
        }
            game.kick(player, Game.SECOND_TEAM, false);
        assertTrue(game.isFinished());
    }

    @Test
    public void testScore01() {
        game.kick(player, Game.SECOND_TEAM, true);
        for (int i = 0; i < 4; i++) {
            game.kick(player, Game.FIRST_TEAM, false);
            game.kick(player, Game.SECOND_TEAM, false);
        }
        game.kick(player, Game.FIRST_TEAM, false);
        assertTrue(game.isFinished());
    }

    @Test
    public void testScore67() {
        for (int i = 0; i < 6; i++) {
            game.kick(player, Game.FIRST_TEAM, true);
            game.kick(player, Game.SECOND_TEAM, true);
        }
        game.kick(player, Game.FIRST_TEAM, false);
        game.kick(player, Game.SECOND_TEAM, true);
        assertTrue(game.isFinished());
        assertEquals("(6)" + team1 + ":" + team2 + "(7)", game.getGameResult());
    }

    @Test
    public void testGameIsNotFinishedAfterForKicks() {
        for (int i = 0; i < 2; i++) {
            game.kick(player, Game.FIRST_TEAM, true);
            game.kick(player, Game.SECOND_TEAM, true);
        }
        for (int i = 0; i < 2; i++) {
            game.kick(player, Game.FIRST_TEAM, false);
            game.kick(player, Game.SECOND_TEAM, false);
        }
        assertFalse(game.isFinished());
    }

    @Test
    public void testPlayerKickWithPlayerEffectiveness() {
        game = spy(Game.class);
        game.setTeams(team1, team2);
        int[] result = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
        when(game.loadLastTen(player)).thenReturn(result);
        assertArrayEquals(result, game.kick(player, Game.SECOND_TEAM, true));
    }

    @Test
    public void testResultBeforeSevenSeries() {
        int series = 7;
        for (int i = 0; i < series; i++) {
            game.kick(player, Game.FIRST_TEAM, true);
            game.kick(player, Game.SECOND_TEAM, true);
        }
        assertEquals("(7)" + team1 + ":" + team2 + "(7)", game.getGameResult());
    }

    @Test
    public void testResultBeforeEightSeries() {
        int series = 8;
        for (int i = 0; i < series; i++) {
            game.kick(player, Game.FIRST_TEAM, true);
            game.kick(player, Game.SECOND_TEAM, true);
        }
        assertEquals("(8)" + team1 + ":" + team2 + "(8)", game.getGameResult());
    }

    @Test
    public void testResultAfterSevenSeriesFirstFail() {
        game = spy(Game.class);
        game.setTeams(team1, team2);
        when(game.costFailure(team1)).thenReturn(500000000);
        int series = 8;
        for (int i = 0; i < series - 1; i++) {
            game.kick(player, Game.FIRST_TEAM, true);
            game.kick(player, Game.SECOND_TEAM, true);
        }
        game.kick(player, Game.FIRST_TEAM, false);
        game.kick(player, Game.SECOND_TEAM, true);
        assertEquals("(7)" + team1 + "[500000000]:" + team2 + "(8)", game.getGameResult());
    }

    @Test
    public void testResultAfterSevenSeriesSecondFail() {
        game = spy(Game.class);
        game.setTeams(team1, team2);
        when(game.costFailure(team2)).thenReturn(500000000);
        int series = 8;
        for (int i = 0; i < series - 1; i++) {
            game.kick(player, Game.SECOND_TEAM, true);
            game.kick(player, Game.FIRST_TEAM, true);
        }
        game.kick(player, Game.SECOND_TEAM, false);
        game.kick(player, Game.FIRST_TEAM, true);
        assertEquals("(8)" + team1 + ":[500000000]" + team2 + "(7)", game.getGameResult());
    }

    @Test
    public void testResultAfterSevenSeriesBothFail() {
        game = spy(Game.class);
        game.setTeams(team1, team2);
        when(game.costFailure(team1)).thenReturn(400000000);
        when(game.costFailure(team2)).thenReturn(500000000);
        int series = 8;
        for (int i = 0; i < series - 1; i++) {
            game.kick(player, Game.SECOND_TEAM, true);
            game.kick(player, Game.FIRST_TEAM, true);
        }
        game.kick(player, Game.SECOND_TEAM, false);
        game.kick(player, Game.FIRST_TEAM, false);
        assertEquals("(7)" + team1 + "[400000000]:[500000000]" + team2 + "(7)", game.getGameResult());
        game.kick(player, Game.SECOND_TEAM, true);
        game.kick(player, Game.FIRST_TEAM, true);
        assertEquals("(8)" + team1 + "[400000000]:[500000000]" + team2 + "(8)", game.getGameResult());
        game.kick(player, Game.SECOND_TEAM, false);
        game.kick(player, Game.FIRST_TEAM, false);
        assertEquals("(8)" + team1 + "[400000000]:[500000000]" + team2 + "(8)", game.getGameResult());
    }

    @Test(expected = IllegalStateException.class)
    public void testShootAfterGameFinishing() {
        for (int i = 0; i < 5; i++) {
            game.kick(player, Game.FIRST_TEAM, true);
            game.kick(player, Game.SECOND_TEAM, false);
        }
        game.kick(player, Game.FIRST_TEAM, true);
    }

    @Test(expected = IllegalStateException.class)
    public void testShootAfterGameFinishingByAdditionalShoots() {
        for (int i = 0; i < 6; i++) {
            game.kick(player, Game.FIRST_TEAM, false);
            game.kick(player, Game.SECOND_TEAM, false);
        }
        game.kick(player, Game.FIRST_TEAM, true);
        game.kick(player, Game.SECOND_TEAM, false);
        game.kick(player, Game.FIRST_TEAM, false);
    }

}