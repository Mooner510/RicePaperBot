package bot;

import bot.cmd.BotEventListener;
import bot.cmd.util.rice.RiceType;
import bot.scheduler.task.RiceTask;
import bot.utils.InteractionIdParser;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.entities.channel.Channel;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.Event;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.components.selections.SelectOption;
import net.dv8tion.jda.api.requests.GatewayIntent;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

import static bot.cmd.BotEventListener.parseId;
import static bot.scheduler.task.RiceTask.send;

public class Main {
    public static final String version = "v1.4.4";

    public static JDA jda;
    public static BotEventListener commandListener;

    public static HashMap<String, SchoolData> schools;
    public static ArrayList<String> sortedSchools;

    public static <T extends Event> void queueLog(User user, Channel textChannel, T obj) {
        EmbedBuilder builder = new EmbedBuilder();
        if (obj instanceof SlashCommandInteractionEvent o) {
            StringJoiner joiner = new StringJoiner(" ");
            for (OptionMapping option : o.getOptions()) {
                switch (option.getType()) {
                    case STRING -> joiner.add(option.getName() + ":" + option.getAsString());
                    case INTEGER -> joiner.add(option.getName() + ":" + option.getAsInt());
                    case BOOLEAN -> joiner.add(option.getName() + ":" + option.getAsBoolean());
                }
            }
            builder.setTitle("Executed Slash Command: /" + o.getName());
            builder.appendDescription("Parameters:");
            if (joiner.length() <= 0) {
                builder.appendDescription("\n> null");
            } else {
                builder.appendDescription("\n> " + joiner);
            }
        } else if (obj instanceof ButtonInteractionEvent o) {
            InteractionIdParser parser = parseId(Objects.requireNonNull(o.getButton().getId()));
            builder.setTitle("Clicked Button: /" + parser.getCmd());
            builder.appendDescription("Parameters:");
            String s = String.join(" ", parser.getArguments());
            if (s.isEmpty()) {
                builder.appendDescription("\n> null");
            } else {
                builder.appendDescription("\n> " + s);
            }
        } else if (obj instanceof StringSelectInteractionEvent o) {
            InteractionIdParser parser = parseId(Objects.requireNonNull(o.getComponent().getId()));
            StringJoiner joiner = new StringJoiner(" ");
            for (SelectOption option : o.getSelectedOptions()) {
                joiner.add(option.getLabel());
            }
            builder.setTitle("Clicked Select Menu: /" + parser.getCmd());
            builder.appendDescription("Parameters: ");
            String s = String.join(" ", parser.getArguments());
            if (s.isEmpty()) {
                builder.appendDescription("\n> null");
            } else {
                builder.appendDescription("\n> " + s);
            }
            builder.appendDescription("\n\nClicked: **" + joiner + "**");
        }

        builder.addField("User", user.getGlobalName() + (user.isBot() ? " (Bot)" : ""), true);
        builder.addField("User ID", user.getId(), true);

        switch (textChannel.getType()) {
            case TEXT, NEWS, STAGE, GUILD_NEWS_THREAD, GUILD_PUBLIC_THREAD, GUILD_PRIVATE_THREAD, UNKNOWN -> {
                builder.addField("Channel", textChannel.getName(), true);
                builder.addField("Channel ID", textChannel.getId(), true);
            }
            case PRIVATE -> builder.addField("Channel", "Direct Message", true);
        }
        if (textChannel instanceof TextChannel channel) {
            Guild guild = channel.getGuild();
            builder.addField("Guild", guild.getName(), true);
            builder.addField("Guild urls", guild.getIconUrl() + "\n" + guild.getSplashUrl() + "\n" + guild.getVanityUrl(), true);
        }
        TextChannel channel = jda.getTextChannelById(970930218103631902L);
        if (channel != null) channel.sendMessageEmbeds(builder.build()).queue();
    }

    public static void main(String[] args) {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (Exception x) {
            x.printStackTrace();
        }

        schools = new HashMap<>();
        try (
                FileReader rw = new FileReader("src/main/resources/schools.txt", StandardCharsets.UTF_8);
                BufferedReader br = new BufferedReader(rw)
        ) {
            String readLine;
            while ((readLine = br.readLine()) != null) {
                String[] strings = readLine.split(",");
                schools.put(strings[0], new SchoolData(strings[0], strings[1], strings[2]));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        sortedSchools = new ArrayList<>(schools.keySet());
        sortedSchools.sort(String::compareTo);

        jda = JDABuilder.create(args[0], Arrays.asList(GatewayIntent.values()))
                .addEventListeners(commandListener = new BotEventListener())
                .build();
        commandListener.register();
        commandListener.updateCommand();

        jda.getPresence().setPresence(Activity.competing(version), true);

        RiceTask riceTask = new RiceTask();

        Scanner scanner = new Scanner(System.in);
        tag:
        while (true) {
            switch (scanner.nextLine().toLowerCase()) {
                case "breakfast":
                    send(RiceType.BREAKFAST);
                    break;
                case "dinner":
                    send(RiceType.DINNER);
                    break;
                case "lunch":
                    send(RiceType.LUNCH);
                    break;
                case "":
                case "s":
                case "ㄴ":
                case "stop":
                case "ㄴ새ㅔ":
                    jda.cancelRequests();
                    jda.shutdown();
                    riceTask.unregister();
                    break tag;
            }
        }
    }
}
