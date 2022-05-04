package bot;

import bot.cmd.BotEventListener;
import bot.cmd.commands.RiceCommand;
import bot.scheduler.task.RiceTask;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Channel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.Event;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.SelectMenuInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.components.selections.SelectOption;
import net.dv8tion.jda.api.requests.GatewayIntent;

import javax.security.auth.login.LoginException;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import java.util.StringJoiner;

import static bot.scheduler.task.RiceTask.send;

public class Main {
    public static JDA jda;
    public static BotEventListener commandListener;

    public static HashMap<String, SchoolData> schools;
    public static ArrayList<String> sortedSchools;

    public static <T extends Event> void queueLog(User user, Channel textChannel, T obj) {
        String tag = null;
        String s = null;
        if(obj instanceof SlashCommandInteractionEvent o) {
            StringJoiner joiner = new StringJoiner(" ");
            for (OptionMapping option : o.getOptions()) {
                switch (option.getType()) {
                    case STRING -> joiner.add(option.getName() + ":" + option.getAsString());
                    case INTEGER -> joiner.add(option.getName() + ":" + option.getAsInt());
                    case BOOLEAN -> joiner.add(option.getName() + ":" + option.getAsBoolean());
                }
            }
            s = "/" + o.getName() + " " + joiner;
            tag = "SlashCommand";
        } else if(obj instanceof ButtonInteractionEvent o) {
            s = "\"" + o.getButton().getLabel() + "\" " + o.getButton().getId();
            tag = "ButtonClick";
        } else if(obj instanceof SelectMenuInteractionEvent o) {
            StringJoiner joiner = new StringJoiner(" ");
            for (SelectOption option : o.getSelectedOptions()) {
                joiner.add(option.getLabel());
            }
            s = joiner.toString();
            tag = "SelectMenuClick";
        }

        if(tag == null || s == null) return;

        try(
                FileWriter fw = new FileWriter(new File("src/main/resources", "command.log"), StandardCharsets.UTF_8, true);
                BufferedWriter bw = new BufferedWriter( fw )
        ) {
            bw.write("[" + tag + "] " + user.getAsTag() + " (" + user.getIdLong() + ")" + ": " + s);
            bw.newLine();
            bw.flush();
        }catch ( IOException e ) {
            e.printStackTrace();
        }

        String s1 = s.isEmpty() ? "" : ("\n> " + s);
        switch (textChannel.getType()) {
            case TEXT ->
                    jda.getTextChannelById(970930218103631902L)
                            .sendMessage("**[ " + tag + " ]** \\#Channel:" + textChannel.getAsMention() + " <" + textChannel.getIdLong() + ">\n" + user.getAsTag() + " " + user.getAsMention() + " <" + user.getIdLong() + ">" + s1).queue();
            case PRIVATE ->
                    jda.getTextChannelById(970930218103631902L)
                            .sendMessage("**[ " + tag + " ]** \\#Channel:DM\n" + user.getAsTag() + " " + user.getAsMention() + s1).queue();
        }
    }

    public static void main(String[] args) {
        try{
            Class.forName("org.sqlite.JDBC");
        }catch(Exception x){
            x.printStackTrace();
        }

        schools = new HashMap<>();
        try(
                FileReader rw = new FileReader("src/main/resources/schools.txt", StandardCharsets.UTF_8);
                BufferedReader br = new BufferedReader( rw )
        ) {
            String readLine;
            while( ( readLine =  br.readLine()) != null ){
                String[] strings = readLine.split(",");
                schools.put(strings[0], new SchoolData(strings[0], strings[1], strings[2]));
            }
        } catch ( IOException e ) {
            e.printStackTrace();
        }

        sortedSchools = new ArrayList<>(schools.keySet());
        sortedSchools.sort(String::compareTo);

        try {
            jda = JDABuilder.createLight(args[0], GatewayIntent.DIRECT_MESSAGES, GatewayIntent.GUILD_MESSAGES, GatewayIntent.DIRECT_MESSAGE_REACTIONS, GatewayIntent.GUILD_MESSAGE_REACTIONS, GatewayIntent.GUILD_MEMBERS, GatewayIntent.GUILD_PRESENCES, GatewayIntent.GUILD_VOICE_STATES)
                    .addEventListeners(commandListener = new BotEventListener())
                    .build();
        } catch (LoginException e) {
            throw new RuntimeException(e);
        }
        commandListener.register();
        commandListener.updateCommand();

        RiceTask riceTask = new RiceTask();

        Scanner scanner = new Scanner(System.in);
        tag: while(true) {
            switch (scanner.nextLine()) {
                case "breakfast":
                case "BREAKFAST":
                    send(RiceCommand.RiceType.BREAKFAST);
                    break;
                case "dinner":
                case "DINNER":
                    send(RiceCommand.RiceType.DINNER);
                    break;
                case "lunch":
                case "LUNCH":
                    send(RiceCommand.RiceType.LUNCH);
                    break;
                case "":
                case "s":
                case "S":
                case "ㄴ":
                case "stop":
                case "ㄴ새ㅔ":
                case "STOP":
                    jda.cancelRequests();
                    jda.shutdown();
                    riceTask.unregister();
                    break tag;
            }
        }
    }
}
