# HR Fixes 1.1.0 交付与验收

本轮定位依据为 2026-10-11 20:08 会话归档、后续实际日志、安装 JAR 的数据和字节码。最终环境按作者更新后的 **268 个正式 JAR** 重建隔离副本：CoO 6.5、FastChunkGen 0.33 和新任务拓展均包含在内；早期 267 环境证据只作补充。没有打开正式个人存档。

## 问题与处理

| 项目 | 根因、处理与状态 |
| --- | --- |
| FTB Quests × Quest Enhance | 关闭动画完成后 `setViewedQuest(null)` 刷新面板。FTB 本来重置面板尺寸后返回，Quest Enhance 的 HEAD 注入却先解引用 `quest`。客户端局部注入保留这个空面板分支；有效任务继续走原方法，不捕获异常、不关闭 Quest Enhance。 |
| intelligent 宝石 | 实际最高优先级定义来自 Arcane Convergence。`curios:spellbook` 未注册，`ars_nouveau:max_mana` 也不是当前属性 ID。单个 KubeJS 数据覆盖改为 `ars_nouveau:ars_nouveau.perk.max_mana`，移除无效类别；保留四类盔甲、操作、权重、品质及全部数值。数据修正在整合包层，**不在 HR Fixes JAR 中**。 |
| JEI socketing | JEI 默认锻造展示用不带孔位的合成输入调用动态 `assemble`，导致对空宝石列表执行 `set(0,...)`。补上该配方的展示扩展，使用原生 matcher/assemble 生成有效示例并链接输入与结果；原始配方、材料消耗及真实装备逻辑没有改动。示例覆盖普通装备和已加载宝石/合法稀有度，不承诺列出所有模组装备组合。 |
| 15 条词缀不足警告 | 检查只统计主规则，忽略备用规则。当前天神剑技装备实际归类为 sword/heavy_weapon/bow，没有使用注册但空置的 `celestial_ranged` 类别。其他五条是 ancient STAT 数量检查；保留原有备用规则，不扩充属性池或改平衡。 |
| 六种远古 LootRule | 原生 ancient 权重为 0，现有数据没有 ANCIENT 专属词缀。JEI 强制生成所有稀有度示例因此报错；原生直接生成的六种钻石装备仍有普通词缀和孔位。**没有新增 ANCIENT 专属能力，也没有消除该诊断日志**。 |
| Forge biome coremod | Aquamirae 7.1.14 的访问转换把 `Biome.f_47437_` 设为 public-f，Forge 重定向要求 private，故失败。Aquamirae 的冰迷宫逻辑实际直接读写该字段；强行改回 private 或重写气候读取会影响其行为。根因已定位，本轮未修复，冰迷宫功能未单独验收。 |
| 其他资源/实体/结构警告 | 未证实适合本轮安全修复的功能损坏，未顺带修改。此前 137 秒停顿仍缺服务器线程采样，未宣称已解决。 |

## 实机证据

- 原生任务页面打开、切换、关闭、空面板刷新；编辑页面打开与原生确认；有效描述的服务器持久数据刷新；关闭后的延迟回包。
- 原生 Checkmark 完成及 S01 奖励领取：测试钱包从 0 到 16，重复领取、保存退出和重启后仍为 16；两个任务完成状态保留。
- 真实锻造菜单产生镶嵌结果，取出后原装备和宝石各消耗一个。装备保存退出、重启后 NBT 相同；实际穿戴测试的法力属性从 100 到 114，脱下恢复 100，符合原 rare 数值 14。
- JEI 中查看锻造配方、普通示例与 rare intelligent 宝石焦点，材料和结果正常显示。新代码没有通过吞错取得通过状态。
- CoO 6.5 门控只拒绝目标 Mixin，Structurify 类正常加载；Goety 主线程与后台线程初始化、交易 NBT 保留及装备生成通过。Eternal Starlight 真实网络更新 true/false 和删除通过。
- 测试辅助模组移除后，以当前 268 JAR 再次完整启动、进世界、保存退出。最终日志中的任务空指针、intelligent 解析失败及 socketing 配方展示错误均为 0。

静态检查与独立源码重建记录见 `evidence/compat-20261012.json`。原始日志、QA 世界及截图只保存在本机调查目录，没有纳入公开源码。

**边界：** 单人客户端及内置服务器验证；专用服务器、多人连接、长期游玩和正式个人旧存档为 **NOT_TESTED**。未把短时间不报错解释为长期稳定，也没有测量或宣传性能提升。

## 文件、发布与回滚

候选 JAR 为 `release/hr-fixes-1.1.0.jar`；文件名、内部版本和 `project.json` 一致。单独的数据修正为 `overrides/kubejs/data/irons_spellbooks/gems/intelligent.json`。构建方式见 `BUILD.md`；英文更新日志及 CurseForge 填写信息见 `CHANGELOG.md`、`CURSEFORGE.md`。

GitHub 使用原 HR-Fixes 仓库，源码版本和二进制哈希可追溯。CurseForge 按所有者最新要求由所有者上传，**本轮没有提交新文件或审核**；上传前需在作者页面确认该版本号尚未使用。

正式部署和备份的精确路径、哈希及状态另存本地 `release/deployment-1.1.0.json`。数据覆盖可单独撤销。旧 JAR 仅允许 CoO 4.6，**不能在当前 CoO 6.5 下直接回装**；保留原文件作为备份，不把它标记为当前环境已验证的回滚版本。若需回退代码，应从源码保留当前 CoO 边界并重新构建验证，不降级或修改第三方 JAR 来凑回滚。

发布候选状态：**READY_WITH_DOCUMENTED_LIMITS**。1.1.0 完整重启验收通过；从独立解压源码构建的 JAR 与候选文件逐字节一致。CurseForge 提交由所有者完成。
