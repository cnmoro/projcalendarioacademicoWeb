package calendarioacademico.servicos;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Douglas
 */
import org.json.JSONObject;
import org.primefaces.model.map.LatLng;
public class GoogleRequest {

    private final static String GOOGLE_URL = "https://maps.googleapis.com/maps/api/geocode/";
    private final static String TYPE = "json";
    private final static String KEY = "AIzaSyBII3VkimKbXuOhA0QuM_6RTqZgWktKQ6w";

    public GoogleRequest() {
    }

    private static String getUrl(String addrs) {
        return GOOGLE_URL + TYPE + "?address=" + addrs + "&key=" + KEY;
    }

    private static String getHTML(String address) throws Exception {
        address = address.replaceAll(" ", "+");
        StringBuilder result = new StringBuilder();
        URL url = new URL(getUrl(address));
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        String line;
        while ((line = rd.readLine()) != null) {
            result.append(line);
        }
        rd.close();
        return result.toString();
    }

    public static LatLng getLatLng(String address) {
        double lat = 0;
        double lng = 0;
        try {
            JSONObject json = new JSONObject(getHTML(address));
            JSONObject results = (JSONObject) json.getJSONArray("results").get(0);
            JSONObject location =  results.getJSONObject("geometry").getJSONObject("location");
            lat = location.getDouble("lat");
            lng = location.getDouble("lng");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return new LatLng(lat, lng);
    }
}
