package bot.cmd;

import bot.cmd.buttons.ShowButton;
import bot.cmd.commands.*;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.SelectMenuInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.HashSet;

import static bot.Main.jda;

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

        buttons.put("rice", new ShowButton());

        updateCommand();
    }

    public void updateCommand() {
        HashSet<SlashCommandData> data = new HashSet<>();
        commands.forEach((s, i) -> data.add(i.getCommand()));
        jda.updateCommands().addCommands(data).queue();
    }

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        String id;
        if(commands.containsKey(id = event.getName())) {
            commands.get(id).onCommand(event);
        }
    }

    @Override
    public void onButtonInteraction(@NotNull ButtonInteractionEvent event) {
        String id;
        if(buttons.containsKey(id = event.getButton().getId())) {
            buttons.get(id).onClick(event);
        }
    }

    @Override
    public void onSelectMenuInteraction(@NotNull SelectMenuInteractionEvent event) {
        String id;
        if(selects.containsKey(id = event.getComponent().getId())) {
            selects.get(id).onSelect(event);
        }
    }
}
