package bot.cmd.commands;

import bot.cmd.BotCommand;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import static bot.Main.jda;

public class BotStateCommand implements BotCommand {

    @Override
    public void onMessage(MessageReceivedEvent event) {
        jda.getPresence().setActivity(Activity.playing("등장"));
        new Thread() {
            @Override
            public void run() {
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                jda.getPresence().setActivity(Activity.watching("꾸에엑"));
            }
        }.start();
    }
}
