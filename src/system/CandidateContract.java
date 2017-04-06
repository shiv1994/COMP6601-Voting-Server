package system;

/**
 * Created by mdls8 on 3/30/2017.
 */
public final class CandidateContract {

    // private constructor to restrict instantiation
    private CandidateContract(){

    }

    // table info
    public static final String TABLE_NAME = "Candidates";
    public static final String CAND_ID_COL = "ID";
    public static final String NAME_COL = "Name";
    public static final String DESCRIPTION_COL = "Description";
    public static final String IMG_URL_COL = "IMG";
    public static final String CAMP_ID_COL = "CampaignID";

    // datatypes
    private static final String INT_TYPE = " INT";
    private static final String STRING_TYPE = " VARCHAR";

    public static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + "(" +
            CAND_ID_COL + INT_TYPE + " PRIMARY KEY AUTO_INCREMENT, " +
            NAME_COL + STRING_TYPE + "(25)" + " NOT NULL, " +
            DESCRIPTION_COL + STRING_TYPE + "(50)" + " NOT NULL, " +
            IMG_URL_COL + STRING_TYPE + "(100), " +
            CAMP_ID_COL + INT_TYPE + " NOT NULL, " +
            "FOREIGN KEY(" + CAMP_ID_COL + ") " + "REFERENCES " + CampaignContract.TABLE_NAME + "(" + CampaignContract.CAMP_ID_COL + ")" +
            ");";

    public static String insertEntry(){
        String sql = "INSERT INTO " + TABLE_NAME + "(" + NAME_COL + ", " + DESCRIPTION_COL + ", " + CAMP_ID_COL + ") " + "VALUES(?, ?, ?);";

        return sql;
    }
}