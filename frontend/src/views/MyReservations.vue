<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { NDataTable, NTag, useMessage, NTabs, NTabPane, NButton, NSpace } from 'naive-ui'
import request from '../utils/request'
import { h } from 'vue'

import { useI18n } from 'vue-i18n'
import { computed } from 'vue'

const { t } = useI18n()
const reservations = ref([])
const activeLoans = ref([])
const message = useMessage()
const user = JSON.parse(localStorage.getItem('user') || '{}')

const reservationColumns = computed(() => [
    { title: t('myReservations.columns.id'), key: 'reservationId' },
    { title: t('myReservations.columns.equipment'), key: 'equipmentName' },
    { title: t('myReservations.columns.startDate'), key: 'scheduledBorrowDate' },
    { title: t('myReservations.columns.endDate'), key: 'scheduledReturnDate' },
    { 
        title: t('myReservations.columns.status'), 
        key: 'status',
        render(row: any) {
             let type = 'default'
             if (row.status === '待审核') type = 'warning'
             else if (row.status === '已批准') type = 'success'
             else if (row.status === '已拒绝') type = 'error'
             return h(NTag, { type: type as any }, { default: () => row.status })
        }
    },
    { title: t('myReservations.columns.reason'), key: 'rejectionReason' }
])

const loanColumns = computed(() => [
    { title: t('myReservations.columns.id'), key: 'recordId' },
    { title: t('myReservations.columns.equipment'), key: 'equipmentName' },
    { title: t('myReservations.columns.borrowTime'), key: 'actualBorrowTime', render(row:any){ return row.actualBorrowTime?.replace('T', ' ') } },
    { title: t('myReservations.columns.dueDate'), key: 'expectedReturnTime', render(row:any){ return row.expectedReturnTime?.replace('T', ' ') } },
    { 
        title: t('myReservations.columns.status'), 
        key: 'borrowStatus',
        render(row: any) {
             return h(NTag, { type: 'info' }, { default: () => row.borrowStatus })
        }
    },
    {
        title: t('myReservations.columns.action'),
        key: 'action',
        render(row: any) {
            return h(NButton, {
                type: 'warning',
                size: 'small',
                disabled: row.borrowStatus !== '使用中',
                onClick: () => handleReturn(row)
            }, { default: () => t('myReservations.returnModal.button') })
        }
    }
])

const fetchReservations = async () => {
    try {
        const res: any = await request.get(`/reservations/user/${user.userId}`)
        reservations.value = res
    } catch (e) {
        message.error(t('messages.fetchReservationsFailed'))
    }
}

const historyLoans = ref([])
const historyColumns = computed(() => [
    { title: t('myReservations.columns.id'), key: 'recordId' },
    { title: t('myReservations.columns.equipment'), key: 'equipmentName' },
    { title: t('myReservations.columns.borrowTime'), key: 'actualBorrowTime', render(row:any){ return row.actualBorrowTime?.replace('T', ' ') } },
    { title: t('myReservations.columns.returnTime'), key: 'actualReturnTime', render(row:any){ return row.actualReturnTime?.replace('T', ' ') } },
    { 
        title: t('myReservations.columns.status'), 
        key: 'borrowStatus',
        render(row: any) {
             let type = 'success'
             if (row.borrowStatus === '损坏') type = 'error'
             else if (row.borrowStatus === '逾期') type = 'warning'
             return h(NTag, { type: type as any }, { default: () => row.borrowStatus })
        }
    },
     { title: t('myReservations.columns.notes'), key: 'notes' }
])

const fetchLoans = async () => {
    try {
        const res: any = await request.get(`/borrowDB/user/${user.userId}`)
        activeLoans.value = res.filter((r:any) => r.borrowStatus === '使用中' || r.borrowStatus === '逾期')
        historyLoans.value = res.filter((r:any) => r.borrowStatus !== '使用中' && r.borrowStatus !== '逾期')
    } catch (e) {
        message.error(t('messages.fetchLoansFailed'))
    }
}

const showReturnModal = ref(false)
const selectedLoan = ref<any>(null)
const returnCondition = ref('good')
const returnNotes = ref('')

const handleReturn = (row: any) => {
    selectedLoan.value = row
    returnCondition.value = 'good'
    returnNotes.value = ''
    showReturnModal.value = true
}

import { NRadioGroup, NRadio, NInput, NModal } from 'naive-ui'

const confirmReturn = async () => {
    try {
        const notes = returnCondition.value === 'good' ? 'Self returned: Good' : `Self returned: Damaged - ${returnNotes.value}`
        await request.post(`/borrowDB/${selectedLoan.value.recordId}/return`, {
            returnStaffId: null, 
            notes: notes
        })
        message.success(t('messages.returnSuccess'))
        showReturnModal.value = false
        fetchLoans()
    } catch (e: any) {
        message.error(e.message || t('messages.returnFailed'))
    }
}

onMounted(() => {
    fetchReservations()
    fetchLoans()
})
</script>

<template>
<div class="bg-white p-4 rounded shadow">
    <h2 class="text-lg font-bold mb-4">{{ t('myReservations.title') }}</h2>
    <n-tabs type="line" animated>
        <n-tab-pane name="reservations" :tab="t('myReservations.tabs.reservations')">
             <n-data-table :columns="reservationColumns" :data="reservations" :pagination="{ pageSize: 10 }" />
        </n-tab-pane>
        <n-tab-pane name="loans" :tab="t('myReservations.tabs.loans')">
             <n-data-table :columns="loanColumns" :data="activeLoans" :pagination="{ pageSize: 10 }" />
        </n-tab-pane>
        <n-tab-pane name="history" :tab="t('myReservations.tabs.history')">
             <n-data-table :columns="historyColumns" :data="historyLoans" :pagination="{ pageSize: 10 }" />
        </n-tab-pane>
    </n-tabs>
    <n-modal v-model:show="showReturnModal" preset="dialog" :title="t('myReservations.returnModal.title')">
        <div class="py-4">
            <p class="mb-2">{{ t('myReservations.returnModal.conditionQuestion') }}</p>
            <n-radio-group v-model:value="returnCondition">
                <n-space>
                    <n-radio value="good">{{ t('myReservations.returnModal.good') }}</n-radio>
                    <n-radio value="damaged">{{ t('myReservations.returnModal.damaged') }}</n-radio>
                </n-space>
            </n-radio-group>
            <div v-if="returnCondition === 'damaged'" class="mt-2">
                <n-input v-model:value="returnNotes" type="textarea" :placeholder="t('myReservations.returnModal.describeDamage')" />
            </div>
        </div>
        <template #action>
            <n-button @click="showReturnModal = false">{{ t('myReservations.returnModal.cancel') }}</n-button>
            <n-button type="primary" @click="confirmReturn">{{ t('myReservations.returnModal.confirm') }}</n-button>
        </template>
    </n-modal>
</div>
</template>
