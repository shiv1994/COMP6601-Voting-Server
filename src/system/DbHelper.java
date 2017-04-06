package system;

import java.sql.*;
import java.util.ArrayList;

// maybe make this a Singleton Class
public final class DbHelper{

    // JDBC Driver name
    private static final String JDBC_DRIVER="com.mysql.jdbc.Driver";

    // Database names
    private static final String DB_NAME = "voting";

    // DB URLS
    private static final String DB_URL = "jdbc:mysql://localhost/" + DB_NAME;

    // DB Credentials - to be replaced by admin entering credentials
    private static final String USERNAME="root";
    private static final String PASSWORD="";

    private static boolean tablesCreated=false;

    private Connection connection=null;

    public DbHelper(){
        setUpConnection();
    }

    private void setUpConnection(){
        try{
            //registering JDBC driver
            Class.forName("com.mysql.jdbc.Driver");

            // opening a connection to database
            System.out.println("Connecting to database...");
            connection = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
            System.out.println("Connected to database...");
        }
        catch (SQLException e){
            System.out.println("SQL Error: " + e.getMessage());
        }
        catch (Exception e){
            System.out.println("Error: " + e.getMessage());
        }
    }

    public void closeConnection(){
        try {
            if (connection != null) {
                connection.close();
                System.out.println("Database connection closed.");
            }
        }
        catch(SQLException e){
            System.out.println("SQL Error: " + e.getMessage());
        }
    }

    public void createTables(){

        if(!tablesCreated){

            try{
                // check if tables already exist in event server went down
                DatabaseMetaData metaData = connection.getMetaData();
                ResultSet rs = metaData.getTables(null,null, "%", null);
                if(rs.next()){
                    System.out.println("Tables already exist");
                    // tables exist already so reset flag to true
                    tablesCreated = true;
                    return;
                }
            }
            catch (SQLException e){
                e.printStackTrace();
            }

            // database tables not yet created
            Statement stmt = null;
            try{
                // create all tables
                stmt = connection.createStatement();
                stmt.executeUpdate(CampaignContract.CREATE_TABLE);
                stmt.executeUpdate(CandidateContract.CREATE_TABLE);
                stmt.executeUpdate(VotingContract.CREATE_TABLE);
                stmt.executeUpdate(HistoryContract.CREATE_TABLE);
                tablesCreated = true;
                System.out.println("Tables successfully created.");
            }
            catch (SQLException e){
                //System.out.println("SQL Error: " + e.getMessage());
                e.printStackTrace();
            }
            finally{
                try{
                    if(stmt != null)
                        stmt.close();
                }
                catch (SQLException e){
                    System.out.println("SQL Error: " + e.getMessage());
                }
            }

        }
        else
            System.out.println("Tables already exist");
    }

    public boolean insertCampaign(Campaign campaign){
        PreparedStatement pstmt = null;

        try{
            // checking to see if there is a campaign that overlaps with one to be added
            String sql = "SELECT * FROM " + CampaignContract.TABLE_NAME + " WHERE " + CampaignContract.START_COL +
                    " >= ? AND " + CampaignContract.END_COL + " <= ?;";

            pstmt = connection.prepareStatement(sql);
            pstmt.setTimestamp(1,campaign.getStart());
            pstmt.setTimestamp(2,campaign.getEnd());

            ResultSet rs = pstmt.executeQuery();

            if(!rs.next()){
                // there are no conflicts so we can proceed to add this campaign to the database

                sql = CampaignContract.insertEntry(campaign);
                pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                pstmt.setString(1,campaign.getName());
                pstmt.setTimestamp(2,campaign.getStart());
                pstmt.setTimestamp(3,campaign.getEnd());

                pstmt.executeUpdate();

                // get auto generated key so that each candidate will match to this campaign
                rs = pstmt.getGeneratedKeys();

                if(rs.next()){
                    int genKey = rs.getInt(1);
                    insertCandidates(campaign.getCandidates(), genKey);
                }
                System.out.println("Campaign successfully added.");
                return true;
            }
            else{
                System.out.println("Campaign cannot be added as it overlaps with the time period of another campaign");
                return false;
            }
        }
        catch(SQLException e){
            //System.out.println("SQL ERROR: " + e.getMessage());
            e.printStackTrace();
        }
        finally {
            try{
                if(pstmt != null)
                    pstmt.close();
            }
            catch (SQLException e){
                //System.out.println(e.getMessage());
                e.printStackTrace();
            }
        }
        return false;
    }

    public void insertCandidates(ArrayList<Candidate> list, int key){
        PreparedStatement pstmt = null;

        try{
            for(Candidate temp : list){
                pstmt = connection.prepareStatement(CandidateContract.insertEntry());
                pstmt.setString(1,temp.getName());
                pstmt.setString(2,temp.getDescription());
                pstmt.setString(3,temp.getImg());
                pstmt.setInt(4,key);

                pstmt.executeUpdate();
            }
        }
        catch(SQLException e){
            //System.out.println("SQL ERROR: " + e.getMessage());
            e.printStackTrace();
        }
        finally {
            try{
                if(pstmt != null)
                    pstmt.close();
            }
            catch (SQLException e){
                //System.out.println(e.getMessage());
                e.printStackTrace();
            }
        }
    }

    public Campaign getActiveCampaign(){
        Campaign active=null;

        PreparedStatement pstmt = null;
        Statement stmt = null;

        try{
            // query to find the nearest campaign to become active
            String sql = "SELECT * FROM ( SELECT * FROM " + CampaignContract.TABLE_NAME + " WHERE " + CampaignContract.STAT_COL +
                    " != 1" + " ORDER BY " + CampaignContract.START_COL + " ASC) AS Camp" + " LIMIT 1;";

            stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            // there will only be one record found if any
            if(rs.next()){
                // record found
                int campID = rs.getInt(CampaignContract.CAMP_ID_COL);
                String campName = rs.getString(CampaignContract.NAME_COL);
                Timestamp startTime = rs.getTimestamp(CampaignContract.START_COL);
                Timestamp endTime = rs.getTimestamp(CampaignContract.END_COL);

                // create the campaign with relevant data
                active = new Campaign(campName, startTime, endTime);
                active.setId(campID);

                // now get the candidates associated to this campaign
                sql = "SELECT * FROM " + CandidateContract.TABLE_NAME + " WHERE " + CandidateContract.CAMP_ID_COL + " = ?;";

                pstmt = connection.prepareStatement(sql);
                pstmt.setInt(1,campID);

                rs = pstmt.executeQuery();

                while(rs.next()){
                    // add each candidate returned by query to the list of candidates for this campaign
                    String candName = rs.getString(CandidateContract.NAME_COL);
                    String desc = rs.getString(CandidateContract.DESCRIPTION_COL);
                    String img = rs.getString(CandidateContract.IMG_URL_COL);

                    //if(img != null)
                        active.addCandidate(candName,desc,img);
                }
            }
        }
        catch (SQLException e){
            e.printStackTrace();
        }

        finally {
            try{
                if(pstmt != null)
                    pstmt.close();
                if(stmt != null)
                    stmt.close();
            }
            catch (SQLException e){
                //System.out.println(e.getMessage());
                e.printStackTrace();
            }
        }

        return active;
    }

    public void closeCampaign(Campaign campaign){
        PreparedStatement pstmt = null;

        try{
            // SQL statement to set the status of the currently active campaign to 1 to indicate it has ended
            String sql = "UPDATE " + CampaignContract.TABLE_NAME + " SET " + CampaignContract.STAT_COL + " = 1 WHERE " +
                    CampaignContract.START_COL + " = ? AND " + CampaignContract.END_COL + " = ? AND " + CampaignContract.STAT_COL +
                    " = 0;";

            pstmt = connection.prepareStatement(sql);
            pstmt.setTimestamp(1,campaign.getStart());
            pstmt.setTimestamp(2,campaign.getEnd());

            pstmt.executeUpdate();
        }

        catch (SQLException e){
            e.printStackTrace();
        }

        finally {
            try{
                if(pstmt != null)
                    pstmt.close();
            }
            catch (SQLException e){
                //System.out.println(e.getMessage());
                e.printStackTrace();
            }
        }
    }

    public void placeVote(String candidate){
        PreparedStatement pstmt = null;

        try{
            // query to get info on this candidate
            String sql = "SELECT * FROM " + CandidateContract.TABLE_NAME + " WHERE " + CandidateContract.NAME_COL + " = ?;";

            pstmt = connection.prepareStatement(sql);
            pstmt.setString(1,candidate);

            ResultSet rs = pstmt.executeQuery();

            if(rs.next()){
                // pull candidate info
                int candId = rs.getInt(CandidateContract.CAND_ID_COL);
                int campId = rs.getInt(CandidateContract.CAMP_ID_COL);

                // now insert voting record into voting table
                sql = VotingContract.insertEntry();
                pstmt = connection.prepareStatement(sql);
                pstmt.setInt(1,candId);
                pstmt.setInt(2,campId);
                pstmt.executeUpdate();
            }
        }

        catch (SQLException e){
            e.printStackTrace();
        }
        finally {
            try{
                if(pstmt != null)
                    pstmt.close();
            }
            catch (SQLException e){
                //System.out.println(e.getMessage());
                e.printStackTrace();
            }
        }
    }

    public void processVotingResults(){
        PreparedStatement pstmt = null;

        try{
            // query to get the number of votes each candidate received in the recently ended campaign in or of highest to lowest
            String sql = "SELECT COUNT(" + VotingContract.VOTE_CAST_COL + ") AS NumVotes, " + VotingContract.VOTE_CAST_COL +
                    " FROM " + VotingContract.TABLE_NAME + " WHERE " + VotingContract.CAMP_ID_COL + " = ? " + "GROUP BY " +
                    VotingContract.VOTE_CAST_COL + " ORDER BY NumVotes DESC" + ";";

            int activeCampId = Server.getActive().getId();
            pstmt = connection.prepareStatement(sql);
            pstmt.setInt(1,activeCampId);

            ResultSet rs = pstmt.executeQuery();

            int firstCheck=0, totalVotes=0, winId=0;

            // iterate through results of voting campaign and get data to add to history table
            while(rs.next()){
                int numVotes = rs.getInt(1);
                int candID = rs.getInt(2);

                totalVotes += numVotes;

                if(firstCheck == 0){
                    // this is the winner
                    winId = candID;
                    firstCheck = 1;
                }

                System.out.println(numVotes + "\t" + candID);
            }

            // insert record of the winner and number of votes cast in the campaign into the history table
            sql = HistoryContract.insertEntry();
            pstmt = connection.prepareStatement(sql);
            pstmt.setInt(1,activeCampId);
            pstmt.setInt(2,winId);
            pstmt.setInt(3,totalVotes);

            pstmt.executeUpdate();

        }
        catch (SQLException e){
            e.printStackTrace();
        }
        finally {
            try{
                if(pstmt != null)
                    pstmt.close();
            }
            catch (SQLException e){
                //System.out.println(e.getMessage());
                e.printStackTrace();
            }
        }
    }

    public ArrayList<Report> getReports(){
        Statement stmt = null;
        ArrayList<Report> report = new ArrayList<>();

        try{
            // add table identifiers
            String sql = "SELECT " + "cam." + CampaignContract.NAME_COL + " can." + CandidateContract.NAME_COL + " " +
                    HistoryContract.WIN_VOTES_COL + " " + HistoryContract.VOTES_CAST_COL + " FROM " + CampaignContract.TABLE_NAME +
                    " cam " + CandidateContract.TABLE_NAME + " can " + HistoryContract.TABLE_NAME + " WHERE " +
                    HistoryContract.ID_COL + " = " + CampaignContract.CAMP_ID_COL + " AND " + HistoryContract.WIN_COL +
                    " = " + CandidateContract.CAND_ID_COL + " ORDER BY " + CampaignContract.END_COL + " ASC" + ";";

            stmt = connection.createStatement();

            ResultSet rs = stmt.executeQuery(sql);

            while(rs.next()){
                String campName = rs.getString(1);
                String candName = rs.getString(2);
                int winVotes = rs.getInt(3);
                int totVotes = rs.getInt(4);

                Report temp = new Report(campName,candName,winVotes,totVotes);
                report.add(temp);
            }

            return report;
        }
        catch (SQLException e){
            e.printStackTrace();
            return report;
        }
        finally {
            try{
                if(stmt != null)
                    stmt.close();
            }
            catch (SQLException e){
                e.printStackTrace();
            }
        }
    }
}