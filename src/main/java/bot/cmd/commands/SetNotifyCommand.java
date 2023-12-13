package bot.cmd.commands;

import bot.cmd.BotCommand;
import bot.utils.BotColor;
import bot.utils.DB;
import bot.SchoolData;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

public class SetNotifyCommand implements BotCommand {
    @Override
    public SlashCommandData getCommand() {
        OptionData option = new OptionData(OptionType.STRING, "설정", "쉽게 급식을 보고 싶은 학교를 적어줘요!", true);
        option.addChoice("켜기", "true")
                .addChoice("끄기", "false");
        return Commands.slash("setnotify", "급식 시간이 다가오면 알림을 전해드려요!")
                .addOptions(option);
    }

    @Override
    public void onCommand(SlashCommandInteractionEvent event) {
        String s = event.getOption("설정").getAsString();
        SchoolData schoolData = DB.getSchool(event.getUser().getIdLong());
        if (schoolData == null) {
            EmbedBuilder builder = new EmbedBuilder();
            builder.setTitle("당신이 다니는 학교는 어디인가요?").setDescription("`/setschool`명령어로 먼저 학교를 설정해줘요!\n처음 보는 학교의 급식을 보내드릴 순 없잖아요?").setColor(BotColor.FAIL);
            event.deferReply(false).addEmbeds(builder.build()).queue();
            return;
        }

        boolean b = s.equals("true");

        String err;
        if ((err = DB.setNotices(event.getUser().getIdLong(), b)) != null) {
            EmbedBuilder builder = new EmbedBuilder();
            builder.setTitle("무언가 문제가 있다!").setDescription("오류 발생! `" + err + "`").setColor(BotColor.FAIL);
            event.deferReply(false).addEmbeds(builder.build()).queue();
            return;
        }

        EmbedBuilder builder = new EmbedBuilder();
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
    }
}
