package com.example.myapplication6;

// ===== ADD: UI 辅助 =====
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.annotation.NonNull;

import android.os.Build;
import android.view.WindowInsets;
import android.view.WindowInsetsController;
import android.widget.TextView;

// ======（chat imports）======
import android.widget.EditText;
import android.widget.ImageButton;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;

import com.google.firebase.FirebaseApp;
import com.google.firebase.ai.FirebaseAI;
import com.google.firebase.ai.java.GenerativeModelFutures;
import com.google.firebase.ai.type.Content;
import com.google.firebase.ai.type.GenerateContentResponse;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class AlertNotification extends AppCompatActivity {
    //监听生命周期
    String Tag="my_tag";
    //跳转到Home页
    private ImageView mB_home;


    // ====== chat state ======
    private RecyclerView chatRecycler;
    private EditText chatInput;
    private ImageButton chatSend;
    private ChatAdapter chatAdapter;
    private final ArrayList<ChatMsg> messages = new ArrayList<>();

    private GenerativeModelFutures modelFutures;           // Java futures wrapper
    // Only completed user/AI exchanges belong in the prompt, never UI status or errors.
    private final ArrayList<String> completedHistory = new ArrayList<>();
    private String alertContext;
    private final Handler mainHandler = new Handler(Looper.getMainLooper());
    private ListenableFuture<GenerateContentResponse> pendingRequest;
    private Runnable requestTimeout;
    private boolean requestInFlight;
    private static final long REQUEST_TIMEOUT_MS = 45_000;
    // 记录“AI is typing…”占位消息在 messages 列表中的位置；-1 表示无
    private int typingPlaceholderPos = -1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_alert_notification);

        // 获取从 Map.java 传递的数据
        Intent intent = getIntent();
        float precipitation = intent.getFloatExtra("precipitation", Float.NaN);
        double latitude = intent.getDoubleExtra("latitude", Double.NaN);
        double longitude = intent.getDoubleExtra("longitude", Double.NaN);
        long timestamp = intent.getLongExtra("timestamp", 0);
        alertContext = AlertChatPrompt.alertContext(precipitation, latitude, longitude, timestamp);

        // 格式化时间
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        String formattedDate = alertContext == null ? "--" : sdf.format(new Date(timestamp));

        // 显示降雨量、经纬度和时间（保持中文）
        TextView tvRain  = findViewById(R.id.tvRainfall);
        TextView tvLat   = findViewById(R.id.tvLatitude);
        TextView tvLng   = findViewById(R.id.tvLongitude);
        TextView tvTime  = findViewById(R.id.tvTime);

        if (alertContext != null) {
            tvRain.setText(String.format(Locale.getDefault(), "🌧️  Rainfall：%.2f mm", precipitation));
            tvLat.setText(String.format(Locale.getDefault(), "📍 Latitude：%.5f", latitude));
            tvLng.setText(String.format(Locale.getDefault(), "📍 Longitude：%.5f", longitude));
            tvTime.setText(String.format(Locale.getDefault(), "⏰ Time：%s", formattedDate));
        } else {
            // Keep the layout's -- placeholders rather than displaying fabricated zero values.
            TextView title = findViewById(R.id.tvCardTitle);
            title.setText(R.string.no_alert_data);
            TextView banner = findViewById(R.id.tvBanner);
            banner.setText(R.string.no_alert_data_description);
            banner.setVisibility(View.VISIBLE);
        }


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            // 设置全屏并隐藏系统导航栏
            getWindow().setDecorFitsSystemWindows(false);
            WindowInsetsController controller = getWindow().getInsetsController();
            if (controller != null) {
                controller.hide(WindowInsets.Type.navigationBars());
                controller.setSystemBarsBehavior(WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE);
            }
        } else {
            // 对于API 30以下的版本，使用旧的沉浸模式实现
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        Log.d(Tag, "onCreate: ");

        mB_home = findViewById(R.id.imageView5);
        mB_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AlertNotification.this, Home.class);
                startActivity(intent);
            }
        });

        // ====== chat UI ======
        chatRecycler = findViewById(R.id.chatRecycler);
        // ADD: 统一气泡间距
        int space = (int) (getResources().getDisplayMetrics().density * 8);
        chatRecycler.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override public void getItemOffsets(android.graphics.Rect outRect, android.view.View view,
                                                 RecyclerView parent, RecyclerView.State state) {
                outRect.top = space; outRect.bottom = space;
                outRect.left = space; outRect.right = space;
            }
        });

// 可选：进入页面后滚到底（首条演示气泡时显著）
        chatRecycler.post(() -> chatRecycler.scrollToPosition(chatAdapter.getItemCount() - 1));

        chatInput  = findViewById(R.id.chatInput);
        // ADD: 回车也能发送（复用你原有的按钮点击逻辑）
        chatInput.setOnEditorActionListener((v, actionId, event) -> {
            boolean pressedEnter = (actionId == EditorInfo.IME_ACTION_SEND)
                    || (actionId == EditorInfo.IME_ACTION_DONE)
                    || (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER
                    && event.getAction() == KeyEvent.ACTION_DOWN);
            if (pressedEnter) {
                chatSend.performClick();
                return true;
            }
            return false;
        });

        chatSend = findViewById(R.id.chatSend);


        if (chatRecycler != null) {
            chatRecycler.setLayoutManager(new LinearLayoutManager(this));
            chatAdapter = new ChatAdapter(messages);
            chatRecycler.setAdapter(chatAdapter);

            // ADD: 新消息自动滚到底
            chatAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
                @Override public void onItemRangeInserted(int positionStart, int itemCount) {
                    chatRecycler.scrollToPosition(chatAdapter.getItemCount() - 1);
                }
            });
        }

        // ====== init Firebase AI ======
        initializeModel();

        // English welcome message
        appendMessageSafe(new ChatMsg("Hi! I can analyze the flood alert and suggest actions.", false));

        // ====== send button ======
        chatSend.setOnClickListener(v -> sendMessage());
    }

    private void initializeModel() {
        if (modelFutures != null) return;
        try {
            FirebaseApp.initializeApp(this);
            modelFutures = GenerativeModelFutures.from(
                    FirebaseAI.getInstance().generativeModel("gemini-2.5-flash"));
        } catch (RuntimeException e) {
            Log.e(Tag, "AI initialization failed", e);
        }
    }

    private void sendMessage() {
        // Also guard keyboard actions, which can call performClick on a disabled button.
        if (requestInFlight || isFinishing() || isDestroyed()) return;
        String userText = chatInput.getText().toString().trim();
        if (userText.isEmpty()) return;
        String prompt = AlertChatPrompt.build(userText, alertContext, completedHistory);

        requestInFlight = true;
        chatSend.setEnabled(false);
        chatInput.setEnabled(false);
        chatInput.setText("");
        appendMessageSafe(new ChatMsg(userText, true));
        typingPlaceholderPos = messages.size();
        appendMessageSafe(new ChatMsg("AI is typing…", false));

        initializeModel();
        if (modelFutures == null) {
            completeRequest(userText, null);
            return;
        }
        try {
            Content content = new Content.Builder().addText(prompt).build();
            final ListenableFuture<GenerateContentResponse> request = modelFutures.generateContent(content);
            pendingRequest = request;
            requestTimeout = () -> {
                if (pendingRequest != request) return;
                Log.w(Tag, "AI request timed out");
                completeRequest(userText, null);
                request.cancel(true);
            };
            mainHandler.postDelayed(requestTimeout, REQUEST_TIMEOUT_MS);
            Futures.addCallback(request, new FutureCallback<GenerateContentResponse>() {
                @Override
                public void onSuccess(GenerateContentResponse result) {
                    if (pendingRequest != request) return;
                    String reply = result == null ? null : result.getText();
                    completeRequest(userText, reply == null || reply.trim().isEmpty() ? null : reply);
                }

                @Override
                public void onFailure(Throwable t) {
                    if (pendingRequest != request) return;
                    Log.e(Tag, "AI generateContent failed", t);
                    completeRequest(userText, null);
                }
            }, ContextCompat.getMainExecutor(this));
        } catch (RuntimeException e) {
            Log.e(Tag, "AI request could not start", e);
            ListenableFuture<GenerateContentResponse> request = pendingRequest;
            completeRequest(userText, null);
            if (request != null) request.cancel(true);
        }
    }

    private void completeRequest(String userText, String reply) {
        if (requestTimeout != null) mainHandler.removeCallbacks(requestTimeout);
        requestTimeout = null;
        pendingRequest = null;
        requestInFlight = false;
        if (isFinishing() || isDestroyed()) return;

        boolean succeeded = reply != null;
        String text = succeeded ? reply : getString(R.string.chat_send_failed);
        if (typingPlaceholderPos >= 0 && typingPlaceholderPos < messages.size()) {
            messages.set(typingPlaceholderPos, new ChatMsg(text, false));
            chatAdapter.notifyItemChanged(typingPlaceholderPos);
        }
        typingPlaceholderPos = -1;
        chatSend.setEnabled(true);
        chatInput.setEnabled(true);
        if (succeeded) {
            completedHistory.add("User: " + userText);
            completedHistory.add("Assistant: " + reply);
            while (completedHistory.size() > 6) completedHistory.remove(0);
        } else {
            chatInput.setText(userText);
            chatInput.setSelection(userText.length());
        }
        chatRecycler.scrollToPosition(messages.size() - 1);
    }

    private void appendMessageSafe(ChatMsg msg) {
        messages.add(msg);
        if (chatAdapter != null) {
            chatAdapter.notifyItemInserted(messages.size() - 1);
            if (chatRecycler != null) chatRecycler.scrollToPosition(messages.size() - 1);
        }
    }

    // Simple chat list implementation (labels switched to English)
    private static class ChatMsg {
        final String text;
        final boolean isUser;
        ChatMsg(String text, boolean isUser) { this.text = text; this.isUser = isUser; }
    }

    private static class ChatAdapter extends RecyclerView.Adapter<ChatViewHolder> {
        private final ArrayList<ChatMsg> data;
        ChatAdapter(ArrayList<ChatMsg> data) { this.data = data; setHasStableIds(true); }
        @Override public long getItemId(int position) { return position; }
        @Override public int getItemCount() { return data.size(); }

        @NonNull
        @Override
        public ChatViewHolder onCreateViewHolder(@NonNull android.view.ViewGroup parent, int viewType) {
            View v = android.view.LayoutInflater.from(parent.getContext())
                    .inflate(android.R.layout.simple_list_item_1, parent, false);
            return new ChatViewHolder(v);
        }
        @Override
        public void onBindViewHolder(@NonNull ChatViewHolder holder, int position) {
            ChatMsg msg = data.get(position);
            TextView tv = holder.itemView.findViewById(android.R.id.text1);
            tv.setText((msg.isUser ? "Me: " : "Assistant: ") + msg.text);
        }
    }
    private static class ChatViewHolder extends RecyclerView.ViewHolder {
        public ChatViewHolder(@NonNull View itemView) { super(itemView); }
    }

    // ====== 原有生命周期 ======
    @Override
    protected void onStart() {
        super.onStart();
        Log.d(Tag, "onStart: ");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(Tag, "onResume: ");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(Tag, "onPause: ");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(Tag, "onStop: ");
    }

    @Override
    protected void onDestroy() {
        if (requestTimeout != null) mainHandler.removeCallbacks(requestTimeout);
        ListenableFuture<GenerateContentResponse> request = pendingRequest;
        pendingRequest = null;
        if (request != null) request.cancel(true);
        super.onDestroy();
        Log.d(Tag, "onDestroy: ");
    }
}
