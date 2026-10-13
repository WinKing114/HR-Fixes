# Current optional loading — 1.1.1-rc.1

Only Minecraft and Forge are loading dependencies. Third-party targets are optional; audited versions and original signatures control patch activation. See `RELEASE-1.1.1-rc.1.md` for current native load cases and limits. Minimal dedicated-server loading is tested; full Hollow Realm dedicated-server gameplay is NOT_TESTED.

---

The notes below describe prior target audits and remain historical; installation constraints from older releases do not apply to 1.1.1-rc.1.

# HR Fixes 1.1.0-rc.1 · 本轮兼容性维护

保留已经验证的 Eternal Starlight 附件修复；其注入类字节码与私有 1.0.0 完全一致。新代码只处理本轮日志确认的三处调用，不修改第三方 JAR、结构规则、交易表、装备权重或正式存档。

## Structurify / Collections Of Optimizations

原 `latest.log` 有 275 次 `MixinStructureOverlapCheck` 注入失败。CoO 4.6 的回调接受 `StructureStart`，而 Structurify 2.0.42 的原方法还要求一个 `int` 参数。

实际配置的 `structurify.leanOverlapSections` 已经为 false。字节码表明开关检查位于回调内部；Mixin 必须先校验签名，因此该开关不能解决加载失败。CoO 的插件也未按此配置跳过注入。公开项目提供的仍是 4.6，未找到可直接替换的已验证修复版本。[CoO 项目](https://www.curseforge.com/minecraft/mc-mods/collections-of-optimizations)。

使用 Forge 内置 coremod 在 CoO 的 `shouldApplyMixin` 入口拒绝这一条完整名称匹配的 Mixin，其余调用继续执行原插件。保留 Structurify 原生重叠检查和全部原配置。没有替换结构算法，也没有修改目标 JAR 文件。精确的可选版本依赖限制适用范围；目标缺席时没有待转换类。

## Goety 世界生成随机源

原日志分别发生在 `FastChunkGen worker #2` 和 `#5`，访问了属于 `Server thread` 的世界共享随机源。FastChunkGen 检测到越线程访问并记录错误；这不是持续性能瓶颈的独立证据，也不能据此声称所有交易/装备原先都生成失败。

- Goety Awaken：`SorcererServant.generateOffers()` 把 `Level.random` 传给 `SorcererTradeManager.generateOffersForLevel`；结构模板加载实体 NBT 时会经过这个入口。
- Goety Ominous：`Zealot.populateDefaultEquipmentSlots(DifficultyInstance)` 第一处武器抽取直接读取 `Level.random`；其余装备抽取已经使用实体随机源。

只重定向上述两个方法内各一处字段读取。服务器主线程仍返回原世界随机源对象，不额外消耗随机数；后台线程返回该实体自己的随机源。没有服务器的客户端路径保持原值。保留交易筛选、数量、权重、装备分支、掉落率、NBT 读写和现有实体数据。后台首次生成的具体结果可能变化，不能承诺与错误路径的随机序列逐项一致。

这些目标没有已发现的线程配置开关。已安装 Goety Ominous 本身为公开的 3.0.0.fix 构建，未确认存在可直接替换的上游修复。没有为避免错误而关闭 FastChunkGen 检测或并行生成。

## 137104 ms 落后与采样限制

下载并用实际安装的 spark 1.10.53 自带协议解析 [JqMr0SV6Dk](https://spark.lucko.me/JqMr0SV6Dk)。持续 433140 ms，但仅包含 Render thread，没有服务器或生成线程堆栈。

日志时钟 21:24:05、21:25:05 的统计请求超时，21:25:30 记录 137104 ms 调度落后，紧接着报告 `#alexscaves:gingerbread_men_wander_through` 结构定位失败。其他已记录的成功定位花 0–86 ms。长定位是强线索；统计超时说明服务器无法及时执行请求，不证明统计逻辑是根因。调度落后不能直接等同于一个游戏 Tick 的实测 MSPT。

该采样不能区分结构搜索中的计算、区块等待及反复 Mixin 失败各花多少时间，本轮不添加结构搜索、生成半径或通用性能补丁。不能把修复 CoO 错误后的短期正常运行宣称为解决了这次停顿。若仍出现同类问题，需要覆盖该段的服务器线程采样，最好包含生成线程，保存到本地即可。

## 验证、安装与回滚

本轮结果见 `evidence/compat-20261010.json`。实际隔离客户端验证 Structurify 类正常加载、目标注入被禁用、无关插件门控保留。原两个初始化方法在主线程及真实后台线程执行：主线程使用原随机源，后台使用实体随机源，交易正常生成，加载已有交易 NBT 后内容完全一致，装备正常生成。原越线程错误未再出现。Eternal Starlight 同步通过真实网络更新和删除回归。

这是针对性实体初始化验证，没有复现正式世界的整段结构搜索/放置，没有覆盖长期运行或专用服务端。早期辅助模组入口和测试方法参数错误不作为补丁验收证据。Forge 自带 biome coremod 报错在原日志和测试中均有，未作为本轮补丁引入的问题处理。

客户端继续需要 Eternal Starlight 修复。新增生成修复运行于逻辑服务器；单人客户端 JAR 同时覆盖内置服务器，专用服务端尚未验证。新目标都是可选依赖：存在时要求 CoO 4.6、Structurify 2.0.42、Goety Awaken 1.3.9.4、Goety Ominous 3.0.0.fix-1.20.1；缺席时不要求安装。`@Pseudo` 避免缺失 Goety 目标时强制加载。二进制指纹在 `project.json`，加载器只检查版本。

只安装一个 `hollowrealm_performance_compat` 模组 ID 的 JAR。关闭游戏后替换旧版本，不能并存。正式实例现已安装 `HR Fixes-1.1.0-rc.1.jar`，原私有 1.0.0 已备份，索引见本地 `compat-deployment.json`；下一次完整重启生效，未运行正式存档验收。全部 264 个第三方 JAR 和相关配置指纹保持不变。

回滚时关闭游戏，按索引只移除 `new_file`，把 `backup` 复制回 `old_file`，原附件修复继续存在。源代码发布包不包含本机部署索引、测试辅助模组、日志或存档。其他资源、战利品、结构池和实体属性警告本轮未逐项验证，不把仍能运行视为它们都没有影响。CurseForge 上传和审核仍需所有者确认；本轮不创建 GitHub Issue。
