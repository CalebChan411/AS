# Smart Delivery Fleet Management System (SDFMS)

本版以你上传的 **AS CLASS DIAGRAM.pdf** 为实现目标，补齐现有 Java prototype。
范围是完成当前 diagram 的 coding；尚未扩展下一轮 Command／Factory 设计。
应用只使用 Java 标准类库，不需要 Maven、第三方库、数据库或网络服务。

## 编译和运行

需要 **JDK 8 或以上版本**（包含 `javac`，只有 JRE 不够）。

Windows：双击 `run.bat`，或者在此目录执行：

```bat
javac *.java
java Main
```

执行原有样例输入：

```bat
java Main < sample_input.txt
```

直接在终端运行时显示菜单和输入提示；重定向输入时只显示操作结果。
例如在 IDE 中 `System.console()` 为 null 时，仍可输入命令，只是不显示提示。

## 命令和输入

命令后所需的每项输入各占一行。司机资料一行的格式为 `ID, Name`。

| 命令 | 功能 | 后续输入 | 可 undo/redo |
| --- | --- | --- | --- |
| `c` | 建立并选择 fleet | 类型 `v`/`r`、ID、名称 | 是 |
| `s` | 选择 current fleet | Fleet ID | 否 |
| `a` | 加入 driver | `ID, Name`、角色数字 | 是 |
| `m` | 更改 driver role | Driver ID、角色数字 | 是 |
| `d` | 删除 driver | Driver ID | 是 |
| `sf` | 显示 current fleet | 无 | 否 |
| `sa` | 显示所有 fleets | 无 | 否 |
| `cn` | 更改 current fleet 名称 | 新名称 | 是 |
| `u` | 撤销最近一项可撤销操作 | 无 | 本身不进入历史 |
| `r` | 重做最近撤销的操作 | 无 | 本身不进入历史 |
| `l` | 显示 undo/redo lists | 无 | 否 |
| `x` | 退出 | 无 | 否 |

角色沿用原有两个 fleet class 的定义：

| Fleet 类型 | Role 1 | Role 2 |
| --- | --- | --- |
| `v` Delivery Van Fleet | Van Driver | Courier Driver |
| `r` Refrigerated Truck Fleet | Refrigerated-Truck Driver | Cold-Chain Delivery Driver |

Fleet ID 不分大小写且不能重复。Driver ID 在同一 fleet 内不分大小写且不能重复；
不同 fleet 可各自拥有相同 ID 的 driver。ID 和名称前后空白会去除。

## 与当前 diagram 对应的实现

- **Command**：`Command` 定义 `execute/undo/redo/displayName/isUndoable`。
  `BaseCommand` 为非撤销命令提供默认行为；`UndoableCommand` 保存共用的
  `FleetManager`，五个变更命令继承它。另六个命令继承 `BaseCommand`。
- **Factory**：`CommandFactory` 创建全部 11 种 command，包括 `UndoCommand`、
  `RedoCommand`、`ListCommand`。`CreateFleetCommand` 经 `FleetFactoryRegistry`
  取得 `FleetFactory`，由两个 concrete fleet factory 创建 fleet。
  `AddDriverCommand` 经 `DriverFactory` 创建 driver，再交给 fleet 验证角色。
- **Memento**：`ModifyRoleCommand` 保存 `DriverRoleMemento(driverID, oldRole)`；
  `ChangeNameCommand` 保存 `FleetNameMemento(fleetID, oldName)`。
  Undo 调用快照的 `restore(target)`，redo 重做新值；redo 不会覆盖旧快照。
- **原类的公开接口**：`FleetUtil` 通过 `getDrivers/addDriver/dropDriver` 查询和恢复司机顺序。
  `DeleteDriverCommand` 保存删除前的对象顺序，undo 会把原 driver 放回原来的位置。

旧文件已按图重命名，没有保留重复的旧版本：

| 旧名 | 当前名称 |
| --- | --- |
| `DeliveryVanFactory` | `DeliveryVanFleetFactory` |
| `RefrigeratedTruckFactory` | `RefrigeratedTruckFleetFactory` |
| `ModifyDriverRoleCommand` | `ModifyRoleCommand` |
| `ChangeFleetNameCommand` | `ChangeNameCommand` |
| `DisplayAllFleetsCommand` | `ShowAllFleetsCommand` |

### 历史操作规则

`CommandInvoker` 维护两个 `Stack<Command>`。只有执行成功的五种变更命令会进入
undo stack，并清空 redo stack。失败操作、显示操作、选择操作和列表操作均不清空 redo。
Undo/redo 成功后才移动 stack entry，redo 调用该 command 的 `redo()`。
列表依样例以 stack 底到顶显示，最后一项是下一次 undo/redo 的目标。

Driver 和名称操作在首次执行时记住实际 fleet/driver 对象；后来切换 current fleet，
undo/redo 仍作用于原目标，不会误改另一个 fleet 中相同 ID 的司机。
撤销建立 fleet 时，如果它仍是 current fleet，就恢复当时的选择；
如果使用者已经选择了另一个仍存在的 fleet，则保留该选择。Redo 建立会再次选择新建 fleet。

## 四个原 class 的保护

以下文件直接来自你上传的 `SDFMS_Java_Prototype_StrictOriginalClasses.zip`，
本次未作任何修改，包括注释、空白和换行：

- `Driver.java`
- `VehicleFleet.java`
- `DeliveryVanFleet.java`
- `RefrigeratedTruckFleet.java`

`ORIGINAL_CLASSES.sha256` 记录原文件校验值，四个文件已与原 ZIP 逐字节比较。
由于当前附件中只有 UML、没有另一份老师原始 Java 源码，这里的逐字节基准是你上传的 ZIP。

**图中需要保留的说明**：diagram 的两个 concrete fleet 将角色常量标成 public (`+`)，
但上传源码实际为 `private final`。遵守你“原四类任何内容不改”的要求，本版保留源码原样。
当前程序也不读取这些 private 常量。

**界面差异**：`sa` 使用每个 fleet 原有的多态 `showFleet()`，会显示类型、名称、ID 和司机明细；
比作业样例的摘要输出详细。这样符合当前 diagram 的 `ShowAllFleetsCommand(manager)`，
不需要给原四类添加方法或在显示 command 中判断具体 fleet 类型。角色输入提示采用通用数字提示。

## 验证

以下测试全部使用 Java，用于开发检查，主程序不依赖它们，不需要 JUnit。

```bat
mkdir build
javac -d build *.java tests/RegressionTests.java
java -cp build RegressionTests
```

另可执行 `java Main < sample_input.txt` 检查完整的命令输入流程。
已验证 Java 8 API 编译兼容。

本版测试结果：

- Java 回归：**224 checks / 10 groups** 通过，覆盖全部 11 种 command、五种变更的
  undo/redo、独立快照、重复恢复、跨 fleet 目标、删除后的对象和顺序、失败后的历史保护。
- 四个原文件与上传 ZIP **逐字节一致**。

Windows `run.bat` 已保留；当前环境运行的是 Linux，未声称实际执行过 Windows batch。
上述检查验证当前 coding，不代替最终报告、设计解释和报告所需的测试截图。
