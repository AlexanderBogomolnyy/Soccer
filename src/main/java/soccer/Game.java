package soccer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Game {

    private Map<String, List<Byte>> teamMap = new HashMap<>();
    private String team1;
    private String team2;

    public Game() {
    }

    public Game(String team1, String team2) {
        setTeams(team1, team2);
    }

    public void setTeams(String team1, String team2) {
        this.team1 = team1;
        this.team2 = team2;
        teamMap.put(team1, new ArrayList<>());
        teamMap.put(team2, new ArrayList<>());
    }

    public boolean isFinished() {
        if (numberOfAttempt(team1) >= 5 && numberOfAttempt(team2) >= 5)
            return Math.abs(getScore(team1) - getScore(team2)) > 0;
        else
            return false;
    }

    public int[] kick(String player, String team, int i) {
        teamMap.get(team).add((byte) i);
        return loadLastTen(player);
    }

    protected int[] loadLastTen(String player) {
        return null;
    }

    public int numberOfAttempt(String team) {
        return teamMap.get(team).size();
    }

    public int getScore(String team) {
        int score = 0;
        for(Byte attempt: teamMap.get(team))
            score += attempt;
        return score;
    }

    public String result() {
        if (numberOfAttempt(team1) > 7 || numberOfAttempt(team2) > 7) {
            if (getScore(team1) != getScore(team2))
                if(getScore(team1) < getScore(team2)) {
                    return "(" + getScore(team1) + ")" + team1 + "[" + costFailure(team1) + "]:" + team2 + "(" + getScore(team2) + ")";
                } else {
                    return "(" + getScore(team1) + ")" + team1 + ":[" + costFailure(team2) + "]" + team2 + "(" + getScore(team2) + ")";
                }
        }
        return "(" + getScore(team1) + ")" + team1 + ":" + team2 + "(" + getScore(team2) + ")";
    }

    protected int costFailure(String team) {
         return 0;
    }
}
