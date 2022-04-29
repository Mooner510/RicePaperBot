package bot.cmd.commands.privateCommand;

import bot.cmd.BotCommand;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.managers.AudioManager;

public class MusicCommand implements BotCommand {
    @Override
    public void onMessage(MessageReceivedEvent event) {
        if (event.getAuthor().isBot()) return;
        Guild guild = event.getGuild();
        VoiceChannel channel = guild.getVoiceChannelById(943567232527654983L);
        AudioManager manager = guild.getAudioManager();
        AudioPlayerManager playerManager = new DefaultAudioPlayerManager();
        AudioSourceManagers.registerRemoteSources(playerManager);
        manager.setSendingHandler(new MySendHandler(playerManager.createPlayer()));
        manager.openAudioConnection(channel);
    }
}
