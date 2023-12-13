package bot.cmd.util.autocomplete;

import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.Command;

import java.util.HashSet;

import static bot.Main.schools;

public class SchoolComplete {
    public static void schoolComplete(CommandAutoCompleteInteractionEvent event) {
        event.replyChoices(schools.keySet().parallelStream()
                .filter(s -> s.startsWith(event.getFocusedOption().getValue()))
                .map(s -> new Command.Choice(s, s))
                .limit(24)
                .toList()).queue();
    }
}
