package bot.utils;

import bot.SchoolData;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.Locale;

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

    public static JSONArray readJsonArrayFromUrl(String url) {
        try(InputStream is = new URL(url).openStream()) {
            BufferedReader rd = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
            String jsonText = readAll(rd);
            return new JSONArray(jsonText);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String urlFormat(SchoolData data, int year, int month, int day) {
        return "https://open.neis.go.kr/hub/mealServiceDietInfo?KEY=83814d97c86242f7ae8e68e83a051933&Type=json&ATPT_OFCDC_SC_CODE=" + data.getTag() + "&SD_SCHUL_CODE=" + data.getCode() +  "&MLSV_YMD=" + year + a(month) + a(day);
    }

    private static String a(int value) {
        if(value < 10) {
            return "0" + value;
        } else {
            return String.valueOf(value);
        }
    }

    public static String currentDate(int year, int month, int day) {
        LocalDate date = LocalDate.of(year, month, day);
        return month + "월 " + day + "일 " + date.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.KOREAN);
    }
}
