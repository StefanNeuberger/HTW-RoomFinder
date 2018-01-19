package s53324_s53849.lsf_app.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

public class Network {
    public static boolean isConnectedToNetwork(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnected();
        return isConnected;
    }

    public static InputStream connectOrThrowIfNoNetworkConnection(Context context, String url) throws IOException {
        if (isConnectedToNetwork(context)) {
            return connect(url);
        } else {
            throw new RuntimeException("No Network access. Please connect to the internet and try again.");
        }
    }

    public static InputStream connect(String url) throws IOException {
        URL u = new URL(url);
        URLConnection connection = u.openConnection();
        InputStream inputStream = new BufferedInputStream(connection.getInputStream());
        return inputStream;
    }
    //public static
}
