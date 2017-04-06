package system;

/**
 * Created by mdls8 on 3/30/2017.
 */
public final class CampaignContract {

    // private constructor to restrict instantiation
    private CampaignContract(){

    }

    // table info
    public static final String TABLE_NAME = "Campaigns";
    public static final String CAMP_ID_COL = "ID";
    public static final String NAME_COL = "Name";
    public static final String START_COL = "Start";
    public static final String END_COL = "End";
    public static final String STAT_COL = "Status";

    // datatypes
    private static final String INT_TYPE = " INT";
    private static final String DATE_TYPE = " DATETIME";
    private static final String STRING_TYPE = " VARCHAR";

    public static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + "(" +
            CAMP_ID_COL + INT_TYPE + " PRIMARY KEY AUTO_INCREMENT, " +
            NAME_COL + STRING_TYPE + "(25)" + " NOT NULL, " +
            START_COL + DATE_TYPE + " NOT NULL, " +
            END_COL + DATE_TYPE + " NOT NULL, " +
            STAT_COL + INT_TYPE + " DEFAULT 0 NOT NULL" +
            ");";

    public static String insertEntry(Campaign campaign){
        String sql = "INSERT INTO " + TABLE_NAME + "(" + NAME_COL + ", " + START_COL + ", " + END_COL + ") " + "VALUES(?, ?, ?);";

        return sql;
    }
}
