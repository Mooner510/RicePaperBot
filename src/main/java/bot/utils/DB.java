package bot.utils;

import bot.Main;
import bot.SchoolData;
import org.jetbrains.annotations.NotNull;

import javax.annotation.CheckReturnValue;
import java.sql.*;
import java.util.HashSet;

public class DB {
    public static SchoolData getSchool(long userId) {
        try(
                Connection c = DriverManager.getConnection("jdbc:sqlite:src/main/resources/DB.db");
                ResultSet r = c.prepareStatement("SELECT * FROM GuildSchool WHERE id=" + userId).executeQuery()
        ) {
            if(r.next()) {
                return Main.schools.get(r.getString("school"));
            } else {
                return null;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @CheckReturnValue
    public static String setSchool(long userId, @NotNull String school) {
        try (
                Connection c = DriverManager.getConnection("jdbc:sqlite:src/main/resources/DB.db");
                PreparedStatement s = c.prepareStatement("INSERT INTO GuildSchool VALUES(?, ?)");
                PreparedStatement s2 = c.prepareStatement("UPDATE GuildSchool SET school=? WHERE id=?")
        ) {
            s2.setString(1, school);
            s2.setLong(2, userId);
            if(s2.executeUpdate() == 0) {
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
        try(
                Connection c = DriverManager.getConnection("jdbc:sqlite:src/main/resources/DB.db");
                ResultSet r = c.prepareStatement("SELECT * FROM RiceNotice").executeQuery()
        ) {
            while (r.next()) {
                if(r.getBoolean("notice")) {
                    notices.add(r.getLong("id"));
                }
            }
            return notices;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return notices;
    }

    @CheckReturnValue
    public static String setNotices(long userId, boolean notice) {
        try (
                Connection c = DriverManager.getConnection("jdbc:sqlite:src/main/resources/DB.db");
                PreparedStatement s = c.prepareStatement("INSERT INTO RiceNotice VALUES(?, ?)");
                PreparedStatement s2 = c.prepareStatement("UPDATE RiceNotice SET notice=? WHERE id=?")
        ) {
            s2.setBoolean(1, notice);
            s2.setLong(2, userId);
            if(s2.executeUpdate() == 0) {
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
}
