package bot.cmd.commands;

import bot.cmd.BotCommand;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class PingCommand implements BotCommand {
    @Override
    public void onMessage(MessageReceivedEvent event) {
        MessageChannel channel = event.getChannel();
        long time = System.currentTimeMillis();
        channel.sendMessage("Pong!").queue(response ->
                response.editMessageFormat("Pong: %d ms", System.currentTimeMillis() - time).queue());
    }
}
