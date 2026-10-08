package dss;

import java.util.List;
import java.util.regex.Pattern;

/** One problem category: key, name, weight in the village score, keyword pattern and suggested scheme. */
public record Category(String key, String name, double weight, Pattern keywords, String suggestion) {

    private static final int F = Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE;

    public static final List<Category> ALL = List.of(
        new Category("water", "Water", 0.20,
            Pattern.compile("पानी|नल|हैंडपंप|नलकूप|जल|pani|water|(?<![a-z])nal(?![a-z])|handpump|tap", F),
            "Inspect handpump/pipeline; apply under Jal Jeevan Mission"),
        new Category("road", "Roads", 0.15,
            Pattern.compile("सड़क|गड्ढ|रोड|पुल|road|pothole", F),
            "Repair the road; apply under PMGSY"),
        new Category("garbage", "Garbage", 0.15,
            Pattern.compile("कचरा|कूड़ा|गंदगी|नाली|सफाई|garbage|kachra|drain|waste", F),
            "Arrange waste collection and drain cleaning (Swachh Bharat Gramin)"),
        new Category("light", "Streetlights", 0.10,
            Pattern.compile("बिजली|लाइट|बल्ब|खंभा|street ?light|bijli|light", F),
            "Repair or install streetlights; check power supply"),
        new Category("crops", "Crops", 0.15,
            Pattern.compile("फसल|खेत|खेती|कीड़|बीज|सिंचाई|crop|fasal|kheti|irrigation", F),
            "Agriculture officer visit; PM-Kisan / irrigation support"),
        new Category("health", "Healthcare", 0.15,
            Pattern.compile("अस्पताल|डॉक्टर|दवा|दवाई|स्वास्थ्य|बीमार|hospital|doctor|dawa|medicine", F),
            "Arrange a health camp / visit to the nearest health centre"),
        new Category("edu", "Education", 0.10,
            Pattern.compile("स्कूल|पढ़ाई|शिक्षक|इंटरनेट|किताब|school|internet|teacher", F),
            "Report to the school education office; Samagra Shiksha support")
    );

    public static Category byKey(String key) {
        return ALL.stream().filter(c -> c.key().equals(key)).findFirst().orElse(null);
    }
}
