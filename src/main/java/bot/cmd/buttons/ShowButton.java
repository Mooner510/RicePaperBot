package bot.cmd.buttons;

import bot.cmd.BotButton;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;

import java.util.concurrent.TimeUnit;

public class ShowButton implements BotButton {
    @Override
    public void onClick(ButtonInteractionEvent event) {
        event.getMessage().delete().queue();
        event.reply(event.getMember().getAsMention() + " 왜누름").queue(m -> m.deleteOriginal().queueAfter(5, TimeUnit.SECONDS));
    }
}
