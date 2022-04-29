package bot.cmd;

import net.dv8tion.jda.api.events.interaction.component.SelectMenuInteractionEvent;

public interface BotSelectMenu {
    void onSelect(SelectMenuInteractionEvent event);
}
