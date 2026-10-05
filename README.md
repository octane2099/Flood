# Flood Help

Flood Help 是一个面向洪水信息与应急辅助场景的 Android 团队项目，包含地图定位、联系人、求助入口、生存提示、设置和 AI 问答页面。

本仓库基于团队项目整理，个人主要负责 Gemini 接口接入与 AI 聊天前端，包括消息展示、上下文组织、异步请求、加载状态及异常处理。其他页面和既有基础功能来自团队协作，不作个人独立原创声明。

## 技术栈

- Java、Android XML 布局、Material Components
- Firebase AI SDK、Gemini、Firebase Realtime Database
- Google Maps、Google Sign-In、Open-Meteo、OkHttp
- Gradle Wrapper、JUnit、Espresso

依赖及版本以 `app/build.gradle.kts` 和 `gradle/libs.versions.toml` 为准。

## AI 模块

首页可以进入 `AlertNotification` 聊天页面。客户端通过 Firebase AI SDK 创建生成模型，当前代码使用 `gemini-2.5-flash`，并通过 Java Futures 异步获取回答。

- `AlertNotification.java`：模型初始化、消息列表、发送状态、45 秒请求超时和错误处理。
- `AlertChatPrompt.java`：组织用户问题、最近的对话记录以及有效的降雨、位置和时间上下文；缺少告警数据时按普通问答处理。
- `activity_alert_notification.xml`、`item_chat_ai.xml`、`item_chat_user.xml`：聊天页面和双方消息布局。

以上 Java 文件位于 `app/src/main/java/com/example/myapplication6/`，布局位于 `app/src/main/res/layout/`。

## 本地运行

### 环境

- Android Studio，Gradle 使用 JDK 17
- Android SDK 34
- Android 7.0 / API 24 或以上的设备或模拟器

### 配置

1. 克隆项目并使用 Android Studio 打开根目录：

   ```bash
   git clone https://github.com/octane2099/Flood.git
   cd Flood
   ```

2. 让 Android Studio 生成 `local.properties`，或参考 `local.properties.example` 设置本机 Android SDK 路径。

3. 在自己的 Firebase 项目中注册 Android 应用，包名为 `com.example.myapplication6`，下载配置文件并放到 `app/google-services.json`。仓库中的 `.example` 只是格式示例，不能代替真实配置。按使用的功能配置 Realtime Database 及访问规则。

4. 将根目录的 `secrets.properties.example` 复制为 `secrets.properties`，填写自己的 `MAPS_API_KEY`。使用 Google 登录时填写 `GOOGLE_WEB_CLIENT_ID`，并为自己的应用配置 OAuth 和签名证书信息。同名环境变量优先于该文件。

5. 在自己的 Firebase 项目中启用 AI 服务并确认代码所选模型可用。当前代码尚未完成 App Check 接入；需要结合自己的 Firebase 控制台设置补齐客户端配置，才能验证相应云端访问。

6. 同步 Gradle，然后构建并运行测试：

   ```powershell
   .\gradlew.bat :app:assembleDebug :app:testDebugUnitTest
   ```

   macOS / Linux 使用 `./gradlew`。连接设备后可执行 `:app:connectedDebugAndroidTest` 运行仪器测试。

本仓库不包含真实服务配置，不能保证直接克隆后即可构建或访问云端。AI、地图和登录等功能需要网络及对应服务配置。AI 回答和示例提示仅用于项目演示，不应替代官方预警或紧急服务。

## 配置隐私

真实服务配置、SDK 路径、签名文件、IDE 设置、构建产物和私有备份已加入 `.gitignore`。仅提交占位模板，不要将真实 Key 写入 Java、XML 或 README，也不要上传含个人配置的 APK。发布前检查暂存文件；忽略规则不会自动清除已经提交的内容。

## 项目来源与第三方说明

- 项目原有 README 引用了 [原团队仓库](https://github.com/malinyang123/111111)。本仓库从整理后的源码快照建立独立提交历史，保留团队项目来源说明；独立历史不改变原作者归属。
- Gradle Wrapper 的版权及 Apache 2.0 声明保留在对应脚本中。Firebase、Google Maps、AndroidX、Material、Open-Meteo、OkHttp 和 Guava 等属于第三方依赖。
- 部分图片和图标仍缺少完整来源记录，包括 `aklogo.png`、`healthicons.png`、`carbon_flood_warning.png`、`mdi_call.png`、`ion_search.png`、`baseline_*`、`ic_baseline_*`、`image*.png`、`group*.png` 和应用启动图标。文件名不能证明作者或许可，复用前需要进一步核对来源与使用条件。
- 生存提示中的部分示例文本仍需校对和核实来源。
- 本仓库暂未指定覆盖整个项目的统一开源许可证；第三方代码和素材遵循各自的许可条件。
