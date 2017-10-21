package mjt.sqliteinformationassistant;

/**
 * Basic object with two Strings,
 *
 * used/implemented to suit UniversalArrayAdapter, which
 * uses passed method's to extract data from the List
 * passed to the adapter.
 *
 * Hence the getString1 and getString2 methods
 *
 * Note UniversalArrayAdapter is bespoke/legacy
 */

@SuppressWarnings("WeakerAccess")
public class DoubleString {
    private final String mString1;
    private final String mString2;

    public DoubleString(String string1, String string2) {
        mString1 = string1;
        mString2 = string2;
    }

    public String getString1() {
        return mString1;
    }

    public String getString2() {
        return mString2;
    }
}
