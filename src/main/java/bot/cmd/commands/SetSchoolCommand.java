package bot.cmd.commands;

import bot.cmd.BotCommand;
import bot.utils.BotColor;
import bot.utils.DB;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

import java.util.HashSet;

import static bot.Main.schools;

public class SetSchoolCommand implements BotCommand {
    @Override
    public SlashCommandData getCommand() {
        return Commands.slash("setschool", "빠르게 확인하고 싶은 학교를 설정할 수 있어요!")
                .addOption(OptionType.STRING, "학교명", "쉽게 급식을 보고 싶은 학교를 적어줘요!", true, true);
    }

    @Override
    public void onCommand(SlashCommandInteractionEvent event) {
        String s = event.getOption("학교명").getAsString();

        String err;
        if ((err = DB.setSchool(event.getUser().getIdLong(), s)) != null) {
            EmbedBuilder builder = new EmbedBuilder();
            builder.setTitle("무언가 문제가 있다!").setDescription("오류 발생! `" + err + "`").setColor(BotColor.FAIL);
            event.deferReply(false).addEmbeds(builder.build()).queue();
            return;
        }

        if (!schools.containsKey(s)) {
            EmbedBuilder builder = new EmbedBuilder();
            builder.setTitle("어머! 그 학교는 대체 어디죠?").setDescription("설마 새로운 학교를 만들 생각은 아닌거죠?\n참고로 학교 이름은 완전한 이름으로 부탁드려요.").setColor(BotColor.FAIL);
            event.deferReply(false).addEmbeds(builder.build()).queue();
            return;
        }

        EmbedBuilder builder = new EmbedBuilder();
        builder.setTitle("학교를 설정했어요!").setDescription("입력하신 `" + s + "`로 학교를 설정했어요!").setColor(BotColor.SUCCESS);
        event.deferReply(false).addEmbeds(builder.build()).queue();
    }

    @Override
    public void onComplete(CommandAutoCompleteInteractionEvent event) {
        HashSet<String> strings = new HashSet<>(schools.keySet());
        strings.removeIf(s -> !s.startsWith(event.getFocusedOption().getValue()));
        HashSet<Command.Choice> choices = new HashSet<>();
        for (String s : strings) {
            if (choices.size() >= 24) break;
            choices.add(new Command.Choice(s, s));
        }
        event.replyChoices(choices).queue();
    }
}
