package bot.cmd.util.rice;

public enum RiceType {
    BREAKFAST("조식"), LUNCH("중식"), DINNER("석식");

    private final String tag;

    RiceType(String s) {
        tag = s;
    }

    public String getTag() {
        return tag;
    }
}
