package system;

import java.util.TimerTask;

/**
 * Created by mdls8 on 4/4/2017.
 */
public class CampaignOver extends TimerTask {
    // helper class to handle automated running of task when a campaign is over

    public void run(){
        // handle determination of winner

        // schedule next timer to run to determine end of next campaign that is active
        Server.nextActiveCamp();
    }
}
