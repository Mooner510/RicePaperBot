package bot.cmd.commands;

import bot.cmd.BotCommand;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.managers.AudioManager;

import java.util.Random;

public class LeaveCommand implements BotCommand {
    @Override
    public void onMessage(MessageReceivedEvent event) {
        if (event.getAuthor().isBot()) return;
        Guild guild = event.getGuild();
        AudioManager manager = guild.getAudioManager();
        if (manager.isConnected()) {
            manager.closeAudioConnection();
            int ran = new Random().nextInt(7);
            String s;
            switch (ran) {
                case 0 -> s = "?";
                case 1 -> s = "ㅋㅋ";
                case 2 -> s = "능이바보";
                case 3 -> s = "물음표";
                case 4 -> s = "뭐.";
                case 5 -> s = "떡볶이 시러요";
                default -> s = "왜요";
            }
            event.getChannel().sendMessage(s).setActionRow(Button.success("show", "누르면 똑똑해짐")).queue();
        }
    }

}
