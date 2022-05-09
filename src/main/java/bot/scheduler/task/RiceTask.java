package bot.scheduler.task;

import bot.SchoolData;
import bot.cmd.commands.RiceCommand;
import bot.utils.DB;
import net.dv8tion.jda.api.entities.MessageEmbed;

import java.util.*;

import static bot.Main.jda;

public class RiceTask {
    private final Timer timer;

    private static Calendar next(RiceCommand.RiceType riceType) {
        Date date = new Date();
        long cur = System.currentTimeMillis();

        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("Asia/Seoul"));
        cal.setTime(date);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        switch (riceType) {
            case BREAKFAST -> {
                cal.set(Calendar.HOUR_OF_DAY, 7);
                cal.set(Calendar.MINUTE, 0);
            }
            case LUNCH -> {
                cal.set(Calendar.HOUR_OF_DAY, 11);
                cal.set(Calendar.MINUTE, 30);
            }
            case DINNER -> {
                cal.set(Calendar.HOUR_OF_DAY, 17);
                cal.set(Calendar.MINUTE, 0);
            }
        }

        if(cal.getTimeInMillis() < cur) cal.add(Calendar.DATE, 1);

        return cal;
    }

    public RiceTask() {
        timer = new Timer();

        for (final RiceCommand.RiceType type : RiceCommand.RiceType.values()) {
            Calendar calender = next(type);
            Date date = calender.getTime();
            timer.scheduleAtFixedRate(
                    new TimerTask() {
                        @Override
                        public void run() {
                            Calendar cal = next(type);
                            send(type, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
                        }
                    },
                    date, 86400000
            );
            System.out.println(type.getTag() + " Queue: " + date);
        }
    }

    public void unregister() {
        timer.cancel();
    }

    public static void send(RiceCommand.RiceType type) {
        Date date = new Date();
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("Asia/Seoul"));
        cal.setTime(date);

        HashSet<Long> notices = DB.getNotices();
        for (Long id : notices) {
            jda.retrieveUserById(id).queue(user -> {
                SchoolData schoolData;
                if (user == null || (schoolData = DB.getSchool(id)) == null) {
                    System.out.println(id);
                } else {
                    user.openPrivateChannel().queue(privateChannel -> {
                        if (privateChannel.canTalk()) {
                            MessageEmbed embed = RiceCommand.getRiceEmbed(schoolData, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) + 1, cal.get(Calendar.DAY_OF_MONTH), type);
                            if(embed != null) privateChannel.sendMessageEmbeds(embed).queue();
                        }
                    });
                }
            });
        }
    }

    public static void send(RiceCommand.RiceType type, int year, int month, int day) {
        HashSet<Long> notices = DB.getNotices();
        for (Long id : notices) {
            jda.retrieveUserById(id).queue(user -> {
                SchoolData schoolData;
                if (user == null || (schoolData = DB.getSchool(id)) == null) {
                    System.out.println(id);
                } else {
                    user.openPrivateChannel().queue(privateChannel -> {
                        if (privateChannel.canTalk()) {
                            MessageEmbed embed = RiceCommand.getRiceEmbed(schoolData, year, month, day, type);
                            if(embed != null) privateChannel.sendMessageEmbeds(embed).queue();
                        }
                    });
                }
            });
        }
    }
}
