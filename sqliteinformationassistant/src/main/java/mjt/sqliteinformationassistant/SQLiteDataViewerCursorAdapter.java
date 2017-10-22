package mjt.sqliteinformationassistant;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Custom Cursor Adapter
 */

public class SQLiteDataViewerCursorAdapter extends CursorAdapter {

    Context mContext;
    int mMaxColumns = 15;
    int[] mMaxStrlengths;
    TextView[] mTextViewList = new TextView[mMaxColumns];
    int[] mTextViewIDList = new int[]{
            R.id.ddv_text1, R.id.ddv_text2, R.id.ddv_text3, R.id.ddv_text4,
            R.id.ddv_text5, R.id.ddv_text6, R.id.ddv_text7, R.id.ddv_text8,
            R.id.ddv_text9, R.id.ddv_text10, R.id.ddv_text11, R.id.ddv_text12,
            R.id.ddv_text13, R.id.ddv_text14, R.id.ddv_text15
    };

    SQLiteDataViewerCursorAdapter (Context context, Cursor csr, int flags) {
        super(context, csr, flags);
        mContext = context;
        if (csr.getColumnCount() < mMaxColumns ) {
            mMaxColumns = csr.getColumnCount();
            mTextViewList = new TextView[mMaxColumns];
        }
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        view = initView(view,cursor);
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        view = super.getView(position, view, parent);
        if (position % 2 == 0) {
            view.setBackgroundColor(0x558888FF); //TODO should not hard code colour
        } else {
            view.setBackgroundColor(0x338888FF); //TODO should not hard code colour
        }
        return view;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(
                R.layout.listview_sqlite_dataviewer_items,
                parent,
                false
        );
    }

    private View initView(View view, Cursor csr) {
            for (int i = 0; i < mMaxColumns; i++) {
                mTextViewList[i] = view.findViewById(mTextViewIDList[i]);
                switch (csr.getType(i)) {
                    case Cursor.FIELD_TYPE_STRING:
                        mTextViewList[i].setText(csr.getString(i));
                        break;
                    case Cursor.FIELD_TYPE_INTEGER:
                        mTextViewList[i].setText(
                                Long.toString(csr.getLong(i))
                        );
                        break;
                    case Cursor.FIELD_TYPE_FLOAT:
                        mTextViewList[i].setText(
                                Double.toString(csr.getDouble(i))
                        );
                        break;
                    case Cursor.FIELD_TYPE_NULL:
                        mTextViewList[i].setText("");
                        break;
                    case Cursor.FIELD_TYPE_BLOB:
                        mTextViewList[i].setText(
                                "BLOB - unsupported"
                        );
                        break;
                    default:
                        mTextViewList[i].setText(
                                "Unknown/Unsupported Colum Type"
                        );
                        break;
                }
            }
            return view;
    }
}
