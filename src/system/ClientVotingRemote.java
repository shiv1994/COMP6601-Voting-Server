package system;

import ClientVoting.ClientVotingInterface;
import system.Campaign;
import system.Candidate;
import system.DbHelper;
import system.Server;

import java.rmi.*;
import java.rmi.server.*;
import java.sql.Timestamp;
import java.util.*;

public class ClientVotingRemote extends UnicastRemoteObject implements ClientVotingInterface {

	public ClientVotingRemote(int port) throws RemoteException{
		super(port);
	} 

	public ArrayList<Candidate> fetchCandidates() throws RemoteException{
		// returns all candidates associated with current campaign, if one exists
		Campaign activeCamp = Server.getActive();

		if(activeCamp != null)
			return activeCamp.getCandidates();
		return null;
	}

	public boolean castVote(String candidate) throws RemoteException{
		// check to see if voting is open for active campaign on server

		if(votingOpen()){
			// client's vote is allowed to go through

			DbHelper dbHelper = new DbHelper();
			dbHelper.placeVote(candidate);
			dbHelper.closeConnection();

			return true;
		}
		return false;
	}

	public boolean votingOpen(){
		Campaign active = Server.getActive();

		// get current date and time
		Timestamp curr = new Timestamp(System.currentTimeMillis());

		if(!active.getStart().after(curr) && !active.getEnd().before(curr)){
			// current date and time is between the start and end times (inclusive) of campaign
			return true;
		}

		System.out.println("Voting period not yet open. Opens at: " + active.getStart());

		return false;
	}

	public boolean canVote() throws RemoteException{
		// returns true if the current time is within
		Timestamp curr = new Timestamp(System.currentTimeMillis());

		Timestamp currCampStartTime = Server.getActive().getStart();

		if(!curr.before(currCampStartTime))
			return true;
		return false;
	}
}