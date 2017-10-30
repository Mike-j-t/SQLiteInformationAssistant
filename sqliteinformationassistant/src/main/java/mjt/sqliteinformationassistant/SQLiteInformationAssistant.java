package mjt.sqliteinformationassistant;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;
import static mjt.sqliteinformationassistant.SQLiteMasterConstants.*;


/**
 * Obtain SQLite Database Information for the App/package
 */

@SuppressWarnings("WeakerAccess")
public class SQLiteInformationAssistant {

    private static final String LOGTAG = "SQLITEDBINFO";
    public static final String INTENTKEY_BASE_BCKGRNDCOLOUR =
            "ik_base_bckgrndclolour";
    public static final String INTENTKEY_DATABASELIST_BCKGRNDCOLOUR =
            "ik_dblist_bckgrndcolour";
    public static final String INTENTKEY_DATABASELIST_HEADINGTEXTCOLOUR =
            "ik_dblist_headingtextcolour";
    public static final String INTENTKEY_DATABASELIST_TEXTCOLOUR =
            "ik_dblist_textcolour";
    public static final String INTENTKEY_TABLELIST_BCKGRNDCOLOUR =
            "ik_tblist_bckgrndcolour";
    public static final String INTENTKEY_TABLELIST_HEADINGTEXTCOLOUR =
            "ik_tblist_headingtextcolour";
    public static final String INTENTKEY_TABLELIST_TEXTCOLOUR =
            "ik_tblist_textcolour";
    public static final String INTENTKEY_COLUMNLIST_BCKGRNDCOLOUR =
            "ik_clist_bckgrndcolour";
    public static final String INTENTKEY_COLUMNLIST_HEADINGTEXTCOLOUR =
            "ik_clist_headingtextcolour";
    public static final String INTENTKEY_COLUMNLIST_TEXTCOLOUR =
            "ik_clist_textcolour";
    public static final String INTENTKEY_DATABASEINFO_BCKGRNDCOLOUR =
            "ik_dbinfo_bckgrndcolour";
    public static final String INTENTKEY_DATABASEINFO_TEXTCOLOUR =
            "ik_dbinfo_textcolour";
    public static final String INTENTKEY_TABLEINFO_BCKGRNDCOLOUR =
            "ik_tbinfo_bckgrndcolour";
    public static final String INTENTKEY_TABLEINFO_TEXTCOLOUR =
            "ik+tblist_textcolour";
    public static final String INTENTKEY_COLUMNINFO_BCKGRNDCOLOUR =
            "ik_cinfo_bckgrndcolour";
    public static final String INTENTKEY_COLUMNINFO_TEXTCOLOUR =
            "ik_cinfo_textcolour";
    public static final String INTENKEY_HEADINGTEXTCCOLOUR =
            "ik_headingtextcolour";
    public static final String INTENTKEY_STRINGCELL_BCKGRNDCOLOUR =
            "ik_stringcell_bckgrndcolour";
    public static final String INTENTKEY_STRINGCELL_TEXTCOLOUR =
            "ik_stringcell_textcolour";
    public static final String INTENTKEY_INTEGERCELL_BCKGRNDCOLOUR =
            "ik_integercell_bckgrndcolour";
    public static final String INTENTKEY_INTEGERCELL_TEXTCOLOUR =
            "ik_inetegercell_textcolour";
    public static final String INTENTKEY_DOUBLECELL_BCKGRNDCOLOUR =
            "ik_doublecell_bckgrndcolour";
    public static final String INTENTKEY_DOUBLECELL_TEXTCOLOUR =
            "ik_doublecell_textcolour";
    public static final String INTENTKEY_BLOBCELL_BCKGRNDCOLOUR =
            "ik_blobcell_bckgrndcolour";
    public static final String INTENTKEY_BLOBCELL_TEXTCOLOUR =
            "ik_blobcell_textcolour";
    public static final String INTENTKEY_UNKNOWNCELL_BCKGRNDCOLOUR =
            "ik_unknowncell_bckgrndcolour";
    public static final String INTENTKEY_UNKNOWNCELL_TEXTCOLOUR =
            "ik_unknowncell_textcolour";



    Context mContext;
    int mBaseBackgroundColour,
            mHeadingTextColour,
            mDatabaseListBackgroundColour,
            mDatabaseListHeadingTextCColour,
            mDatabaseListTextColour,
            mDatabaseInfoBackgroundColour,
            mDatabaseInfoTextColour,
            mTableListBackgroundColour,
            mTableListHeadingTextColour,
            mTableListTextColour,
            mTableInfoBackgroundColour,
            mTableInfoTextColour,
            mColumnListBackgroundColour,
            mColumnListHeadingTextColour,
            mColumnListTextColour,
            mColumnInfoBackgroundColour,
            mColumnInfoTextColour,
            mStringCellBackgroundColour,
            mIntegerCellBackgroundColour,
            mDoubleCellBackgroundColour,
            mBlobCellBackgroundColour,
            mUnknownBackgroundCellColour,
            mStringCellTextColour,
            mIntegerCelltextColour,
            mDoubleCelltextColour,
            mBlobCelltextColour,
            mUnkownCelltextColour;

    /**
     * Constructor (short form) for an SQLiteInformationAssistant instance;
     * Note invokes the full form with logmode defaulting to false.
     *
     * @param context   The context this is to be run in, needed for
     *                  SQLiteDatabase access.
     */
    public SQLiteInformationAssistant(Context context) {

        this(context,false);
    }

    /**
     * Constructor (longer form) for an SQLiteInformationAssistant instance.
     *
     * @param context   The context this is to be run in, needed for
     *                  SQLiteDatabase access.
     * @param logmode   A boolean, if true, that turns on Logging;
     *                  WARNING this can result in a lot of lines
     *                  being written to the log.
     */
    public SQLiteInformationAssistant(Context context, boolean logmode) {

        this.mContext = context;
        // Set Default Display Colours as per color resources
        // Base colours
        mBaseBackgroundColour = ContextCompat.getColor(
                mContext,
                R.color.default_basebackground
        );
        mHeadingTextColour =
                ContextCompat.getColor(mContext,R.color.default_text_color);
        // Database dispay colours
        mDatabaseListBackgroundColour = ContextCompat.getColor(
                mContext,
                R.color.default_database_colour
        );
        mDatabaseListHeadingTextCColour = mHeadingTextColour;
        mDatabaseListTextColour = mHeadingTextColour;
        mDatabaseInfoBackgroundColour = mDatabaseListBackgroundColour;
        mDatabaseInfoTextColour = mHeadingTextColour;

        // Table display colours
        mTableListBackgroundColour = ContextCompat.getColor(
                mContext,
                R.color.default_table_colour
        );
        mTableListHeadingTextColour = mHeadingTextColour;
        mTableListTextColour = mHeadingTextColour;
        mTableInfoBackgroundColour = mTableListBackgroundColour;
        mTableInfoTextColour = mHeadingTextColour;

        // Column Display colours
        mColumnListBackgroundColour = ContextCompat.getColor(
                mContext,
                R.color.default_column_colour
        );
        mColumnListHeadingTextColour = mHeadingTextColour;
        mColumnListTextColour = mHeadingTextColour;
        mColumnInfoBackgroundColour = mColumnListBackgroundColour;
        mColumnInfoTextColour = mHeadingTextColour;

        // DataViewer Display Colours (for column types)
        mStringCellBackgroundColour =
                ContextCompat.getColor(mContext,R.color.default_string_cell);
        mStringCellTextColour = mHeadingTextColour;
        mIntegerCellBackgroundColour =
                ContextCompat.getColor(mContext,R.color.default_integer_cell);
        mIntegerCelltextColour = mHeadingTextColour;
        mDoubleCellBackgroundColour =
                ContextCompat.getColor(mContext,R.color.default_double_cell);
        mDoubleCelltextColour = mHeadingTextColour;
        mBlobCellBackgroundColour =
                ContextCompat.getColor(mContext,R.color.default_blob_cell);
        mBlobCelltextColour = mHeadingTextColour;
        mUnknownBackgroundCellColour =
                ContextCompat.getColor(mContext,R.color.default_unknown_cell);
        mUnkownCelltextColour = mHeadingTextColour;
;

        // If logging Mode is on then report on databases found
        if (logmode) {
            for (DatabaseInfo di: getDatabaseList()) {
                Log.d(LOGTAG,"Found and Stored Database = " +
                        di.getname() +
                        " with " +
                        Integer.toString(di.getTableList().size()) +
                        " tables."
                );
                ArrayList<DoubleString> databaseinfo = di.getDBInfoAsDoubleStringArrayList();
                for (DoubleString ds: databaseinfo) {
                    Log.d(LOGTAG,
                            ds.getString1() +
                                    " has a value of " +
                                    ds.getString2()
                    );
                }
                for (TableInfo ti: di.getTableList()) {
                    Log.d(LOGTAG,"\tFound Table = " +
                            ti.getTableName() +
                            " with " +
                            Integer.toString(ti.getColumnList().size()) +
                            " columns."
                    );
                    logTableRowData(di,ti);
                    for (ColumnInfo ci: ti.getColumnList()) {
                        Log.d(LOGTAG,"\t\tFound Column = " +
                                ci.getColumnName() +
                                " Type = " +
                                ci.getColumnType() +
                                " NOT NUll = " +
                                ci.getColumnNotNullAsString() +
                                " DEFAULT VALUE = " +
                                ci.getColumnDefaultValue() +
                                " PRIMARY KEY POSITION = " +
                                Integer.toString(ci.getColumnPrimaryKeyPosition())
                        );
                    }
                }
            }
        }
    }

    public void setBaseBackgroundColour(int colour) { mBaseBackgroundColour = colour; }
    public void setHeadingTextColour(int colour) {
        mHeadingTextColour = colour;
    }
    public void setDatabaseListBackgroundColour(int colour) { mDatabaseListBackgroundColour = colour;}
    public void setDatabaseListHeadingTextColour(int colour) { mDatabaseListHeadingTextCColour = colour; }
    public void setDatabaseListTextColour(int colour) { mDatabaseListTextColour = colour;}
    public void setDatabaseInfoBackgroundColour(int colour) { mDatabaseInfoBackgroundColour = colour;}
    public void setDatabaseInfoTextColour(int colour) { mDatabaseInfoTextColour = colour; }
    public void setTableListBackgroundColour(int colour) { mTableListBackgroundColour = colour; }
    public void setTableListHeadingTextColour(int colour) { mTableListHeadingTextColour = colour; }
    public void setTableListTextColour(int colour) { mTableListTextColour = colour; }
    public void setTableInfoBackgroundColour(int colour) { mTableInfoBackgroundColour = colour; }
    public void setTableInfoTextcolour(int colour) { mTableInfoTextColour = colour; }
    public void setColumnListBackgroundColour(int colour) { mColumnListBackgroundColour = colour; }
    public void setColumnListHeadingTextColour(int colour) { mColumnListHeadingTextColour = colour; }
    public void setColumnListTextColour(int colour) { mColumnListTextColour = colour; }
    public void setColumnInfoBackgroundColour(int colour) { mColumnInfoBackgroundColour = colour; }
    public void setColumnInfoTextColour(int colour) { mColumnInfoTextColour = colour; }
    public void setStringCellBackgroundColour(int colour) {
        mStringCellBackgroundColour = colour;
    }
    public void setStringCellTextColour(int colour) {
        mStringCellTextColour = colour;
    }
    public void setIntegerCellBackgroundColour(int colour) {
        mIntegerCellBackgroundColour = colour;
    }
    public void setIntegerCelltextColour(int colour) {
        mIntegerCelltextColour = colour;
    }
    public void setDoubleCellBackgroundColour(int colour) {
        mDoubleCellBackgroundColour = colour;
    }
    public void setDoubleCelltextColour(int colour) {
        mDoubleCelltextColour = colour;
    }
    public void setBlobCellBackgroundColour(int colour) {
        mBlobCellBackgroundColour = colour;
    }
    public void setBlobCelltextColour(int colour) {
        mBlobCelltextColour = colour;
    }
    public void setUnknownBackgroundCellColour(int colour) {
        mUnknownBackgroundCellColour = colour;
    }
    public void setUnkownCelltextColour(int colour) {
        mUnkownCelltextColour = colour;
    }

    public void show() {
        Intent intent = new Intent(mContext,SQLiteViewerActivity.class);

        // Prepare to pass display colours
        // Overall
        intent.putExtra(INTENKEY_HEADINGTEXTCCOLOUR,
                mHeadingTextColour);
        intent.putExtra(INTENTKEY_BASE_BCKGRNDCOLOUR,
                mBaseBackgroundColour);

        // Database List and Info Display colours
        intent.putExtra(INTENTKEY_DATABASELIST_BCKGRNDCOLOUR,
                mDatabaseListBackgroundColour);
        intent.putExtra(INTENTKEY_DATABASELIST_HEADINGTEXTCOLOUR,
                mDatabaseListHeadingTextCColour);
        intent.putExtra(INTENTKEY_DATABASELIST_TEXTCOLOUR,
                mDatabaseListTextColour);
        intent.putExtra(INTENTKEY_DATABASEINFO_BCKGRNDCOLOUR,
                mDatabaseInfoBackgroundColour);
        intent.putExtra(INTENTKEY_DATABASEINFO_TEXTCOLOUR,
                mDatabaseInfoTextColour);

        // Table List and Info Display colours
        intent.putExtra(INTENTKEY_TABLELIST_BCKGRNDCOLOUR,
                mTableListBackgroundColour);
        intent.putExtra(INTENTKEY_TABLELIST_HEADINGTEXTCOLOUR,
                mTableListHeadingTextColour);
        intent.putExtra(INTENTKEY_TABLELIST_TEXTCOLOUR,
                mTableListTextColour);
        intent.putExtra(INTENTKEY_TABLEINFO_BCKGRNDCOLOUR,
                mTableInfoBackgroundColour);
        intent.putExtra(INTENTKEY_TABLEINFO_BCKGRNDCOLOUR,
                mTableInfoTextColour);

        // Column List and Info Display colours
        intent.putExtra(INTENTKEY_COLUMNLIST_BCKGRNDCOLOUR,
                mColumnListBackgroundColour);
        intent.putExtra(INTENTKEY_COLUMNLIST_HEADINGTEXTCOLOUR,
                mColumnListHeadingTextColour);
        intent.putExtra(INTENTKEY_COLUMNLIST_TEXTCOLOUR,
                mColumnListTextColour);
        intent.putExtra(INTENTKEY_COLUMNINFO_BCKGRNDCOLOUR,
                mColumnInfoBackgroundColour);
        intent.putExtra(INTENTKEY_COLUMNINFO_TEXTCOLOUR,
                mColumnInfoTextColour);

        // Data Viewer Type colours
        intent.putExtra(INTENTKEY_STRINGCELL_BCKGRNDCOLOUR,
                mStringCellBackgroundColour);
        intent.putExtra(INTENTKEY_STRINGCELL_TEXTCOLOUR,
                mStringCellTextColour);
        intent.putExtra(INTENTKEY_INTEGERCELL_BCKGRNDCOLOUR,
                mIntegerCellBackgroundColour);
        intent.putExtra(INTENTKEY_INTEGERCELL_TEXTCOLOUR,
                mIntegerCelltextColour);
        intent.putExtra(INTENTKEY_DOUBLECELL_BCKGRNDCOLOUR,
                mDoubleCellBackgroundColour);
        intent.putExtra(INTENTKEY_DOUBLECELL_TEXTCOLOUR,
                mDoubleCelltextColour);
        intent.putExtra(INTENTKEY_BLOBCELL_BCKGRNDCOLOUR,
                mBlobCellBackgroundColour);
        intent.putExtra(INTENTKEY_BLOBCELL_TEXTCOLOUR,
                mBlobCelltextColour);
        intent.putExtra(INTENTKEY_UNKNOWNCELL_BCKGRNDCOLOUR,
                mUnknownBackgroundCellColour);
        intent.putExtra(INTENTKEY_UNKNOWNCELL_TEXTCOLOUR,
                mUnkownCelltextColour);

        mContext.startActivity(intent);
    }

    /**
     * Retrieve a list of the Database for the App/package
     *
     * @return An ArrayList of DatabaseInfo objects;
     *          Note each will include a list of TableInfo objects and
     *          each TableInfo object will incluse a list of ColumnInfo
     *          objects.
     */
    private ArrayList<DatabaseInfo> getDatabaseList() {
        ArrayList<DatabaseInfo> rv = new ArrayList<>();
        File db_directory = new File(mContext.getDatabasePath("x").getParent());
        String[] filelist = db_directory.list();
        for (String s: filelist) {
            if(!(s.contains("-journal"))) {
                rv.add(new DatabaseInfo(mContext,s));
            }
        }
        return rv;
    }

    /**
     * Write the data stored in a table to the LogCat;
     * Note! this is very much redundant as the DataViewer display data
     * @param di    The DatabaseInfo object that owns the table
     * @param ti    The TableInfo object for the table
     */
    private void logTableRowData(DatabaseInfo di, TableInfo ti) {

        // Open the Database
        SQLiteDatabase db =
                SQLiteDatabase.openDatabase(
                        di.getPath(),
                        null,
                        SQLiteDatabase.OPEN_READONLY
                );
        // Get the data from the table
        Cursor csr = getAllRowsFromTable(db,ti.getTableName(),true,null);
        logCursorData(csr);
        csr.close();
        db.close();
    }

    /**
     * Generic get all rows from an SQlite table,
     * allowing the existence of the table to be checked and also
     * allowing the ROWID to be added AS a supplied string
     *
     * @param db                    The SQLiteDatabase
     * @param tablename             The name of the table from which the
     *                              returned cursor will be created from;
     *                              Note!
     * @param use_error_checking    Whether ot not to try to detect errors
     *                              currently just table doesn't exist,
     *                              true to turn on, false to turn off
     *                              ERROR_CHECKING_ON = true
     *                              ERROR_CHECKING_OFF = false
     * @param forceRowidAs          If length of string passed is 1 or greater
     *                              then a column, as an alias of ROWID, will be
     *                              added to the cursor
     * @return                      the extracted cursor, or in the case of the
     *                              underlying table not existing an empty cursor
     *                              with no columns
     */
    public static Cursor getAllRowsFromTable(SQLiteDatabase db,
                                             String tablename,
                                             @SuppressWarnings("SameParameterValue") boolean use_error_checking,
                                             @SuppressWarnings("SameParameterValue") String forceRowidAs) {
        String[] columns = null;

        // Tablename must be at least 1 character in length
        if (tablename.length() < 1) {
            Log.d(LOGTAG,new Object(){}.getClass().getEnclosingMethod().getName() +
                    " is finishing as the provided tablename is less than 1 character in length"
            );
            return new MatrixCursor(new String[]{});
        }

        // If use_error_checking is true then check that the table exists
        // in the sqlite_master table
        if (use_error_checking) {
            Cursor chkcsr = db.query(
                    SQLITE_MASTER,
                    null,
                    SM_TABLE_TYPE_COLUMN + "=? AND "
                            + SM_TABLENAME_COLUMN + "=?",
                    new String[]{SM_TYPE_TABLE,tablename},
                    null,null,null
            );

            // Ooops table is not in the Database so return an empty
            // column-less cursor
            if (chkcsr.getCount() < 1) {
                Log.d(LOGTAG,"Table " + tablename +
                        " was not located in the SQLite Database Master Table."
                );
                // return empty cursor with no columns
                return new MatrixCursor(new String[]{});

            }
            chkcsr.close();
        }

        // If forcing an alias of ROWID then user ROWID AS ???, *
        if(forceRowidAs != null && forceRowidAs.length() > 0) {
            columns = new String[]{"rowid AS " +forceRowidAs,"*"};
        }

        // Finally return the Cursor but trap any exceptions
        try {
            return db.query(tablename, columns, null, null, null, null, null);
        } catch (Exception e) {
            Log.d(LOGTAG,"Exception encountered but trapped when querying table " + tablename +
                    " Message was: \n" + e.getMessage());
            Log.d(LOGTAG,"Stacktrace was:");
            e.printStackTrace();
            return new MatrixCursor(new String[]{});
        }
    }

    /**
     * Write the contents of the Cursor to the log
     * @param csr   The Cursor that is to be displayed in the log
     */
    public static void logCursorData(Cursor csr) {
        int columncount = csr.getColumnCount();
        int rowcount = csr.getCount();
        int csrpos = csr.getPosition();
        Log.d(LOGTAG,
                new Object(){}.getClass().getEnclosingMethod().getName() +
                        " Cursor has " +
                        Integer.toString(rowcount) +
                        " rows with " +
                        Integer.toString(columncount) + " columns."
        );
        csr.moveToPosition(-1);     //Ensure that all rows are retrieved
        while (csr.moveToNext()) {
            String unobtainable = "unobtainable!";
            String logstr = "Information for row " + Integer.toString(csr.getPosition() + 1) + " offset=" + Integer.toString(csr.getPosition());
            for (int i=0; i < columncount;i++) {
                logstr = logstr + "\n\tFor Column " + csr.getColumnName(i);
                switch (csr.getType(i)) {
                    case Cursor.FIELD_TYPE_NULL:
                        logstr = logstr + " Type is NULL";
                        break;
                    case Cursor.FIELD_TYPE_FLOAT:
                        logstr = logstr + " Type is FLOAT";
                        break;
                    case Cursor.FIELD_TYPE_INTEGER:
                        logstr = logstr + " Type is INTEGER";
                        break;
                    case Cursor.FIELD_TYPE_STRING:
                        logstr = logstr + " Type is STRING";
                        break;
                    case Cursor.FIELD_TYPE_BLOB:
                        logstr = logstr + " Type is BLOB";
                        break;
                }
                String strval_log = " value as String is ";
                String lngval_log = " value as long is ";
                String dblval_log = " value as double is ";
                String blbval_log = "";
                try {
                    strval_log = strval_log + csr.getString(i);
                    lngval_log = lngval_log + csr.getLong(i);
                    dblval_log = dblval_log +  csr.getDouble(i);
                } catch (Exception e) {
                    strval_log = strval_log + unobtainable;
                    lngval_log = lngval_log + unobtainable;
                    dblval_log = dblval_log + unobtainable;
                    try {
                        blbval_log = " value as blob is " +
                                getBytedata(csr.getBlob(i),24);
                    } catch (Exception e2) {
                        e2.printStackTrace();
                    }

                }
                logstr = logstr + strval_log + lngval_log + dblval_log + blbval_log;
            }
            Log.d(LOGTAG,logstr);
        }
        csr.moveToPosition(csrpos); // restore cursor position
    }

    /**
     * Return a hex string of the given byte array
     * @param bytes     The byte array to be converted to a hexadecimal string
     * @param limit     the maximum number of bytes;
     *                  note returned string will be up to twice as long
     * @return          The byte array represented as a hexadecimal string
     */
    public static String getBytedata(byte[] bytes, int limit) {
        if (bytes.length < limit) {
            return convertBytesToHex(bytes);
        } else {
            byte[] subset = new byte[limit];
            System.arraycopy(bytes,0,subset,0,limit);
            return convertBytesToHex(subset);
        }
    }

    // HEX characters as a char array for use by convertBytesToHex
    private final static char[] hexarray = "0123456789ABCDEF".toCharArray();

    /**
     * Return a hexadecimal string representation of the passed byte array
     * @param bytes     The byte array to be represented.
     * @return          The string representing the byte array as hexadecimal
     */
    private static String convertBytesToHex(byte[] bytes) {
        char[] hexstr = new char[bytes.length * 2];
        for (int i=0; i < bytes.length; i++) {
            int h = bytes[i] & 0xFF;
            hexstr[i * 2] = hexarray[h >>> 4];
            hexstr[i * 2 + 1] = hexarray[h & 0xF];
        }
        return new String(hexstr);
    }
}
