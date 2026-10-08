package dss;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** Rule-based complaint classifier: picks a category and a 1-5 severity from free text (Hindi or English). */
public final class Classifier {

    private static final int F = Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE;
    private static final Pattern DURATION = Pattern.compile("तीन दिन|कई दिन|हफ्त|महीने|days|weeks|month|दिनों", F);
    private static final Pattern INTENSITY = Pattern.compile("बहुत|भारी|खतरनाक|urgent|severe|बच्चे|बीमार|दुर्घटना|accident", F);
    private static final Pattern ABSENCE = Pattern.compile("बिल्कुल|कोई नहीं|बंद|नहीं आ|no water", F);

    public record Result(Category category, int severity) {}

    private Classifier() {}

    public static Result classify(String text) {
        Category best = null;
        int bestCount = 0;
        for (Category c : Category.ALL) {
            Matcher m = c.keywords().matcher(text);
            int n = 0;
            while (m.find()) n++;
            if (n > bestCount) { bestCount = n; best = c; }   // ties go to the first category
        }
        int sev = 2;
        if (DURATION.matcher(text).find()) sev++;
        if (INTENSITY.matcher(text).find()) sev++;
        if (ABSENCE.matcher(text).find()) sev++;
        return new Result(best, Math.min(5, sev));
    }
}
