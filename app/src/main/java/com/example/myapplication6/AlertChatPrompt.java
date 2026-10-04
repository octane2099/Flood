package com.example.myapplication6;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/** Builds model input separately from the messages used to render the chat UI. */
final class AlertChatPrompt {
    private AlertChatPrompt() {}

    static String alertContext(float rainfall, double latitude, double longitude, long timestamp) {
        // Missing extras use NaN; zero rainfall and coordinates can be valid observations.
        if (Float.isNaN(rainfall) || Float.isInfinite(rainfall) || rainfall < 0
                || Double.isNaN(latitude) || Math.abs(latitude) > 90
                || Double.isNaN(longitude) || Math.abs(longitude) > 180
                || timestamp <= 0) {
            return null;
        }
        String date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z", Locale.US)
                .format(new Date(timestamp));
        return String.format(Locale.US,
                "Current alert context:\n- Rainfall: %.2f mm\n- Latitude: %.5f\n"
                        + "- Longitude: %.5f\n- Time: %s\n",
                rainfall, latitude, longitude, date);
    }

    static String build(String latestUserText, String alertContext, List<String> completedHistory) {
        StringBuilder prompt = new StringBuilder(
                "You are a Flood Alert Assistant. Answer in concise English.\n");
        if (alertContext == null) {
            prompt.append("No current alert data is available. Answer as a general question. "
                    + "Do not assume current rainfall, location, time, or local flood risk.\n");
        } else {
            prompt.append(alertContext);
        }
        prompt.append('\n');
        int start = Math.max(0, completedHistory.size() - 6);
        for (int i = start; i < completedHistory.size(); i++) {
            prompt.append(completedHistory.get(i)).append('\n');
        }
        prompt.append("User: ").append(latestUserText).append("\nAssistant: ");
        return prompt.toString();
    }
}
