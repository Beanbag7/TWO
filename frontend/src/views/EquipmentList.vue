<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { NGrid, NGridItem, NCard, NTag, NButton, NInput, NSelect, useMessage, NModal, NDatePicker, NStatistic, NGi } from 'naive-ui'
import request from '../utils/request'
import { useI18n } from 'vue-i18n'

const message = useMessage()
const equipments = ref<any[]>([])
const types = ref<any[]>([])
const searchText = ref('')
const typeFilter = ref<number | null>(null)

const showModal = ref(false)
const selectedEquipment = ref<any>(null)
const dateRange = ref<[number, number] | null>(null)
const reservationNote = ref('')

const typeOptions = computed(() => {
    return types.value.map(t => ({ label: t.typeName, value: t.typeId }))
})

const filteredEquipments = computed(() => {
    return equipments.value.filter(e => {
        const matchName = e.equipmentName.toLowerCase().includes(searchText.value.toLowerCase())
        const matchType = typeFilter.value ? e.typeId === typeFilter.value : true
        return matchName && matchType
    })
})

const fetchEquipments = async () => {
    try {
        const res: any = await request.get('/equipments')
        equipments.value = res
    } catch (e) {
        message.error(t('messages.loadEquipmentsFailed'))
    }
}

const dashboardStats = ref({
    totalEquipments: 0,
    availableEquipments: 0,
    borrowedEquipments: 0
})

const fetchStats = async () => {
    try {
        const res: any = await request.get('/equipments/stats')
        dashboardStats.value = res
    } catch (e) {
        // quiet fail
    }
}

const fetchTypes = async () => {
    try {
        const res: any = await request.get('/equipments/types')
        types.value = res
    } catch (e) {
        console.error(e)
    }
}

const openReserveModal = (eq: any) => {
    selectedEquipment.value = eq
    dateRange.value = null
    reservationNote.value = ''
    showModal.value = true
}

const confirmReservation = async () => {
    if (!dateRange.value) {
        message.warning(t('messages.selectDates'))
        return
    }
    const user = JSON.parse(localStorage.getItem('user') || '{}')
    try {
        await request.post('/reservations', {
            userId: user.userId,
            equipmentId: selectedEquipment.value.equipmentId,
            scheduledBorrowDate: new Date(dateRange.value[0]).toISOString().split('T')[0],
            scheduledReturnDate: new Date(dateRange.value[1]).toISOString().split('T')[0],
            notes: reservationNote.value
        })
        message.success(t('messages.reservationSubmitted'))
        showModal.value = false
    } catch (e: any) {
        message.error(e.message ? e.message : t('messages.reserveFailed'))
    }
}

onMounted(() => {
    fetchEquipments()
    fetchTypes()
    fetchStats()
})

const getStatusType = (status: string) => {
    switch (status) {
        case '可用': return 'success';
        case '借出': return 'warning';
        case '维护中': return 'error';
        default: return 'default';
    }
}

const disablePastDates = (ts: number) => {
    return ts < Date.now() - 86400000; // Disable dates before today (allow today)
}
const { t } = useI18n()
</script>

<template>
<div>
    <div class="mb-6 p-4 bg-white dark:bg-gray-800 rounded-lg shadow-sm">
        <n-grid :x-gap="12" :cols="4">
            <n-gi :span="3">
                <n-input v-model:value="searchText" size="large" round :placeholder="t('equipmentList.searchPlaceholder')">
                    <template #prefix>
                        <i class="ri-search-line text-gray-400"></i>
                    </template>
                </n-input>
            </n-gi>
            <n-gi :span="1">
                <n-select v-model:value="typeFilter" size="large" :options="typeOptions" :placeholder="t('equipmentList.filterType')" clearable />
            </n-gi>
        </n-grid>
    </div>

    <n-grid :x-gap="12" :cols="3" class="mb-6">
        <n-gi>
            <n-card>
                <n-statistic :label="t('equipmentList.total')" :value="dashboardStats.totalEquipments" />
            </n-card>
        </n-gi>
        <n-gi>
            <n-card>
                <n-statistic :label="t('equipmentList.available')" :value="dashboardStats.availableEquipments" />
            </n-card>
        </n-gi>
        <n-gi>
            <n-card>
                 <n-statistic :label="t('equipmentList.borrowed')" :value="dashboardStats.borrowedEquipments" />
            </n-card>
        </n-gi>
    </n-grid>

    <n-grid :cols="4" :x-gap="12" :y-gap="12">
        <n-grid-item v-for="eq in filteredEquipments" :key="eq.equipmentId">
            <n-card hoverable>
                <template #header>
                    <div class="truncate font-bold">{{ eq.equipmentName }}</div>
                </template>
                <template #header-extra>
                    <n-tag :type="getStatusType(eq.status)">{{ eq.status }}</n-tag>
                </template>
                <div class="text-sm text-gray-500 mb-2">
                    <div>{{ t('equipmentList.type') }}: {{ eq.typeName }}</div>
                    <div>{{ t('equipmentList.location') }}: {{ eq.storageLocation }}</div>
                    <div>{{ t('equipmentList.price') }}: ¥{{ eq.price }}</div>
                </div>
                <template #action>
                    <n-button type="primary" size="small" :disabled="eq.status !== '可用'" @click="openReserveModal(eq)">
                        {{ t('equipmentList.reserve') }}
                    </n-button>
                </template>
            </n-card>
        </n-grid-item>
    </n-grid>

    <n-modal v-model:show="showModal" preset="dialog" :title="t('equipmentList.modalTitle')">
        <div class="py-4">
            <p class="mb-2">{{ t('equipmentList.modalEquipmentLabel') }}: <b>{{ selectedEquipment?.equipmentName }}</b></p>
            <n-date-picker 
                v-model:value="dateRange" 
                type="daterange" 
                clearable 
                class="mb-4"
                :is-date-disabled="disablePastDates"
            />
            <n-input
                v-model:value="reservationNote"
                type="textarea"
                :placeholder="t('equipmentList.notePlaceholder')"
            />
        </div>
        <template #action>
            <n-button @click="showModal = false">{{ t('equipmentList.cancel') }}</n-button>
            <n-button type="primary" @click="confirmReservation">{{ t('equipmentList.confirm') }}</n-button>
        </template>
    </n-modal>
</div>
</template>
