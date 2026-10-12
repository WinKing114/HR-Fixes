# HR Fixes

Targeted compatibility and bug fixes for the Hollow Realm modpack.

Current candidate: **1.1.0**, Minecraft **1.20.1**, Forge **47.4.16**. This is an independent third-party project, not an official Eternal Starlight release.

The JAR retains the attachment, Structurify/CoO and Goety fixes, and adds the Quest Enhance empty-panel guard and Apotheosis/JEI socketing display fix. Target versions are audited and explicitly bounded in `META-INF/mods.toml`. Dedicated servers are NOT_TESTED.

Replace the previous HR Fixes JAR while the game is closed; install only one `hollowrealm_performance_compat` mod ID. The accompanying intelligent-gem repair is a separate modpack data file under `overrides/kubejs/data/` and is not bundled in the JAR. Its existing numeric values are retained.

[Build](BUILD.md) · [Changelog](CHANGELOG.md) · [Validation and delivery report](RELEASE-1.1.0.md) · [CurseForge upload information](CURSEFORGE.md)

Source and build outputs have matching versions. This release does not change quest identities, progress/rewards, loot rarity weights, saved-world formats, or structure-generation rules. It does not claim measured FPS/TPS/memory improvements.

---

## Historical private 1.0.0 performance investigation (2026-10-10)

The following environment, scripts and deployment paths describe the original investigation only. Current installation, validation and rollback information is in the delivery report above.

适用实测环境：Minecraft 1.20.1 / Forge 47.4.16，264 个已安装 JAR，EternalStarlight-0.9.0-hotfix.jar。只新增自有兼容 JAR，不修改第三方 JAR、存档或玩法配置。

## 诊断与处理

用户提供的 `wy9sfuPghi.sparkprofile` 为客户端 Java 全线程、4 ms 间隔、约 149.7 秒的采样。时间包括等待，不等同于有效 CPU 时间。

- 服务端最近一分钟约 20 TPS、平均 MSPT 2.25 ms、P95 4.32 ms；没有持续性服务端瓶颈证据。
- `SyncAttachmentsPacket.handle` 在四个时间窗口都有空指针路径，总计 4052 ms，占渲染线程采样约 2.71%。安装包第 57 行为 `player.level()`，Forge 客户端接收路径却传入 `Context.getSender()` 的空值。异步结果被丢弃，解释了日志不刷屏而采样仍有异常热点。不是未知附件告警，现有配置不能修复。
- 兼容补丁仅在客户端空玩家入口补用当前本地玩家，再运行原附件解析和同步方法。不存在本地玩家时安全返回；非空参数保留原处理。专用服务器不加载此客户端注入。
- FWA 灯笼渲染在原报告中占渲染线程约 2.10%，最后一个时间窗口约 6.91%。正式实例在这份采样之后已经设为 `lantern_state="false"`，实机确认该值被正确读取、门动画仍开启。本次保留现状，未再改配置。已保存当前配置副本供后续发布使用。
- Xaero 地图写入约占渲染线程采样 2.20%；地图处理总计约 2.99%，不能叠加这些包含关系。另有已优化的等待。缺少帧时间尖峰归因证据，未降低更新频率或地图质量。
- Gnetum 包含 HUD 渲染调用不能说明它造成额外开销；区块后台等待不能按 CPU 热点处理，均未修改。

## 实际验证

基线隔离实例真实复现空玩家收包异常，附件值未更新。修复实例使用同一 264-JAR 基线、相同配置，加一个兼容 JAR；离线测试身份、新建平坦世界，没有复制正式存档或账号。

修复后：空玩家收包正常更新附件；正常玩家收包正常；没有本地玩家时不抛异常。完整退出并再次启动后的同项验证也通过。约 34.15 秒的全线程新采样中，渲染线程没有空指针或灯笼动画节点。静态灯笼画面已检查。

FWA 使用原生 FrameTimer 进行了同场景开关观察，但“开启”侧的约 29.25 秒采样没有出现灯笼动画节点，说明该进程内开关试验没有建立可信的动画负载对照。保留原始数据并标为 INCONCLUSIVE，不据帧时间差宣称收益。早期渲染事件计数与零线程采样均不作为性能证据。

这证明附件错误路径已修复，不证明正式存档整体 FPS 的提升幅度，也不替代完整 Eternal Starlight Boss 战斗、维度或专用服务器验收。正式实例下次启动加载补丁。

## 构建、验证与回滚

- 源码：`src/`；当前候选构建：项目根目录执行 `python .code/performance/build_patch.py`，输出到 `release/`。只需本地 Java 17、Forge/Minecraft/Mixin 编译库及已审查的 Eternal Starlight JAR；不读取启动器账号、不依赖完整整合包清单、不下载或修改第三方 JAR。
- 验证：`python quality/2026-10-10-performance/verify.py`，读取此次真实隔离运行记录。`evidence/` 保存简要诊断和验收数据，完整运行资料位于 `.runtime/performance-20261010/`。
- 部署：`python quality/2026-10-10-performance/apply.py apply`，要求运行验收通过且正式实例的基线 JAR 和相关配置未变化。只安装 `mods/hollowrealm-performance-compat-1.0.0.jar`。
- 回滚：旧 `apply.py rollback` 按部署时的原文件名校验。当前正式实例中该同哈希私有文件已命名为 `HR Fixes-1.0.0.jar`；关闭游戏后可只移除此自有 JAR，或先恢复原文件名再使用旧脚本。备份索引见 `deployment.json`；对应 `.runtime/backups/` 目录是明确的回滚资料，需保留。

没有重建 Beta RC1、刷新旧模组锁、修改任务、升级或移除模组。后续发布整合此补丁时使用本层源码及已验证产物；Eternal Starlight 更新后需重新核对收包路径，不能直接沿用验收结论。
