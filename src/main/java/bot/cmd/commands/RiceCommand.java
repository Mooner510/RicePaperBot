package bot.cmd.commands;

import bot.cmd.BotCommand;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import static bot.utils.Json.readJsonFromUrl;
import static bot.utils.Json.urlFormat;

public class RiceCommand implements BotCommand {
    @Override
    public SlashCommandData getCommand() {
        return Commands.slash("rice", "오늘은 뭐가 나올까?");
    }

    @Override
    public void onCommand(SlashCommandInteractionEvent event) {
        Date date = new Date();
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("Asia/Seoul"));
        cal.setTime(date);
        JSONObject object = readJsonFromUrl(urlFormat(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) + 1, cal.get(Calendar.DAY_OF_MONTH)));
        System.out.println(object.toString(3));
    }
}
