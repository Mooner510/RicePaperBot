package bot.cmd.selects;

import bot.SchoolData;
import bot.cmd.BotButton;
import bot.cmd.BotSelectMenu;
import bot.cmd.commands.RiceCommand;
import net.dv8tion.jda.api.entities.Emoji;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.SelectMenuInteractionEvent;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.selections.SelectMenu;
import net.dv8tion.jda.api.interactions.components.selections.SelectOption;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import static bot.Main.schools;
import static bot.cmd.BotEventListener.createId;
import static bot.cmd.commands.RiceCommand.getRiceEmbed;

public class RiceSelects implements BotSelectMenu {

    @Override
    public void onSelect(SelectMenuInteractionEvent event) {
        if ((event.getInteraction().getSelectedOptions().size() <= 0)) {
            return;
        }
        String[] split = event.getInteraction().getSelectedOptions().get(0).getValue().split(":");
        SchoolData schoolData = schools.get(split[0]);
        Date date = new Date(Long.parseLong(split[1]));

        Calendar c1 = Calendar.getInstance(TimeZone.getTimeZone("Asia/Seoul"));
        Calendar c2 = Calendar.getInstance(TimeZone.getTimeZone("Asia/Seoul"));
        c1.setTime(date);
        c1.add(Calendar.DAY_OF_MONTH, -1);
        c2.setTime(date);
        c2.add(Calendar.DAY_OF_MONTH, 1);

        Calendar c = Calendar.getInstance(TimeZone.getTimeZone("Asia/Seoul"));
        SelectMenu.Builder builder = SelectMenu.create(createId(event.getUser().getIdLong(), "rice", "select"));
        SimpleDateFormat format = new SimpleDateFormat("yyyy년 MM월 dd일");
        for(int i = -12; i <= 12; i++) {
            c.setTime(date);
            c.add(Calendar.DAY_OF_MONTH, i);
            builder.addOption(format.format(c.getTime()), schoolData.getName() + ":" + c.getTimeInMillis());
        }
        builder.setRequiredRange(0, 1);
        builder.setPlaceholder(format.format(date));

        event.deferEdit().setEmbeds(getRiceEmbed(schoolData, Long.parseLong(split[1]), RiceCommand.RiceType.values()))
                .setActionRows(
                        ActionRow.of(
                                builder.build()
                        ), ActionRow.of(
                                Button.primary(createId(event.getUser().getIdLong(), "rice", schoolData.getName(), c1.getTimeInMillis()), Emoji.fromUnicode("U+2B05")),
                                Button.primary(createId(event.getUser().getIdLong(), "rice", schoolData.getName(), c2.getTimeInMillis()), Emoji.fromUnicode("U+27A1"))
                        )
                ).queue();
    }
}
