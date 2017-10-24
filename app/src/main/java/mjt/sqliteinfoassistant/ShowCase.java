package mjt.sqliteinfoassistant;

import android.content.ContentValues;
import android.content.ReceiverCallNotAllowedException;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

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
    public static ArrayList<byte[]> insertblobdata = new ArrayList<byte[]>();
    Random rnd = new Random(System.currentTimeMillis());


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_case);

        // Setup the button so a click starts SIA
        final Button tryitout = (Button) this.findViewById(R.id.tryitoutbutton);
        tryitout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tryitout();
            }
        });


        // Prepare some test databases
        createDataBases();
    }

    private void tryitout() {
        // just use it without any customisation (not that there is any yet)
        new SQLiteInformationAssistant(this).show();
    }

    private void createDataBases() {

        // An empty database
        //SQLiteDatabase emptydb;
        SQLiteDatabase emptydb = openOrCreateDatabase("emptydb001",MODE_PRIVATE,null);
        emptydb.close();
        initInsertBlobData();

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
            int rowstoadd = rnd.nextInt(20) + 1;
            for (int ra=0; ra < rowstoadd; ra++) {
                for (int j = 0; j < (i + 1); j++) {
                    int doffset = rnd.nextInt(insertdata.length);
                    int boffset = rnd.nextInt(insertblobdata.size());
                    if (rnd.nextInt(5) > 3) {
                        byte[] insbyte = insertblobdata.get(boffset);
                        cv.put("COL_" + Integer.toString(j), insbyte);
                    } else {
                        cv.put("COL_" + Integer.toString(j), insertdata[doffset]);
                    }
                }
                testdb001.insert(tblname, null, cv);
                cv.clear();
            }
        }
        testdb001.close();
    }

    private void initInsertBlobData() {
        for (int i=0; i < 25; i++) {
            byte[] ba = new byte[rnd.nextInt(128) + 1];
            for (int j=0; j < ba.length; j++) {
                ba[j] = (byte) rnd.nextInt(127);
            }
            insertblobdata.add(ba);
        }
    }
}
