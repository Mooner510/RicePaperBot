package bot.cmd.buttons;

import bot.SchoolData;
import bot.cmd.BotButton;
import bot.cmd.commands.RiceCommand;
import net.dv8tion.jda.api.entities.Emoji;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

import java.util.*;

import static bot.Main.schools;
import static bot.cmd.BotEventListener.createId;
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
        event.deferEdit().setEmbeds(getRiceEmbed(schoolData, Long.parseLong(arguments[1]), RiceCommand.RiceType.values()))
                .setActionRow(
                        Button.primary(createId(event.getUser().getIdLong(), "rice", schoolData.getName(), c1.getTimeInMillis()), Emoji.fromUnicode("U+2B05")),
                        Button.primary(createId(event.getUser().getIdLong(), "rice", schoolData.getName(), c2.getTimeInMillis()), Emoji.fromUnicode("U+27A1"))
                ).queue();
    }
}
