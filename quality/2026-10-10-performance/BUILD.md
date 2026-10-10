# HR Fixes 构建与维护

保留原工程的 `src/` 和 Python/javac 构建方式，不需要整个 Hollow Realm 实例或启动器 JSON。

要求：Python 3、JDK 17，以及本地 Minecraft 1.20.1 SRG 客户端库、Forge 47.4.16 客户端库、JavaFML 47.4.16、Mixin 0.8.5 和目标 `EternalStarlight-0.9.0-hotfix.jar`。这些依赖只用于编译，不放入源码包或最终 JAR。库目录布局采用 Minecraft 启动器的 Maven 路径；构建器缺失依赖时明确失败，不自动下载。

在源码包根目录运行：

```powershell
python .code/performance/build_patch.py --libraries "C:/path/to/.minecraft/libraries" --eternal-starlight "C:/path/to/EternalStarlight-0.9.0-hotfix.jar" --javac "C:/path/to/jdk-17/bin/javac.exe"
```

当前工作区可直接使用默认路径运行。可用 `--output-dir` 更改产物位置。默认产物位于 `quality/2026-10-10-performance/release/`。

`project.json` 的 `version` 为产物文件名和模组元数据版本的共同来源。发布资源位于 `resources/`；许可证全文随 JAR 一起打包。构建使用 Java 17 字节码、固定 ZIP 时间和排序，相同源码及依赖应产生相同哈希。

编译前校验目标 JAR 的已审查 SHA-256。该检查不能替代运行验收，也不代表游戏加载器会检查二进制哈希。目标更新后先审查 Forge 收包路径与 `handle(Player)` 语义，再决定是否修改版本边界；不要只改依赖版本让旧注入强行通过。

历史私有 1.0.0 文件保留在 `overrides/mods/`，对应旧 `apply.py`、`verify.py`、`deployment.json`。当前候选不覆盖旧产物、不重写旧验收记录，也未部署到正式实例。不得同时安装这两个相同模组 ID 的 JAR。

局部运行验收辅助脚本位于工作区 `.code/performance/review_*.js` 和 `review_runtime.py`，仅用于隔离实例。源码发布包不包含这些诊断脚本、日志、存档或依赖。完整重启、附件接收和客户端生命周期验证结果见 `AUDIT.md` 与 `evidence/hr-fixes-review.json`。
