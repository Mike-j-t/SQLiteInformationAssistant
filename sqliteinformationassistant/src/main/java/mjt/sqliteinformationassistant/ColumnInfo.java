package mjt.sqliteinformationassistant;

import java.util.ArrayList;

/**
 * ColumnInfo contains infromation in regard to an SQLite column;
 * it's :-
 *      name,
 *      type,
 *      the notnull flag,
 *      the default value (used when the a value is not provided),
 *      the position in the primary key (0 if not in the primary key),
 *      the column's id value (meaningless except within sqlite_master),
 *      and the table that own's
 *
 *  Include's standard getters, no setters (constructor only) and 1 method,
 *      isColumnInPrimaryKey
 *
 *  It is intended that all information is obtained via inspection of the
 *  SQLite internals e.g. the sqlite_master table and or SQLite PRAGMA queries
 *  as such this class is of limited general purpose use.
 */

@SuppressWarnings("WeakerAccess")
class ColumnInfo {
    private final String mColumnName;
    private final String mColumnType;
    private final boolean mColumnNotNull;
    private final String mColumnDefaultValue;
    private final int mColumnPrimaryKeyPosition;
    private final int mColumnId;
    private final TableInfo mOwningTable;

    /**
     * ColumnInfo Constructor
     * @param columnname            The name of the Column.
     * @param columntype            The type of the Column.
     * @param notnull               The Column's NOT NULL flag.
     * @param defaultvalue          The Column's default value.
     * @param primarykeyposition    The Column's primary key position,
     *                              0 indicates not part of the Primary Key.
     * @param columnid              the Column's id in the sqlite_master table.
     * @param owningtable           The tableinfo object that represent the
     *                              table that this Column is part of.
     */
    ColumnInfo(
            String columnname,
            String columntype,
            boolean notnull,
            String defaultvalue,
            int primarykeyposition,
            int columnid,
            TableInfo owningtable) {
        mColumnName = columnname;
        mColumnType = columntype;
        mColumnNotNull = notnull;
        mColumnDefaultValue = defaultvalue;
        mColumnPrimaryKeyPosition = primarykeyposition;
        mColumnId = columnid;
        mOwningTable = owningtable;
    }

    /**
     * Retrieves the Column's name.
     * @return Column name as a String.
     */
    String getColumnName() { return mColumnName; }

    /**
     * Retrieves the Column's type.
     * @return Column type as a String.
     */
    String getColumnType() { return mColumnType; }

    /**
     * Retrieves the Column's default value (may not be set).
     * @return The default value as a string.
     */
    String getColumnDefaultValue() { return mColumnDefaultValue; }

    /**
     * Retrieves the Column's NOT NULL flag,
     * if NOT NULL is true then a row cannot be inserted if this
     * columns value will be NULL.
     * @return the NOT NULL flag as a boolean.
     */
    boolean getColumnNotNull() {return mColumnNotNull; }

    /**
     * Retrieves the NOT NULL flag as a String
     * @return the String true or false according to the NOT NULL value.
     */
    String getColumnNotNullAsString() { return Boolean.toString(mColumnNotNull); }

    /**
     * returns the position of the column within the Primary Index
     * @return The position as an integer,
     *          0 if the column is not part of the Primary Index.
     */
    int getColumnPrimaryKeyPosition() { return mColumnPrimaryKeyPosition; }

    /**
     * Checks if the column is within the Primary Index
     * @return true if the column is in the Primary Index,
     *          false if the column is not in the Primary Index.
     */
    boolean isColumnInPrimaryKey() { return mColumnPrimaryKeyPosition > 0; }

    /**
     * Retrieves the Column's id within the sqlite_master table,
     * There would very likely be little if any need for this value.
     * @return  The unique identifier within the sqlite_master table as
     *          and integer.
     */
    int getColumnId() { return mColumnId; }
    String getOwningTable() { return mOwningTable.getTableName(); }

    /**
     * Get the Column information as a set of paired strings;
     * The first part of a pair being a description of the value;
     * The second part of a pair being the value converted to a string.
     *
     * @return An ArrayList of the column information as DoubleString objects.
     */
    ArrayList<DoubleString> getColumnInfoAsDoubleString() {
        ArrayList<DoubleString> rv = new ArrayList<>();
        rv.add(new DoubleString("Column Name",
                this.getColumnName())
        );
        rv.add(new DoubleString("Column Type",
                this.getColumnType())
        );
        rv.add(new DoubleString("NOT NULL",
                Boolean.toString(this.getColumnNotNull()))
        );
        rv.add(new DoubleString("Default value",
                this.getColumnDefaultValue())
        );
        String pkeydesc = "Position in Primary Key/Index";
        String notinpkey = "Not in Primary Key/Index";
        if (this.isColumnInPrimaryKey()) {
            rv.add(new DoubleString(pkeydesc,
                    Integer.toString(this.getColumnPrimaryKeyPosition())));
        } else {
            rv.add(new DoubleString(pkeydesc,notinpkey));
        }
        return rv;
    }
}
