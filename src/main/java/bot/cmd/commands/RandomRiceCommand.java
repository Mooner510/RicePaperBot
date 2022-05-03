package bot.cmd.commands;

import bot.SchoolData;
import bot.cmd.BotCommand;
import net.dv8tion.jda.api.entities.Emoji;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.selections.SelectMenu;

import java.text.SimpleDateFormat;
import java.util.*;

import static bot.Main.schools;
import static bot.cmd.BotEventListener.createId;
import static bot.cmd.commands.RiceCommand.getRiceEmbed;

public class RandomRiceCommand implements BotCommand {
    @Override
    public SlashCommandData getCommand() {
        return Commands.slash("ranice", "Random-Rice라는 뜻이며, 랜덤한 학교의 오늘 급식을 보여줘요!");
    }

    @Override
    public void onCommand(SlashCommandInteractionEvent event) {
        ArrayList<String> set = new ArrayList<>(schools.keySet());
        SchoolData schoolData = schools.get(set.get(new Random().nextInt(set.size())));

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

        event.deferReply(false).addEmbeds(getRiceEmbed(schoolData, year, month, day, RiceCommand.RiceType.values()))
                .addActionRow(
                        builder.build()
                ).addActionRow(
                        Button.primary(createId(event.getUser().getIdLong(), "rice", schoolData.getName(), c1.getTimeInMillis()), Emoji.fromUnicode("U+2B05")),
                        Button.primary(createId(event.getUser().getIdLong(), "rice", schoolData.getName(), c2.getTimeInMillis()), Emoji.fromUnicode("U+27A1"))
                ).queue();
    }
}
