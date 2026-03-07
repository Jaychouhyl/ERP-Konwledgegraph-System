<template>
    <div class="settings-container">
        <!-- 基本信息卡 -->
        <div class="settings-card card-base">
            <div class="card-title">
                <Icon icon="mdi:domain" class="title-icon" /> 基本信息
            </div>
            <div class="settings-grid">
                <div class="setting-item">
                    <div class="setting-label">公司名称</div>
                    <div class="setting-value">深圳智行科技有限公司 (SmartX Technology)</div>
                </div>
                <div class="setting-item">
                    <div class="setting-label">系统标题</div>
                    <div class="setting-value">SmartX ERP — 智能企业资源计划系统</div>
                </div>
                <div class="setting-item">
                    <div class="setting-label">系统版本</div>
                    <div class="setting-value">v1.0.0-beta (Spring Cloud 微服务架构)</div>
                </div>
                <div class="setting-item">
                    <div class="setting-label">技术栈</div>
                    <div class="setting-value">Vue3 + Pinia + Spring Cloud + MyBatis-Flex + Neo4j + Redis + Seata</div>
                </div>
            </div>
        </div>

        <!-- 主题设置卡 -->
        <div class="settings-card card-base">
            <div class="card-title">
                <Icon icon="mdi:palette-outline" class="title-icon" /> 主题设置
            </div>
            <div class="settings-rows">
                <div class="setting-row">
                    <div class="row-info">
                        <div class="row-name">界面模式</div>
                        <div class="row-desc">切换日间/夜间模式，夜间模式更护眼</div>
                    </div>
                    <div class="theme-toggle" :class="{ 'is-dark': localIsDark }" @click="toggleLocalTheme">
                        <Icon :icon="localIsDark ? 'mdi:weather-night' : 'mdi:weather-sunny'"
                            class="toggle-state-icon" />
                        <span>{{ localIsDark ? '夜间模式' : '日间模式' }}</span>
                    </div>
                </div>
                <div class="setting-row">
                    <div class="row-info">
                        <div class="row-name">主题色</div>
                        <div class="row-desc">当前系统主色调（后续版本支持自定义）</div>
                    </div>
                    <div class="color-preview">
                        <span class="color-dot" style="background: #1677ff;"></span>
                        <span>#1677ff（品牌蓝）</span>
                    </div>
                </div>
                <div class="setting-row">
                    <div class="row-info">
                        <div class="row-name">侧边栏默认状态</div>
                        <div class="row-desc">登录后侧边栏默认展开还是收起</div>
                    </div>
                    <select v-model="sidebarDefault" class="vben-input select mini-select">
                        <option value="expand">展开</option>
                        <option value="mini">收起</option>
                    </select>
                </div>
            </div>
        </div>

        <!-- 安全设置卡 -->
        <div class="settings-card card-base">
            <div class="card-title">
                <Icon icon="mdi:shield-lock-outline" class="title-icon" /> 安全设置
            </div>
            <div class="settings-rows">
                <div class="setting-row">
                    <div class="row-info">
                        <div class="row-name">登录超时时间</div>
                        <div class="row-desc">超过此时间无操作，Token 将自动失效</div>
                    </div>
                    <select v-model="loginTimeout" class="vben-input select mini-select">
                        <option value="30">30 分钟</option>
                        <option value="60">1 小时</option>
                        <option value="120">2 小时</option>
                        <option value="480">8 小时</option>
                    </select>
                </div>
                <div class="setting-row">
                    <div class="row-info">
                        <div class="row-name">操作日志</div>
                        <div class="row-desc">查看系统所有操作人的行为审计日志</div>
                    </div>
                    <button class="vben-btn default" disabled>查看日志 (开发中)</button>
                </div>
            </div>
        </div>

        <!-- 关于系统 -->
        <div class="settings-card card-base">
            <div class="card-title">
                <Icon icon="mdi:information-outline" class="title-icon" /> 关于系统
            </div>
            <div class="about-content">
                <p><b>SmartX ERP 知识图谱智能决策系统</b></p>
                <p>基于 Spring Cloud 微服务 + Neo4j 知识图谱 + RAG 大语言模型的新一代 ERP 系统。</p>
                <p>集采购管理、销售管理、库存管理、财务流水、AI 风控于一体。</p>
                <div class="tech-tags">
                    <span class="tech-tag">Vue 3</span>
                    <span class="tech-tag">Pinia</span>
                    <span class="tech-tag">Spring Cloud</span>
                    <span class="tech-tag">Nacos</span>
                    <span class="tech-tag">MyBatis-Flex</span>
                    <span class="tech-tag">Redis</span>
                    <span class="tech-tag">Seata</span>
                    <span class="tech-tag">Neo4j</span>
                    <span class="tech-tag">Gateway</span>
                </div>
                <p class="copyright">© 2026 SmartX Technology. All rights reserved.</p>
            </div>
        </div>
    </div>
</template>

<script setup>
import { ref } from 'vue'
import { Icon } from '@iconify/vue'

const localIsDark = ref(document.documentElement.classList.contains('dark'))
const sidebarDefault = ref('expand')
const loginTimeout = ref('120')

const toggleLocalTheme = () => {
    localIsDark.value = !localIsDark.value
    if (localIsDark.value) document.documentElement.classList.add('dark')
    else document.documentElement.classList.remove('dark')
}
</script>

<style scoped>
.settings-container {
    display: flex;
    flex-direction: column;
    gap: 16px;
    max-width: 900px;
}

.card-base {
    background: var(--content-bg, #fff);
    border: 1px solid var(--border-color, #f0f0f0);
    border-radius: 8px;
    padding: 24px;
    transition: all 0.3s;
}

.card-title {
    font-size: 16px;
    font-weight: bold;
    color: var(--text-color, #333);
    margin-bottom: 20px;
    padding-bottom: 12px;
    border-bottom: 1px solid var(--border-color, #f0f0f0);
    display: flex;
    align-items: center;
    gap: 8px;
}

.title-icon {
    font-size: 20px;
    color: #1677ff;
}

.settings-grid {
    display: grid;
    grid-template-columns: 1fr 1fr;
    gap: 16px;
}

.setting-item {
    padding: 12px 0;
    border-bottom: 1px solid var(--border-color, #f0f0f0);
}

.setting-item:nth-last-child(-n+2) {
    border-bottom: none;
}

.setting-label {
    font-size: 13px;
    color: var(--text-color-secondary, #999);
    margin-bottom: 6px;
}

.setting-value {
    font-size: 14px;
    color: var(--text-color, #333);
    font-weight: 500;
}

.settings-rows {
    display: flex;
    flex-direction: column;
}

.setting-row {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 16px 0;
    border-bottom: 1px solid var(--border-color, #f0f0f0);
}

.setting-row:last-child {
    border-bottom: none;
}

.row-info {
    flex: 1;
}

.row-name {
    font-size: 14px;
    font-weight: 500;
    color: var(--text-color, #333);
    margin-bottom: 4px;
}

.row-desc {
    font-size: 13px;
    color: var(--text-color-secondary, #999);
}

.theme-toggle {
    display: flex;
    align-items: center;
    gap: 8px;
    padding: 6px 16px;
    border-radius: 20px;
    cursor: pointer;
    transition: all 0.3s;
    background: #e6f4ff;
    color: #1677ff;
    font-size: 13px;
    font-weight: 500;
    border: 1px solid #91caff;
}

.theme-toggle.is-dark {
    background: #1f1f1f;
    color: #e5e6eb;
    border-color: #434343;
}

.toggle-state-icon {
    font-size: 16px;
}

.color-preview {
    display: flex;
    align-items: center;
    gap: 8px;
    font-size: 13px;
    color: var(--text-color, #333);
}

.color-dot {
    width: 16px;
    height: 16px;
    border-radius: 50%;
    display: inline-block;
    border: 2px solid #fff;
    box-shadow: 0 0 0 1px #d9d9d9;
}

.mini-select {
    width: 120px;
    height: 32px;
    font-size: 13px;
}

.vben-input.select {
    padding: 4px 8px;
    border: 1px solid var(--border-color, #d9d9d9);
    border-radius: 6px;
    background: var(--content-bg, #fff);
    color: var(--text-color, #333);
    outline: none;
    cursor: pointer;
    transition: border-color 0.3s;
}

.vben-input.select:focus {
    border-color: #1677ff;
}

.vben-btn {
    padding: 6px 16px;
    border-radius: 6px;
    font-size: 13px;
    cursor: pointer;
    border: none;
    transition: all 0.2s;
}

.vben-btn.default {
    background: transparent;
    color: var(--text-color, #333);
    border: 1px solid var(--border-color, #d9d9d9);
}

.vben-btn.default:hover {
    border-color: #1677ff;
    color: #1677ff;
}

.vben-btn:disabled {
    opacity: 0.5;
    cursor: not-allowed;
}

.about-content {
    line-height: 1.8;
    color: var(--text-color, #333);
    font-size: 14px;
}

.about-content p {
    margin: 8px 0;
}

.tech-tags {
    display: flex;
    flex-wrap: wrap;
    gap: 8px;
    margin: 16px 0;
}

.tech-tag {
    padding: 4px 12px;
    border-radius: 4px;
    font-size: 12px;
    background: #f0f5ff;
    color: #1677ff;
    border: 1px solid #d6e4ff;
}

.copyright {
    margin-top: 20px;
    font-size: 12px;
    color: var(--text-color-secondary, #999);
}
</style>