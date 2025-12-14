<script setup lang="ts">
import { NConfigProvider, NMessageProvider, NDialogProvider, NGlobalStyle, darkTheme, zhCN, dateZhCN } from 'naive-ui'
import { useThemeStore } from './store/theme'

const { isDark } = useThemeStore()
</script>

<template>
  <n-config-provider :theme="isDark ? darkTheme : null" :locale="zhCN" :date-locale="dateZhCN">
    <n-global-style />
    <n-message-provider>
      <n-dialog-provider>
        <router-view />
      </n-dialog-provider>
    </n-message-provider>
  </n-config-provider>
</template>

<style>
/* Global styles */
html, body, #app {
  height: 100%;
  margin: 0;
  display: flex;
  flex-direction: column;
}
</style>
