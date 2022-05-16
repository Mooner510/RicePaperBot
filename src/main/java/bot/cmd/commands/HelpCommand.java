package bot.cmd.commands;

import bot.cmd.BotCommand;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

public class HelpCommand implements BotCommand {
    @Override
    public SlashCommandData getCommand() {
        return Commands.slash("help", "모든 명령어가 정리되어 있다!");
    }

    @Override
    public void onCommand(SlashCommandInteractionEvent event) {
        EmbedBuilder builder = new EmbedBuilder();
        builder.setTitle("명령어").setDescription("< > = 필수 입력란, [ ] = 선택 입력란")
                .addField("/rice [학교명]", "오늘의 급식을 보여줘요.\n\n버튼을 클릭해 날짜를 하루씩 변경할 수 있어요.\n또는 선택메뉴를 이용해 ±12일까지 빠르게 검색할 수 있답니다.", false)
                .addField("/setschool <학교명>", "자신의 학교를 설정해요.\n\n학교명을 입력하지 않아도 `/rice`명령어를 사용할 수 있게 되요.\n또한 선택한 학교의 알림도 받으실 수 있게 된답니다.\n설정해두면 매우 편리하겠죠?", false)
                .addField("/setnotify <켜기|끄기>", "알림을 받을지 설정해요.\n\n조식, 중식, 석식을 일정 시간마다 알려드려요.\n구지 `/rice`명령어를 쓰지 않아도 알려드린다구요!", false)
                .addField("/ranice", "Random-Rice라는 뜻이에요.\n랜덤한 학교의 오늘 급식을 알려줘요.\n버그 찾을때 정말 좋다구요?", false);
        event.deferReply(false).addEmbeds(builder.build()).queue();
    }
}
