package mjt.sqliteinformationassistant;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Locale;
import static mjt.sqliteinformationassistant.SQLiteInformationAssistant.*;

/**
 * Custom Cursor Adapter
 */

public class SQLiteDataViewerCursorAdapter extends CursorAdapter {

    Context mContext;
    Locale mCurrentLocale;
    int mMaxColumns = 15,
            mTextBackgroundColour,
            mIntegerBackgroundColour,
            mRealBackGroundColour,
            mBlobBackgroundColour,
            mUnknownBackgroundColour,
            mTextTextColour,
            mIntegerTextColour,
            mRealTextColour,
            mBlobTextColour,
            mUnknownTextColour;
    int[] mMaxStrlengths;
    TextView[] mTextViewList = new TextView[mMaxColumns];
    int[] mTextViewIDList = new int[]{
            R.id.ddv_text1, R.id.ddv_text2, R.id.ddv_text3, R.id.ddv_text4,
            R.id.ddv_text5, R.id.ddv_text6, R.id.ddv_text7, R.id.ddv_text8,
            R.id.ddv_text9, R.id.ddv_text10, R.id.ddv_text11, R.id.ddv_text12,
            R.id.ddv_text13, R.id.ddv_text14, R.id.ddv_text15
    };

    SQLiteDataViewerCursorAdapter (Context context, Cursor csr, int flags, Intent intent) {
        super(context, csr, flags);
        mContext = context;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            mCurrentLocale = mContext.getResources().getConfiguration().getLocales().get(0);
        } else {
            mCurrentLocale = mContext.getResources().getConfiguration().locale;
        }
        if (csr.getColumnCount() < mMaxColumns ) {
            mMaxColumns = csr.getColumnCount();
            mTextViewList = new TextView[mMaxColumns];
        }
        mTextBackgroundColour = intent.getIntExtra(INTENTKEY_STRINGCELL_BCKGRNDCOLOUR, R.color.default_string_cell);
        mIntegerBackgroundColour = intent.getIntExtra(INTENTKEY_INTEGERCELL_BCKGRNDCOLOUR, R.color.default_integer_cell);
        mRealBackGroundColour = intent.getIntExtra(INTENTKEY_DOUBLECELL_BCKGRNDCOLOUR, R.color.default_double_cell);
        mBlobBackgroundColour = intent.getIntExtra(INTENTKEY_BLOBCELL_BCKGRNDCOLOUR, R.color.default_blob_cell);
        mUnknownBackgroundColour = intent.getIntExtra(INTENTKEY_UNKNOWNCELL_BCKGRNDCOLOUR,R.color.default_unknown_cell);
        mTextTextColour = intent.getIntExtra(INTENTKEY_STRINGCELL_TEXTCOLOUR,R.color.default_text_color);
        mIntegerTextColour = intent.getIntExtra(INTENTKEY_INTEGERCELL_TEXTCOLOUR,R.color.default_text_color);
        mRealTextColour = intent.getIntExtra(INTENTKEY_DOUBLECELL_TEXTCOLOUR, R.color.default_text_color);
        mBlobTextColour = intent.getIntExtra(INTENTKEY_BLOBCELL_TEXTCOLOUR, R.color.default_text_color);
        mUnknownTextColour = intent.getIntExtra(INTENTKEY_UNKNOWNCELL_TEXTCOLOUR, R.color.default_text_color);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        initView(view,cursor);
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        view = super.getView(position, view, parent);
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
                        mTextViewList[i].setBackgroundColor(mTextBackgroundColour);
                        mTextViewList[i].setTextColor(mTextTextColour);
                        break;
                    case Cursor.FIELD_TYPE_INTEGER:
                        mTextViewList[i].setText(
                                String.format(
                                    mCurrentLocale,
                                    "%1$d",
                                    csr.getLong(i)
                                )
                        );
                        mTextViewList[i].setBackgroundColor(mIntegerBackgroundColour);
                        mTextViewList[i].setTextColor(mIntegerTextColour);
                        break;
                    case Cursor.FIELD_TYPE_FLOAT:
                        mTextViewList[i].setText(
                                String.format(
                                        mCurrentLocale,
                                        "%1$f",
                                        csr.getDouble(i)
                                )
                        );
                        mTextViewList[i].setBackgroundColor(mRealBackGroundColour);
                        mTextViewList[i].setTextColor(mRealTextColour);
                        break;
                    case Cursor.FIELD_TYPE_NULL:
                        mTextViewList[i].setText(
                                mContext.getResources().getString(R.string.nullvalue));
                        mTextViewList[i].setBackgroundColor(mUnknownBackgroundColour);
                        mTextViewList[i].setTextColor(mUnknownTextColour);
                        break;
                    case Cursor.FIELD_TYPE_BLOB:
                        mTextViewList[i].setText(
                                SQLiteInformationAssistant
                                        .getBytedata(csr.getBlob(i),24)
                        );
                        mTextViewList[i].setBackgroundColor(mBlobBackgroundColour);
                        mTextViewList[i].setTextColor(mBlobTextColour);
                        break;
                    default:
                        mTextViewList[i].setText(
                                mContext.getResources().getString(R.string.unsupportedcolumntype)
                        );
                        mTextViewList[i].setBackgroundColor(mUnknownBackgroundColour);
                        mTextViewList[i].setTextColor(mUnknownBackgroundColour);
                        break;
                }
            }
            return view;
    }
}
