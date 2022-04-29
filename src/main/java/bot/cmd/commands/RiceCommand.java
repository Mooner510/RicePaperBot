package bot.cmd.commands;

import bot.cmd.BotCommand;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.*;

import static bot.utils.Json.readJsonFromUrl;
import static bot.utils.Json.urlFormat;

public class RiceCommand implements BotCommand {
    @Override
    public SlashCommandData getCommand() {
        return Commands.slash("rice", "오늘은 뭐가 나올까");
    }

    private String gguk(String s){
        final String replace = s.replaceAll("\\(([^(^)]+)\\)", "").replaceAll("<([^<^>]+)>", "").replace(".", "");
        StringJoiner joiner = new StringJoiner(", ");
        for (String string : replace.split(" ")) {
            if(!string.isEmpty()) joiner.add(string);
        }
        return joiner.toString();
    }

    @Override
    public void onCommand(SlashCommandInteractionEvent event) {
        Date date = new Date();
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("Asia/Seoul"));
        cal.setTime(date);
        JSONObject object = readJsonFromUrl(urlFormat(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) + 1, cal.get(Calendar.DAY_OF_MONTH)));
        EmbedBuilder builder = new EmbedBuilder();
        builder.setTitle("급식표").setDescription("오늘급식").setColor(new Random().nextInt(16777216));
        final JSONArray array = object.getJSONArray("mealServiceDietInfo").getJSONObject(1).getJSONArray("row");
        // \(([^(^)]+)\)

        builder
                .addField(new MessageEmbed.Field("조식", gguk(array.getJSONObject(0).getString("DDISH_NM")), false))
                .addField(new MessageEmbed.Field("중식", gguk(array.getJSONObject(1).getString("DDISH_NM")), false))
                .addField(new MessageEmbed.Field("석식", gguk(array.getJSONObject(2).getString("DDISH_NM")), false));

        event.deferReply(false).addEmbeds(builder.build()).queue();
    }
}
