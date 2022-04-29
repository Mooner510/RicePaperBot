package bot.cmd;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageDeleteEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static bot.Main.jda;

public class UserLogger extends ListenerAdapter {

    public static final HashMap<Long, Map.Entry<Long, String>> oldMessages = new HashMap<>();

//    private void restore() {
//        final Iterator<Map.Entry<Long, String>> it = oldMessages.entrySet().iterator();
//        int i = 0;
//        while (it.hasNext()) {
//            final Map.Entry<Long, String> next = it.next();
//            if(i++ >= 1000) {
//                i
//            }
//        }
//    }


    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        oldMessages.put(event.getMessageIdLong(), new AbstractMap.SimpleEntry<>(event.getMember().getIdLong(), event.getMessage().getContentRaw()));
    }

    @Override
    public void onMessageDelete(@NotNull MessageDeleteEvent event) {
        if(oldMessages.containsKey(event.getMessageIdLong())) {
            final TextChannel channel = event.getJDA().getChannelById(TextChannel.class, 969239043244183593L);
            final Map.Entry<Long, String> entry = oldMessages.get(event.getMessageIdLong());
            EmbedBuilder builder = new EmbedBuilder();
            final User sender = jda.getUserById(entry.getKey());
            builder.setAuthor(sender.getName(), sender.getAvatarUrl(), sender.getAvatarUrl());
            channel.sendMessage("메시지가 뭐가 사라짐!").setEmbeds(builder.build()).queue();
        }
    }
}
