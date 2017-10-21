package mjt.sqliteinfoassistant;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import mjt.sqliteinformationassistant.SQLiteInformationAssistant;

public class ShowCase extends AppCompatActivity {

    public static final String[] coltypes = new String[]{"INTEGER","TEXT","REAL","NUMERIC","BLOB"};
    public static final String[] insertdata = new String[] {
            "The quick fox was brown but couldn't jump the fence, but tried so wasn't that lazy.",
            "1234.5678",
            "121",
            "678946",
            "1",
            "This is not a BLOB",
            "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789",
            "999",
            "NOW",
            "0.0.0.0",
            "<<<<<<<<>>>>>>>>"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_case);

        // Prepare some test databases
        createDataBases();
        // Note!
        // Alternative constructor with 2nd parameter as true will write
        // database information to the log
        SQLiteInformationAssistant SIA = new SQLiteInformationAssistant(this);
        SIA.show();     // show method invokes the SQLiteViewer activity
        new SQLiteInformationAssistant(this).show();
    }

    private void createDataBases() {

        // An empty database
        //SQLiteDatabase emptydb;
        SQLiteDatabase emptydb = openOrCreateDatabase("emptydb001",MODE_PRIVATE,null);
        emptydb.close();

        // Create a database with quite a few tables with varying # of rows and
        // with varying data
        SQLiteDatabase testdb001 = openOrCreateDatabase("testdb001",MODE_PRIVATE,null);
        for (int i =0; i < 20; i++) {
            String tblname = "testtable" + Integer.toString(i);
            String tbl_crt_sql = "CREATE TABLE IF NOT EXISTS " + tblname +
                    " (" +
                    "_id INTEGER PRIMARY KEY, ";
            ContentValues cv = new ContentValues();

            // create columns based upon table number
            for (int j=0; j < (i+1); j++) {
                String colname = "COL_" + Integer.toString(j) + " ";
                int typeoffset = j % coltypes.length;
                int dataoffset = j % insertdata.length;
                tbl_crt_sql = tbl_crt_sql + colname + coltypes[typeoffset];
                if (j != i) {
                    tbl_crt_sql = tbl_crt_sql + ",";
                }
                cv.put(colname,insertdata[dataoffset]);
            }
            tbl_crt_sql = tbl_crt_sql + ")";
            // Create the table
            testdb001.execSQL(tbl_crt_sql);

            // table created so insert some data
            for (int j = 0; j < (i+1); j++) {
                int dataoffset = j % insertdata.length;
                cv.put("COL_" + Integer.toString(j),insertdata[dataoffset]);
                testdb001.insert(tblname,null,cv);
            }
        }
        testdb001.close();
    }
}
