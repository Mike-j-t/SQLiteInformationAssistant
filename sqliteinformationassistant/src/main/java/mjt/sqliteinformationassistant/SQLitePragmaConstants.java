package mjt.sqliteinformationassistant;

/**
 * Constants used for the building of SQLite PRAGMA statements
 * and in the case of multi-column results the subsquent column
 * names.
 *
 * Most PRAGMAs (generally with the expection of ????_list) return
 * a cursor with a single column whose name is the same as the
 * option used.
 *
 * PRAGMA statements start with the PRAGMA keyword (PRAGMA_STATEMENT constant)
 * which is then followed by the respective followed by the name of the pragma
 * to be invoked e.g.
 *      PRAGMA user_version
 * is used to extract the user version (commonly known as the database version).
 *
 * PRAGMA's can be used to query and in many cases update e.g.
 *      PRAGMA user_version = 2
 * can be used to set the user version to 2.
 *
 * NOTE! in general the SQLiteDatabase rawQuery method should be used for
 * inspection as the execSql method cannot return a result.
 *  It appears that execSql should be used when updating a value
 */

class SQLitePragmaConstants {

    // PRAGMA keyword used by all pragmas
    static final String PRAGMA_STATEMENT = "PRAGMA ";   // PRAGMA keyword

    // List pragmas see related constants for column names
    static final String PRAGMA_DATABASELIST = "database_list"; //<< See PRAGMA_DBLIST_?????
    static final String PRAGMA_FOREIGNKEYLIST = "foreign_key_list"; //<< See PRAGMA_FRGNKEY_?????
    static final String PRAGMA_INDEXLIST = "index_list";
    static final String PRAGMA_INDEXINFO = "index_info"; //<< See PRAGMA INDEXINFO_????
    static final String PRAGMA_TABLEINFO = "table_info"; //<< See PRAGMA_TABLEINFO_????

    //**** NOTE about compile_options ****
    // Note the compile options returns just a sinlge column named
    // compile_options (so like single result queries the PRAGMA name can
    // also be used for the colum name). However it should return many rows.
    // This does not appear to work on Android though.
    static final String PRAGMA_COMPILEOPTIONS = "compile_options;";

    static final String PRAGMA_USERVERSION = "user_version";
    static final String PRAGMA_ENCODING = "encoding";
    static final String PRAGMA_AUTOVACUUM = "auto_vacuum";
    static final String PRAGMA_BUSYTIMEOUT = "busy_timeout";
    //**** NOTE about busy_timeout the column name is timeout
    static final String PRAGMA_BUSYTIMEOUT_COL = "timeout"; //<< busy_timeout column
    static final String PRAGMA_CACHESIZE = "cache_size";
    static final String PRAGMA_CACHESPILL = "cache_spill";
    static final String PRAGMA_CASESENSITIVELIKE = "case_sensitive_like";
    static final String PRAGMA_CELLSIZECHECK = "cell_size_check";
    static final String PRAGMA_CHECKPOINTFULLSYNC = "checkpoint_full_sync";
    static final String PRAGMA_DEFERFOREIGNKEYS = "defer_foreign_keys";
    static final String PRAGMA_FOREIGNKEYCHECK = "foreign_key_check";
    static final String PRAGMA_FOREIGNKEYS = "foreign_keys";
    static final String PRAGMA_FREELISTCOUNT = "freelist_count";
    static final String PRAGMA_FULLSYNC = "fullsync";
    static final String PRAGMA_IGNORECHECKCONSTRAINTS = "ignore_check_constraints";
    static final String PRAGMA_JOURNALMODE = "journal_mode";
    static final String PRAGMA_JOURNALSIZELIMIT = "journal_size_limit";
    static final String PRAGMA_LOCKINGMODE = "locking_mode";
    static final String PRAGMA_MAXPAGECOUNT = "max_page_count";
    static final String PRAGMA_MMAPSIZE = "mmap_size";
    static final String PRAGMA_PAGECOUNT = "page_count";
    static final String PRAGMA_PAGESIZE = "page_size";
    static final String PRAGMA_READUNCOMITTED = "read_uncomitted";
    static final String PRAGMA_RECURSIVETRIGGERS = "recursive_triggers";
    static final String PRAGMA_REVERSEUNORDEREDSELECTS = "reverse_unordered_selects";
    static final String PRAGMA_SECUREDELETE = "secure_delete";
    static final String PRAGMA_SOFTHEAPLIMIT = "soft_heap_limit";
    static final String PRAGMA_SYNCHRONOUS = "synchronous";
    static final String PRAGMA_TEMPSTORE = "temp_store";
    static final String PRAGMA_THREADS = "threads";
    static final String PRAGMA_WALAUTOCHECKPOINT = "wal_autocheckpoint";

    // Columns for multi-column results
    // from PRAGMA database_list
    static final String PRAGMA_DBLIST_SEQ_COL = "seq";
    static final String PRAGMA_DBLIST_NAME_COL = "name";
    static final String PRAGMA_DBLIST_FILE_COL = "file";
    // from PRAGMA table_info
    static final String PRAGMA_TABLEINFO_CID_COL = "cid";
    static final String PRAGMA_TABLEINFO_NAME_COl = "name";
    static final String PRAGMA_TABLEINFO_TYPE_COL = "type";
    static final String PRAGMA_TABLEINFO_NOTNULL_COL = "notnull";
    static final String PRAGMA_TABLEINFO_DEFAULTVALUE_COL = "dflt_value";
    static final String PRAGMA_TABLEINFO_PRIMARYKEY_COL = "pk";
    // from PRAGMA index_info
    static final String PRAGMA_INDEXINFO_SEQNO_COL = "seqno";
    static final String PRAGMA_INDEXINFO_CID_COL = "cid";
    static final String PRAGMA_INDEXINFO_NAME_COL = "name";
    // from PRAGMA index_list
    static final String PRAGMA_INDEXLIST_SEQ_COL = "seq";
    static final String PRAGMA_INDEXLIST_NAME_COL = "name";
    static final String PRAGMA_INDEXLIST_UNIQUE_COL = "unique";
    static final String PRAGMA_INDEXLIST_ORIGIN_COL = "origin";
    static final String PRAGMA_INDEXLIST_PARTIAL_COL = "partial";
    // from PRAGMA foreign_key_list
    static final String PRAGMA_FRGNKEY_ID_COL = "id";
    static final String PRAGMA_FRGNKEY_SEQ_COL = "seq";
    static final String PRAGMA_FRGNKEY_TABLE_COL = "table";
    static final String PRAGMA_FRGNKEY_FROM_COL = "from";
    static final String PRAGMA_FRGNKEY_TO_COL = "to";
    static final String PRAGMA_FRGNKEY_ONUPDATE_COL = "on_update";
    static final String PRAGMA_FRGNKEY_ONDELETE_COL = "on_delete";
    static final String PRAGMA_FRGNKEY_MATCH_COL = "match";
}
