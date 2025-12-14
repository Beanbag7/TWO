<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { NTabs, NTabPane, NDataTable, NButton, useMessage, NInput, NModal, NCard, NFormItem, NGrid, NGi, NStatistic, NForm, NSelect, NInputNumber, NFormItemGi } from 'naive-ui'
import request from '../utils/request'
import { useI18n } from 'vue-i18n'
import type { Reservation } from '../types'
import { computed, h } from 'vue'

const { t } = useI18n()
const message = useMessage()
const pendingReservations = ref<Reservation[]>([])
const activeTab = ref('approvals')
const loading = ref(false)
const user = JSON.parse(localStorage.getItem('user') || '{}')

const approvalColumns = computed(() => [
    { title: t('admin.columns.id'), key: 'reservationId' },
    { title: t('admin.columns.user'), key: 'userName' },
    { title: t('admin.columns.equipment'), key: 'equipmentName' },
    { title: t('admin.columns.start'), key: 'scheduledBorrowDate' },
    { title: t('admin.columns.end'), key: 'scheduledReturnDate' },
    {
        title: t('admin.columns.action'),
        key: 'action',
        render(row: Reservation) {
            return [
                h(NButton, {
                    type: 'success',
                    size: 'small',
                    style: 'margin-right: 8px',
                    onClick: () => handleApprove(row)
                }, { default: () => t('admin.buttons.approve') }),
                h(NButton, {
                    type: 'error',
                    size: 'small',
                    onClick: () => handleReject(row)
                }, { default: () => t('admin.buttons.reject') })
            ]
        }
    }
])

// Fetch pending
const fetchPending = async () => {
    loading.value = true
    try {
        const res = await request.get<any, Reservation[]>('/reservations')
        pendingReservations.value = res.filter((r) => r.status === '待审核')
    } catch (e) {
        message.error(t('messages.fetchPendingFailed'))
    } finally {
        loading.value = false
    }
}

const handleApprove = async (row: Reservation) => {
    try {
        await request.post(`/reservations/${row.reservationId}/approve`, {
            approverId: user.userId
        })

        message.success(t('messages.approved'))
        fetchPending()
    } catch (e) {
        message.error(t('messages.approveFailed'))
    }
}

const handleReject = async (row: Reservation) => {
     try {
        await request.post(`/reservations/${row.reservationId}/reject`, {
            approverId: user.userId,
            reason: 'Admin rejected'
        })

        message.success(t('messages.rejected'))
        fetchPending()
    } catch (e) {
        message.error(t('messages.rejectFailed'))
    }
}



const newEquipment = ref({
    equipmentName: '',
    typeId: null,
    brand: '',
    model: '',
    serialNumber: '',
    price: 0,
    storageLocation: ''
})
const types = ref([])
const typeOptions = ref([])

const fetchTypes = async () => {
    try {
        const res: any = await request.get('/equipments/types')
        types.value = res
        typeOptions.value = res.map((t: any) => ({ label: t.typeName, value: t.typeId }))
    } catch (e) {
        console.error(e)
    }
}

const handleAddEquipment = async () => {
    try {
        await request.post('/equipments', newEquipment.value)
        message.success(t('messages.equipmentAdded'))
        // Reset form
        newEquipment.value = {
            equipmentName: '',
            typeId: null,
            brand: '',
            model: '',
            serialNumber: '',
            price: 0,
            storageLocation: ''
        }
    } catch (e: any) {
        message.error(e.message || t('messages.addFailed'))
    }
}

const maintenanceList = ref([])
const maintenanceColumns = computed(() => [
    { title: t('admin.columns.id'), key: 'equipmentId' },
    { title: t('admin.columns.equipment'), key: 'equipmentName' },
    { title: t('admin.form.type'), key: 'typeName' },
    { title: t('admin.columns.status'), key: 'status' },
    { title: t('admin.form.location'), key: 'storageLocation' },
    { title: t('admin.repairModal.noteLabel'), key: 'damageDescription' },
    { title: t('admin.columns.date'), key: 'damageTime' },
    {
        title: t('admin.columns.action'),
        key: 'action',
        render(row: any) {
            return h(NButton, {
                type: 'primary',
                size: 'small',
                onClick: () => handleRepair(row)
            }, { default: () => t('admin.buttons.markRepaired') })
        }
    }
])

const historyList = ref([])
const historyColumns = computed(() => [
    { title: t('admin.columns.equipment'), key: 'equipmentName' },
    { title: t('admin.form.type'), key: 'typeName' },
    { title: t('admin.columns.damageNote'), key: 'damageDescription' },
    { title: t('admin.columns.damageDate'), key: 'damageTime' },
    { title: t('admin.columns.repairNote'), key: 'resolutionNotes' }
])

const fetchHistory = async () => {
    try {
        const res: any = await request.get('/equipments/history')
        historyList.value = res
    } catch (e) {
        // message.error('Failed to load history')
    }
}

const showRepairModal = ref(false)
const repairNote = ref('')
const currentRepairRow = ref<any>(null)

const handleRepair = (row: any) => {
    currentRepairRow.value = row
    repairNote.value = ''
    showRepairModal.value = true
}

const confirmRepair = async () => {
    if (!currentRepairRow.value) return
    try {
        await request.post(`/equipments/${currentRepairRow.value.equipmentId}/repair`, { notes: repairNote.value || 'Fixed' })
        message.success(t('messages.equipmentRepaired'))
        showRepairModal.value = false
        fetchMaintenance()
        fetchHistory()
    } catch (e) {
        message.error(t('messages.markRepairedFailed'))
    }
}

const fetchMaintenance = async () => {
    try {
        const res: any = await request.get('/equipments/maintenance')
        maintenanceList.value = res
    } catch (e) {
        message.error(t('messages.loadMaintenanceFailed'))
    }
}

const dashboardStats = ref({
    maintenanceEquipments: 0,
    pendingReservations: 0
})

const fetchStats = async () => {
    try {
        const res: any = await request.get('/equipments/stats')
        dashboardStats.value = res
    } catch (e) {
        // quiet fail
    }
}

onMounted(() => {
    fetchPending()
    fetchTypes()
    fetchMaintenance()
    fetchHistory()
    fetchStats()
})
</script>

<template>
<div class="bg-white p-4 rounded shadow">
    <h2 class="text-xl font-bold mb-4">{{ t('admin.title') }}</h2>
    
    <n-grid :x-gap="12" :y-gap="8" :cols="2" class="mb-4">
         <n-gi>
            <n-card>
                 <n-statistic :label="t('admin.maintenance')" :value="dashboardStats.maintenanceEquipments" />
            </n-card>
        </n-gi>
        <n-gi>
            <n-card>
                 <n-statistic :label="t('admin.pending')" :value="dashboardStats.pendingReservations" />
            </n-card>
        </n-gi>
    </n-grid>

    <n-tabs v-model:value="activeTab" type="line">
        <n-tab-pane name="approvals" :tab="t('admin.tabs.approvals')">
            <n-data-table :columns="approvalColumns" :data="pendingReservations" :loading="loading" striped />
        </n-tab-pane>
        <n-tab-pane name="equipment" :tab="t('admin.tabs.equipment')">
             <n-card :title="t('admin.headers.addNew')" class="shadow-sm">
                <n-form label-placement="top">
                    <n-grid :cols="2" :x-gap="24">
                        <n-form-item-gi :label="t('admin.form.name')">
                            <n-input v-model:value="newEquipment.equipmentName" :placeholder="t('admin.placeholders.name')" />
                        </n-form-item-gi>
                        <n-form-item-gi :label="t('admin.form.type')">
                             <n-select v-model:value="newEquipment.typeId" :options="typeOptions" :placeholder="t('admin.placeholders.type')" />
                        </n-form-item-gi>
                        <n-form-item-gi :label="t('admin.form.brand')">
                            <n-input v-model:value="newEquipment.brand" :placeholder="t('admin.placeholders.brand')" />
                        </n-form-item-gi>
                        <n-form-item-gi :label="t('admin.form.model')">
                            <n-input v-model:value="newEquipment.model" :placeholder="t('admin.placeholders.model')" />
                        </n-form-item-gi>
                        <n-form-item-gi :label="t('admin.form.sn')">
                            <n-input v-model:value="newEquipment.serialNumber" :placeholder="t('admin.placeholders.sn')" />
                        </n-form-item-gi>
                         <n-form-item-gi :label="t('admin.form.price')">
                            <n-input-number v-model:value="newEquipment.price" :placeholder="t('admin.placeholders.price')" class="w-full" :show-button="false">
                                <template #prefix>¥</template>
                            </n-input-number>
                        </n-form-item-gi>
                    </n-grid>
                    <n-form-item :label="t('admin.form.location')">
                        <n-input v-model:value="newEquipment.storageLocation" :placeholder="t('admin.placeholders.location')" />
                    </n-form-item>
                    
                    <div class="flex justify-end mt-4">
                        <n-button type="primary" size="large" @click="handleAddEquipment">
                            <template #icon><i class="ri-add-line"></i></template>
                            {{ t('admin.buttons.addEquipment') }}
                        </n-button>
                    </div>
                </n-form>
             </n-card>
        </n-tab-pane>
        <n-tab-pane name="maintenance" :tab="t('admin.tabs.maintenance')">
            <n-data-table :columns="maintenanceColumns" :data="maintenanceList" striped />
        </n-tab-pane>
        <n-tab-pane name="history" :tab="t('admin.tabs.history')">
            <n-data-table :columns="historyColumns" :data="historyList" striped />
        </n-tab-pane>
    </n-tabs>

    <n-modal v-model:show="showRepairModal">
        <n-card style="width: 600px" :title="t('admin.repairModal.title')" :bordered="false" size="huge" role="dialog" aria-modal="true">
            <n-form-item :label="t('admin.repairModal.noteLabel')">
                <n-input v-model:value="repairNote" type="textarea" :placeholder="t('admin.repairModal.notePlaceholder')" />
            </n-form-item>
            <template #footer>
                <div class="flex justify-end space-x-2">
                    <n-button @click="showRepairModal = false">{{ t('admin.repairModal.cancel') }}</n-button>
                    <n-button type="primary" @click="confirmRepair">{{ t('admin.repairModal.confirm') }}</n-button>
                </div>
            </template>
        </n-card>
    </n-modal>
</div>
</template>
