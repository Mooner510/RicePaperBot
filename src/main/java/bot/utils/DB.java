package bot.utils;

import bot.Main;
import bot.SchoolData;
import org.jetbrains.annotations.NotNull;

import java.sql.*;
import java.util.*;

public class DB {
    public static SchoolData getSchool(long userId) {
        try (
                Connection c = DriverManager.getConnection("jdbc:sqlite:src/main/resources/DB.db");
                ResultSet r = c.prepareStatement("SELECT * FROM GuildSchool WHERE id=" + userId).executeQuery()
        ) {
            if (r.next()) {
                return Main.schools.get(r.getString("school"));
            } else {
                return null;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String setSchool(long userId, @NotNull String school) {
        try (
                Connection c = DriverManager.getConnection("jdbc:sqlite:src/main/resources/DB.db");
                PreparedStatement s = c.prepareStatement("INSERT INTO GuildSchool VALUES(?, ?)");
                PreparedStatement s2 = c.prepareStatement("UPDATE GuildSchool SET school=? WHERE id=?")
        ) {
            s2.setString(1, school);
            s2.setLong(2, userId);
            if (s2.executeUpdate() == 0) {
                s.setLong(1, userId);
                s.setString(2, school);
                s.executeUpdate();
            }
            return null;
        } catch (SQLException e) {
            e.printStackTrace();
            return e.getSQLState();
        }
    }

    public static HashSet<Long> getNotices() {
        HashSet<Long> notices = new HashSet<>();
        try (
                Connection c = DriverManager.getConnection("jdbc:sqlite:src/main/resources/DB.db");
                ResultSet r = c.prepareStatement("SELECT * FROM RiceNotice").executeQuery()
        ) {
            while (r.next()) {
                if (r.getBoolean("notice")) {
                    notices.add(r.getLong("id"));
                }
            }
            return notices;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return notices;
    }

    public static String setNotices(long userId, boolean notice) {
        try (
                Connection c = DriverManager.getConnection("jdbc:sqlite:src/main/resources/DB.db");
                PreparedStatement s = c.prepareStatement("INSERT INTO RiceNotice VALUES(?, ?)");
                PreparedStatement s2 = c.prepareStatement("UPDATE RiceNotice SET notice=? WHERE id=?")
        ) {
            s2.setBoolean(1, notice);
            s2.setLong(2, userId);
            if (s2.executeUpdate() == 0) {
                s.setLong(1, userId);
                s.setBoolean(2, notice);
                s.executeUpdate();
            }
            return null;
        } catch (SQLException e) {
            e.printStackTrace();
            return e.getSQLState();
        }
    }

    public record GuildNotice(long getGuildId, long getChannelId, String getSchool) {
    }

    public static Set<GuildNotice> getGuildNotices() {
        Set<GuildNotice> notices = new HashSet<>();
        try (
                Connection c = DriverManager.getConnection("jdbc:sqlite:src/main/resources/DB.db");
                ResultSet r = c.prepareStatement("SELECT * FROM GuildNotice").executeQuery()
        ) {
            while (r.next()) {
                if (r.getBoolean("notice")) {
                    notices.add(new GuildNotice(r.getLong("guild_id"), r.getLong("channel_id"), r.getString("school")));
                }
            }
            return notices;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Collections.emptySet();
    }

    public static boolean checkGuildNotice(long guildId) {
        try (
                Connection c = DriverManager.getConnection("jdbc:sqlite:src/main/resources/DB.db");
                PreparedStatement s = c.prepareStatement("SELECT * FROM GuildNotice WHERE guild_id=?");
        ) {
            s.setLong(1, guildId);
            return s.executeQuery().next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static void revokeGuildNotice(long guildId) {
        try (
                Connection c = DriverManager.getConnection("jdbc:sqlite:src/main/resources/DB.db");
                PreparedStatement s = c.prepareStatement("DELETE FROM GuildNotice WHERE guild_id=?");
        ) {
            s.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void setGuildNotice(long guildId, long channelId, String school) {
        try (
                Connection c = DriverManager.getConnection("jdbc:sqlite:src/main/resources/DB.db");
                PreparedStatement s = c.prepareStatement("INSERT INTO GuildNotice VALUES(?, ?, ?)");
                PreparedStatement s2 = c.prepareStatement("UPDATE GuildNotice SET channel_id=?, school=? WHERE guild_id=?")
        ) {
            s2.setLong(1, guildId);
            s2.setLong(2, channelId);
            s2.setString(2, school);
            if (s2.executeUpdate() == 0) {
                s.setLong(1, channelId);
                s.setString(2, school);
                s.setLong(3, guildId);
                s.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void setGuildNoticeOnlyChannel(long guildId, long channelId) {
        try (
                Connection c = DriverManager.getConnection("jdbc:sqlite:src/main/resources/DB.db");
                PreparedStatement s2 = c.prepareStatement("UPDATE GuildNotice SET channel_id=? WHERE guild_id=?")
        ) {
            s2.setLong(1, channelId);
            s2.setLong(2, guildId);
            s2.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void setGuildNoticeOnlySchool(long guildId, String school) {
        try (
                Connection c = DriverManager.getConnection("jdbc:sqlite:src/main/resources/DB.db");
                PreparedStatement s2 = c.prepareStatement("UPDATE GuildNotice SET school=? WHERE guild_id=?")
        ) {
            s2.setString(1, school);
            s2.setLong(2, guildId);
            s2.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
