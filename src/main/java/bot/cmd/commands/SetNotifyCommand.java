package bot.cmd.commands;

import bot.cmd.BotCommand;
import bot.utils.BotColor;
import bot.utils.DB;
import bot.SchoolData;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;

import static bot.cmd.util.autocomplete.SchoolComplete.schoolComplete;

public class SetNotifyCommand implements BotCommand {
    @Override
    public SlashCommandData getCommand() {
        OptionData option = new OptionData(OptionType.STRING, "설정", "쉽게 급식을 보고 싶은 학교를 적어줘요!", true);
        option.addChoice("켜기", "true")
                .addChoice("끄기", "false");
        OptionData option2 = new OptionData(OptionType.CHANNEL, "채널", "알림을 보낼 채널을 입력해주세요!", false);
        return Commands.slash("setnotify", "급식 시간이 다가오면 알림을 전해드려요!")
                .addSubcommands(
                        new SubcommandData("personal", "개인적인 DM 푸시 알림 설정이에요!").addOptions(option),
                        new SubcommandData("guild", "서버의 채널 알림이에요! 하위 옵션을 아무것도 입력하지 않으면 알림을 끌 수 있어요.")
                                .addOptions(option2)
                                .addOption(OptionType.STRING, "학교명", "쉽게 급식을 보고 싶은 학교를 적어줘요!", false, true)
                );
    }

    @Override
    public void onCommand(SlashCommandInteractionEvent event) {
        SchoolData schoolData;
        EmbedBuilder builder;
        switch (event.getSubcommandName()) {
            case "personal":
                String s = event.getOption("설정").getAsString();
                schoolData = DB.getSchool(event.getUser().getIdLong());
                if (schoolData == null) {
                    builder = new EmbedBuilder();
                    builder.setTitle("당신이 다니는 학교는 어디인가요?")
                            .setDescription("`/setschool`명령어로 먼저 학교를 설정해줘요!\n처음 보는 학교의 급식을 보내드릴 순 없잖아요?")
                            .setColor(BotColor.FAIL);
                    event.deferReply(false).addEmbeds(builder.build()).queue();
                    return;
                }

                boolean b = s.equals("true");

                String err;
                if ((err = DB.setNotices(event.getUser().getIdLong(), b)) != null) {
                    builder = new EmbedBuilder();
                    builder.setTitle("무언가 문제가 있다!")
                            .setDescription("오류 발생! `" + err + "`")
                            .setColor(BotColor.FAIL);
                    event.deferReply(false).addEmbeds(builder.build()).queue();
                    return;
                }

                builder = new EmbedBuilder();
                if (b) {
                    builder.setTitle("알림을 활성화 했어요!").setDescription(
                            """
                                    앞으로 급식시간이 다가올 때마다 DM으로 알려줘요.
                                    DM에 메시지를 보낼 수 있도록 서버 설정 및 개인 보안 설정을 바꿔야 해요!
                                                                        
                                    학교마다 급식 시간이 다르기 때문에 대략적인 시간보다 일찍 알려드린답니다!
                                            
                                    **조식**: 7시
                                    **중식**: 11시 30분
                                    **조식**: 5시
                                    """
                    ).setColor(BotColor.SUCCESS);
                } else {
                    builder.setTitle("알림을 비활성화 했어요!").setDescription("더 이상 급식시간이 다가올 때마다 DM으로 알려드리지 않아요.").setColor(BotColor.FAIL);
                }
                event.deferReply(false).addEmbeds(builder.build()).queue();
                break;
            case "guild":
                if (!event.getChannelType().isGuild()) {
                    event.deferReply(true).setContent("서버에서만 사용 가능한 명령입니다!").queue();
                    return;
                }

                OptionMapping channelOption = event.getOption("채널");
                OptionMapping schoolOption = event.getOption("학교명");
                builder = new EmbedBuilder();

                long guildId = event.getGuild().getIdLong();
                if (channelOption == null && schoolOption == null) {
                    DB.revokeGuildNotice(guildId);
                    builder.setTitle("알림을 비활성화 했어요!").setDescription("더 이상 급식시간이 다가올 때마다 이 서버에 알려드리지 않아요.").setColor(BotColor.FAIL);
                    event.deferReply(false).addEmbeds(builder.build()).queue();
                    return;
                }

                builder.setTitle("알림 설정을 변경했어요!").setColor(BotColor.SUCCESS);

                if (channelOption != null && schoolOption != null) {
                    TextChannel textChannel = channelOption.getAsChannel().asTextChannel();
                    String school = schoolOption.getAsString();
                    builder.appendDescription("채널 변경: 앞으로 " + textChannel.getAsMention() + "에서 알려드릴게요!\n");
                    builder.appendDescription("학교 변경: 앞으로 `" + school + "`의 급식 정보로 알려드릴게요!");
                    builder.appendDescription("""
                            \n학교마다 급식 시간이 다르기 때문에 대략적인 시간보다 일찍 알려드린답니다!
                                    
                            **조식**: 7시
                            **중식**: 11시 30분
                            **조식**: 5시
                            """);
                    DB.setGuildNotice(guildId, textChannel.getIdLong(), school);
                } else {
                    if (DB.checkGuildNotice(guildId)) {
                        if (channelOption != null) {
                            TextChannel textChannel = channelOption.getAsChannel().asTextChannel();
                            builder.appendDescription("채널 변경: 앞으로 " + textChannel.getAsMention() + "에서 알려드릴게요!");
                            DB.setGuildNoticeOnlyChannel(guildId, textChannel.getIdLong());
                        }

                        if (schoolOption != null) {
                            String school = schoolOption.getAsString();
                            builder.appendDescription("학교 변경: 앞으로 `" + school + "`의 급식 정보로 알려드릴게요!");
                            DB.setGuildNoticeOnlySchool(guildId, school);
                        }
                    } else {
                        builder.setTitle("이런!")
                                .setDescription("처음 서버 알림을 설정 한다면 채널과 학교를 모두 입력해 주셔야 해요.\n그 후에 채널과 학교를 수정할 수 있어요.")
                                .setColor(BotColor.FAIL);
                    }
                }
                event.deferReply(false).addEmbeds(builder.build()).queue();
                break;
        }
    }

    @Override
    public void onComplete(CommandAutoCompleteInteractionEvent event) {
        schoolComplete(event);
    }
}
