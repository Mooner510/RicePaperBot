package bot.cmd;

import bot.cmd.buttons.ShowButton;
import bot.cmd.commands.*;
import bot.cmd.commands.privateCommand.*;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import java.util.HashMap;

public class BotEventListener extends ListenerAdapter {
    public static HashMap<String, BotCommand> commands;
    public static HashMap<String, BotButton> buttons;

    public BotEventListener() {
        commands = new HashMap<>();
        buttons = new HashMap<>();
    }

    public void register() {
        this.registerCommand();
        this.registerButton();
    }

    public void registerCommand() {
        commands.put("밥", new RiceCommand());
    }


    public void registerButton() {
        buttons.put("show", new ShowButton());
    }

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        String cmd;
        if(commands.containsKey(cmd = event.getMessage().getContentRaw())) {
            commands.get(cmd).onMessage(event);
        }
    }

    @Override
    public void onButtonInteraction(@NotNull ButtonInteractionEvent event) {
        System.out.println(event.getButton());
        String id;
        if(buttons.containsKey(id = event.getButton().getId())) {
            buttons.get(id).onClick(event);
        }
    }
}
