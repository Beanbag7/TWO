<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { NForm, NFormItem, NInput, NButton, useMessage } from 'naive-ui'
import request from '../utils/request'

const router = useRouter()
const message = useMessage()

// Tab state: 'login' or 'register'
const activeTab = ref('login')

const loginForm = ref({
    username: '',
    password: ''
})

const registerForm = ref({
    username: '',
    password: '',
    fullName: '',
    phone: '',
    email: ''
})

const handleLogin = async () => {
    try {
        const res: any = await request.post('/users/login', {
            username: loginForm.value.username,
            password: loginForm.value.password
        })
        localStorage.setItem('user', JSON.stringify(res))
        message.success('登录成功')
        router.push('/')
    } catch (e: any) {
        message.error(e.message || '登录失败')
    }
}

const handleRegister = async () => {
    try {
        await request.post('/users/register', {
            employeeStudentId: registerForm.value.username,
            password: registerForm.value.password,
            fullName: registerForm.value.fullName,
            phoneNumber: registerForm.value.phone,
            email: registerForm.value.email,
            userType: '学生', // Default to student
            department: '未知'
        })
        message.success('注册成功，请直接登录')
        activeTab.value = 'login'
    } catch (e: any) {
        message.error(e.message || '注册失败')
    }
}
</script>

<template>
<div class="relative min-h-screen flex items-center justify-center overflow-hidden bg-gray-900">
    <!-- Background Image -->
    <div class="absolute inset-0 z-0">
        <img src="/login-bg.png" alt="Background" class="w-full h-full object-cover opacity-80" />
        <div class="absolute inset-0 bg-gradient-to-br from-black/40 to-emerald-900/30"></div>
    </div>

    <!-- Login Card -->
    <div class="relative z-10 w-full max-w-md p-8 transform transition-all duration-500 hover:scale-[1.01]">
        <div class="absolute inset-0 bg-white/10 backdrop-blur-xl rounded-2xl border border-white/20 shadow-2xl"></div>
        
        <div class="relative z-20">
            <!-- Header -->
            <div class="text-center mb-8">
                <div class="inline-flex items-center justify-center w-16 h-16 rounded-full bg-emerald-500/20 mb-4 border border-emerald-400/30 shadow-[0_0_15px_rgba(16,185,129,0.3)]">
                    <i class="ri-instance-fill text-3xl text-emerald-400"></i>
                </div>
                <h1 class="text-3xl font-bold text-white tracking-tight text-shadow">
                    设备借用管理系统
                </h1>
                <p class="text-emerald-100/70 mt-2 text-sm font-light">Laboratory Equipment Management</p>
            </div>

            <!-- Custom Tab Switcher -->
            <div class="flex p-1 bg-white/10 rounded-lg mb-6 border border-white/10 backdrop-blur-sm">
                <button 
                    @click="activeTab = 'login'"
                    class="flex-1 py-2 rounded-md text-sm font-medium transition-all duration-300"
                    :class="activeTab === 'login' ? 'bg-emerald-600 text-white shadow-lg' : 'text-white/70 hover:bg-white/10 hover:text-white'"
                >
                    登录
                </button>
                <button 
                    @click="activeTab = 'register'"
                    class="flex-1 py-2 rounded-md text-sm font-medium transition-all duration-300"
                    :class="activeTab === 'register' ? 'bg-emerald-600 text-white shadow-lg' : 'text-white/70 hover:bg-white/10 hover:text-white'"
                >
                    注册
                </button>
            </div>

            <!-- Login Form -->
            <div v-if="activeTab === 'login'" class="animate-fade-in-up">
                <n-form size="large">
                    <n-form-item :show-label="false">
                        <n-input v-model:value="loginForm.username" placeholder="请输入工号/学号" class="!bg-white/5 !border-white/10 !text-white placeholder:!text-gray-400 focus:!bg-white/10 focus:!border-emerald-500/50 transition-colors">
                            <template #prefix>
                                <i class="ri-user-line text-emerald-400/70"></i>
                            </template>
                        </n-input>
                    </n-form-item>
                    <n-form-item :show-label="false">
                        <n-input type="password" show-password-on="click"  @keypress.enter="handleLogin" v-model:value="loginForm.password" placeholder="请输入密码" class="!bg-white/5 !border-white/10 !text-white placeholder:!text-gray-400 focus:!bg-white/10 focus:!border-emerald-500/50 transition-colors">
                            <template #prefix>
                                <i class="ri-lock-2-line text-emerald-400/70"></i>
                            </template>
                        </n-input>
                    </n-form-item>
                    <n-button type="primary" block size="large" @click="handleLogin" class="!mt-2 !bg-gradient-to-r !from-emerald-600 !to-teal-600 hover:!from-emerald-500 hover:!to-teal-500 !border-0 shadow-lg shadow-emerald-900/50 !font-bold">
                        登录系统
                    </n-button>
                </n-form>
            </div>

            <!-- Register Form -->
            <div v-if="activeTab === 'register'" class="animate-fade-in-up">
                <n-form size="large">
                    <n-form-item :show-label="false">
                        <n-input v-model:value="registerForm.username" placeholder="设置工号/学号" class="!bg-white/5 !border-white/10 !text-white placeholder:!text-gray-400">
                            <template #prefix><i class="ri-id-card-line text-emerald-400/70"></i></template>
                        </n-input>
                    </n-form-item>
                    <n-form-item :show-label="false">
                        <n-input v-model:value="registerForm.fullName" placeholder="真实姓名" class="!bg-white/5 !border-white/10 !text-white placeholder:!text-gray-400">
                            <template #prefix><i class="ri-user-smile-line text-emerald-400/70"></i></template>
                        </n-input>
                    </n-form-item>
                    <n-form-item :show-label="false">
                        <n-input type="password" show-password-on="click" v-model:value="registerForm.password" placeholder="设置密码" class="!bg-white/5 !border-white/10 !text-white placeholder:!text-gray-400">
                            <template #prefix><i class="ri-lock-2-line text-emerald-400/70"></i></template>
                        </n-input>
                    </n-form-item>
                    <n-form-item :show-label="false">
                        <n-input v-model:value="registerForm.phone" placeholder="联系电话" class="!bg-white/5 !border-white/10 !text-white placeholder:!text-gray-400">
                            <template #prefix><i class="ri-smartphone-line text-emerald-400/70"></i></template>
                        </n-input>
                    </n-form-item>
                    <n-form-item :show-label="false">
                        <n-input v-model:value="registerForm.email" placeholder="电子邮箱" class="!bg-white/5 !border-white/10 !text-white placeholder:!text-gray-400">
                            <template #prefix><i class="ri-mail-line text-emerald-400/70"></i></template>
                        </n-input>
                    </n-form-item>
                    <n-button type="success" block size="large" @click="handleRegister" class="!mt-2 !bg-emerald-600/80 hover:!bg-emerald-500 !border-0 !font-bold">
                        立即注册
                    </n-button>
                </n-form>
            </div>
        </div>
    </div>
    
    <!-- Footer -->
    <div class="absolute bottom-4 text-center w-full z-10 text-white/30 text-xs">
        &copy; 2025 实验室设备管理系统 | Laboratory Equipment Management System
    </div>
</div>
</template>

<style scoped>
.text-shadow {
    text-shadow: 0 2px 10px rgba(0,0,0,0.5);
}

:deep(.n-input .n-input__input-el) {
    color: white !important;
}

.animate-fade-in-up {
    animation: fadeInUp 0.4s ease-out;
}

@keyframes fadeInUp {
    from {
        opacity: 0;
        transform: translateY(10px);
    }
    to {
        opacity: 1;
        transform: translateY(0);
    }
}
</style>
