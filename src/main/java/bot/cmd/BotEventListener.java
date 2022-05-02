package bot.cmd;

import bot.cmd.buttons.RiceButton;
import bot.cmd.commands.*;
import bot.cmd.selects.RiceSelects;
import bot.utils.InteractionIdParser;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.SelectMenuInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.HashSet;
import java.util.StringJoiner;

import static bot.Main.jda;
import static bot.Main.queueLog;

public class BotEventListener extends ListenerAdapter {
    public static HashMap<String, BotCommand> commands;
    public static HashMap<String, BotButton> buttons;
    public static HashMap<String, BotSelectMenu> selects;

    public BotEventListener() {
        commands = new HashMap<>();
        buttons = new HashMap<>();
        selects = new HashMap<>();
    }

    public void register() {
        commands.put("rice", new RiceCommand());
        commands.put("setschool", new SetSchoolCommand());
        commands.put("setnotify", new SetNotifyCommand());

        buttons.put("rice", new RiceButton());

        selects.put("rice", new RiceSelects());
        updateCommand();
    }

    public void updateCommand() {
        HashSet<SlashCommandData> data = new HashSet<>();
        commands.forEach((s, i) -> data.add(i.getCommand()));
        jda.updateCommands().addCommands(data).queue();
    }

    public static String createId(long userId, String command, Object... arguments) {
        StringJoiner joiner = new StringJoiner("/");
        for (Object o : arguments) joiner.add(String.valueOf(o));
        return userId+":"+command+"%"+joiner;
    }

    public static InteractionIdParser parseId(String id) {
        String[] v1 = id.split(":");
        String[] v2 = v1[1].split("%");
        String[] v3 = v2[1].split("/");
        return new InteractionIdParser(Long.parseLong(v1[0]), v2[0], v3);
    }

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        String id;
        if(commands.containsKey(id = event.getName())) {
            commands.get(id).onCommand(event);
            queueLog("SlashCommand", event.getUser(), id);
        }
    }

    @Override
    public void onCommandAutoCompleteInteraction(@NotNull CommandAutoCompleteInteractionEvent event) {
        String id;
        if(commands.containsKey(id = event.getName())) {
            commands.get(id).onComplete(event);
        }
    }

    @Override
    public void onButtonInteraction(@NotNull ButtonInteractionEvent event) {
        String id = event.getButton().getId();
        if(id != null) {
            InteractionIdParser parser = parseId(id);
            if (parser.compare(event.getUser())) {
                buttons.get(parser.getCmd()).onClick(event, parser.getArguments());
                queueLog("ButtonInteract", event.getUser(), id);
            }
        }
    }

    @Override
    public void onSelectMenuInteraction(@NotNull SelectMenuInteractionEvent event) {
        String id = event.getSelectMenu().getId();
        if(id != null) {
            id = id.split(":")[0];
            selects.get(id).onSelect(event);
        }
    }
}
