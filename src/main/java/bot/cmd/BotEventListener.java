package bot.cmd;

import bot.cmd.commands.*;
import bot.utils.InteractionIdParser;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
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

    public BotEventListener() {
        commands = new HashMap<>();
    }

    public void register() {
        commands.put("rice", new RiceCommand());
        commands.put("setschool", new SetSchoolCommand());
        commands.put("setnotify", new SetNotifyCommand());
        commands.put("help", new HelpCommand());
        commands.put("ranice", new RandomRiceCommand());
        commands.put("changes", new ChangesCommand());
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
        return userId + ":" + command + "%" + joiner;
    }

    public static InteractionIdParser parseId(String id) {
        String[] v1 = id.split(":");
        String[] v2 = v1[1].split("%");
        String[] v3 = v2[1].split("/");
        return new InteractionIdParser(Long.parseLong(v1[0]), v2[0], v3);
    }

    public HashMap<Long, Long> commandDelay = new HashMap<>();

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        long time = commandDelay.getOrDefault(event.getUser().getIdLong(), 0L);
        long l = System.currentTimeMillis();
        if (time + 2000 >= l) {
            event.deferReply(true).setContent("잠시 멈춰요! 천천히좀 해줘요;;").queue();
            commandDelay.put(event.getUser().getIdLong(), l);
            return;
        } else {
            commandDelay.put(event.getUser().getIdLong(), l);
        }
        String id;
        if (commands.containsKey(id = event.getName())) {
            commands.get(id).onCommand(event);
            queueLog(event.getUser(), event.getChannel(), event);
        }
    }

    @Override
    public void onCommandAutoCompleteInteraction(@NotNull CommandAutoCompleteInteractionEvent event) {
        String id;
        if (commands.containsKey(id = event.getName())) {
            commands.get(id).onComplete(event);
        }
    }

    @Override
    public void onButtonInteraction(@NotNull ButtonInteractionEvent event) {
        String id = event.getButton().getId();
        if (id != null) {
            InteractionIdParser parser = parseId(id);
            if (parser.compare(event.getUser())) {
                BotCommand command = commands.get(parser.getCmd());
                if (command instanceof BotButton button) {
                    button.onClick(event, parser.getArguments());
                    queueLog(event.getUser(), event.getChannel(), event);
                }
            }
        }
    }

    @Override
    public void onStringSelectInteraction(StringSelectInteractionEvent event) {
        String id = event.getComponent().getId();
        if (id != null) {
            InteractionIdParser parser = parseId(id);
            if (parser.compare(event.getUser())) {
                BotCommand command = commands.get(parser.getCmd());
                if (command instanceof BotSelectMenu menu) {
                    menu.onSelect(event);
                    queueLog(event.getUser(), event.getChannel(), event);
                }
            }
        }
    }
}
