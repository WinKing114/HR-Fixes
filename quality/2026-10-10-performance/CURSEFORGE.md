# HR Fixes · 发布资料

状态：发布候选，本候选未公开上传。所有者已提供现有项目 [1736729](https://authors.curseforge.com/#/projects/1736729/description)，后续资料用于该项目，不创建重复项目。作者后台未能通过当前工具读取，页面现有字段仍未核验。技术验收见 `evidence/hr-fixes-review.json`；头像由项目所有者另行提供，公开提交必须经所有者确认。

## 项目与文件字段

| 字段 | 内容 |
| --- | --- |
| Project name | HR Fixes |
| Summary | Targeted compatibility and bug fixes for the Hollow Realm modpack. |
| Game / Class | Minecraft / Mods |
| Main category | Bug Fixes（以提交页面实际可选项为准） |
| Loader / Game version | Forge / 1.20.1 |
| Tested and enforced Forge | 47.4.16 |
| Environment | Client；服务器不需要安装 |
| License | MIT，见 `LICENSE`，与 JAR 元数据一致 |
| File | `release/hr-fixes-1.0.1-rc.1.jar` |
| File display name | HR Fixes 1.0.1-rc.1 — Forge 1.20.1 |
| Release type | Beta（候选版本） |
| Required dependency | [Eternal Starlight Forge](https://www.curseforge.com/minecraft/mc-mods/eternal-starlight-forge)，项目 ID 1567133 |

CurseForge 的项目依赖不能充分表达同版本不同二进制文件的限制；在详细说明及更新日志明确要求 `EternalStarlight-0.9.0-hotfix.jar`。目标内部版本为 `0.9.0`，不是 `0.9.0-hotfix`。不要关联 Fabric/NeoForge 原版项目。

## 可直接使用的英文详细说明

**HR Fixes** provides targeted compatibility and bug fixes for the **Hollow Realm** modpack. Each fix addresses an issue observed during development or testing; this project does not provide a general compatibility framework.

### Current fix

Eternal Starlight Forge's attachment synchronization handler receives a null player reference for server-to-client packets. Its original handler then attempts to access the player's level, causing a NullPointerException before applying the attachment data.

HR Fixes supplies the current local player on the client main thread and lets Eternal Starlight perform its original entity lookup and attachment decoding. Packets received without a local player are ignored; they are not retained or replayed into a later world. Non-null calls retain their original behavior.

### Requirements and scope

- Minecraft **1.20.1**, **Forge 47.4.16**.
- **Eternal Starlight Forge: `EternalStarlight-0.9.0-hotfix.jar`**, internal mod version **0.9.0**.
- Install HR Fixes on the **client**, including single-player clients. Servers do not require it.
- This build targets the tested Hollow Realm environment. Other Eternal Starlight builds, Minecraft versions, loaders and patch combinations are not supported by this candidate.

The audited target JAR's SHA-256 is `6f3bfb62c887723c593973dfe325992d4886a3dbfd1a8a05d26829fc5aeb4096`. The loader checks mod versions, not this checksum; a different binary sharing version 0.9.0 still needs a new compatibility review.

### Installation and removal

Close Minecraft, then place the HR Fixes JAR in the instance's `mods` folder. Install the required Eternal Starlight Forge build separately. Fully restart Minecraft after adding, updating or removing the fix.

Keep **one** copy of HR Fixes. Replace the earlier private 1.0.0 JAR, originally named `hollowrealm-performance-compat-1.0.0.jar` (also distributed privately as `HR Fixes-1.0.0.jar`). They use the stable mod ID `hollowrealm_performance_compat` and must not be installed together.

To remove HR Fixes, close Minecraft and remove its JAR. It adds no saved-world content and performs no data migration. Removing it while using the affected Eternal Starlight build restores the original attachment synchronization failure.

### Attribution and limits

This is an **independent, third-party Hollow Realm fix**, not an official Eternal Starlight release. Eternal Starlight remains a separate dependency, credited to its original and Forge-port authors. Its code, textures, sounds and other assets are not bundled in HR Fixes.

This candidate addresses an attachment synchronization error. It makes **no measured FPS, TPS or memory improvement claim**. Validation covers specific synchronization and client lifecycle behavior, not every Eternal Starlight feature or long-term stability in every modpack.

## 文件更新日志

**1.0.1-rc.1**

- Publish the existing Hollow Realm attachment compatibility fix as **HR Fixes**.
- Restore Eternal Starlight Forge 0.9.0 hotfix's client attachment processing when the supplied player is null; retain the original decoding and non-null behavior.
- Preserve the stable mod ID `hollowrealm_performance_compat`.
- Restrict the candidate to Minecraft 1.20.1 and the tested Forge 47.4.16; require Eternal Starlight Forge 0.9.0 on clients.
- Include the MIT license and an independently rebuildable source project. No unrelated mod fixes or gameplay changes.

## 授权与发布边界

沿用原补丁已声明的 MIT，为自有代码补齐许可证全文，版权署名为 Hollow Realm contributors。最终公开时由所有者确认该署名与许可。JAR 只包含本项目两个类及元数据/许可证，不含 Eternal Starlight 或其他依赖的类与资源；不分发其反编译文本或依赖 JAR。目标项目页面区分代码许可和素材使用权限，本候选未使用这些素材。[目标项目说明](https://www.curseforge.com/minecraft/mc-mods/eternal-starlight-forge)，[MIT 正文](https://opensource.org/license/mit)。

不要上传 `overrides/` 整体、历史 1.0.0 JAR、性能报告、测试脚本、测试存档、日志或本机依赖。独立模组文件只上传上表候选 JAR。可另附 `release/hr-fixes-1.0.1-rc.1-sources.zip` 供源码维护。

头像设计交付约束：独立 **HR Fixes** 字样；黑白高对比、深色字面、白色描边与立体像素挤出，缩略图清晰；沿用 Hollow Realm 气质，不直接改写或复制参考原图，不使用 Eternal Starlight/Minecraft 官方标识。由所有者提供并确认权利。CurseForge 要求原创、1:1 PNG，至少 400×400；原 HOLLOW REALM FIXER 图仅作参考，不作为本项目头像上传。[官方头像要求](https://support.curseforge.com/support/solutions/articles/9000199552-project-submission-guide-and-tips)。

公开提交前：提供并核验上述头像、现有项目的描述和依赖字段；所有者审核本资料、候选文件、MIT 署名与验证限制并明确确认。源码地址：[GitHub](https://github.com/WinKing114/HR-Fixes)；问题追踪：[Issues](https://github.com/WinKing114/HR-Fixes/issues)。本次未执行文件上传、页面修改或提交审核。正式版本通过验证与平台审核后，再更新 Hollow Realm 分发清单使用对应 CurseForge 文件；本次未改整合包发布清单。
