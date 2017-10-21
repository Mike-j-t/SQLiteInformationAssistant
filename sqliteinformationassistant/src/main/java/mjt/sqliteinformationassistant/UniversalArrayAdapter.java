package mjt.sqliteinformationassistant;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Locale;

/**
 * Universal adapter
 */
class UniversalArrayAdapter extends ArrayAdapter<Object> {

    private final Activity activity;
    @SuppressWarnings("FieldCanBeLocal")
    private final int mBaseLayoutResourcceID;
    private final List mObjectList;
    private final int mItemLayoutResourceID;
    private final Method[] mMethodlist;
    private final int[] mViewList;
    private final String[] mFormatOverrides;
    private final LayoutInflater mInflater;


    /**
     * ArrayAdapter that can handle objects with getters as an alternative to
     *
     * @param calling_activity          The owning activity for the instance
     * @param base_layout_resource_id   The base layout resource
     * @param objectlist                 The List
     * @param item_layout_resource_id   The Item layout resource
     * @param methodlist                Methods used to obtain the data
     * @param viewlist                  Views into which the data is placed;
     *                                  NOTE the methods and views MUST be paired;
     *                                  NOTE there must be at least 1 pair;
     *                                  Exceptions if either NOTE is contravened.
     */
    private UniversalArrayAdapter(Activity calling_activity,
                                  int base_layout_resource_id,   // ID (int) of layout
                                  List objectlist,  // List of Objects e.g. ArrayList<object>
                                  int item_layout_resource_id,   // ID (int) of Item layout
                                  Method[] methodlist,
                                  int[] viewlist,
                                  String[] formatoverrides
    ) {
        super(calling_activity,base_layout_resource_id,objectlist);
        this.activity = calling_activity;
        this.mBaseLayoutResourcceID = base_layout_resource_id;
        this.mObjectList = objectlist;
        this.mItemLayoutResourceID = item_layout_resource_id;
        this.mMethodlist = methodlist;
        this.mViewList = viewlist;
        this.mFormatOverrides = formatoverrides;

        mInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (mMethodlist.length != mViewList.length) {
            throw new RuntimeException(
                    "Number of Elements for MethodList(5th parameter) and ViewList(6th parameter) do not match." +
                            " Elements must be paired."
            );
        }
        if (mMethodlist.length < 1 || mViewList.length < 1) {
            throw new RuntimeException(
                    "0 Elelemnts in the MethodList(5th parameter) and/or ViewList(6th parameter)." +
                            " At least 1 element per pair is required."
            );
        }
    }

    public UniversalArrayAdapter(Activity calling_activity,
                                 int base_layout_resource_id,   // ID (int) of layout
                                 List objectlist,  // List of Objects e.g. ArrayList<object>
                                 int item_layout_resource_id,   // ID (int) of Item layout
                                 Method[] methodlist,
                                 int[] viewlist
    ) {
        this(calling_activity, base_layout_resource_id, objectlist, item_layout_resource_id, methodlist, viewlist, new String[]{});
    }

    @NonNull
    @SuppressLint("ViewHolder")
    @Override
    public View getView(int position, View convertview, @NonNull ViewGroup parent) {
        View view;
        view = mInflater.inflate(mItemLayoutResourceID,null);
        // loop through the Method/View pairs setting the View (TextView)
        // with text as obtained from the paried method
        for (int i=0; i < mMethodlist.length;i++) {
            String formatoverride = "";
            if (i < mFormatOverrides.length) {
                formatoverride = mFormatOverrides[i];
            }
            ((TextView) view.findViewById(
                    mViewList[i])).setText(
                    getDataFromProvidedMethod(
                            mMethodlist[i],
                            mObjectList.get(position),
                            formatoverride
                    )
            );
        }
        return view;
    }

    /**
     * Get the data from the object as a string according to the provided
     * method; Copes with methods that return long, int, float, double and String
     * @param method    The method to be used to extract the data
     * @param object    The Object from which the data is to be extracted
     * @param format    A format to overide the default format
     * @return          The extracted data as a string;
     *                  Will return unhandled method type if the method
     *                  does not return a listed type.
     */
    private String getDataFromProvidedMethod(Method method, Object object, String format) {
        Locale locale;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            locale = activity.getResources().getConfiguration().getLocales().get(0);
        } else {
            //noinspection deprecation
            locale = activity.getResources().getConfiguration().locale;
        }
        String returntype = method.getReturnType().toString();
        if (returntype.equals(long.class.toString())) {
            try {
                return String.format(locale, substituteFormatOveride("%d",format), (long) method.invoke(object));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (returntype.equals(int.class.toString())) {
            try {
                return String.format(locale, substituteFormatOveride("%d",format), (int) method.invoke(object));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (returntype.equals(String.class.toString())) {
            try {
                return (String) method.invoke(object);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (returntype.equals(float.class.toString())) {
            try {
                return String.format(locale, substituteFormatOveride("%f",format), (float) method.invoke(object));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (returntype.equals(double.class.toString())) {
            try {
                return String.format(locale,substituteFormatOveride("%f",format), (double) method.invoke(object));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return "unhandled method type";
    }

    /**
     * Short version of getDataFromProvidedMethod without format override
     * @param method    The method to be invoked on the object
     * @param object    The Object to be acted upon and have a method invoked
     * @return          The resultant String
     */
    private String getDataFromProvidedMethod(Method method, Object object) {
        return getDataFromProvidedMethod(method,object,"");
    }

    /**
     * Substitute default formatting string with overridng formatting string if
     * the overriding string is great than 1 character
     * @param dflt      The default string to use to format the string
     * @param override  The override string to replace the default string
     * @return          The formatting string to be used
     */
    private String substituteFormatOveride(String dflt, String override) {
        if (override.length() > 0) {
            return override;
        }
        return dflt;
    }

    public void swapItems(List objectlist) {
        if (objectlist != null) {
            this.mObjectList.clear();
            this.mObjectList.addAll(objectlist);
            this.notifyDataSetChanged();
        }
    }
}
