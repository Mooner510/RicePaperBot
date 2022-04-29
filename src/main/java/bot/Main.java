package bot;

import bot.cmd.BotEventListener;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;

import javax.security.auth.login.LoginException;
import java.util.Scanner;

public class Main {
    public static JDA jda;
    public static BotEventListener commandListener;

    public static void main(String[] args) {
        try {
            jda = JDABuilder.createLight(args[0], GatewayIntent.DIRECT_MESSAGES, GatewayIntent.GUILD_MESSAGES, GatewayIntent.DIRECT_MESSAGE_REACTIONS, GatewayIntent.GUILD_MESSAGE_REACTIONS, GatewayIntent.GUILD_MEMBERS, GatewayIntent.GUILD_PRESENCES, GatewayIntent.GUILD_VOICE_STATES)
                    .addEventListeners(commandListener = new BotEventListener())
                    .build();
        } catch (LoginException e) {
            throw new RuntimeException(e);
        }
        commandListener.register();
        commandListener.updateCommand();
        Scanner scanner = new Scanner(System.in);
        tag: while(true) {
            switch (scanner.nextLine()) {
                case "s":
                case "S":
                case "ㄴ":
                case "stop":
                case "ㄴ새ㅔ":
                case "STOP":
                    jda.shutdown();
                    break tag;
            }
        }
    }
}
