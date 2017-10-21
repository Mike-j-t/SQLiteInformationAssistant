package mjt.sqliteinformationassistant;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.database.sqlite.SQLiteDatabase;
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

    final Context mContext;

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

    public void show() {
        Intent intent = new Intent(mContext,SQLiteViewerActivity.class);
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

    private void logTableRowData(DatabaseInfo di, TableInfo ti) {

        SQLiteDatabase db = SQLiteDatabase.openDatabase(di.getPath(),null,SQLiteDatabase.OPEN_READONLY);
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
    private static String getBytedata(byte[] bytes, @SuppressWarnings("SameParameterValue") int limit) {
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
