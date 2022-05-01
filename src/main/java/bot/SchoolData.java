package bot;

public record SchoolData(String name, String tag, String code) {

    public String getName() {
        return name;
    }

    public String getTag() {
        return tag;
    }

    public String getCode() {
        return code;
    }
}
