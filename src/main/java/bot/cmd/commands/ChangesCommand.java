package bot.cmd.commands;

import bot.cmd.BotCommand;
import bot.utils.Json;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import org.json.JSONArray;
import org.json.JSONObject;

import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.StringJoiner;

public class ChangesCommand implements BotCommand {
    @Override
    public SlashCommandData getCommand() {
        return Commands.slash("changes", "마지막으로 봇이 업데이트 된 후 변경사항을 확인합니다!");
    }

    @Override
    public void onCommand(SlashCommandInteractionEvent event) {
        EmbedBuilder builder = new EmbedBuilder();
        JSONArray json = Json.readJsonArrayFromUrl("https://api.github.com/repos/Mooner510/RicePaperBot/commits");
        JSONObject data = json.getJSONObject(0);
        JSONObject commit = data.getJSONObject("commit");
        String message = commit.getString("message");
        String[] split = message.split("\n");
        builder.setTitle(split[0], data.getString("html_url"));
        StringJoiner joiner = new StringJoiner("\n");
        for (int i = 1; i < split.length; i++) if(!split[i].isEmpty()) joiner.add(split[i]);
        builder.setDescription(joiner.toString());
        JSONObject committer = commit.getJSONObject("committer");
        builder.setFooter(committer.getString("name") + " " + committer.getString("date").replace("T", " ").replace("Z", ""));
        event.deferReply(false).addEmbeds(builder.build()).queue();
    }
}
