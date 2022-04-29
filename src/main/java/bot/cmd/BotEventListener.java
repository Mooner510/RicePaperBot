package bot.cmd;

import bot.cmd.buttons.ShowButton;
import bot.cmd.commands.*;
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
        commands.put("떡볶이 먹으러가요", new MusicCommand());
        commands.put("떡볶이 먹으러갈까?", new MusicCommand());
        commands.put("같이가요", new MusicCommand());
        commands.put("!ping", new PingCommand());
        commands.put("핑", new PingCommand());
        commands.put("현석이", new PingCommand());
        commands.put("나가", new LeaveCommand());
        commands.put("꺼져", new LeaveCommand());
        commands.put("엄지", new TalkCommandUmg());
        commands.put("능", new TalkCommandSeoul());
        commands.put("능히", new TalkCommandSeoul());
        commands.put("별", new TalkCommandStar());
        commands.put("시?험", new ImageCommandTest());
        commands.put("시험", new ImageCommandTest());
        commands.put("스게", new ImageCommandSugee());
        commands.put("비키니", new ImageCommandBikini());
        commands.put("시발", new ImageCommandSibal());
        commands.put("자", new ImageCommandPenisgood());
        commands.put("여우", new ImageCommandYae());
        commands.put("상태", new BotStateCommand());
        commands.put("명령어", new CommandList());
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
