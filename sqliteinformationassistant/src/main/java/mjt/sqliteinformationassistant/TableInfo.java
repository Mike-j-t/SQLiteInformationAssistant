package mjt.sqliteinformationassistant;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import static mjt.sqliteinformationassistant.SQLitePragmaConstants.*;

/**
 * A TableInfo holds information regarding an SQLite table;
 * Note this includes an ArrayList of ColumnInfo objects.
 */

@SuppressWarnings("WeakerAccess")
class TableInfo {
    private final String mTableName;
    private final String mTableCreateSQL;
    private final DatabaseInfo mOwningDatabase;
    private final ArrayList<ColumnInfo> mTableColumns;
    private int mColumnsInPrimaryKey;

    /**
     * Construct a TableInfo object
     *
     * @param tablename         The Name of the table as a String.
     * @param tablecreateSQL    The SQL used to create the table as a String.
     * @param owningdatabase    The DatabaseInfo object that owns the table.
     */
    TableInfo(String tablename, String tablecreateSQL, DatabaseInfo owningdatabase) {
        mTableName = tablename;
        mTableCreateSQL = tablecreateSQL;
        mOwningDatabase = owningdatabase;
        mTableColumns = getOwnedColumnList();
        setColumnsInPrimaryKey(mTableColumns);
    }

    // Getters
    String getTableName() { return mTableName; }
    String getTableCreateSQL() {return  mTableCreateSQL; }
    String getOwningDatabase() { return mOwningDatabase.getname(); }
    int getColumnsInPrimaryKey() { return mColumnsInPrimaryKey; }

    ArrayList<ColumnInfo> getColumnList() { return mTableColumns; }

    /**
     * Calculate the number of columns used in the primary key.
     *
     * @param columns           An ArrayList of ColumnInfo objects
     */
    private void setColumnsInPrimaryKey(ArrayList<ColumnInfo> columns) {
        for (ColumnInfo ci: columns) {
            if (ci.isColumnInPrimaryKey()) {
                this.mColumnsInPrimaryKey++;
            }
        }
    }

    /**
     * Create the list of ColumnInfo objects according to the Columns in the database.
     *
     * @return The columns as found via pragma_tableinfo as an ArrayList of
     *          ColumnInfo objects.
     */
    private ArrayList<ColumnInfo> getOwnedColumnList() {
        ArrayList<ColumnInfo> rv = new ArrayList<>();
        String dbpath = mOwningDatabase.getPath();
        Cursor csr;
        SQLiteDatabase db =
                SQLiteDatabase.openDatabase(
                        dbpath,
                        null,
                        SQLiteDatabase.OPEN_READONLY
                );
        csr = db.rawQuery(
                PRAGMA_STATEMENT + PRAGMA_TABLEINFO +
                        "(" +
                        mTableName +
                        ")",
                null
        );
        while (csr.moveToNext()) {
            rv.add(new ColumnInfo(
                    csr.getString(
                            csr.getColumnIndex(
                                    PRAGMA_TABLEINFO_NAME_COl
                            )),
                    csr.getString(
                            csr.getColumnIndex(
                                    PRAGMA_TABLEINFO_TYPE_COL
                            )),
                    csr.getInt(
                            csr.getColumnIndex(
                                    PRAGMA_TABLEINFO_NOTNULL_COL
                            )) > 0,
                    csr.getString(
                            csr.getColumnIndex(
                                    PRAGMA_TABLEINFO_DEFAULTVALUE_COL
                            )),
                    csr.getInt(
                            csr.getColumnIndex(
                                    PRAGMA_TABLEINFO_PRIMARYKEY_COL
                            )),
                    csr.getInt(
                            csr.getColumnIndex(
                                    PRAGMA_TABLEINFO_CID_COL
                            )),
                    this)
            );
        }
        csr.close();
        db.close();
        return rv;
    }

    /**
     * Get the Table information as a set of paired string;
     * The first part of a pair being a description of the value;
     * The second part of a pair being the value converted to a string.
     *
     * @return An ArrayList of the table information as DoubleString objects.
     */
    ArrayList<DoubleString> getTableInfoAsDoubleStringArrayList() {
        ArrayList<DoubleString> rv = new ArrayList<>();
        rv.add(new DoubleString("Table Name",
                this.getTableName())
        );
        rv.add(new DoubleString("Create SQL",
                this.getTableCreateSQL())
        );
        rv.add(new DoubleString("Number of Columns",
                Integer.toString(this.getColumnList().size()))
        );
        rv.add(new DoubleString("Columns in Primary Key",
                Integer.toString(getColumnsInPrimaryKey()))
        );
        return rv;
    }
}
