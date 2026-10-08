package dss;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class Main {

    public static void main(String[] args) throws IOException {
        int port = args.length > 0 ? Integer.parseInt(args[0]) : 8080;
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
        server.createContext("/api/health", ex -> send(ex, 200, "{\"status\":\"ok\"}"));
        server.createContext("/api/categories", Main::categories);
        server.createContext("/api/classify", Main::classify);
        server.createContext("/api/priority", Main::priority);
        server.start();
        System.out.println("DSS backend running at http://localhost:" + port + "/api/health");
    }

    private static void categories(HttpExchange ex) throws IOException {
        StringBuilder sb = new StringBuilder("[");
        for (Category c : Category.ALL) {
            if (sb.length() > 1) sb.append(',');
            sb.append("{\"key\":").append(q(c.key())).append(",\"name\":").append(q(c.name()))
              .append(",\"weight\":").append(c.weight()).append('}');
        }
        send(ex, 200, sb.append(']').toString());
    }

    private static void classify(HttpExchange ex) throws IOException {
        String text = params(ex).get("text");
        if (text == null || text.isBlank()) { send(ex, 400, "{\"error\":\"text is required\"}"); return; }
        Classifier.Result r = Classifier.classify(text);
        String cat = r.category() == null ? "null" : q(r.category().key());
        String name = r.category() == null ? "null" : q(r.category().name());
        String tip = r.category() == null ? "null" : q(r.category().suggestion());
        String priority = r.severity() >= 4 ? "High" : "Medium";
        send(ex, 200, "{\"category\":" + cat + ",\"categoryName\":" + name + ",\"severity\":" + r.severity()
                + ",\"priority\":" + q(priority) + ",\"suggestion\":" + tip + "}");
    }

    private static void priority(HttpExchange ex) throws IOException {
        Map<String, String> p = params(ex);
        Map<String, Integer> survey = new HashMap<>();
        for (Category c : Category.ALL) {
            if (p.containsKey(c.key())) {
                try { survey.put(c.key(), Math.max(0, Math.min(100, Integer.parseInt(p.get(c.key()))))); }
                catch (NumberFormatException e) { send(ex, 400, "{\"error\":\"invalid value for " + c.key() + "\"}"); return; }
            }
        }
        List<Scoring.Complaint> complaints = new ArrayList<>();
        String raw = p.getOrDefault("complaints", "");
        for (String part : raw.split(",")) {
            String[] kv = part.split(":");
            if (kv.length == 2 && Category.byKey(kv[0].trim()) != null) {
                try { complaints.add(new Scoring.Complaint(kv[0].trim(), Math.max(1, Math.min(5, Integer.parseInt(kv[1].trim()))))); }
                catch (NumberFormatException ignored) { }
            }
        }
        Integer score = Scoring.villageScore(survey, complaints);
        send(ex, 200, "{\"score\":" + score + ",\"label\":" + q(Scoring.label(score)) + "}");
    }

    private static Map<String, String> params(HttpExchange ex) {
        Map<String, String> m = new HashMap<>();
        String qs = ex.getRequestURI().getRawQuery();
        if (qs == null) return m;
        for (String pair : qs.split("&")) {
            int i = pair.indexOf('=');
            if (i < 0) continue;
            m.put(URLDecoder.decode(pair.substring(0, i), StandardCharsets.UTF_8),
                  URLDecoder.decode(pair.substring(i + 1), StandardCharsets.UTF_8));
        }
        return m;
    }

    private static String q(String s) {
        return "\"" + s.replace("\\", "\\\\").replace("\"", "\\\"") + "\"";
    }

    private static void send(HttpExchange ex, int code, String body) throws IOException {
        byte[] b = body.getBytes(StandardCharsets.UTF_8);
        ex.getResponseHeaders().set("Content-Type", "application/json; charset=utf-8");
        ex.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
        ex.sendResponseHeaders(code, b.length);
        try (OutputStream os = ex.getResponseBody()) { os.write(b); }
    }
}
