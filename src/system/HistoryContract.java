package system;

/**
 * Created by mdls8 on 4/5/2017.
 */
public final class HistoryContract {

    // private constructor to restrict instantiation
    private HistoryContract(){

    }

    // table info
    public static final String TABLE_NAME = "History";
    public static final String ID_COL = "HistoryId";
    public static final String WIN_COL = "Winner";
    public static final String VOTES_CAST_COL = "VotesCast";

    // datatypes
    private static final String INT_TYPE = " INT";

    public static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + "(" +
            ID_COL + INT_TYPE + " PRIMARY KEY, " +
            WIN_COL + INT_TYPE + " NOT NULL, " +
            VOTES_CAST_COL + INT_TYPE + " NOT NULL, " +
            "FOREIGN KEY(" + WIN_COL + ")" + "REFERENCES " + CandidateContract.TABLE_NAME + "(" + CandidateContract.CAND_ID_COL + "), " +
            "FOREIGN KEY(" + ID_COL + ")" + "REFERENCES " + CampaignContract.TABLE_NAME + "(" + CampaignContract.CAMP_ID_COL + ") " +
            ");";

    public static String insertEntry(){
        String sql = "INSERT INTO " + TABLE_NAME + "(" + ID_COL + "," + WIN_COL + "," + VOTES_CAST_COL + ") " + "VALUES(?,?,?);";

        return sql;
    }
}