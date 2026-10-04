# 隐私清理与独立仓库说明

本副本于 2026-10-05 从脱敏工作目录源码创建，使用独立 Git 仓库。
首次提交是源码快照，没有继承旧提交，没有配置远端或上游分支，也没有上传。
原项目的文件、历史和远端设置均保留在原目录中。

## 已排除的信息

- 原 `.git/` 和含历史 Key 的旧提交。
- 真实 `secrets.properties`、`app/google-services.json` 和 `local.properties`。
- IDE 设置、构建缓存、APK、签名文件、私有备份和旧导出包。
- 本机 Git 身份中的私人邮箱。新仓库首次提交使用通用署名
  `FloodHelp contributors <snapshot@example.invalid>`；邮箱仅为占位值，
  不代表 GitHub 账号，也不代表全部代码由提交者原创。

## 服务配置与运行

源码保留 `.example` 配置模板。运行前请按 README 配置自己的 Android SDK、
Firebase 项目、Google Maps Key 和登录所需的 OAuth Client ID。
Maps Key 通过 Manifest 占位符注入，OAuth Client ID 通过构建配置生成资源；
本地真实配置受 `.gitignore` 忽略。

此次复制不修改应用代码。副本未携带运行所需的真实服务配置，因此此次源码检查
不代表已完成副本的构建或云端功能验证。云端服务和访问权限仍需按自己的项目配置。
不要将使用真实配置构建的 APK 当作脱敏源码分享。

## 在哪里查看提交历史和远端

在 Android Studio 打开本副本，进入 **View → Tool Windows → Git → Log** 查看历史，
进入 **Git → Manage Remotes** 查看远端。首次创建时应只有一个提交且远端列表为空。

在本副本根目录也可以运行：

```powershell
 git log --oneline --decorate --graph --all
 git rev-list --count HEAD
 git remote -v
 git branch -vv
```

首次创建时提交数量为 `1`，`git remote -v` 没有输出。原团队仓库网址作为来源说明
保留在文档中，不是本副本的 Git 远端配置。

## 检查范围与来源说明

检查公开候选文件和首次提交中的常见 Key、OAuth Client ID、私钥标记、个人路径，
并与原项目本地配置中的真实值比对。此检查不能替代后续变更的审查。
新建仓库不会撤销旧仓库已经公开的信息，也不会改变代码和素材的来源。
第三方声明和未确认的素材来源继续保留，详见 [SOURCE_REVIEW.md](SOURCE_REVIEW.md)。
