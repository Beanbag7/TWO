import { createApp } from 'vue'
import './index.css'
import App from './App.vue'
import { createPinia } from 'pinia'
import router from './router'
import 'remixicon/fonts/remixicon.css'
// Naive UI is tree-shakable, but for simplicity we can just use it globally or import components.
// Typically we use <n-config-provider> in App.vue and automatic import.
// For now, let's setup the basics.

import i18n from './i18n'

const app = createApp(App)
app.use(createPinia())
app.use(router)
app.use(i18n)
app.mount('#app')
