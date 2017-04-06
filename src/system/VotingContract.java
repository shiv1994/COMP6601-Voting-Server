package system;

import system.CandidateContract;

/**
 * Created by mdls8 on 3/30/2017.
 */
public final class VotingContract {

    // private constructor to restrict instantiation
    private VotingContract(){

    }

    // table info
    public static final String TABLE_NAME = "Voting";
    public static final String VOTE_ID_COL = "VoteId";
    public static final String DATE_COL = "VoteTime";
    public static final String VOTE_CAST_COL = "CandidateId";
    public static final String CAMP_ID_COL = "CampaignId";

    // datatypes
    private static final String INT_TYPE = " INT";
    private static final String DATE_TYPE = " DATETIME";

    public static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + "(" +
            VOTE_ID_COL + INT_TYPE + " PRIMARY KEY AUTO_INCREMENT," +
            DATE_COL + DATE_TYPE + " NOT NULL DEFAULT CURRENT_TIMESTAMP, " +
            VOTE_CAST_COL + INT_TYPE + " NOT NULL, " +
            CAMP_ID_COL + INT_TYPE + " NOT NULL, " +
            "FOREIGN KEY(" + VOTE_CAST_COL + ")" + "REFERENCES " + CandidateContract.TABLE_NAME + "(" + CandidateContract.CAND_ID_COL + ")" +
            ");";

    public static String insertEntry(){
        String sql = "INSERT INTO " + TABLE_NAME + "(" + VOTE_CAST_COL + "," + CAMP_ID_COL + ") " + "VALUES(?,?);";

        return sql;
    }

}
