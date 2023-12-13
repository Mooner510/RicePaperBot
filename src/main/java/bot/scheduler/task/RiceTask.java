package bot.scheduler.task;

import bot.Main;
import bot.SchoolData;
import bot.cmd.util.rice.Rice;
import bot.cmd.util.rice.RiceType;
import bot.utils.BotColor;
import bot.utils.DB;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static bot.Main.jda;

public class RiceTask {
    private final Timer timer;

    public RiceTask() {
        timer = new Timer();
        TimerTask task1 = new TimerTask() {
            @Override
            public void run() {
                send(RiceType.BREAKFAST);
            }
        };

        TimerTask task2 = new TimerTask() {
            @Override
            public void run() {
                send(RiceType.LUNCH);
            }
        };

        TimerTask task3 = new TimerTask() {
            @Override
            public void run() {
                send(RiceType.DINNER);
            }
        };
        Date date = new Date();
        long cur = System.currentTimeMillis();

        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("Asia/Seoul"));
        cal.setTime(date);
        System.out.println("Today: " + cal.getTime());

        cal.set(Calendar.HOUR_OF_DAY, 7);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);

        System.out.println("Breakfast Date: " + cal.getTime());
        if (cal.getTimeInMillis() < cur) {
            System.out.println("+ Add Breakfast Date");
            cal.add(Calendar.DATE, 1);
        }

        timer.scheduleAtFixedRate(task1, cal.getTime(), 86400000);
        System.out.println("Breakfast Queue: " + cal.getTime());

        cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY, 11);
        cal.set(Calendar.MINUTE, 30);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        System.out.println("Lunch Date: " + cal.getTime());
        if (cal.getTimeInMillis() < cur) {
            System.out.println("+ Add Lunch Date");
            cal.add(Calendar.DATE, 1);
        }

        timer.scheduleAtFixedRate(task2, cal.getTime(), 86400000);
        System.out.println("Lunch Queue: " + cal.getTime());

        cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY, 17);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        System.out.println("Dinner Date: " + cal.getTime());
        if (cal.getTimeInMillis() < cur) {
            System.out.println("+ Add Dinner Date");
            cal.add(Calendar.DATE, 1);
        }

        timer.scheduleAtFixedRate(task3, cal.getTime(), 86400000);
        System.out.println("Dinner Queue: " + cal.getTime());
    }

    public void unregister() {
        timer.cancel();
    }

    public static void send(RiceType type) {
        Date date = new Date();
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("Asia/Seoul"));
        cal.setTime(date);
        guildNotice(type, cal);
        personalNotice(type, cal);
    }

    public static void guildNotice(RiceType type, Calendar cal) {
        new Thread(() -> {
            List<DB.GuildNotice> notices = DB.getGuildNotices();
            int noticeSize = notices.size();
            EmbedBuilder builder = new EmbedBuilder().setTitle("Queued Guild Query - " + type).setColor(BotColor.RICE)
                    .appendDescription("For **" + noticeSize + "** users.\n");
            AtomicInteger done = new AtomicInteger();
            AtomicInteger fail = new AtomicInteger();
            ExecutorService threadPool = Executors.newCachedThreadPool();
            for (DB.GuildNotice notice : notices) {
                threadPool.submit(() -> {
                    SchoolData schoolData = Main.schools.get(notice.getSchool());
                    TextChannel channel = jda.getTextChannelById(notice.getChannelId());
                    if (channel == null) {
                        Guild guild = jda.getGuildById(notice.getGuildId());
                        if (guild == null) {
                            fail.incrementAndGet();
                            return;
                        }
                        channel = guild.getTextChannelById(notice.getChannelId());
                        if (channel == null) {
                            fail.incrementAndGet();
                            return;
                        }
                    }
                    MessageEmbed embed = Rice.getRiceEmbed(schoolData, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) + 1, cal.get(Calendar.DAY_OF_MONTH), type);
                    if (embed != null) {
                        channel.sendMessageEmbeds(embed).queue();
                        done.incrementAndGet();
                    } else {
                        fail.incrementAndGet();
                    }
                });
            }
            threadPool.shutdown();
            try {
                threadPool.awaitTermination(600, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            builder.addField("Succeed", done.get() + "/" + notices.size(), false);
            builder.addField("Failed", fail.get() + "/" + notices.size(), false);
            TextChannel channel = jda.getTextChannelById(979213156712853514L);
            if (channel != null) channel.sendMessageEmbeds(builder.build()).queue();
        }).start();
    }

    public static void personalNotice(RiceType type, Calendar cal) {
        new Thread(() -> {
            HashSet<Long> notices = DB.getNotices();
            int noticeSize = notices.size();
            EmbedBuilder builder = new EmbedBuilder().setTitle("Queued Query - " + type).setColor(BotColor.SUCCESS)
                    .appendDescription("For **" + noticeSize + "** users.\n");
            StringJoiner done = new StringJoiner("\n- ");
            StringJoiner fail = new StringJoiner("\n- ");
            AtomicInteger integer = new AtomicInteger();
            for (Long id : notices) {
                new Thread(() -> {
                    jda.retrieveUserById(id).queue(user -> {
                        SchoolData schoolData;
                        if (user == null || (schoolData = DB.getSchool(id)) == null) {
                            if (user == null) {
                                fail.add("id: " + id);
                                return;
                            }
                            done.add(user.getGlobalName());
                            System.out.println(id);
                            integer.incrementAndGet();
                        } else {
                            MessageEmbed embed = Rice.getRiceEmbed(schoolData, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) + 1, cal.get(Calendar.DAY_OF_MONTH), type);
                            user.openPrivateChannel().queue(privateChannel -> {
                                if (privateChannel.canTalk()) {
                                    if (embed != null) privateChannel.sendMessageEmbeds(embed).queue();
                                    done.add(user.getGlobalName());
                                } else {
                                    fail.add(user.getGlobalName());
                                }
                                integer.incrementAndGet();
                            });
                        }
                    });
                }).start();
            }
            while (integer.get() < noticeSize) {
            }
            if (done.length() > 0) builder.addField("Succeed", "```yml\n- " + done + "\n```", false);
            if (fail.length() > 0) builder.addField("Failed", "```yml\n- " + fail + "\n```", false);
            TextChannel channel = jda.getTextChannelById(979213156712853514L);
            if (channel != null) channel.sendMessageEmbeds(builder.build()).queue();
        }).start();
    }

    public static void send(RiceType type, int year, int month, int day) {
        HashSet<Long> notices = DB.getNotices();
        for (Long id : notices) {
            jda.retrieveUserById(id).queue(user -> {
                SchoolData schoolData;
                if (user == null || (schoolData = DB.getSchool(id)) == null) {
                    System.out.println(id);
                } else {
                    MessageEmbed embed = Rice.getRiceEmbed(schoolData, year, month, day, type);
                    user.openPrivateChannel().queue(privateChannel -> {
                        if (privateChannel.canTalk()) {
                            if (embed != null) privateChannel.sendMessageEmbeds(embed).queue();
                        }
                    });
                }
            });
        }
    }
}
