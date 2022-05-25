package bot.utils;

import net.dv8tion.jda.api.entities.User;

public record InteractionIdParser(long getUserId, String getCmd, String[] getArguments) {
    public boolean compare(User user) {
        return user.getIdLong() == getUserId;
    }
}
