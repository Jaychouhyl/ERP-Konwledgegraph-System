package com.smartx.rag.tools;

// 🌟 这里是最新版 AgentScope Java 1.0.9 的正确导包
import io.agentscope.core.tool.Tool;
import io.agentscope.core.tool.ToolParam;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * 🛠️ 智能体专属工具：BOM 齐套性与缺件检查
 */
@Component
public class BomShortageCheckTool {

    // 🌟 核心变化 1：直接用 @Tool 注解声明，大模型会自动读取 name 和 description
    @Tool(name = "check_bom_shortage", description = "当需要生产特定数量的产品（如 SmartX-1）时，调用此工具检查底层物料的库存是否充足，并返回缺件列表。")
    public Object checkBomShortage(
            // 🌟 核心变化 2：使用 @ToolParam，AgentScope 会自动把大模型的 JSON 转成 Java 强类型参数！再也不用 map.get() 了！
            @ToolParam(name = "productName", description = "产品名称，例如：SmartX-1") String productName,
            @ToolParam(name = "targetQuantity", description = "目标生产数量，必须为整数") Integer targetQuantity) {

        System.out.println("🤖 [Agent Tool 被触发] 正在为 " + productName + " 模拟计算 " + targetQuantity + " 台的缺件情况...");

        Map<String, Object> shortageReport = new HashMap<>();

        // 这里的逻辑保持不变，模拟风控缺件情况
        if ("SmartX-1".equals(productName) && targetQuantity > 6000) {
            shortageReport.put("status", "缺料 (SHORTAGE)");
            shortageReport.put("missing_components", Map.of(
                    "OLED面板", targetQuantity - 6000,
                    "硅晶圆", targetQuantity - 5000
            ));
            shortageReport.put("suggestion", "请向 Samsung 和 TSMC 发起紧急采购单！");
        } else {
            shortageReport.put("status", "齐套 (READY)");
            shortageReport.put("suggestion", "物料充足，可直接下发制造指令至【阶段2: 组装过程】。");
        }

        return shortageReport;
    }
}