package bot.cmd.commands;

import bot.cmd.BotCommand;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.json.JSONObject;

import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class RiceCommand implements BotCommand {
    private static String readAll(Reader rd) throws IOException {
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

    private static String urlFormat(int year, int month, int day) {
        return "https://open.neis.go.kr/hub/mealServiceDietInfo?KEY=83814d97c86242f7ae8e68e83a051933&Type=json&ATPT_OFCDC_SC_CODE=G10&SD_SCHUL_CODE=7430310&MLSV_YMD=" + year + a(month) + a(day);
    }

    private static String a(int value) {
        if(value < 10) {
            return "0" + value;
        } else {
            return String.valueOf(value);
        }
    }

    @Override
    public void onMessage(MessageReceivedEvent event) {
        Date date = new Date();
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("Asia/Seoul"));
        cal.setTime(date);
        JSONObject object = readJsonFromUrl(urlFormat(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) + 1, cal.get(Calendar.DAY_OF_MONTH)));
        System.out.println(object.toString(3));
    }
}
