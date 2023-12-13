package bot.cmd.util.autocomplete;

import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.Command;

import java.util.HashSet;

import static bot.Main.schools;

public class SchoolComplete {
    public static void schoolComplete(CommandAutoCompleteInteractionEvent event) {
        HashSet<String> strings = new HashSet<>(schools.keySet());
        strings.removeIf(s -> !s.startsWith(event.getFocusedOption().getValue()));
        HashSet<Command.Choice> choices = new HashSet<>();
        for (String s : strings) {
            if (choices.size() >= 24) break;
            choices.add(new Command.Choice(s, s));
        }
        event.replyChoices(choices).queue();
    }
}
