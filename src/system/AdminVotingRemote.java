package system;

import AdminVoting.AdminVotingInterface;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;

import system.*;

/**
 * Created by mdls8 on 4/5/2017.
 */
public class AdminVotingRemote extends UnicastRemoteObject implements AdminVotingInterface {

    public AdminVotingRemote(int port) throws RemoteException {
        super(port);
    }

    public boolean addCampaign(Campaign campaign) throws RemoteException{
        System.out.println(campaign.toString());
        DbHelper dbHelper = new DbHelper();
        Boolean result = dbHelper.insertCampaign(campaign);
        dbHelper.closeConnection();
        return result;
    }

    public ArrayList<Report> getReports() throws RemoteException{
        DbHelper dbHelper = new DbHelper();
        ArrayList<Report> report = dbHelper.getReports();
        dbHelper.closeConnection();
        return report;
    }
}
