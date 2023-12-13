package bot.cmd;

import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;

public interface BotSelectMenu {
    void onSelect(StringSelectInteractionEvent event);
}
