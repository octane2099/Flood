# 源码与素材来源核对

本文件记录可从现有文件确认的来源信息及待核对项，不对整个项目作原创或授权完备的声明。

> [!WARNING]
> 本副本使用独立 Git 仓库，没有继承旧历史，也未配置远端或上传。
> 源码和素材来源仍需保留、核对；重新初始化 Git 不改变作者归属。查看方法见
> [PRIVACY_REVIEW.md](PRIVACY_REVIEW.md#在哪里查看提交历史和远端)。

## 已处理

- Java 注释中的个人协作标记已移除，原有功能说明保留。
- README 的安装命令已改为当前使用者自己的仓库占位地址，避免克隆到旧仓库。
  原 README 引用了 <https://github.com/malinyang123/111111.git>；此处保留该来源线索，
  尚未验证它与当前全部源码的关系，也未据此推断许可证。
- `nav_header.xml` 中旧课程名称与学校邮箱占位文字已替换为应用名称及功能说明。
- `app/src/main/res/drawable/headerbkg.png` 的嵌入元数据包含第三方作者
  Iqra Shaikh、Canva 工具信息及广告/素材追踪标识。该 PNG 已移出公开源码，
  由新建的简单 XML 渐变背景 `headerbkg.xml` 替代。
  原图及原有元数据保存在原项目的本机备份 `.private/privacy-backup/` 中，未复制到本副本。

## 保留的第三方与协作信息

- `gradlew`、`gradlew.bat` 是 Gradle Wrapper 脚本，其现有版权与 Apache 2.0 声明保留。
  `gradle/wrapper/gradle-wrapper.jar` 是构建所需的第三方组件。
- Firebase、Google Maps、Open-Meteo、AndroidX、Material、OkHttp、Guava 等为第三方依赖。
  具体依赖及版本以 `app/build.gradle.kts` 和 `gradle/libs.versions.toml` 为准。

## 待核对

- `drawable/`、`mipmap-*/` 和 `ic_launcher-playstore.png` 中剩余图片的来源记录不完整。
  特别是 `aklogo.png`、`healthicons.png`、`carbon_flood_warning.png`、`mdi_call.png`、
  `ion_search.png` 及 `baseline_*` / `ic_baseline_*` 图标，需要核对原始下载来源和使用条件。
  文件名只是核对线索，不能据此确认作者、许可证或是否原创。
- `image*.png`、`group*.png` 等导出图片也缺少来源说明，本次未删除，以免破坏现有界面。
- 生存提示中的示例文本应核实来源并校对内容；现有英文资源还包含与洪水无关的示例。
- 仓库尚无覆盖整个项目的明确 LICENSE 文件，本次未擅自为他人的代码或素材添加新许可证。

后续确认来源后，应补充对应出处及必要声明；确定不用的素材可替换或删除。
