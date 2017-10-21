package mjt.sqliteinformationassistant;

/**
 * Constants used for accessing the sqlite_master table
 */

class SQLiteMasterConstants {

    // SQLite MASTER TABLE definitions

    // The sqlite_mast table name
    static final String SQLITE_MASTER = "sqlite_master";
    // Column Names
    static final String SM_TABLE_TYPE_COLUMN = "type";
    static final String SM_NAME_COLUMN = "name";
    static final String SM_TABLENAME_COLUMN = "tbl_name";
    static final String SM_ROOTPAGE_COLUMN = "rootpage";
    static final String SM_SQL_COLUMN = "sql";
    // Types in the type column
    static final String SM_TYPE_TABLE = "table";
    static final String SM_TYPE_INDEX = "index";
}
