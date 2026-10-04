package com.example.myapplication6;

import android.content.Intent;
import android.app.Activity;
import android.app.Instrumentation;
import android.widget.TextView;
import android.widget.EditText;

import androidx.recyclerview.widget.RecyclerView;
import com.google.common.util.concurrent.ListenableFuture;

import java.lang.reflect.Field;
import java.util.List;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
public class AlertNotificationTest {
    private Intent alertIntent() {
        return new Intent(InstrumentationRegistry.getInstrumentation().getTargetContext(),
                AlertNotification.class);
    }

    @Test
    public void homeOpensChatWithoutInventingAnAlert() {
        Instrumentation instrumentation = InstrumentationRegistry.getInstrumentation();
        Instrumentation.ActivityMonitor monitor = instrumentation.addMonitor(
                AlertNotification.class.getName(), null, false);
        try (ActivityScenario<Home> scenario = ActivityScenario.launch(Home.class)) {
            scenario.onActivity(activity -> activity.findViewById(R.id.Alert_Notification).performClick());
            Activity alert = instrumentation.waitForMonitorWithTimeout(monitor, 10_000);
            assertNotNull(alert);
            instrumentation.runOnMainSync(() -> {
                try {
                    assertEquals(alert.getString(R.string.no_alert_data),
                            ((TextView) alert.findViewById(R.id.tvCardTitle)).getText().toString());
                    assertEquals(android.view.View.VISIBLE, alert.findViewById(R.id.tvBanner).getVisibility());
                    assertEquals("⏰ Time：--", ((TextView) alert.findViewById(R.id.tvTime)).getText().toString());
                    assertTrue(alert.findViewById(R.id.chatSend).isEnabled());
                } finally {
                    alert.finish();
                }
            });
        } finally {
            instrumentation.removeMonitor(monitor);
        }
    }

    @Test
    public void mapAlertShowsProvidedWeatherData() {
        Intent intent = alertIntent().putExtra("precipitation", 60f)
                .putExtra("latitude", -33.8688).putExtra("longitude", 151.2093)
                .putExtra("timestamp", 1_700_000_000_000L);
        try (ActivityScenario<AlertNotification> scenario = ActivityScenario.launch(intent)) {
            scenario.onActivity(activity -> {
                TextView rainfall = activity.findViewById(R.id.tvRainfall);
                TextView latitude = activity.findViewById(R.id.tvLatitude);
                TextView time = activity.findViewById(R.id.tvTime);
                assertTrue(rainfall.getText().toString().contains("60"));
                assertTrue(latitude.getText().toString().contains("33"));
                assertTrue(time.getText().toString().contains("2023"));
                assertEquals(android.view.View.GONE, activity.findViewById(R.id.tvBanner).getVisibility());
            });
        }
    }

    @Test
    public void incompleteAlertShowsMissingDataAndEmptySendDoesNothing() {
        try (ActivityScenario<AlertNotification> scenario = ActivityScenario.launch(
                alertIntent().putExtra("precipitation", 60f))) {
            scenario.onActivity(activity -> {
                assertEquals(activity.getString(R.string.no_alert_data),
                        ((TextView) activity.findViewById(R.id.tvCardTitle)).getText().toString());
                activity.findViewById(R.id.chatSend).performClick();
                assertTrue(activity.findViewById(R.id.chatSend).isEnabled());
                assertEquals("", ((EditText) activity.findViewById(R.id.chatInput)).getText().toString());
            });
        }
    }

    @Test
    public void failedRequestRestoresQuestionAndAllowsRetry() {
        try (ActivityScenario<AlertNotification> scenario = ActivityScenario.launch(alertIntent())) {
            scenario.onActivity(activity -> {
                ((EditText) activity.findViewById(R.id.chatInput)).setText("How do I prepare?");
                activity.findViewById(R.id.chatSend).performClick();
                ListenableFuture<?> request = (ListenableFuture<?>) field(activity, "pendingRequest");
                assertNotNull(request);
                // Cancellation exercises the SDK future's failure callback without relying on cloud availability.
                request.cancel(true);
            });
            InstrumentationRegistry.getInstrumentation().waitForIdleSync();
            scenario.onActivity(activity -> {
                assertTrue(activity.findViewById(R.id.chatSend).isEnabled());
                assertEquals("How do I prepare?",
                        ((EditText) activity.findViewById(R.id.chatInput)).getText().toString());
                assertTrue(((List<?>) field(activity, "completedHistory")).isEmpty());
                activity.findViewById(R.id.chatSend).performClick();
                assertFalse(activity.findViewById(R.id.chatSend).isEnabled());
                assertFalse(activity.findViewById(R.id.chatInput).isEnabled());
                assertNotNull(field(activity, "pendingRequest"));
            });
        }
    }

    @Test
    public void timeoutRestoresInputAndOldTimeoutCannotEndRetry() {
        try (ActivityScenario<AlertNotification> scenario = ActivityScenario.launch(alertIntent())) {
            scenario.onActivity(activity -> {
                ((EditText) activity.findViewById(R.id.chatInput)).setText("What should I pack?");
                activity.findViewById(R.id.chatSend).performClick();
                // A keyboard send action must not start a second concurrent request.
                activity.findViewById(R.id.chatSend).performClick();
                RecyclerView chat = activity.findViewById(R.id.chatRecycler);
                assertEquals(3, chat.getAdapter().getItemCount());
                Runnable timeout = (Runnable) field(activity, "requestTimeout");
                assertNotNull(timeout);
                // Trigger the real timeout handler immediately instead of waiting 45 seconds.
                timeout.run();
                assertTrue(activity.findViewById(R.id.chatSend).isEnabled());
                assertTrue(activity.findViewById(R.id.chatInput).isEnabled());
                assertEquals("What should I pack?",
                        ((EditText) activity.findViewById(R.id.chatInput)).getText().toString());
                assertTrue(((List<?>) field(activity, "completedHistory")).isEmpty());
                activity.findViewById(R.id.chatSend).performClick();
                Object retry = field(activity, "pendingRequest");
                assertNotNull(retry);
                timeout.run();
                assertSame(retry, field(activity, "pendingRequest"));
                assertFalse(activity.findViewById(R.id.chatSend).isEnabled());
            });
        }
    }

    // Inspect/cancel the pending operation only in tests; production code needs no test-only API.
    private Object field(AlertNotification activity, String name) {
        try {
            Field field = AlertNotification.class.getDeclaredField(name);
            field.setAccessible(true);
            return field.get(activity);
        } catch (ReflectiveOperationException e) {
            throw new AssertionError(e);
        }
    }
}
