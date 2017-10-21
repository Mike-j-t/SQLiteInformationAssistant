package mjt.sqliteinformationassistant;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.io.File;
import java.util.ArrayList;
import static mjt.sqliteinformationassistant.SQLitePragmaConstants.*;
import static mjt.sqliteinformationassistant.SQLiteMasterConstants.*;

/**
 * A class for defining a DatabaseInfo object that holds much of
 * the data available
 */

@SuppressWarnings("WeakerAccess")
class DatabaseInfo {

    private final String mDBName;
    private final String mDBPath;
    private int mDBUserVersion;
    private String mDBAutoVacuum = "0";
    private int mDBBusyTimeout = 0;
    private long mDBCacheSize = 0;
    private String mDBCacheSpill = "";
    private boolean mDBCaseSensitiveLike = false;
    private boolean mDBCellSizeCheck = false;
    private boolean mDBCheckPointFullSync = false;
    private final ArrayList<String>
            mDBCompileOptions = new ArrayList<>();
    private boolean mDBDeferForeignKeys = false;
    private boolean mDBForeignKeyEnforcement = false;
    private String mDBEncoding = "";
    private int mDBFreeListCount = 0;
    private boolean mDBFullSync = false;
    private boolean mDBExists = false;
    private boolean mDBIgnoreCheckConstraints = false;
    private String mDBJournalMode = "";
    private int mDBJournalSizeLimit = 0;
    private String mDBLockingMode = "";
    private int mDBMaxPageCount = 0;
    private int mDBMMapSize = 0;
    private int mDBPageCount = 0;
    private int mDBPageSize = 0;
    private boolean mDBReadUncomitted = false;
    private boolean mDBRecursiveTriggers = false;
    private boolean mDBReverseUnorderdSelects = false;
    private String mDBSecureDelete = "";
    private int mDBSoftHeapLimit = 0;
    private int mDBSynchronous = 0;
    private int mDBTempStore = 0;
    private int mDBThreads = 0;
    private int mDBWALAutoCheckpoint = 0;
    private final ArrayList<TableInfo> mTableList;

    /**
     * Construct a DatabaseInfo object, obtaining the data from the database;
     * Obtaining the databasse information is a two stag process;
     * First the tables owned by the database are extracted from the
     * sqlite_master database and;
     * Second numnerous PRAGMA's are run to extract databae information;
     * The first step will generate a list of TableInfo objects, each of which
     * will have a list of ColumnInfo objects
     *
     * @param context       the context to be used when opening the database
     * @param databasename  the name of the database to be opened.
     */
    DatabaseInfo(Context context, String databasename) {
        this.mDBName = databasename;
        mDBPath = context.getDatabasePath(databasename).getPath();
        File dbfile = context.getDatabasePath(databasename);
        mDBExists = dbfile.exists();
        // Get the tables owned by the database as a list of TableInfo objects
        // (Tableinfo object will have a list of ColumnInfo objects)
        mTableList = getOwnedTableList();
        // get the Database information via running PRAGMA's
        setDBDetails();
    }

    /**
     * Create the list of TableInfo objects according to the tables in the database;
     * The tables are determined by accessing the sqlite_master table
     * @return The tables as found in the sqlite_master table as an ArrayList
     *          of TableInfo objects
     */
    private ArrayList<TableInfo> getOwnedTableList() {
        ArrayList<TableInfo> rv = new ArrayList<>();
        Cursor csr;
        SQLiteDatabase db =
                SQLiteDatabase.openDatabase(mDBPath,
                        null,
                        SQLiteDatabase.OPEN_READONLY
                );
        csr = db.query(
                SQLITE_MASTER,
                null,
                SM_TABLE_TYPE_COLUMN + "=?",
                new String[]{SM_TYPE_TABLE},
                null,null,null
        );
        while (csr.moveToNext()) {
            rv.add(new TableInfo(
                    csr.getString(
                            csr.getColumnIndex(
                                    SM_NAME_COLUMN
                            )
                    ),
                    csr.getString(
                            csr.getColumnIndex(
                                    SM_SQL_COLUMN
                            )
                    ),
                    this
                    )
            );
        }
        csr.close();
        db.close();
        return rv;
    }

    /**
     * Retrieve data information using PRAGMA statements as per
     * https://sqlite.org/pragma.html
     */
    private void setDBDetails() {
        Cursor csr;
        SQLiteDatabase db = SQLiteDatabase.openDatabase(mDBPath,null,SQLiteDatabase.OPEN_READONLY);
        csr = db.rawQuery(PRAGMA_STATEMENT + PRAGMA_USERVERSION,null);
        // Get User Version
        if (csr.moveToFirst()) {
            mDBUserVersion = csr.getInt(csr.getColumnIndex(PRAGMA_USERVERSION));
        }
        // Get Auto_Vacuum status
        csr = db.rawQuery(PRAGMA_STATEMENT + PRAGMA_AUTOVACUUM,null);
        if (csr.moveToFirst()) {
            mDBAutoVacuum = csr.getString(csr.getColumnIndex(PRAGMA_AUTOVACUUM));
        }
        // Get Busy Timeout value
        csr = db.rawQuery(PRAGMA_STATEMENT + PRAGMA_BUSYTIMEOUT,null);
        if (csr.moveToFirst()) {
            mDBBusyTimeout = csr.getInt(csr.getColumnIndex(PRAGMA_BUSYTIMEOUT_COL));
        }
        // Get cache size
        csr = db.rawQuery(PRAGMA_STATEMENT + PRAGMA_CACHESIZE,null);
        if (csr.moveToFirst()) {
            mDBCacheSize = csr.getLong(csr.getColumnIndex(PRAGMA_CACHESIZE));
        }
        // Get cache spill value
        csr = db.rawQuery(PRAGMA_STATEMENT + PRAGMA_CACHESPILL,null);
        if (csr.moveToFirst()) {
            mDBCacheSpill = csr.getString(csr.getColumnIndex(PRAGMA_CACHESPILL));
        }
        // Get CaseSensitiveLike value
        csr = db.rawQuery(PRAGMA_STATEMENT + PRAGMA_CASESENSITIVELIKE,null);
        if (csr.moveToFirst()) {
            mDBCaseSensitiveLike =  (csr.getInt(csr.getColumnIndex(PRAGMA_CASESENSITIVELIKE)) > 0);
        }
        // Get the Cell Size Check value
        csr = db.rawQuery(PRAGMA_STATEMENT + PRAGMA_CELLSIZECHECK,null);
        if (csr.moveToFirst()) {
            mDBCellSizeCheck = (csr.getInt(csr.getColumnIndex(PRAGMA_CELLSIZECHECK)) > 0);
        }
        // get the CheckPoint Full Sync (only for MAC OSX)
        csr = db.rawQuery(PRAGMA_STATEMENT + PRAGMA_CHECKPOINTFULLSYNC,null);
        if (csr.moveToFirst()) {
            mDBCheckPointFullSync = (csr.getInt(csr.getColumnIndex(PRAGMA_CHECKPOINTFULLSYNC)) > 0);
        }
        // get complie options list
        csr = db.rawQuery(PRAGMA_STATEMENT + PRAGMA_COMPILEOPTIONS,null);
        while (csr.moveToNext()) {
            mDBCompileOptions.add(csr.getString(csr.getColumnIndex(PRAGMA_COMPILEOPTIONS)));
        }
        // get defer foreign keys flag
        csr = db.rawQuery(PRAGMA_STATEMENT + PRAGMA_DEFERFOREIGNKEYS,null);
        if (csr.moveToFirst()) {
            mDBDeferForeignKeys = (csr.getInt(csr.getColumnIndex(PRAGMA_DEFERFOREIGNKEYS)) > 0);
        }
        // get Encoding
        csr = db.rawQuery(PRAGMA_STATEMENT + PRAGMA_ENCODING,null);
        if (csr.moveToFirst()) {
            mDBEncoding = csr.getString(csr.getColumnIndex(PRAGMA_ENCODING));
        }
        // Get Foreign Key Enforcement flag
        csr = db.rawQuery(PRAGMA_STATEMENT + PRAGMA_FOREIGNKEYS,null);
        if (csr.moveToFirst()) {
            mDBForeignKeyEnforcement = (csr.getInt(csr.getColumnIndex(PRAGMA_FOREIGNKEYS)) > 0);
        }
        // get count of unused pages
        csr = db.rawQuery(PRAGMA_STATEMENT + PRAGMA_FREELISTCOUNT,null);
        if (csr.moveToFirst()) {
            mDBFreeListCount = csr.getInt(csr.getColumnIndex(PRAGMA_FREELISTCOUNT));
        }
        // get the F_FULLSYNC flag (Mac OSX)
        csr = db.rawQuery(PRAGMA_STATEMENT + PRAGMA_FULLSYNC,null);
        if (csr.moveToFirst()) {
            mDBFullSync = (csr.getInt(csr.getColumnIndex(PRAGMA_FULLSYNC)) > 0);
        }
        // Get ignore check constraints flag
        csr = db.rawQuery(PRAGMA_STATEMENT + PRAGMA_IGNORECHECKCONSTRAINTS,null);
        if (csr.moveToFirst()) {
            mDBIgnoreCheckConstraints = (csr.getInt(csr.getColumnIndex(PRAGMA_IGNORECHECKCONSTRAINTS)) > 0);
        }
        // Get Journal mode
        csr = db.rawQuery(PRAGMA_STATEMENT + PRAGMA_JOURNALMODE,null);
        if (csr.moveToFirst()) {
            mDBJournalMode = csr.getString(csr.getColumnIndex(PRAGMA_JOURNALMODE));
        }
        // Get Journal Size Limit
        csr = db.rawQuery(PRAGMA_STATEMENT + PRAGMA_JOURNALSIZELIMIT,null);
        if (csr.moveToFirst()) {
            mDBJournalSizeLimit = csr.getInt(csr.getColumnIndex(PRAGMA_JOURNALSIZELIMIT));
        }
        // Get Locking Mode
        csr = db.rawQuery(PRAGMA_STATEMENT + PRAGMA_LOCKINGMODE,null);
        if (csr.moveToFirst()) {
            mDBLockingMode = csr.getString(csr.getColumnIndex(PRAGMA_LOCKINGMODE));
        }
        // Get the MAx Page Count
        csr = db.rawQuery(PRAGMA_STATEMENT + PRAGMA_MAXPAGECOUNT,null);
        if ((csr.moveToFirst())) {
            mDBMaxPageCount = csr.getInt(csr.getColumnIndex(PRAGMA_MAXPAGECOUNT));
        }
        // get the MMAP (memory mapped IO memory)
        csr = db.rawQuery(PRAGMA_STATEMENT + PRAGMA_MMAPSIZE,null);
        if (csr.moveToFirst()) {
            mDBMMapSize = csr.getInt(csr.getColumnIndex(PRAGMA_MMAPSIZE));
        }
        // get the page count
        csr = db.rawQuery(PRAGMA_STATEMENT + PRAGMA_PAGECOUNT,null);
        if (csr.moveToFirst()) {
            mDBPageCount = csr.getInt(csr.getColumnIndex(PRAGMA_PAGECOUNT));
        }
        // get the page size
        csr = db.rawQuery(PRAGMA_STATEMENT + PRAGMA_PAGESIZE,null);
        if (csr.moveToFirst()) {
            mDBPageSize = csr.getInt(csr.getColumnIndex(PRAGMA_PAGESIZE));
        }
        // get Read Uncomitted flag
        csr = db.rawQuery(PRAGMA_STATEMENT + PRAGMA_READUNCOMITTED,null);
        if (csr.moveToFirst()) {
            mDBReadUncomitted = (csr.getInt(csr.getColumnIndex(PRAGMA_READUNCOMITTED)) > 0);
        }
        // get Reci=ursive triggers flag
        csr = db.rawQuery(PRAGMA_STATEMENT + PRAGMA_RECURSIVETRIGGERS,null);
        if (csr.moveToFirst()) {
            mDBRecursiveTriggers = (csr.getInt(csr.getColumnIndex(PRAGMA_RECURSIVETRIGGERS)) > 0);
        }
        // get Reverse Unordered Selects flag
        csr = db.rawQuery(PRAGMA_STATEMENT + PRAGMA_REVERSEUNORDEREDSELECTS,null);
        if (csr.moveToFirst()) {
            mDBReverseUnorderdSelects = (csr.getInt(csr.getColumnIndex(PRAGMA_REVERSEUNORDEREDSELECTS)) > 0);
        }
        // get Secure Delete Mode
        csr = db.rawQuery(PRAGMA_STATEMENT + PRAGMA_SECUREDELETE,null);
        if (csr.moveToFirst()) {
            mDBSecureDelete = csr.getString(csr.getColumnIndex(PRAGMA_SECUREDELETE));
        }
        // get Soft heap Limit
        csr = db.rawQuery(PRAGMA_STATEMENT + PRAGMA_SOFTHEAPLIMIT,null);
        if (csr.moveToFirst()) {
            mDBSoftHeapLimit = csr.getInt(csr.getColumnIndex(PRAGMA_SOFTHEAPLIMIT));
        }
        // get Synchronous indicator
        csr = db.rawQuery(PRAGMA_STATEMENT + PRAGMA_SYNCHRONOUS,null);
        if (csr.moveToFirst()) {
            mDBSynchronous = csr.getInt(csr.getColumnIndex(PRAGMA_SYNCHRONOUS));
        }
        // get Temp Store indicator
        csr = db.rawQuery(PRAGMA_STATEMENT + PRAGMA_TEMPSTORE,null);
        if (csr.moveToFirst()) {
            mDBTempStore = csr.getInt(csr.getColumnIndex(PRAGMA_TEMPSTORE));
        }
        // get Threads number
        csr = db.rawQuery(PRAGMA_STATEMENT + PRAGMA_THREADS,null);
        if (csr.moveToFirst()) {
            mDBThreads = csr.getInt(csr.getColumnIndex(PRAGMA_THREADS));
        }
        // get WAL Autocheckpoint interval
        csr = db.rawQuery(PRAGMA_STATEMENT + PRAGMA_WALAUTOCHECKPOINT,null);
        if (csr.moveToFirst()) {
            mDBWALAutoCheckpoint = csr.getInt(csr.getColumnIndex(PRAGMA_WALAUTOCHECKPOINT));
        }

        csr.close(); // Done with the Cursor so close it.
        db.close(); // Done  with the Database so close it.
    }

    // getters
    int getUserVersion(){ return mDBUserVersion; }
    String getAutoVacuum(){ return mDBAutoVacuum; }
    int getBusyTimeout() { return  mDBBusyTimeout; }
    long getCacheSize() { return mDBCacheSize; }
    String getCacheSpill() {return mDBCacheSpill; }
    boolean getCaseSensitiveLike(){return mDBCaseSensitiveLike; }
    boolean getCellSizeCheck() {return mDBCellSizeCheck; }
    boolean getCheckPointFullSync() {return mDBCheckPointFullSync; }
    ArrayList<String> getCompileOptions() { return mDBCompileOptions; }
    boolean getDeferForeignKeys() { return mDBDeferForeignKeys; }
    boolean getForeignKeyEnforcement() { return  mDBForeignKeyEnforcement; }
    String getEncoding() { return mDBEncoding; }
    int getFreeListCount() { return mDBFreeListCount; }
    boolean getFullSync() { return mDBFullSync; }
    boolean getExists() { return mDBExists; }
    boolean getIgnoreCheckConstraints() {return mDBIgnoreCheckConstraints; }
    String getJournalMode() { return mDBJournalMode; }
    int getJournalSizeLimit() {return mDBJournalSizeLimit; }
    String getLockingMode() {return mDBLockingMode; }
    int getMaxPageCount() { return mDBMaxPageCount; }
    int getMMapSize() { return mDBMMapSize; }
    int getPageCount() { return mDBPageCount;}
    int getPageSize() { return mDBPageSize; }
    String getPath() { return mDBPath; }
    String getname() { return mDBName; }
    boolean getReadUncomitted() { return mDBReadUncomitted; }
    boolean getRecursiveTriggers() { return mDBRecursiveTriggers; }
    boolean getReverseUnorderedSelects() { return mDBReverseUnorderdSelects; }
    String getSecuredelete() { return mDBSecureDelete; }
    int getSoftheapLimit() { return mDBSoftHeapLimit; }
    int getSynchronous() { return mDBSynchronous; }
    int getTempStore() { return mDBTempStore; }
    int getThreads() { return mDBThreads; }
    int getWALAutoCheckPoint() { return mDBWALAutoCheckpoint; }
    ArrayList<TableInfo> getTableList() { return this.mTableList; }

    /**
     * Determine and return the String that reflects the synchronous value
     *
     * @param synchronous   The database value as an integer
     * @return              The respective string that reflects the value
     */
    String getSynchronousAsString(int synchronous) {
        switch (synchronous) {
            case 0:
                return "OFF";
            case 1:
                return "NORMAL";
            case 2:
                return "FULL";
            case 3:
                return  "EXTRA";
            default:
                return "UNKNOWN";
        }
    }

    /**
     * Determine and return the String that reflects the tempstore value
     *
     * @param tempstore     The tempstore value as an integer
     * @return              the respective String that reflects the value
     */
    String getTempStoreAsString(int tempstore) {
        switch  (tempstore) {
            case 0:
                return "DEFAULT";
            case 1:
                return "FILE";
            case 2:
                return "MEMEORY";
            default:
                return  "UNKNOWN";
        }
    }

    /**
     * Get the database information as a set of paired strings;
     * the first part of a pair being a description of the value;
     * the second part of the pair being the value converted to a String
     *
     * @return an ArrayList of the Database information as DoubleString objects
     */
    ArrayList<DoubleString> getDBInfoAsDoubleStringArrayList() {
        ArrayList<DoubleString> rv = new ArrayList<>();
        rv.add(new DoubleString("Database Name", this.getname()));
        rv.add(new DoubleString("Database path",this.getPath()));
        rv.add(new DoubleString("User Version",Integer.toString(this.getUserVersion())));
        rv.add(new DoubleString("Encoding",this.getEncoding()));
        rv.add(new DoubleString("Auto Vacuum", this.getAutoVacuum()));
        rv.add(new DoubleString("Busy Timeout",Integer.toString(this.getBusyTimeout())));
        rv.add(new DoubleString("Cache Size",Long.toString(this.getCacheSize())));
        rv.add(new DoubleString("Cache Spill",this.getCacheSpill()));
        rv.add(new DoubleString("Case Sensitive Like", Boolean.toString(this.getCaseSensitiveLike())));
        rv.add(new DoubleString("Cell Size Check",Boolean.toString(this.getCellSizeCheck())));
        rv.add(new DoubleString("Checkpoint Full Sync",Boolean.toString(this.getCheckPointFullSync())));
        rv.add(new DoubleString("Defer Foreign Keys",Boolean.toString(this.getDeferForeignKeys())));
        rv.add(new DoubleString("Enforce Foreign Keys", Boolean.toString(this.getForeignKeyEnforcement())));
        rv.add(new DoubleString("Freelist Count",Integer.toString(this.getFreeListCount())));
        rv.add(new DoubleString("Full Sync",Boolean.toString(this.getFullSync())));
        rv.add(new DoubleString("Ignore Check Constraints",Boolean.toString(this.getIgnoreCheckConstraints())));
        rv.add(new DoubleString("Journal Mode",this.getJournalMode()));
        rv.add(new DoubleString("Journal Size Limit",Integer.toString(this.getJournalSizeLimit())));
        rv.add(new DoubleString("Locking Mode",this.getLockingMode()));
        rv.add(new DoubleString("Max page Count",Integer.toString(this.getMaxPageCount())));
        rv.add(new DoubleString("Memory Map Size",Integer.toString(this.getMMapSize())));
        rv.add(new DoubleString("Page Count",Integer.toString(this.getPageCount())));
        rv.add(new DoubleString("Page Size",Integer.toString(this.getPageSize())));
        rv.add(new DoubleString("Read Uncomitted",Boolean.toString(this.getReadUncomitted())));
        rv.add(new DoubleString("Recursive Triggers",Boolean.toString(this.getRecursiveTriggers())));
        rv.add(new DoubleString("Reverse UnOrdered Selects",Boolean.toString(this.getReverseUnorderedSelects())));
        rv.add(new DoubleString("Secure Delete",this.getSecuredelete()));
        rv.add(new DoubleString("Soft Heap Limit",Integer.toString(this.getSoftheapLimit())));
        rv.add(new DoubleString("Synchronous",this.getSynchronousAsString(this.getSynchronous())));
        rv.add(new DoubleString("Temp Store",this.getTempStoreAsString(this.getTempStore())));
        rv.add(new DoubleString("Thread Limit",Integer.toString(this.getThreads())));
        rv.add(new DoubleString("WAL AutoChkPnt Intrvl",Integer.toString(this.getWALAutoCheckPoint())));
        String str1 = "Compile Options";
        if (getCompileOptions().size() < 1) {
            rv.add(new DoubleString("Unable to Obtain Compile Options",""));
        }
        for (String s: this.getCompileOptions()) {
            rv.add(new DoubleString(str1,s));
            str1 = "";
        }
        return rv;
    }
}
