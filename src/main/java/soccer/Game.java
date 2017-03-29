package soccer;

import java.util.ArrayList;
import java.util.List;

public class Game {

    public static final int FIRST_TEAM = 0;
    public static final int SECOND_TEAM = 1;
    private static final int SERIES = 5;
    private static final int BREAK_ON_POINTS_FOR_PRELIMINARIES = 3;
    private static final int LOSER_STATISTIC_BARRIER = 7;

    private String firstTeam;
    private String secondTeam;
    private int[] score = new int[2];
    private int[] kicksInSeries = new int[2];
    private boolean finished;
    private int teamStartedGame = -1;

    private List<Integer> loserList = new ArrayList<>();

    public Game() {
    }

    public Game(String firstTeam, String secondTeam) {
        setTeams(firstTeam, secondTeam);
    }

    public void setTeams(String firstTeam, String secondTeam) {
        this.firstTeam = firstTeam;
        this.secondTeam = secondTeam;
    }

    public boolean isFinished() {
        return finished;
    }

    public int[] kick(String player, int registeredTeam, boolean goal) {
        checkSequence(registeredTeam);
        if (goal)
            score[registeredTeam]++;
        kicksInSeries[registeredTeam]++;
        checkIsFinished();
        return loadLastTen(player);
    }

    private void checkSequence(int registeredTeam) {
        checkTeamIsRegisteredInGame(registeredTeam);
        if(teamStartedGame == -1) {
            teamStartedGame = registeredTeam;
        } else {
            if((kicksInSeries[FIRST_TEAM] == kicksInSeries[SECOND_TEAM]) == (registeredTeam != teamStartedGame)) {
                throw new IllegalStateException("Team can't shoot twice in a row.");
            }
        }

    }

    private void checkIsFinished() {
        if(finished)
            throw new IllegalStateException("Game already finished.");
        if(kicksInSeries[FIRST_TEAM] >= SERIES
                && kicksInSeries[SECOND_TEAM] >= SERIES
                && kicksInSeries[FIRST_TEAM] == kicksInSeries[SECOND_TEAM]
                && score[FIRST_TEAM] != score[SECOND_TEAM]) {
            finished = true;
        }
        if(kicksInSeries[FIRST_TEAM] < SERIES
                && kicksInSeries[SECOND_TEAM] < SERIES
                && kicksInSeries[FIRST_TEAM] == kicksInSeries[SECOND_TEAM]
                && Math.abs(score[FIRST_TEAM] - score[SECOND_TEAM]) == BREAK_ON_POINTS_FOR_PRELIMINARIES) {
            finished = true;
        }
    }

    private void checkTeamIsRegisteredInGame(int team) {
        if (team != FIRST_TEAM && team != SECOND_TEAM)
            throw new IllegalArgumentException("This team is not in the game!");
    }

    protected int[] loadLastTen(String player) {
        return null;
    }

    public int numberOfAttempt(int registeredTeam) {
        checkTeamIsRegisteredInGame(registeredTeam);
        return kicksInSeries[registeredTeam];
    }

    public int getScore(int registeredTeam) {
        checkTeamIsRegisteredInGame(registeredTeam);
        return score[registeredTeam];
    }

    public String getGameResult() {
        StringBuilder sb = new StringBuilder();
        sb.append("(").append(getScore(FIRST_TEAM)).append(")").append(firstTeam);
        if(checkTeamIsLoser(FIRST_TEAM))
            addCostOfLosersInOutput(sb, firstTeam);
        sb.append(":");
        if(checkTeamIsLoser(SECOND_TEAM))
            addCostOfLosersInOutput(sb, secondTeam);
        sb.append(secondTeam).append("(").append(getScore(SECOND_TEAM)).append(")");
        return sb.toString();
    }

    private void addCostOfLosersInOutput(StringBuilder sb, String teamName) {
        sb.append("[").append(costFailure(teamName)).append("]");
    }

    private boolean checkTeamIsLoser(int registeredTeam) {
        if (loserList.contains(registeredTeam))
            return true;
        if (numberOfAttempt(registeredTeam) > LOSER_STATISTIC_BARRIER
                && score[registeredTeam] < kicksInSeries[registeredTeam]) {
            loserList.add(registeredTeam);
            return true;
        }
        return false;
    }

    protected int costFailure(String team) {
         return 0;
    }
}
