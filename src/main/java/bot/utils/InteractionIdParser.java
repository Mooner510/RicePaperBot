package bot.utils;

import net.dv8tion.jda.api.entities.User;

public record InteractionIdParser(long userId, String cmd, String[] arguments) {

    public String getCmd() {
        return cmd;
    }

    public boolean compare(User user) {
        return user.getIdLong() == userId;
    }

    public String[] getArguments() {
        return arguments;
    }
}
