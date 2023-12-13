package bot.cmd.buttons;

import bot.cmd.BotButton;
import bot.cmd.BotEventListener;
import bot.SchoolData;
import bot.cmd.commands.RiceCommand;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.selections.StringSelectMenu;

import java.text.SimpleDateFormat;
import java.util.*;

import static bot.Main.schools;
import static bot.cmd.commands.RiceCommand.getRiceEmbed;

public class RiceButton implements BotButton {

    @Override
    public void onClick(ButtonInteractionEvent event, String[] arguments) {
        SchoolData schoolData = schools.get(arguments[0]);
        Date date = new Date(Long.parseLong(arguments[1]));

        Calendar c1 = Calendar.getInstance(TimeZone.getTimeZone("Asia/Seoul"));
        Calendar c2 = Calendar.getInstance(TimeZone.getTimeZone("Asia/Seoul"));
        c1.setTime(date);
        c1.add(Calendar.DAY_OF_MONTH, -1);
        c2.setTime(date);
        c2.add(Calendar.DAY_OF_MONTH, 1);

        Calendar c = Calendar.getInstance(TimeZone.getTimeZone("Asia/Seoul"));
        StringSelectMenu.Builder builder = StringSelectMenu.create(BotEventListener.createId(event.getUser().getIdLong(), "rice", "select"));
        SimpleDateFormat format = new SimpleDateFormat("yyyy년 MM월 dd일");
        for (int i = -12; i <= 12; i++) {
            c.setTime(date);
            c.add(Calendar.DAY_OF_MONTH, i);
            builder.addOption(format.format(c.getTime()), schoolData.getName() + ":" + c.getTimeInMillis());
        }
        builder.setPlaceholder(format.format(date));

        event.deferEdit().setEmbeds(getRiceEmbed(schoolData, Long.parseLong(arguments[1]), RiceCommand.RiceType.values()))
                .setActionRow(
                        builder.build(),
                        Button.primary(BotEventListener.createId(event.getUser().getIdLong(), "rice", schoolData.getName(), c1.getTimeInMillis()), Emoji.fromUnicode("U+2B05")),
                        Button.primary(BotEventListener.createId(event.getUser().getIdLong(), "rice", schoolData.getName(), c2.getTimeInMillis()), Emoji.fromUnicode("U+27A1"))
                ).queue();
    }
}
