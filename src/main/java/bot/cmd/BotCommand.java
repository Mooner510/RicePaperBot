package bot.cmd;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public interface BotCommand {
    void onMessage(MessageReceivedEvent event);
}
