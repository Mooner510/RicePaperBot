package bot.utils;

import org.json.JSONObject;

import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class Json {
    public static String readAll(Reader rd) throws IOException {
        StringBuilder sb = new StringBuilder();
        int cp;
        while ((cp = rd.read()) != -1) {
            sb.append((char) cp);
        }
        return sb.toString();
    }

    public static JSONObject readJsonFromUrl(String url) {
        try(InputStream is = new URL(url).openStream()) {
            BufferedReader rd = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
            String jsonText = readAll(rd);
            return new JSONObject(jsonText);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String urlFormat(int year, int month, int day) {
        return "https://open.neis.go.kr/hub/mealServiceDietInfo?KEY=83814d97c86242f7ae8e68e83a051933&Type=json&ATPT_OFCDC_SC_CODE=G10&SD_SCHUL_CODE=7430310&MLSV_YMD=" + year + a(month) + a(day);
    }

    private static String a(int value) {
        if(value < 10) {
            return "0" + value;
        } else {
            return String.valueOf(value);
        }
    }
}
