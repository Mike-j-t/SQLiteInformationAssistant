package mjt.sqliteinfoassistant;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;
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
    public static ArrayList<byte[]> insertblobdata = new ArrayList<>();
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
        // Get an SQLiteInformationAssistant instance
        // Note! the show method invokes the Activity
        SQLiteInformationAssistant SIA = new SQLiteInformationAssistant(this);

        // Instance customisation i.e set Display attributes
        //SIA.setBaseBackgroundColour(0xFFFF00FF);
        SIA.setHeadingTextColour(0xFF999999);
        SIA.setDatabaseListHeadingTextColour(0xFF00FFFF);
        SIA.setTableListHeadingTextColour(0xFF0077FF);
        SIA.setColumnListHeadingTextColour(0xFF77FF00);

        SIA.setDatabaseListTextColour(0xFFFFFFFF);
        SIA.setDatabaseInfoTextColour(0xFFFF0000);
        SIA.setTableListTextColour(0xFFFF00FF);
        SIA.setTableInfoTextcolour(0xFF0000FF);
        SIA.setColumnListTextColour(0xFFFFFF00);
        SIA.setColumnInfoTextColour(0xFF000000);

        //SIA.setStringCellBackgroundColour(0xFFEEEEEE);
        //SIA.setStringCellTextColour(0xFF0000FF);
        //SIA.setIntegerCellBackgroundColour(0xFFFF0000);
        //SIA.setIntegerCelltextColour(0XFFFFFFFF);
        //SIA.setDoubleCellBackgroundColour(0xFF00FFFF);
        //SIA.setDoubleCelltextColour(0XFF5555FF);
        //SIA.setBlobCellBackgroundColour(0xFF44FF55);
        //SIA.setBlobCelltextColour(0xFFFFFF00);
        // Note
        SIA.setBytesToShowInBlob(128);
        SIA.show();
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
            StringBuilder sb = new StringBuilder(" CREATE TABLE IF NOT EXISTS "
                    + tblname + "(" +
                    "_id INTEGER PRIMARY KEY, "
            );
            ContentValues cv = new ContentValues();

            // create columns based upon table number
            for (int j=0; j < (i+1); j++) {
                String colname = "COL_" + Integer.toString(j) + " ";
                int typeoffset = j % coltypes.length;
                int dataoffset = j % insertdata.length;
                sb.append(colname);
                sb.append(coltypes[typeoffset]);
                if (j != i) {
                    sb.append(",");
                }
                cv.put(colname,insertdata[dataoffset]);
            }
            sb.append(")");
            testdb001.execSQL(sb.toString());

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
