package mjt.sqliteinformationassistant;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;

public class SQLiteViewerActivity extends AppCompatActivity {

    static final int RIGHTLISTVIEW = 0;
    static final int LEFTLISTVIEW = 1;
    static final int SHOWDATABASEINFO = 0;
    static final int SHOWTABLEINFO = 1;
    static final int SHOWCOLUMNINFO = 2;

    // Buttons mDone finishes activity, mPrev reverts to Database/Table List
    Button mDone, mPrev;
    // ListViews where data is displayed
    TextView mMainheading,
            mLeftListViewHeading,
            mRightListViewHeading, mInfoListViewheading;
    ListView mLeftListView, mRightListView, mBottomListView;
    // Adapaters for the data lists
    UniversalArrayAdapter
            mDatabaseAdapter,   // Database List
            mTableAdapter,      // Table List
            mColumnAdapter,     // Column List
            mInfoAdapter;       // list paired strings according to logic
    // The database info (extracted once from actual database)
    ArrayList<DatabaseInfo> mDatabases;
    // Lists of object methods used to display the respective data
    Method[] mDatabaseListMethods,
            mTableListMethods,
            mColumnListMethods,
            mDoubleStringMethods;
    // Current positions
    int mCurrentDatabase = 0;
    int mCurrentTable = 0;
    int mCurrentColumn = 0;
    // Context
    Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.mContext = this;
        setContentView(R.layout.activity_sqlite_viewer);
        // Prepare buttons
        mDone = (Button) this.findViewById(R.id.dbv_donebutton);
        prepDoneButton(mDone);
        mPrev = (Button) this.findViewById(R.id.dbv_prevbutton);
        prepPrevButton(mPrev,false);
        // get ListViews
        mLeftListView =
                (ListView) this.findViewById(R.id.dbv_left_listview);
        mRightListView =
                (ListView) this.findViewById(R.id.dbv_right_listview);
        mBottomListView =
                (ListView) this.findViewById(R.id.dbv_bottom_listview);
        // Headings
        mMainheading = (TextView) this.findViewById(R.id.dbv_heading_text);
        mLeftListViewHeading =
                (TextView) this.findViewById(R.id.dbv_left_listview_heading);
        mRightListViewHeading =
                (TextView) this.findViewById(R.id.dbv_right_listview_heading);
        mInfoListViewheading =
                (TextView) this.findViewById(R.id.dbv_bottom_listview_heading);

        // Setup the method lists for the UniversalArrayAdpaters
        setMethodsLists();

        // Get the database information
        mDatabases = getDatabaseList();
        // initially show the Database List in the Left ListView;
        // Show initial Lists
        showDatabaseList();             // Databases Listed on Left
        showTableList(RIGHTLISTVIEW);   // Tables for first Database on right
        showInfo(SHOWDATABASEINFO);     // Detailed Database info for first
    }

    /**
     * Set the LeftListView onItemClickListener according to what the
     * leftListView is displaying;
     * The leftListView can either display the list of databases or
     * The list of tables in the current Database;
     *
     * If the Database List is being displayed the click will:-
     * a) Display the Database's Tables in the right display, and
     * b) Display the Database's detailed information in the lower view;
     *
     * If a TableList is being displayed the click will:-
     * a) Display the clicked table's columns in the right display, and
     * b) Display the clicked Tables, detail information in the lower view.
     * c) Enable Long clicking of a Table to view the Table's data
     *      (Enabling LongClick is done in showTableList method)
     *
     *
     * @param viewtype The type that is being viewed as an integer;
     *                 (SHOWDATABASEINFO or SHOWTABLEINFO)
     */
    private void setLeftListViewItemClickHandler(int viewtype) {
        switch (viewtype) {
            case    SHOWDATABASEINFO:
                mLeftListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        mCurrentDatabase = position;
                        showTableList(RIGHTLISTVIEW);
                        showInfo(SHOWDATABASEINFO);
                    }
                });
                break;
            case SHOWTABLEINFO:
                mLeftListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        mCurrentTable = position;
                        showTableList(LEFTLISTVIEW);
                        showColumnList();
                        showInfo(SHOWTABLEINFO);
                    }
                });
                break;
        }
    }

    /**
     * Set the RightListView onItemClickListener according to what the
     * RightListView is displaying;
     *
     * The RightListView can display either a TableList or a
     * ColumnList;
     *
     * If a Table List is displayed the click will:-
     * a) Switch to displaying the Table's in the Left view,
     * b) Display the Table's detailed information in the ower view, and
     * c) Display the Table's columns in the Right view.
     * d) Enable Long clicking of a Table to view the Table's data
     *      (Enabling LongClick is done in showTableList method)
     *
     * If the Column List is displayed the click will:-
     * a) Display the clicked Column's detailed information in the lower view
     *
     * @param viewtype an integer that represents the type of data to be viewed/listed
     */
    private void setRightListViewItemClickHandler(int viewtype) {
        switch (viewtype) {
            case    SHOWTABLEINFO:
                mRightListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        mCurrentTable = position;
                        showTableList(LEFTLISTVIEW);
                        showColumnList();
                        showInfo(SHOWTABLEINFO);
                        setLeftListViewItemClickHandler(SHOWTABLEINFO);
                        setRightListViewItemClickHandler(SHOWCOLUMNINFO);
                        mPrev.setVisibility(View.VISIBLE);
                        prepPrevButton(mPrev,true);
                    }
                });
                break;
            case SHOWCOLUMNINFO:
                mRightListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        mCurrentColumn = position;
                        showInfo(SHOWCOLUMNINFO);
                    }
                });
                break;
        }
    }

    /**
     * Show the extracted list of Databases in the Left view,
     * setting the respective Item click listeners;
     *
     * Note when first using an Adapter the adapter instance will be
     * instantiated, subsequently the List will be swapped
     */
    private void showDatabaseList() {

        // Always create and build an Array specific to the view
        // (just in case)
        ArrayList<DatabaseInfo> databaselist = new ArrayList<>();
        for (DatabaseInfo di: mDatabases) {
            databaselist.add(di);
        }
        // Instantiate an instance of a Universal Array Adapter for the
        // Database List, if not already instantiated.
        if (mDatabaseAdapter == null) {
            mDatabaseAdapter = new UniversalArrayAdapter(
                    this,
                    android.R.layout.simple_list_item_1,
                    databaselist,
                    android.R.layout.simple_list_item_1,
                    mDatabaseListMethods,
                    new int[]{android.R.id.text1}
            );
        } else {
            // if already instantiated the swap to new list
            mDatabaseAdapter.swapItems(databaselist);
        }
        // Set the ListViews with thier respective adapters
        mLeftListView.setAdapter(mDatabaseAdapter);
        mRightListView.setAdapter(mTableAdapter);
        // Setup the respective item click handling
        setLeftListViewItemClickHandler(SHOWDATABASEINFO);
        setRightListViewItemClickHandler(SHOWTABLEINFO);
        mLeftListViewHeading.setText(getResources().getString(R.string.dblist));
        mRightListViewHeading.setText(getResources().getString(
                R.string.tbllist,
                mDatabases
                        .get(mCurrentDatabase)
                        .getname())
        );
    }

    /**
     * Show the TableList in the respective ListView,
     * Right view if Tables are listed along with the Database List,
     * Left view if Tables and Columns are listed.
     * Tables and Column are listed when a Table in the Right list is
     * clicked, swapping the Table List from the Right View to the Left View.
     *
     * @param side an integer representing the view (Left or Right) in which
     */
    private void showTableList(int side) {

        // Always rebuild the List to be used for the ListView
        ArrayList<TableInfo> tablelist = new ArrayList<>();
        for (TableInfo ti: mDatabases.get(mCurrentDatabase).getTableList()) {
            tablelist.add(ti);
        }

        // Instantiate an instance of a Universal Array Adapter for the
        // Table List, if not already instantiated.
        if (mTableAdapter == null) {
            mTableAdapter = new UniversalArrayAdapter(
                    this,
                    android.R.layout.simple_list_item_1,
                    tablelist,
                    android.R.layout.simple_list_item_1,
                    mTableListMethods,
                    new int[]{android.R.id.text1}
            );
        } else {
            // If already instantiated the swap to new list
            mTableAdapter.swapItems(tablelist);
        }
        // Set the respective Righ or Left view with the Table List adapter
        switch (side) {
            case RIGHTLISTVIEW:
                mRightListView.setAdapter(mTableAdapter);
                mRightListViewHeading.setText(getResources()
                        .getString(
                                R.string.tbllist,
                                mDatabases.get(mCurrentDatabase)
                                        .getname()
                        )
                );
                break;
            case LEFTLISTVIEW:
                mLeftListView.setAdapter(mTableAdapter);
                mLeftListViewHeading.setText(getResources()
                        .getString(
                                R.string.tbllist,
                                mDatabases.get(mCurrentDatabase)
                                        .getname()
                        )
                );
                break;
        }
        // Setup the TableList's Item LongClick handling
        setTableLisItemLongClickHandler(side);
    }

    /**
     * Setup ItemLongClick handling according to the View (Right or Left)
     * in which the Table's are listed
     *
     * @param side an integer that determines which View (right or Left)
     *             the ItemLongClick listener will be applied to.
     */
    private void setTableLisItemLongClickHandler(int side ) {
        // Default to Right ListView
        ListView selected_listview = mRightListView;

        // Clear all ItemLongClick listeners
        mRightListView.setOnItemLongClickListener(null);
        mLeftListView.setOnItemLongClickListener(null);

        // If the Left view is to be setup then swap to the Left ListView
        if (side == LEFTLISTVIEW) {
            selected_listview = mLeftListView;
        }

        // Set the respective listener
        selected_listview.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(mContext,SQLiteDataViewer.class);
                intent.putExtra(
                        SQLiteDataViewer.INTENTKEY_DATABASEPATH,
                        mDatabases
                                .get(mCurrentDatabase)
                                .getPath()
                );
                intent.putExtra(
                        SQLiteDataViewer.INTENTKEY_DATABASENAME,
                        mDatabases
                                .get(mCurrentDatabase)
                                .getname()
                );
                intent.putExtra(
                        SQLiteDataViewer.INTENTKEY_TABLENAME,
                        mDatabases
                                .get(mCurrentDatabase)
                                .getTableList()
                                .get(position)
                                .getTableName()
                );
                startActivityForResult(intent,
                        SQLiteDataViewer.REQUESTCODE_SQLITEDATAVIEWER
                );
                return true;
            }
        });
    }

    /**
     * Instantiate and populate the ColumnList adapter
     */
    private void showColumnList() {
        // Always rebuild a list specific to the adapter
        ArrayList<ColumnInfo> columnlist = new ArrayList<>();
        for (ColumnInfo ci: mDatabases
                .get(mCurrentDatabase)
                .getTableList()
                .get(mCurrentTable)
                .getColumnList()) {
            columnlist.add(ci);
        }

        // set the Current column to the first in the list
        mCurrentColumn = 0;

        // Instantiate a Universal Array Adapter instance for the
        // ColumnList, if not already instantiated
        if (mColumnAdapter == null) {
            mColumnAdapter = new UniversalArrayAdapter(
                    this,
                    android.R.layout.simple_list_item_1,
                    columnlist,
                    android.R.layout.simple_list_item_1,
                    mColumnListMethods,
                    new int[]{android.R.id.text1}
            );
        } else {
            // If already instantiated swap to the new ColumnList
            mColumnAdapter.swapItems(columnlist);
        }
        // Set the Right ListView to use the Column adapter
        mRightListView.setAdapter(mColumnAdapter);
        mRightListViewHeading.setText(getResources().getString(
                R.string.collist,
                mDatabases
                        .get(mCurrentDatabase)
                        .getTableList()
                        .get(mCurrentTable)
                        .getTableName()
        ));
    }

    /**
     * Show the respective detailed information in the Lower View;
     * The Lower View can list detailed information about the current
     * database, the current Table or the current column;
     * Clicking an item results in that Item's detailed information being
     * listed;
     * All detailed information is provied a DoubleString ArrayList (two strings)
     * The first string is a description of the value which in the second string;
     *
     *
     * @param typetoshow an integer that represents the information type to be displayed
     */
    private void showInfo(int typetoshow) {

        // Initialse the Adapter specific DoubleString ArrayList
        ArrayList<DoubleString> infolist = new ArrayList<>();
        switch (typetoshow) {
            // If Database detailed information then build the adpater
            // specific ArrayList from the current database
            case SHOWDATABASEINFO:
                for (DoubleString il: mDatabases
                        .get(mCurrentDatabase)
                        .getDBInfoAsDoubleStringArrayList()) {
                    infolist.add(il);
                }
                mInfoListViewheading.setText(getResources().getString(
                        R.string.dbinfo,
                        mDatabases.get(mCurrentDatabase).getname())
                );
                break;
            // If Table detailed information then build the adpater
            // specific ArrayList from the current Table, which will
            // be owned by the current database.
            case SHOWTABLEINFO:
                for (DoubleString il:
                        mDatabases
                        .get(mCurrentDatabase)
                        .getTableList()
                        .get(mCurrentTable)
                        .getTableInfoAsDoubleStringArrayList()) {
                    infolist.add(il);
                }
                mInfoListViewheading.setText(getResources().getString(
                        R.string.tblinfo,
                        mDatabases
                                .get(mCurrentDatabase)
                                .getTableList()
                                .get(mCurrentTable)
                                .getTableName(),
                        mDatabases.get(mCurrentDatabase).getname())
                );
                break;
            // If Column detailed information then build the adpater
            // specific ArrayList from the current Column, which will be
            // owned by the current table, which itself is owned by the
            // current database.
            case SHOWCOLUMNINFO:
                for (DoubleString il: mDatabases
                        .get(mCurrentDatabase)
                        .getTableList()
                        .get(mCurrentTable)
                        .getColumnList()
                        .get(mCurrentColumn)
                        .getColumnInfoAsDoubleString()) {
                    infolist.add(il);
                }
                mInfoListViewheading.setText(getResources().getString(
                        R.string.colinfo,
                        mDatabases
                                .get(mCurrentDatabase)
                                .getTableList()
                                .get(mCurrentTable)
                                .getColumnList()
                                .get(mCurrentColumn)
                                .getColumnName(),
                        mDatabases
                                .get(mCurrentDatabase)
                                .getTableList()
                                .get(mCurrentTable)
                                .getTableName(),
                        mDatabases
                                .get(mCurrentDatabase)
                                .getname())
                );
                break;
        }
        // If not already instantiated, create a UniversalArrayAdapter
        // instance for detailed information
        if (mInfoAdapter == null) {
            mInfoAdapter = new UniversalArrayAdapter(
                    this,
                    android.R.layout.simple_list_item_2,
                    infolist,
                    android.R.layout.simple_list_item_2,
                    mDoubleStringMethods,
                    new int[]{
                            android.R.id.text1,
                            android.R.id.text2
                    }
            );
        } else {
            // If already instamtiated then swap to the new list
            mInfoAdapter.swapItems(infolist);
        }
        mBottomListView.setAdapter(mInfoAdapter);
    }

    /**
     * Prepare the Done button to return to the invoking activity
     * @param btn The Button to be prepared
     */
    private void prepDoneButton(Button btn) {
        btn.setText(this.getResources().getString(R.string.done_button_text));
        btn.setVisibility(View.VISIBLE);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    /**
     * Prepare the Previous Button (note text now DATABASES) according to
     * the state required;
     *
     * a) When Database/Table Lists are displayed together, the DATABASES
     * or Previous Button is not required as Databases are listed;
     *
     * b) When Table/Column Lists are displayed, the DATABASES
     * or Previous Button is required to allow the Database/Table list
     * combination to be displayed again (hence why originally called
     * previous).
     *
     * @param btn   The Button to be prepared.
     * @param use   Whether or not to use (HIDE/SHOW) the button,
     *              true to use, false to not use, the button.
     */
    private void prepPrevButton(Button btn, Boolean use) {
        btn.setText(this.getResources().getString(R.string.prev_button_text));
        if (!use) {
            btn.setVisibility(View.GONE);
            return;
        }
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatabaseList();
                showTableList(RIGHTLISTVIEW);
                showInfo(SHOWDATABASEINFO);
                mPrev.setVisibility(View.GONE);
            }
        });

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
        File db_directory = new File(this.getDatabasePath("x").getParent());
        String[] filelist = db_directory.list();
        for (String s: filelist) {
            if(!(s.contains("-journal"))) {
                rv.add(new DatabaseInfo(this,s));
            }
        }
        return rv;
    }

    /**
     * Set the list of methods that will be passed to the respective
     * Universal Array Adapters;
     * Done here as a Try/Catch block is required
     */
    private void setMethodsLists() {
        try {
            // For DatabaseList just the DatabaseInfo's getname method is
            // used to extract the data for the List.
            mDatabaseListMethods = new Method[] {
                    DatabaseInfo.class.getDeclaredMethod("getname")
            };
            // For TableList just the TableInfo's getTableName method is
            // used to extract the data for the List.
            mTableListMethods = new Method[] {
                    TableInfo.class.getDeclaredMethod("getTableName")
            };
            // For ColumnList just the CColumnInfo's getColumnName method is
            // used to extract the data for the List.
            mColumnListMethods = new Method[] {
                    ColumnInfo.class.getDeclaredMethod("getColumnName")
            };
            // For detailed Information two methods are used to extract the
            // data, which is held in a DoubleString object, so the
            // DoubleString's getString1 and getString2 methods.
            mDoubleStringMethods = new Method[] {
                    DoubleString.class.getDeclaredMethod("getString1"),
                    DoubleString.class.getDeclaredMethod("getString2")
            };
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }
}
