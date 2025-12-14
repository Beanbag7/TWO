<script setup lang="ts">
import { h } from 'vue'
import { useRouter } from 'vue-router'
import { NLayout, NLayoutSider, NLayoutHeader, NLayoutContent, NMenu, NButton, NSpace, NText } from 'naive-ui'
import type { MenuOption } from 'naive-ui'

const router = useRouter()
const user = JSON.parse(localStorage.getItem('user') || '{}')

import { useThemeStore } from '../store/theme'
import { useI18n } from 'vue-i18n'
import { computed } from 'vue'

const { t, locale } = useI18n()
const { isDark, toggleTheme } = useThemeStore()

// Reactive menu options for translation
const menuOptions = computed(() => {
    const opts: MenuOption[] = [
        { label: t('menu.equipmentList'), key: 'equipment', icon: () => h('i', { class: 'ri-macbook-line' }) },
        { label: t('menu.myReservations'), key: 'my-reservations', icon: () => h('i', { class: 'ri-calendar-check-line' }) },
    ]
    if (user.userType?.includes('管理员')) {
        opts.push({ label: t('menu.adminDashboard'), key: 'admin', icon: () => h('i', { class: 'ri-admin-line' }) })
    }
    return opts
})

const toggleLanguage = () => {
    locale.value = locale.value === 'en' ? 'zh' : 'en'
}

const handleMenuUpdate = (key: string) => {
    if (key === 'equipment') router.push('/equipment')
    else if (key === 'my-reservations') router.push('/my-reservations')
    else if (key === 'admin') router.push('/admin')
}

const handleLogout = () => {
    localStorage.removeItem('user')
    router.push('/login')
}
</script>

<template>
<n-layout has-sider class="h-screen">
    <n-layout-sider bordered width="240" :collapsed-width="64" collapse-mode="width" show-trigger>
        <div class="h-16 flex items-center justify-center font-bold text-xl border-b overflow-hidden whitespace-nowrap">
            <n-space align="center" :size="8" class="px-4">
                <i class="ri-instance-fill text-2xl text-emerald-500"></i>
                <span class="text-emerald-600 dark:text-emerald-400">
                    {{ t('systemName') }}
                </span>
            </n-space>
        </div>
        <n-menu :options="menuOptions" @update:value="handleMenuUpdate" />
    </n-layout-sider>
    <n-layout>
        <n-layout-header bordered class="h-16 flex items-center justify-between px-6">
            <div class="font-medium text-lg">{{ t('testingPhase') }}</div>
            <n-space align="center">
                <n-button circle secondary @click="toggleTheme">
                    <template #icon>
                        <i :class="isDark ? 'ri-moon-line' : 'ri-sun-line'"></i>
                    </template>
                </n-button>
                <n-button size="small" @click="toggleLanguage">
                    {{ locale === 'en' ? '中文' : 'English' }}
                </n-button>
                <n-text>{{ user.fullName }} ({{ user.userType }})</n-text>
                <n-button size="small" type="error" @click="handleLogout">{{ t('logout') }}</n-button>
            </n-space>
        </n-layout-header>
        <n-layout-content content-style="padding: 24px;">
            <router-view v-slot="{ Component }">
                <transition name="fade-slide" mode="out-in">
                    <component :is="Component" />
                </transition>
            </router-view>
        </n-layout-content>
    </n-layout>
</n-layout>
</template>

<style scoped>
.fade-slide-enter-active,
.fade-slide-leave-active {
  transition: opacity 0.3s ease, transform 0.3s ease;
}

.fade-slide-enter-from {
  opacity: 0;
  transform: translateX(10px);
}

.fade-slide-leave-to {
  opacity: 0;
  transform: translateX(-10px);
}
</style>
