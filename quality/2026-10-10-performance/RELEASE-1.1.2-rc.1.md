# HR-FIXES-1.1.2-001 — 1.1.2-rc.1

状态：**NOT_READY**。真实的安全门控修复、候选构建和下述测试已完成；未发布、未部署正式实例。继续保留指定稳定基线。

## 缺陷、根因与证据边界

基线 `54060e5fbd26852306a241208fec814e4a8da356` 从 GitHub 按完整提交 ID 取回。取回时远端 `main` 为 `c65b060`，早于指定基线。基线独立重建 SHA-256 为 `0b272bbf80fcd5a0ac99ba90f156752cb362bc5ce07432531ad0829b32bedd8f`，与保留的 1.1.1-rc.1 JAR 逐字节一致。

旧门控仅确认模组版本和方法签名，读取时使用 `SKIP_CODE`。两个 Redirect 却要求恰好一次指定字段读取（`require=1, allow=1`）。同版本号的目标保留方法但缺失或重复该指令时，旧门控仍 APPLY，违反自身“未知实现安全跳过”的契约。

真实安装二进制审计：

| 目标 | 方法 / 描述符 | 原始匹配指令 |
| --- | --- | --- |
| Goety Awaken 1.3.9.4 `SorcererServant` | `generateOffers()V` | 偏移 34，恰好 1 次 |
| Goety Ominous 3.0.0.fix-1.20.1 `Zealot` | `populateDefaultEquipmentSlots(Lnet/minecraft/world/DifficultyInstance;)V` | 偏移 4，恰好 1 次 |

两处均为 opcode 180，owner `net/minecraft/world/level/Level`，name `f_46441_`，descriptor `Lnet/minecraft/util/RandomSource;`。JAR / class 哈希及原始方法反汇编在 `evidence/1.1.2-rc.1/raw-targets.json`。**原始已审计 JAR 的位置合法，本轮未证明它们在旧版下存在该注入崩溃；修复的是可复现的加载保护缺口。**

原生 Forge 47.4.16 / Java 21 对照使用完全自编写的目标夹具，没有改写第三方 JAR：保持已审计版本号及方法签名，分别提供 0 次和 2 次字段读取。稳定版均 APPLY，分别产生 `InjectionError: (0/1) succeeded` 和 `2 succeeded of 1 allowed`；候选版均 SKIP，成功启动世界并正常保存退出。这是原生 Forge 下的合成目标复现，不冒充实际 Goety JAR 的旧版故障。重复目标对照连 QA/目标夹具 JAR 哈希也完全相同；缺失目标对照的类条目相同，ZIP 时间戳不同。

## 最小修复与审计

仅修改生产类 `HRFixesMixinPlugin.java`：两个 Goety 分支改为检查原始字节码；目标方法必须唯一，匹配字段读取必须恰好一次，读取或解析失败则跳过。继续通过 `MixinService.getResourceAsStream` 获取原始资源，不调用转换字节码提供器、不主动加载目标类。其余四个 Mixin 分支、模组版本白名单和 CoreMod 门控保留原有行为。

相对稳定 JAR，只有 `HRFixesMixinPlugin.class` 与内置版本 `META-INF/mods.toml` 不同；六个 Mixin、两个辅助实现、CoreMod JS、Mixin 配置逐字节一致。客户端四项仍只在 Mixin 配置的 `client` 列表；公共列表仍只有两个 Goety Mixin。强制加载依赖仍只有 Minecraft / Forge。CoO 4.8 × Structurify 2.0.43 未纳入白名单或处理范围。

版本由 `project.json` 统一提供；`mods.toml` 源模板保留 `@VERSION@`，构建替换后内置版本为 `1.1.2-rc.1`。Java 17 构建流程未替换。旧有本地名为 1.1.2 的候选内部为 `1.1.1`，SHA-256 `dae171ed2b3b05ba6e0fd4b6a0c6800c189d0066f675f88f5325bccaf71abbca`；没有覆盖它或把它作为新版证据。其 ZIP 仅装有同一 JAR，并非源码包。已检查的本地候选和既有 HR Fixes 目录未找到配套 WIP 源码/合成测试文件，本轮重新建立对照。

## 本轮验证

| 项目 | 结果与实际范围 |
| --- | --- |
| 原始目标字节码 | PASS；两个哈希锁定 JAR，各 1 次准确匹配 |
| 自动化门控对照 | PASS；70 项相同检查，基线 18 项预期失败，候选 70/70。真实类字节与内存变体；发现列表/资源访问为测试桩 |
| 原生 Forge 异常位置对照 | PASS；自编写缺失/重复目标，基线真实注入失败，候选安全跳过 |
| Forge 47.4.22 / Java 21 客户端 | PASS；隔离最小依赖组合，六项针对性回归、世界进入和正常保存 |
| Forge 47.4.16 / Java 21 客户端 | PASS；相同六项针对性回归、世界进入和正常保存 |
| 最小 Forge 47.4.16 专服 | PASS；无可选目标启动、保存、退出；真实 Awaken 单独组合的主/后台交易、退出重启和交易 NBT 恢复也通过 |
| 可选依赖缺失 / 未知版本 | PASS；原生无目标客户端/专服，原生自编写未知版本夹具；70 项测试另覆盖缺失发现列表与各白名单门控 |
| Goety 两项 Redirect | PASS（客户端）；真实合并方法、主线程交易/装备和后台线程交易/装备。专服见阻塞及独立 Awaken 结果 |
| 原有六项回归 | PASS（针对性 smoke）；两个 Goety 路径、附件空 sender/显式玩家/无本地玩家直接处理、Quest Enhance 空面板刷新与尺寸、JEI 实际 socketing 布局三项非空及 focus-link 注入合并 |
| CoO 已审计门控 | PASS；20 项白名单组合检查；原生专服 CoO 6.5 × Structurify 2.0.43 只拒绝 overlap 门控、保留无关门控 |
| 世界保存、重进与相关 NBT | PASS（限定范围）；47.4.22 Goety 隔离世界正常保存、全新 JVM 重进，原生 `NbtIo` 文件恢复的交易 NBT 语义一致；不是自然生成实体的区块持久化证明 |
| 独立源码重建 / SHA-256 | 见 `release/provenance-1.1.2-rc.1.json`；交付前要求完整 JAR 字节一致、解压源码门控测试通过、全部 class major=61 |

客户端回归只调用已加载原生 API；没有恢复或编辑正式任务，没有领取正奖励，没有拷贝个人存档。附件测试直接调用处理器，没有声称已测试实际网络传输。JEI 验证了当前布局构建及数据，不把 handler 合并等同于所有焦点交互均验收。

## 阻塞与 NOT_TESTED

实际 `goetyominous-3.0.0.fix-1.20.1.jar` 在专服自动订阅器注册时加载 `net.minecraft.client.player.LocalPlayer`，被 Forge `RuntimeDistCleaner` 拒绝；稳定版与候选版在相同目标依赖组合均失败，尚未进入世界。此组合的失败不能记为 HR Fixes 修复成功，也不能通过改写该第三方 JAR 绕过。旧新对照及独立 Awaken 运行结果在 `evidence/1.1.2-rc.1/native-results.json`。

**NOT_TESTED / NEEDS_RUNTIME_TEST：** Zealot 的实际专服注入后玩法、完整整合包客户端/专服、多人、长期稳定、自然生成实体的区块保存与重载、真实附件网络收发、任务正奖励/持久描述回包等完整 UI 路径、原生锻造消耗与各稀有度焦点轮换。其他已审计 CoO 组合本轮仅有门控自动化与字节一致性证据，不冒充原生组合实测。Forge 47.4.22 专服未准备和运行。

原始字节校验只能检查资源中的实现，不能保证后续其他转换器不会再改变目标；Mixin 的严格注入约束继续保留。本轮不降低 `require`、不吞掉后续 Mixin 错误，不声称支持任意同版本重打包。

因此维持 **NOT_READY**，候选仅供审查/继续验收。关闭专服目标缺口需要一个不触发上述客户端类加载错误的合法第三方运行组合，再补真实目标专服测试；不在本任务中修改第三方模组。其他完整行为缺项也需补测后再作发布决定。

## 交付与回滚

候选 JAR：`release/hr-fixes-1.1.2-rc.1.jar`。独立源码包：`release/hr-fixes-1.1.2-rc.1-sources.zip`。SHA-256 与重建结果：`release/provenance-1.1.2-rc.1.json`。源码 diff 和稳定版回滚 JAR 随外层交付目录保留。

正式实例没有部署本候选。将来如在测试实例安装，关闭游戏/服务器后只保留一个 `hollowrealm_performance_compat` JAR；回滚时移除该候选并恢复保留的 `hr-fixes-1.1.1-rc.1.jar`，校验上述稳定版 SHA-256。不要据文件名误选现有内部版本为 1.1.1 的同名候选。无需修改世界、实体 NBT、KubeJS 或其他模组文件。CurseForge 由所有者负责；本轮 NOT_SUBMITTED，未推送或发布远端版本。
