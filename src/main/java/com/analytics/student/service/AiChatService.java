package com.analytics.student.service;

import com.analytics.student.model.AnomalyResult;
import com.analytics.student.model.CorrelationResult;
import com.analytics.student.model.PerformanceCategory;
import com.analytics.student.model.Student;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class AiChatService {

    public static final String DEFAULT_API_KEY = "sk_19gf0h5l_rD3w6gmsJEZszBEWOOCBi2ro";
    private static final String SARVAM_API_URL = "https://api.sarvam.ai/v1/chat/completions";

    private static final String[] SUPPORTED_MODELS = {
        "sarvam-105b-conversations",
        "sarvam-105b"
    };

    private final ObjectMapper mapper = new ObjectMapper();
    private final HttpClient httpClient;
    private String apiKey;
    private boolean offlineMode = false;
    private StudentService studentService;
    
    // To maintain conversation history (OpenAI / Sarvam format)
    private final ArrayNode messagesArray;

    public AiChatService() {
        HttpClient client = null;
        try {
            client = HttpClient.newBuilder()
                    .connectTimeout(Duration.ofSeconds(20))
                    .build();
        } catch (Throwable t) {
            System.err.println("Notice: Running AiChatService in offline/local fallback mode: " + t.getMessage());
            this.offlineMode = true;
        }
        this.httpClient = client;
        this.messagesArray = mapper.createArrayNode();
        setApiKey(DEFAULT_API_KEY);
    }

    public void setStudentService(StudentService studentService) {
        this.studentService = studentService;
    }

    public void setApiKey(String apiKey) {
        if (apiKey == null) {
            this.apiKey = null;
            return;
        }
        String clean = apiKey.trim()
                .replaceAll("^[\"']+|[\"']+$", "")
                .replaceAll("[.\\s]+$", "");
        this.apiKey = clean.isEmpty() ? null : clean;
        this.offlineMode = false;
    }

    public String getApiKey() {
        return this.apiKey != null ? this.apiKey : DEFAULT_API_KEY;
    }

    public boolean hasApiKey() {
        return this.apiKey != null && !this.apiKey.trim().isEmpty();
    }

    public void setOfflineMode(boolean offlineMode) {
        this.offlineMode = offlineMode;
    }

    public boolean isOfflineMode() {
        return this.offlineMode;
    }

    /**
     * Set a system instruction/context with the loaded student database summary.
     */
    public void setContext(String contextSummary) {
        messagesArray.removeAll();
        addMessageToHistory("system", "You are the AI Assistant for 'EduTrack', an analytics application for student performance. Here is the current database summary:\n" + contextSummary + "\n\nAnswer student analytics questions concisely, helpfully, and with data insights.");
        addMessageToHistory("assistant", "Hello! I am your EduTrack AI Assistant powered by Sarvam AI. I have analyzed your current student database. How can I help you today?");
    }

    public String sendMessage(String prompt) throws Exception {
        if (offlineMode || httpClient == null) {
            return generateOfflineResponse(prompt);
        }

        if (!hasApiKey()) {
            return generateOfflineResponse(prompt);
        }

        String cleanKey = this.apiKey;

        // Add user prompt to history
        addMessageToHistory("user", prompt);

        String lastError = null;
        for (String model : SUPPORTED_MODELS) {
            // Build the request payload in OpenAI/Sarvam chat format
            ObjectNode requestBody = mapper.createObjectNode();
            requestBody.put("model", model);
            requestBody.set("messages", messagesArray);
            requestBody.put("temperature", 0.7);
            requestBody.put("max_tokens", 1000);
            String requestJson = requestBody.toString();

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(SARVAM_API_URL))
                    .header("Content-Type", "application/json")
                    .header("api-subscription-key", cleanKey)
                    .header("Authorization", "Bearer " + cleanKey)
                    .POST(HttpRequest.BodyPublishers.ofString(requestJson))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                JsonNode root = mapper.readTree(response.body());
                try {
                    JsonNode choiceNode = root.path("choices").get(0);
                    JsonNode messageNode = choiceNode.path("message");
                    String aiResponse = messageNode.path("content").asText(null);
                    
                    // Fallback to reasoning_content if content is empty
                    if (aiResponse == null || aiResponse.trim().isEmpty() || "null".equalsIgnoreCase(aiResponse)) {
                        if (messageNode.hasNonNull("reasoning_content")) {
                            aiResponse = messageNode.path("reasoning_content").asText("");
                        }
                    }
                    
                    if (aiResponse != null && !aiResponse.trim().isEmpty()) {
                        addMessageToHistory("assistant", aiResponse);
                        return aiResponse;
                    }
                } catch (Exception e) {
                    throw new Exception("Error parsing AI response from Sarvam AI model " + model);
                }
            } else if (response.statusCode() == 404 || (response.statusCode() == 400 && (response.body().contains("model") || response.body().contains("Model")))) {
                // If model not found or unsupported, try next candidate
                lastError = "Model " + model + " error: " + response.body();
                continue;
            } else {
                if (messagesArray.size() > 0) {
                    messagesArray.remove(messagesArray.size() - 1);
                }
                String errorDetail = response.body();
                try {
                    JsonNode errNode = mapper.readTree(response.body());
                    if (errNode.has("error") && errNode.get("error").has("message")) {
                        errorDetail = errNode.get("error").get("message").asText();
                    } else if (errNode.has("detail")) {
                        errorDetail = errNode.get("detail").asText();
                    } else if (errNode.has("message")) {
                        errorDetail = errNode.get("message").asText();
                    }
                } catch (Exception ignored) {}
                throw new Exception("Sarvam AI Error (" + response.statusCode() + "): " + errorDetail);
            }
        }

        if (messagesArray.size() > 0) {
            messagesArray.remove(messagesArray.size() - 1);
        }
        throw new Exception("Sarvam AI Error: Could not connect (" + lastError + ")");
    }

    /**
     * Local Smart Rule-Based Engine that answers data questions offline without any API key.
     */
    private String generateOfflineResponse(String prompt) {
        if (studentService == null) {
            return "I am the Local Analytics Assistant. Please load data from the 'Import data' tab first!";
        }

        List<Student> students = studentService.getAllStudents();
        if (students.isEmpty()) {
            return "No students are currently loaded in the database. Please import a CSV dataset first to begin analysis!";
        }

        String lower = prompt.toLowerCase();

        if (lower.contains("summary") || lower.contains("overview") || lower.contains("stat") || lower.contains("average")) {
            long high = students.stream().filter(s -> s.getPerformanceCategory() == PerformanceCategory.HIGH).count();
            long med = students.stream().filter(s -> s.getPerformanceCategory() == PerformanceCategory.MEDIUM).count();
            long low = students.stream().filter(s -> s.getPerformanceCategory() == PerformanceCategory.LOW).count();

            double avgMarks = students.stream().mapToDouble(Student::getCurrentMarks).average().orElse(0.0);
            double avgStudy = students.stream().mapToDouble(Student::getStudyHours).average().orElse(0.0);
            double avgSleep = students.stream().mapToDouble(Student::getSleepHours).average().orElse(0.0);
            double avgAtt = students.stream().mapToDouble(Student::getAttendance).average().orElse(0.0);

            return String.format(
                "📊 **Class Analytics Overview**:\n\n" +
                "• **Total Students**: %d\n" +
                "• **Average Marks**: %.1f%%\n" +
                "• **Average Study Hours**: %.1f hrs/day\n" +
                "• **Average Sleep**: %.1f hrs/night\n" +
                "• **Average Attendance**: %.1f%%\n" +
                "• **Performance Breakdown**: %d High (%.0f%%), %d Medium (%.0f%%), %d Low (%.0f%%).",
                students.size(),
                avgMarks,
                avgStudy,
                avgSleep,
                avgAtt,
                high, (high * 100.0 / students.size()),
                med, (med * 100.0 / students.size()),
                low, (low * 100.0 / students.size())
            );
        }

        if (lower.contains("top") || lower.contains("high") || lower.contains("best")) {
            List<Student> top = students.stream()
                    .sorted(Comparator.comparingDouble(Student::getCurrentMarks).reversed())
                    .limit(5)
                    .collect(Collectors.toList());
            StringBuilder sb = new StringBuilder("🌟 **Top 5 High Performers**:\n\n");
            for (int i = 0; i < top.size(); i++) {
                Student s = top.get(i);
                sb.append(String.format("%d. **%s** (%s) - Marks: **%.1f%%** | Study: %.1fh/day | Sleep: %.1fh\n",
                        i + 1, s.getName(), s.getStudentId(), s.getCurrentMarks(), s.getStudyHours(), s.getSleepHours()));
            }
            return sb.toString();
        }

        if (lower.contains("low") || lower.contains("weak") || lower.contains("fail") || lower.contains("struggle")) {
            List<Student> weak = students.stream()
                    .filter(s -> s.getPerformanceCategory() == PerformanceCategory.LOW)
                    .sorted(Comparator.comparingDouble(Student::getCurrentMarks))
                    .limit(5)
                    .collect(Collectors.toList());
            if (weak.isEmpty()) {
                return "🎉 Excellent news! There are currently no students categorized as Low Performers (< 50% marks).";
            }
            StringBuilder sb = new StringBuilder("⚠️ **Students Requiring Academic Support (Low Performers)**:\n\n");
            for (Student s : weak) {
                sb.append(String.format("• **%s** (%s): Marks **%.1f%%** | Study: %.1fh | Sleep: %.1fh | Attendance: %.1f%%\n",
                        s.getName(), s.getStudentId(), s.getCurrentMarks(), s.getStudyHours(), s.getSleepHours(), s.getAttendance()));
            }
            sb.append("\n💡 **Recommendation**: Target interventions around attendance and establishing consistent study habits.");
            return sb.toString();
        }

        if (lower.contains("correlat") || lower.contains("pearson") || lower.contains("relationship")) {
            CorrelationResult res = studentService.calculateCorrelation(AnalyticsEngine.METRIC_STUDY_HOURS, AnalyticsEngine.METRIC_CURR_MARKS);
            return String.format(
                "📈 **Correlation Analysis (Study Hours vs Marks)**:\n\n" +
                "• **Pearson r**: **%.3f** (%s)\n" +
                "• **Caveat**: *Correlation does not imply causation.* While higher study hours relate to higher marks in this class, individual learning styles, sleep quality, and attendance also play vital roles.",
                res.getPearsonR(), res.getStrengthInterpretation()
            );
        }

        if (lower.contains("anomaly") || lower.contains("outlier") || lower.contains("deviation")) {
            List<AnomalyResult> anomalies = studentService.detectAnomalies(2.0);
            if (anomalies.isEmpty()) {
                return "✅ No statistical anomalies (|z| ≥ 2.0) detected in the current cohort.";
            }
            StringBuilder sb = new StringBuilder("🔍 **Statistical Anomalies Detected (|z| ≥ 2.0)**:\n\n");
            for (AnomalyResult a : anomalies) {
                sb.append(String.format("• **%s** (%s): %s\n", a.getStudent().getName(), a.getStudent().getStudentId(), a.getExplanation()));
            }
            return sb.toString();
        }

        // Search for student by name or ID
        for (Student s : students) {
            if (lower.contains(s.getName().toLowerCase()) || lower.contains(s.getStudentId().toLowerCase())) {
                return String.format(
                    "👤 **Student Profile: %s (%s)**:\n\n" +
                    "• Current Marks: **%.1f%%** (Previous: %.1f%%)\n" +
                    "• Category: **%s**\n" +
                    "• Study: %.1f hrs/day | Sleep: %.1f hrs/day\n" +
                    "• Attendance: %.1f%% | Screen Time: %.1f hrs/day | Exercise: %.1f hrs/day",
                    s.getName(), s.getStudentId(), s.getCurrentMarks(), s.getPreviousMarks(),
                    s.getPerformanceCategory(), s.getStudyHours(), s.getSleepHours(),
                    s.getAttendance(), s.getScreenTime(), s.getExerciseHours()
                );
            }
        }

        return "🤖 **EduTrack Local Assistant**:\n" +
               "I can analyze your dataset instantly! Try asking me:\n" +
               "• *'Give me an overview of the class'* (or *'summary'*)\n" +
               "• *'Who are the top performers?'*\n" +
               "• *'Which students need help?'* (or *'low performers'*)\n" +
               "• *'What is the correlation between study hours and marks?'*\n" +
               "• *'Show anomalies'* or *'Search student S001'*";
    }

    private void addMessageToHistory(String role, String text) {
        ObjectNode messageNode = mapper.createObjectNode();
        messageNode.put("role", role);
        messageNode.put("content", text);
        messagesArray.add(messageNode);
    }
}
