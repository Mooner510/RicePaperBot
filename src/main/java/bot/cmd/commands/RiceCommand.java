package bot.cmd.commands;

import bot.Main;
import bot.cmd.BotButton;
import bot.cmd.BotCommand;
import bot.cmd.BotEventListener;
import bot.cmd.BotSelectMenu;
import bot.cmd.util.rice.Rice;
import bot.cmd.util.rice.RiceType;
import bot.utils.BotColor;
import bot.utils.DB;
import bot.SchoolData;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.selections.StringSelectMenu;

import java.text.SimpleDateFormat;
import java.util.*;

import static bot.Main.schools;
import static bot.cmd.BotEventListener.createId;
import static bot.cmd.util.rice.Rice.getRiceEmbed;

public class RiceCommand implements BotCommand, BotSelectMenu, BotButton {
    @Override
    public SlashCommandData getCommand() {
        return Commands.slash("rice", "오늘의 급식은 뭐가 나올까?")
                .addOption(OptionType.STRING, "학교명", "급식을 보고 싶은 학교를 선택해줘요!", false, true);
    }

    @Override
    public void onCommand(SlashCommandInteractionEvent event) {
        SchoolData schoolData;
        OptionMapping s = event.getOption("학교명");

        if (s == null) {
            if ((schoolData = DB.getSchool(event.getUser().getIdLong())) == null) {
                EmbedBuilder builder = new EmbedBuilder();
                builder.setTitle("어머! 당신의 학교는 어디인가요?").setDescription("빠른 급식 명령어(학교를 입력하지 않음)를 사용하시려면 학교를 등록해주세요\n`/setschool`명령어를 통해 학교를 등록할 수 있습니다!").setColor(BotColor.FAIL);
                event.deferReply(false).addEmbeds(builder.build()).queue();
                return;
            }
        } else {
            schoolData = Main.schools.get(s.getAsString());
        }

        if (schoolData == null) {
            EmbedBuilder builder = new EmbedBuilder();
            builder.setTitle("어머! 그 학교는 대체 어디죠?").setDescription("설마 새로운 학교를 만들 생각은 아닌거죠?\n참고로 학교 이름은 완전한 이름으로 부탁드려요.").setColor(BotColor.FAIL);
            event.deferReply(false).addEmbeds(builder.build()).queue();
            return;
        }

        Date date = new Date();
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("Asia/Seoul"));
        cal.setTime(date);

        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH) + 1;
        int day = cal.get(Calendar.DAY_OF_MONTH);

        Calendar c1 = Calendar.getInstance(TimeZone.getTimeZone("Asia/Seoul"));
        Calendar c2 = Calendar.getInstance(TimeZone.getTimeZone("Asia/Seoul"));
        c1.setTime(date);
        c1.add(Calendar.DAY_OF_MONTH, -1);
        c2.setTime(date);
        c2.add(Calendar.DAY_OF_MONTH, 1);

        Calendar c = Calendar.getInstance(TimeZone.getTimeZone("Asia/Seoul"));
        StringSelectMenu.Builder builder = StringSelectMenu.create(BotEventListener.createId(event.getUser().getIdLong(), "rice", "select"));
        SimpleDateFormat format = new SimpleDateFormat("yyyy년 MM월 dd일");
        for (int i = -12; i <= 12; i++) {
            c.setTime(date);
            c.add(Calendar.DAY_OF_MONTH, i);
            builder.addOption(format.format(c.getTime()), schoolData.getName() + ":" + c.getTimeInMillis());
        }
        builder.setRequiredRange(0, 1);
        builder.setPlaceholder(format.format(cal.getTime()));

        event.deferReply(false).addEmbeds(Rice.getRiceEmbed(schoolData, year, month, day, RiceType.values()))
                .addActionRow(
                        builder.build()
                ).addActionRow(
                        Button.primary(BotEventListener.createId(event.getUser().getIdLong(), "rice", schoolData.getName(), c1.getTimeInMillis()), Emoji.fromUnicode("U+2B05")),
                        Button.primary(BotEventListener.createId(event.getUser().getIdLong(), "rice", schoolData.getName(), c2.getTimeInMillis()), Emoji.fromUnicode("U+27A1"))
                ).queue();
    }

    @Override
    @SuppressWarnings("unchecked")
    public void onComplete(CommandAutoCompleteInteractionEvent event) {
        ArrayList<String> strings = (ArrayList<String>) Main.sortedSchools.clone();
        strings.removeIf(s -> !s.startsWith(event.getFocusedOption().getValue()));
        ArrayList<Command.Choice> choices = new ArrayList<>();
        for (String s : strings) {
            if (choices.size() >= 24) break;
            choices.add(new Command.Choice(s, s));
        }
        event.replyChoices(choices).queue();
    }

    @Override
    public void onSelect(StringSelectInteractionEvent event) {
        if ((event.getInteraction().getSelectedOptions().isEmpty())) {
            return;
        }
        String[] split = event.getInteraction().getSelectedOptions().get(0).getValue().split(":");
        SchoolData schoolData = schools.get(split[0]);
        Date date = new Date(Long.parseLong(split[1]));

        Calendar c1 = Calendar.getInstance(TimeZone.getTimeZone("Asia/Seoul"));
        Calendar c2 = Calendar.getInstance(TimeZone.getTimeZone("Asia/Seoul"));
        c1.setTime(date);
        c1.add(Calendar.DAY_OF_MONTH, -1);
        c2.setTime(date);
        c2.add(Calendar.DAY_OF_MONTH, 1);

        Calendar c = Calendar.getInstance(TimeZone.getTimeZone("Asia/Seoul"));
        StringSelectMenu.Builder builder = StringSelectMenu.create(createId(event.getUser().getIdLong(), "rice", "select"));
        SimpleDateFormat format = new SimpleDateFormat("yyyy년 MM월 dd일");
        for (int i = -12; i <= 12; i++) {
            c.setTime(date);
            c.add(Calendar.DAY_OF_MONTH, i);
            builder.addOption(format.format(c.getTime()), schoolData.getName() + ":" + c.getTimeInMillis());
        }
        builder.setRequiredRange(0, 1);
        builder.setPlaceholder(format.format(date));

        event.deferEdit().setEmbeds(getRiceEmbed(schoolData, Long.parseLong(split[1]), RiceType.values()))
                .setActionRow(
                        builder.build(),
                        Button.primary(createId(event.getUser().getIdLong(), "rice", schoolData.getName(), c1.getTimeInMillis()), Emoji.fromUnicode("U+2B05")),
                        Button.primary(createId(event.getUser().getIdLong(), "rice", schoolData.getName(), c2.getTimeInMillis()), Emoji.fromUnicode("U+27A1"))
                ).queue();
    }

    @Override
    public void onClick(ButtonInteractionEvent event, String[] arguments) {
        SchoolData schoolData = schools.get(arguments[0]);
        Date date = new Date(Long.parseLong(arguments[1]));

        Calendar c1 = Calendar.getInstance(TimeZone.getTimeZone("Asia/Seoul"));
        Calendar c2 = Calendar.getInstance(TimeZone.getTimeZone("Asia/Seoul"));
        c1.setTime(date);
        c1.add(Calendar.DAY_OF_MONTH, -1);
        c2.setTime(date);
        c2.add(Calendar.DAY_OF_MONTH, 1);

        Calendar c = Calendar.getInstance(TimeZone.getTimeZone("Asia/Seoul"));
        StringSelectMenu.Builder builder = StringSelectMenu.create(BotEventListener.createId(event.getUser().getIdLong(), "rice", "select"));
        SimpleDateFormat format = new SimpleDateFormat("yyyy년 MM월 dd일");
        for (int i = -12; i <= 12; i++) {
            c.setTime(date);
            c.add(Calendar.DAY_OF_MONTH, i);
            builder.addOption(format.format(c.getTime()), schoolData.getName() + ":" + c.getTimeInMillis());
        }
        builder.setPlaceholder(format.format(date));

        event.deferEdit().setEmbeds(getRiceEmbed(schoolData, Long.parseLong(arguments[1]), RiceType.values()))
                .setActionRow(
                        builder.build(),
                        Button.primary(BotEventListener.createId(event.getUser().getIdLong(), "rice", schoolData.getName(), c1.getTimeInMillis()), Emoji.fromUnicode("U+2B05")),
                        Button.primary(BotEventListener.createId(event.getUser().getIdLong(), "rice", schoolData.getName(), c2.getTimeInMillis()), Emoji.fromUnicode("U+27A1"))
                ).queue();
    }
}
