package bot.cmd;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

public interface BotCommand {
    SlashCommandData getCommand();

    void onCommand(SlashCommandInteractionEvent event);
}
