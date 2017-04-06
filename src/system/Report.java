package system;

import java.io.Serializable;

/**
 * Created by mdls8 on 4/5/2017.
 */
public class Report implements Serializable {
    private String campaignName;
    private String winner;
    private int winVotes;
    private int totalVotes;

    public Report(String campaignName, String winner, int winVotes, int totalVotes) {
        this.campaignName = campaignName;
        this.winner = winner;
        this.winVotes = winVotes;
        this.totalVotes = totalVotes;
    }

    public String getCampaignName() {
        return campaignName;
    }

    public void setCampaignName(String campaignName) {
        this.campaignName = campaignName;
    }

    public String getWinner() {
        return winner;
    }

    public void setWinner(String winner) {
        this.winner = winner;
    }

    public int getWinVotes() {
        return winVotes;
    }

    public void setWinVotes(int winVotes) {
        this.winVotes = winVotes;
    }

    public int getTotalVotes() {
        return totalVotes;
    }

    public void setTotalVotes(int totalVotes) {
        this.totalVotes = totalVotes;
    }

    @Override
    public String toString() {
        return "Report{" +
                "campaignName='" + campaignName + '\'' +
                ", winner='" + winner + '\'' +
                ", winVotes=" + winVotes +
                ", totalVotes=" + totalVotes +
                '}';
    }
}
