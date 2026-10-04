package com.example.myapplication6;

import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.Assert.*;

public class AlertChatPromptTest {
    @Test
    public void currentQuestionAppearsOnceWithCompletedHistory() {
        String prompt = AlertChatPrompt.build("Should I leave?", null,
                Arrays.asList("User: What should I pack?", "Assistant: Pack water."));
        assertEquals(1, prompt.split("Should I leave\\?", -1).length - 1);
        assertTrue(prompt.contains("User: What should I pack?\nAssistant: Pack water."));
        assertFalse(prompt.contains("AI is typing"));
        assertTrue(prompt.endsWith("User: Should I leave?\nAssistant: "));
    }

    @Test
    public void onlyLastSixHistoryMessagesAreSent() {
        String prompt = AlertChatPrompt.build("latest", null, Arrays.asList(
                "User: old question", "Assistant: old answer",
                "User: question 1", "Assistant: answer 1", "User: question 2",
                "Assistant: answer 2", "User: question 3", "Assistant: answer 3"));
        assertFalse(prompt.contains("old question"));
        assertFalse(prompt.contains("old answer"));
        assertTrue(prompt.contains("User: question 1"));
        assertTrue(prompt.contains("Assistant: answer 3"));
    }

    @Test
    public void missingContextDoesNotInventWeatherOrLocation() {
        String prompt = AlertChatPrompt.build("How do I prepare?", null, Collections.emptyList());
        assertTrue(prompt.contains("Answer as a general question"));
        assertFalse(prompt.contains("Rainfall:"));
        assertFalse(prompt.contains("Latitude:"));
        assertFalse(prompt.contains("Time:"));
        assertFalse(prompt.contains("1970"));
    }

    @Test
    public void completeContextIsIncludedAndZeroValuesRemainValid() {
        String context = AlertChatPrompt.alertContext(0, 0, 0, 1_700_000_000_000L);
        assertNotNull(context);
        String prompt = AlertChatPrompt.build("What does this mean?", context, Collections.emptyList());
        assertTrue(prompt.contains("Rainfall: 0.00 mm"));
        assertTrue(prompt.contains("Latitude: 0.00000"));
        assertTrue(prompt.contains("Longitude: 0.00000"));
        assertFalse(prompt.contains("No current alert data"));
    }

    @Test
    public void incompleteAndInvalidContextIsRejected() {
        long time = 1_700_000_000_000L;
        assertNull(AlertChatPrompt.alertContext(Float.NaN, -33, 151, time));
        assertNull(AlertChatPrompt.alertContext(60, Double.NaN, 151, time));
        assertNull(AlertChatPrompt.alertContext(60, -33, Double.NaN, time));
        assertNull(AlertChatPrompt.alertContext(60, -33, 151, 0));
        assertNull(AlertChatPrompt.alertContext(-1, -33, 151, time));
        assertNull(AlertChatPrompt.alertContext(Float.POSITIVE_INFINITY, -33, 151, time));
        assertNull(AlertChatPrompt.alertContext(60, 91, 151, time));
        assertNull(AlertChatPrompt.alertContext(60, -33, 181, time));
    }
}
