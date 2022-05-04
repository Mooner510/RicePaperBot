package bot.cmd.commands;

import bot.Main;
import bot.SchoolData;
import bot.cmd.BotCommand;
import bot.utils.BotColor;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Emoji;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.selections.SelectMenu;
import org.json.JSONArray;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;

import static bot.Main.sortedSchools;
import static bot.cmd.BotEventListener.createId;
import static bot.utils.DB.getSchool;
import static bot.utils.Json.*;

public class RiceCommand implements BotCommand {
    @Override
    public SlashCommandData getCommand() {
        return Commands.slash("rice", "오늘의 급식은 뭐가 나올까?")
                .addOption(OptionType.STRING, "학교명", "급식을 보고 싶은 학교를 선택해줘요!", false, true);
    }

    private static String gguk(String s){
        String[] strings = s.split("<br/>");
        int v = strings.length;
        for (int i = 0; i < v; i++) {
            strings[i] = strings[i].replaceAll("\\(([^(^)]+)\\)", "").replaceAll("([.*\\-]+)", "");
            if(strings[i].length() <= 1) continue;
            while(true) {
                char b = strings[i].charAt(strings[i].length() - 1);
                if(b >= 32 && b <= 126) {
                    strings[i] = strings[i].substring(0, strings[i].length() - 1);
                } else {
                    break;
                }
            }
        }
        return String.join("\n", strings);
    }

    public enum RiceType {
        BREAKFAST("조식"), LUNCH("중식"), DINNER("석식");

        private final String tag;

        RiceType(String s) {
            tag = s;
        }

        public String getTag() {
            return tag;
        }
    }

    public static MessageEmbed getRiceEmbed(SchoolData schoolData, long time, RiceType... type) {
        Date date = new Date(time);
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("Asia/Seoul"));
        cal.setTime(date);

        return getRiceEmbed(schoolData, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) + 1, cal.get(Calendar.DAY_OF_MONTH), type);
    }

    public static MessageEmbed getRiceEmbed(SchoolData schoolData, int year, int month, int day, RiceType... type) {
        String url = urlFormat(schoolData, year, month, day);
        System.out.println(url);

        JSONObject object = readJsonFromUrl(url);
        EmbedBuilder builder = new EmbedBuilder();
        builder.setTitle(currentDate(month, day) + " 급식표 :rice:").setColor(BotColor.RICE);

        HashSet<String> babs = new HashSet<>();

        for (RiceType riceType : type) {
            babs.add(riceType.getTag());
        }

        boolean done = false;

        if(object.has("mealServiceDietInfo")) {
            final JSONArray array = object.getJSONArray("mealServiceDietInfo").getJSONObject(1).getJSONArray("row");

            HashMap<String, String> index = new HashMap<>();

            int length = array.length();
            for (int i = 0; i < length; i++) {
                JSONObject json = array.getJSONObject(i);
                index.put(json.getString("MMEAL_SC_NM"), json.getString("DDISH_NM"));
            }

            for (String s : babs) {
                String data = index.get(s);
                if(data == null) {
                    builder.addField(s, "급식이 없어요!", false);
                } else {
                    builder.addField(s, gguk(index.get(s)), false);
                    done = true;
                }
            }
        } else {
            builder.setDescription("앗! 이날은 급식이 아에 없나 봐요!\n\n달력을 확인해 보세요. 이날이 휴일은 아닌가요?\n혹은 재량휴업이나 방학같이 급식을 제공하지 않는 날일 수도 있어요.");
            done = true;
        }

        if(done) {
            builder.setFooter("학교명: " + schoolData.getName());
            return builder.build();
        } else {
            return null;
        }
    }

    @Override
    public void onCommand(SlashCommandInteractionEvent event) {
        SchoolData schoolData;
        OptionMapping s = event.getOption("학교명");

        if(s == null) {
            if((schoolData = getSchool(event.getUser().getIdLong())) == null) {
                EmbedBuilder builder = new EmbedBuilder();
                builder.setTitle("어머! 당신의 학교는 어디인가요?").setDescription("빠른 급식 명령어(학교를 입력하지 않음)를 사용하시려면 학교를 등록해주세요\n`/setschool`명령어를 통해 학교를 등록할 수 있습니다!").setColor(BotColor.FAIL);
                event.deferReply(false).addEmbeds(builder.build()).queue();
                return;
            }
        } else {
            schoolData = Main.schools.get(s.getAsString());
        }

        if(schoolData == null) {
            EmbedBuilder builder = new EmbedBuilder();
            builder.setTitle("어머! 그 학교는 대체 어디죠?").setDescription("설마 새로운 학교를 만들 생각은 아닌거죠?\n참고로 학교 이름은 완전한 이름으로 부탁드려요.").setColor(BotColor.FAIL);
            event.deferReply(false).addEmbeds(builder.build()).queue();
            return;
        }

        Date date = new Date();
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("Asia/Seoul"));
        cal.setTime(date);

        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH) + 1;
        int day = cal.get(Calendar.DAY_OF_MONTH);

        Calendar c1 = Calendar.getInstance(TimeZone.getTimeZone("Asia/Seoul"));
        Calendar c2 = Calendar.getInstance(TimeZone.getTimeZone("Asia/Seoul"));
        c1.setTime(date);
        c1.add(Calendar.DAY_OF_MONTH, -1);
        c2.setTime(date);
        c2.add(Calendar.DAY_OF_MONTH, 1);

        Calendar c = Calendar.getInstance(TimeZone.getTimeZone("Asia/Seoul"));
        SelectMenu.Builder builder = SelectMenu.create("rice");
        SimpleDateFormat format = new SimpleDateFormat("yyyy년 MM월 dd일");
        for(int i = -12; i <= 12; i++) {
            c.setTime(date);
            c.add(Calendar.DAY_OF_MONTH, i);
            builder.addOption(format.format(c.getTime()), schoolData.getName() + ":" + c.getTimeInMillis());
        }
        builder.setRequiredRange(0, 1);
        builder.setPlaceholder(format.format(cal.getTime()));

        event.deferReply(false).addEmbeds(getRiceEmbed(schoolData, year, month, day, RiceType.values()))
                .addActionRow(
                        builder.build()
                ).addActionRow(
                        Button.primary(createId(event.getUser().getIdLong(), "rice", schoolData.getName(), c1.getTimeInMillis()), Emoji.fromUnicode("U+2B05")),
                        Button.primary(createId(event.getUser().getIdLong(), "rice", schoolData.getName(), c2.getTimeInMillis()), Emoji.fromUnicode("U+27A1"))
                ).queue();
    }

    @Override
    @SuppressWarnings("unchecked")
    public void onComplete(CommandAutoCompleteInteractionEvent event) {
        ArrayList<String> strings = (ArrayList<String>) sortedSchools.clone();
        strings.removeIf(s -> !s.startsWith(event.getFocusedOption().getValue()));
        ArrayList<Command.Choice> choices = new ArrayList<>();
        for (String s : strings) {
            if(choices.size() >= 24) break;
            choices.add(new Command.Choice(s, s));
        }
        event.replyChoices(choices).queue();
    }
}
