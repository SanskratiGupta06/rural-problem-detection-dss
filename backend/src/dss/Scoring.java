package dss;

import java.util.List;
import java.util.Map;

/** Village priority scoring: blends survey severity (70%) with open complaints (30%), then weights categories. */
public final class Scoring {

    public record Complaint(String category, int severity) {}

    private Scoring() {}

    /** Severity (0-100) of one category, or null if there is no data at all. */
    public static Integer categorySeverity(Integer survey, List<Complaint> openComplaints, String key) {
        int fromComplaints = Math.min(100, openComplaints.stream()
                .filter(c -> c.category().equals(key))
                .mapToInt(c -> c.severity() * 20).sum());
        boolean hasComplaint = openComplaints.stream().anyMatch(c -> c.category().equals(key));
        if (survey == null && !hasComplaint) return null;
        if (survey == null) return fromComplaints;
        return (int) Math.floor(0.7 * survey + 0.3 * fromComplaints + 0.5);
    }

    /** Weighted village score (0-100), or null when no category has data. */
    public static Integer villageScore(Map<String, Integer> survey, List<Complaint> openComplaints) {
        double total = 0, weight = 0;
        for (Category c : Category.ALL) {
            Integer s = categorySeverity(survey.get(c.key()), openComplaints, c.key());
            if (s != null) { total += s * c.weight(); weight += c.weight(); }
        }
        return weight == 0 ? null : (int) Math.floor(total / weight + 0.5);
    }

    public static String label(Integer score) {
        if (score == null) return "No data";
        if (score >= 70) return "Critical";
        if (score >= 50) return "High";
        if (score >= 30) return "Moderate";
        return "Low";
    }
}
