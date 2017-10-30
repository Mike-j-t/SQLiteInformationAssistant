package mjt.sqliteinformationassistant;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.lang.reflect.Method;
import java.util.List;

/**
 * An extended version of the UniversalArrayAdapter to cater for changing
 * the text and background colours programmatically;
 *
 */

public class SQLiteViewerCustomAdapter extends UniversalArrayAdapter {

    int[] mViewlist;
    int mBckGrndColour;
    int mTextColour;

    SQLiteViewerCustomAdapter(Activity calling_activity,
                              int layout_resource_id,
                              List objectlist,
                              Method[] methodlist,
                              int[] viewlist,
                              String[] formatoverrides,
                              int backgroundcolour,
                              int textcolour) {
        super(calling_activity,
                layout_resource_id,
                objectlist,
                layout_resource_id,
                methodlist,
                viewlist,
                formatoverrides);
        mViewlist = viewlist;
        mBckGrndColour = backgroundcolour;
        mTextColour = textcolour;
    }

    @Override
    public View getView(int position,
                        View convertview,
                        @NonNull ViewGroup parent) {

        View view = super.getView(position, convertview, parent);

        for (int i: mViewlist) {
            TextView tv = view.findViewById(i);
            tv.setBackgroundColor(mBckGrndColour);
            tv.setTextColor(mTextColour);
        }
        return view;
    }

    public void setBackgroundColour(int colour) {
        this.mBckGrndColour = colour;
    }
    public void setTextColour(int colour) {
        this.mTextColour = colour;
    }
}
