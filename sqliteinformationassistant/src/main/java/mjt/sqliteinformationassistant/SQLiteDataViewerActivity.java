package mjt.sqliteinformationassistant;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.widget.CursorAdapter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import static mjt.sqliteinformationassistant.SQLiteInformationAssistant.*;

public class SQLiteDataViewerActivity extends AppCompatActivity {

    // Define Intent keys for data passed from the invoking activity
    public static final String
            INTENTKEY_DATABASENAME = "ik_dataabsename",
            INTENTKEY_DATABASEPATH = "ik_databasepath",
            INTENTKEY_TABLENAME = "ik_tablename";

    // Request code for returning data (not needed but..)
    public static final int REQUESTCODE_SQLITEDATAVIEWER = 11;

    String mDataBasepath,
            mDatabaseName,
            mTableName;
    TextView mHeading,
            mLegendText,
            mLegendInteger,
            mLegendReal,
            mLegendBlob,
            mLegendUnknown;
    int[] mTextViewID_HeadingList;
    int mBaseBackgroundColour,
            mBytesToShowInBlob;
    LinearLayout mBaseBackground;
    ListView mDataLisView;
    Button mDoneButton;
    SQLiteDataViewerCursorAdapter mSDVCA;
    Cursor mCSR;
    SQLiteDatabase mDB;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sqlite_dataviewer);

        mBaseBackground = (LinearLayout) this.findViewById(R.id.ddv_entire_layout);
        mHeading = (TextView) this.findViewById(R.id.ddv_heading);
        mDoneButton = (Button) this.findViewById(R.id.ddv_done);
        mLegendText = (TextView) this.findViewById(R.id.ddv_legend_text);
        mLegendInteger = (TextView) this.findViewById(R.id.ddv_legend_integer);
        mLegendReal = (TextView) this.findViewById(R.id.ddv_legend_real);
        mLegendBlob = (TextView) this.findViewById(R.id.ddv_legend_blob);
        mLegendUnknown = (TextView) this.findViewById(R.id.ddv_legend_unknown);

        // get the id's for the column headings
        mTextViewID_HeadingList = new int[]{
                R.id.ddv_hdgtext1, R.id.ddv_hdgtext2, R.id.ddv_hdgtext3,
                R.id.ddv_hdgtext4, R.id.ddv_hdgtext5,R.id.ddv_hdgtext6,
                R.id.ddv_hdgtext7, R.id.ddv_hdgtext8, R.id.ddv_hdgtext9,
                R.id.ddv_hdgtext10, R.id.ddv_hdgtext11, R.id.ddv_hdgtext12,
                R.id.ddv_hdgtext13, R.id.ddv_hdgtext14, R.id.ddv_hdgtext15
        };
        // get the ListView that will display the data
        mDataLisView = (ListView) this.findViewById(R.id.ddv_datalistview);

        // get the data passed from the invoking activity
        mDatabaseName = getIntent().getStringExtra(INTENTKEY_DATABASENAME);
        mDataBasepath = getIntent().getStringExtra(INTENTKEY_DATABASEPATH);
        mTableName = getIntent().getStringExtra(INTENTKEY_TABLENAME);
        mBaseBackgroundColour = getIntent().getIntExtra(INTENTKEY_BASE_BCKGRNDCOLOUR,R.color.default_basebackground);
        mLegendText.setBackgroundColor(getIntent().getIntExtra(INTENTKEY_STRINGCELL_BCKGRNDCOLOUR,R.color.default_string_cell));
        mLegendInteger.setBackgroundColor(getIntent().getIntExtra(INTENTKEY_INTEGERCELL_BCKGRNDCOLOUR,R.color.default_integer_cell));
        mLegendReal.setBackgroundColor(getIntent().getIntExtra(INTENTKEY_DOUBLECELL_BCKGRNDCOLOUR,R.color.default_double_cell));
        mLegendBlob.setBackgroundColor(getIntent().getIntExtra(INTENTKEY_BLOBCELL_BCKGRNDCOLOUR,R.color.default_blob_cell));
        mLegendUnknown.setBackgroundColor(getIntent().getIntExtra(INTENTKEY_UNKNOWNCELL_BCKGRNDCOLOUR,R.color.default_unknown_cell));
        mBaseBackground.setBackgroundColor(getIntent().getIntExtra(INTENTKEY_BASE_BCKGRNDCOLOUR,R.color.default_basebackground));
        mBytesToShowInBlob = getIntent().getIntExtra(INTENTKEY_BYTESTOSHOWINBLOB,DEFAULT_BYTES_TO_SHOW_IN_BLOB);

        mHeading.setText(getResources().getString(R.string.dataviewheading,mDatabaseName,mTableName));

        // Open the Database and get a cursor with an _id column
        mDB = SQLiteDatabase.openDatabase(mDataBasepath,null,SQLiteDatabase.OPEN_READONLY);
        mCSR = mDB.query(mTableName,
                new String[]{"rowid AS _id,*"},null,null,null,null,null);

        // Count the occurences of columns named _id
        int idcount = 0;
        int columncount = mCSR.getColumnCount();
        for (int i=0; i < columncount; i++) {
            if (mCSR.getColumnName(i).equals("_id")) {
                idcount++;
            }
        }
        // if there the number of _id columns is more than 1 then
        // get a cursor without the rowid AS _id as it is not needed
        // and could be confusing
        if (idcount > 1) {
            mCSR = mDB.query(mTableName,null,null,null,null,null,null);
        }

        // determine how many columns will be displayed ensuring a
        // maximum of 15 columns
        int maxcols = 15;
        if (mCSR.getColumnCount() < 15) {
            maxcols = mCSR.getColumnCount();
        }

        // Set the Column Headings to the respective Column Names
        for (int i=0; i < maxcols; i++) {
            ((TextView) this.findViewById(mTextViewID_HeadingList[i])).setText(mCSR.getColumnName(i));
        }

        // Prepare the Custom Cursor Adapter
        mSDVCA = new SQLiteDataViewerCursorAdapter(this,
                mCSR,
                CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER,
                getIntent(),
                mBytesToShowInBlob
        );
        // Attach the Adapter to the ListView
        mDataLisView.setAdapter(mSDVCA);

        // Finally setup the DONE button to return to the invoking
        // acctivity if clicked (i.e. finish this activity)
        mDoneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mCSR.close();   // Close the cursor
        mDB.close();    // Close the database
    }
}
