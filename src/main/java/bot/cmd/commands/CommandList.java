package bot.cmd.commands;

import bot.cmd.BotCommand;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class CommandList implements BotCommand {
    @Override
    public void onMessage(MessageReceivedEvent event) {
        MessageChannel channel = event.getChannel();
        long time = System.currentTimeMillis();
        channel.sendMessage("```떡볶이 먹으러가요 : 고구마방 입장\n떡볶이 먹으러갈까? : 고구마방 입장\n같이가요 : 고구마방 입장\n" +
                "!ping : 퐁\n" + "핑 : 퐁\n" + "현석이 : 퐁\n"+ "나가 : 고구마방 퇴장\n"+ "꺼져 : 고구마방 퇴장\n```").queue();
    }
}