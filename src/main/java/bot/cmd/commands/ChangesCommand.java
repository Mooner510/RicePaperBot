package bot.cmd.commands;

import bot.cmd.BotCommand;
import bot.cmd.BotSelectMenu;
import bot.utils.Json;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.interactions.components.selections.StringSelectMenu;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.util.AbstractMap;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;
import java.util.regex.Pattern;

public class ChangesCommand implements BotCommand, BotSelectMenu {
    @Override
    public SlashCommandData getCommand() {
        return Commands.slash("changes", "마지막으로 봇이 업데이트 된 후 변경사항을 확인합니다!");
    }

    public static Pattern pattern = Pattern.compile("v\\d(\\.\\d+)+ update - .+");

    @Override
    public void onCommand(SlashCommandInteractionEvent event) {
        List<AbstractMap.SimpleImmutableEntry<JSONObject, String[]>> json = Json
                .readJsonArrayFromUrl("https://api.github.com/repos/Mooner510/RicePaperBot/commits")
                .toList()
                .stream()
                .map(v -> {
                    JSONObject wrapped = (JSONObject) JSONObject.wrap(v);
                    String[] arr = wrapped.getJSONObject("commit").getString("message").split("\n");
                    return new AbstractMap.SimpleImmutableEntry<>(wrapped, arr);
                })
                .filter(v -> pattern.matcher(v.getValue()[0]).matches())
                .toList();
        if (!json.isEmpty()) {
            EmbedBuilder builder = getEmbedBuilder(json);
            StringSelectMenu.Builder versionBuilder = StringSelectMenu.create("version");
            json.forEach(v -> versionBuilder.addOption(v.getValue()[0], v.getKey().getString("sha")));
            event.deferReply(false).addActionRow(versionBuilder.build()).addEmbeds(builder.build()).queue();
        } else {
            event.deferReply(false).setContent("이런! 최근 업데이트 내용을 찾기 어렵네요. 어디로 사라진걸까요?").queue();
        }
    }

    @NotNull
    private static EmbedBuilder getEmbedBuilder(List<AbstractMap.SimpleImmutableEntry<JSONObject, String[]>> json) {
        return getEmbedBuilder(json.get(0));
    }

    @NotNull
    private static EmbedBuilder getEmbedBuilder(AbstractMap.SimpleImmutableEntry<JSONObject, String[]> json) {
        EmbedBuilder builder = new EmbedBuilder();
        JSONObject data = json.getKey();
        JSONObject commit = data.getJSONObject("commit");
        String[] split = json.getValue();
        builder.setTitle(split[0], data.getString("html_url"));
        StringJoiner joiner = new StringJoiner("\n");
        for (int i = 1; i < split.length; i++) if (!split[i].isEmpty()) joiner.add(split[i]);
        builder.setDescription(joiner.toString());
        JSONObject committer = commit.getJSONObject("committer");
        builder.setFooter(committer.getString("name") + " " + committer.getString("date").replace("T", " ").replace("Z", ""));
        return builder;
    }

    @Override
    public void onSelect(StringSelectInteractionEvent event) {
        JSONObject jsonObject = Json
                .readJsonFromUrl("https://api.github.com/repos/Mooner510/RicePaperBot/commits/"
                        + event.getSelectedOptions().get(0).getValue());
        String[] arr = jsonObject.getJSONObject("commit").getString("message").split("\n");
        AbstractMap.SimpleImmutableEntry<JSONObject, String[]> json = new AbstractMap.SimpleImmutableEntry<>(jsonObject, arr);
        EmbedBuilder builder = getEmbedBuilder(json);
        event.editMessageEmbeds(builder.build()).setActionRow(event.getSelectMenu()).queue();
    }
}
