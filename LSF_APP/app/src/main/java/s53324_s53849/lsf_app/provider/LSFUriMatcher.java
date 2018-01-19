package s53324_s53849.lsf_app.provider;

import android.content.UriMatcher;
import android.net.Uri;
import android.util.SparseArray;

/**
 * A class with only one public method used to match uris to their corresponding {@link LSFUriEnum}.
 */
public class LSFUriMatcher {
    private UriMatcher matcher;
    //maps the codes of the uri enums to the enums
    private SparseArray<LSFUriEnum> map;

    /**
     * Returns the {@link LSFUriEnum} which matches to the given uri or null if no match has been found.
     *
     * @return the {@link LSFUriEnum} which matches to the given uri or null if no match has been found.
     */
    public LSFUriEnum match(Uri uri) {
        int code = matcher.match(uri);
        return match(code);
    }

    private LSFUriEnum match(int code) {
        return map.get(code);
    }

    public LSFUriMatcher() {
        this.matcher = new UriMatcher(UriMatcher.NO_MATCH);
        map = new SparseArray<>();
        for (LSFUriEnum uriEnum : LSFUriEnum.values()) {
            //add to the list of uris that result in a match by the UriMatcher
            matcher.addURI(LSFContract.PROVIDER_AUTHORITY, uriEnum.baseUri, uriEnum.code);
            //add to the map
            map.append(uriEnum.code, uriEnum);
        }
    }
}
