package system;

import java.io.IOException;
import java.io.Serializable;
import java.net.ServerSocket;
import java.rmi.Naming;
import java.rmi.server.RMIServerSocketFactory;
import java.util.Timer;
import javax.net.ServerSocketFactory;
import javax.net.ssl.SSLServerSocketFactory;

import AdminVoting.AdminVotingInterface;
import ClientVoting.ClientVotingInterface;
import sun.rmi.registry.RegistryImpl;

public class Server implements RMIServerSocketFactory, Serializable{
	// Singleton Class

	private static Server server=null;
	private static Campaign active;

	public static Server getInstance(){
		// uses double checked locking to make sure only one thread can execute at a time and not unnecessarily locking the method without first checking if it needs to be locked
		if(server == null){
			// we now give thread that reached this position lock, otherwise no lock is necessary
			synchronized (Server.class){
				if(server == null){
					server = new Server();
				}
			}
		}
		return server;
	}

	// private constructor to restrict instantiation to within this class
	private Server(){

//		try{
//
//		}
//		catch(RemoteException e){
//			System.out.println(e);
//		}

		try{
			RegistryImpl impl = new RegistryImpl(5000);
			RegistryImpl impl2 = new RegistryImpl(5001);
			ClientVotingInterface stub = (ClientVotingInterface) new ClientVotingRemote(5000);
			Naming.rebind("rmi://localhost:5000/voting", stub);

			AdminVotingInterface stub2 = (AdminVotingInterface) new AdminVotingRemote(5001);
			Naming.rebind("rmi://localhost:5001/voting", stub2);
			System.out.println("Server up.");
		}
		catch(Exception e){
			System.out.println(e);
		}

		// setup database
		DbHelper dbHelper = new DbHelper();
		dbHelper.createTables();

		// fetch first active campaign
		active = dbHelper.getActiveCampaign();
		dbHelper.closeConnection();

		// schedule end of first active campaign
		if(active != null){
			// server actually has campaign to load when it comes online so schedule its end
			scheduleEndCampaign();
		}
		else
			System.out.println("No active campaign to be loaded.");
	}

	public static void main(String args[]){
		server = getInstance();
	}

	public ServerSocket createServerSocket(int port) throws IOException{
		ServerSocketFactory factory = SSLServerSocketFactory.getDefault();
		ServerSocket socket = factory.createServerSocket(port);
		return socket;
	}

	public static void nextActiveCamp(){
		// closes current campaign and gets the next active campaign if there is one

		DbHelper dbHelper = new DbHelper();
		dbHelper.processVotingResults();
		dbHelper.closeCampaign(active);
		active = dbHelper.getActiveCampaign();
		dbHelper.closeConnection();

		if(active != null){
			// there is another campaign available we want to schedule its end
			System.out.println(active);
			scheduleEndCampaign();
		}
		else
			System.out.println("no more campaigns available to be loaded.");

	}

	public static Campaign getActive(){
		return active;
	}

	private static void scheduleEndCampaign(){
		// for the active campaign, this method sets a task to run automatically when that campaign has ended
		CampaignOver ended = new CampaignOver();

		// creating new Timer
		Timer timer = new Timer();

		// scheduling task to run when active campaign has ended
		timer.schedule(ended, active.getEnd());
	}
}