package bot.cmd.commands;

import bot.cmd.BotCommand;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.interactions.components.LayoutComponent;

import java.io.File;
import java.util.concurrent.TimeUnit;

public class TalkCommandStar implements BotCommand {
    @Override
    public void onMessage(MessageReceivedEvent event) {
        MessageChannel channel = event.getChannel();
        long time = System.currentTimeMillis();
        channel.sendMessage("테토").queue(r -> r.delete().queueAfter(7, TimeUnit.MILLISECONDS));
        channel.sendFile(new File("C:\\Users\\DSM2022\\Downloads\\107_20220227015140.png")).queue();
    }
}