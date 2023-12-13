package bot.cmd.util.rice;

import bot.Main;
import bot.SchoolData;
import bot.utils.BotColor;
import bot.utils.BotUtils;
import bot.utils.Json;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.*;

public class Rice {
    private static String gguk(String s) {
        String[] strings = s.split("<br/>");
        int v = strings.length;
        for (int i = 0; i < v; i++) {
            strings[i] = strings[i].replaceAll("\\(([^(^)]+)\\)", "").replaceAll("([.*\\-]+)", "");
            if (strings[i].length() <= 1) continue;
            while (true) {
                char b = strings[i].charAt(strings[i].length() - 1);
                if (b >= 32 && b <= 126) {
                    strings[i] = strings[i].substring(0, strings[i].length() - 1);
                } else {
                    break;
                }
            }
        }
        return String.join("\n", strings);
    }

    public static MessageEmbed getRiceEmbed(SchoolData schoolData, long time, RiceType... type) {
        Date date = new Date(time);
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("Asia/Seoul"));
        cal.setTime(date);

        return getRiceEmbed(schoolData, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) + 1, cal.get(Calendar.DAY_OF_MONTH), type);
    }

    public static MessageEmbed getRiceEmbed(SchoolData schoolData, int year, int month, int day, RiceType... type) {
        String url = Json.urlFormat(schoolData, year, month, day);
        System.out.println(url);

        JSONObject object = Json.readJsonFromUrl(url);
        EmbedBuilder builder = new EmbedBuilder();
        builder.setTitle(Json.currentDate(year, month, day) + " 급식표 :rice:").setColor(BotColor.RICE)
                .appendDescription("`영양 정보(단위: g): [탄수화물/단백질/지방]`");

        HashSet<String> babs = new HashSet<>();

        for (RiceType riceType : type) {
            babs.add(riceType.getTag());
        }

        boolean done = false;

        if (object.has("mealServiceDietInfo")) {
            final JSONArray array = object.getJSONArray("mealServiceDietInfo").getJSONObject(1).getJSONArray("row");

            HashMap<String, String> index = new HashMap<>();
            HashMap<String, String> calInfo = new HashMap<>();
            HashMap<String, String> ntrInfo = new HashMap<>();

            int length = array.length();
            for (int i = 0; i < length; i++) {
                JSONObject json = array.getJSONObject(i);
                String tag = json.getString("MMEAL_SC_NM");
                index.put(tag, json.getString("DDISH_NM"));
                calInfo.put(tag, json.getString("CAL_INFO"));
                String s = json.getString("NTR_INFO").replace("<br/>", ":").replace(" ", "");
                String[] split = s.split(":");
                ntrInfo.put(tag, new StringJoiner("/")
                        .add(BotUtils.parseString(Double.parseDouble(split[1]), 1, true))
                        .add(BotUtils.parseString(Double.parseDouble(split[3]), 1, true))
                        .add(BotUtils.parseString(Double.parseDouble(split[5]), 1, true))
                        .toString());
            }

            for (String s : babs) {
                String data = index.get(s);
                if (data == null) {
                    builder.addField(s, "급식이 없어요!", false);
                } else {
                    builder.addField(s + " - " + calInfo.get(s) + " [" + ntrInfo.get(s) + "]", gguk(index.get(s)), false);
                    done = true;
                }
            }
        } else {
            builder.setDescription("앗! 이날은 급식이 아에 없나 봐요!\n\n달력을 확인해 보세요. 이날이 휴일은 아닌가요?\n혹은 재량휴업이나 방학같이 급식을 제공하지 않는 날일 수도 있어요.");
            done = true;
        }

        if (done) {
            builder.setFooter("학교명: " + schoolData.getName() + " [" + Main.version + "]");
            return builder.build();
        } else {
            return null;
        }
    }
}
