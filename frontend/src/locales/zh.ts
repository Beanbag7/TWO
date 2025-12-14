export default {
    systemName: '设备借用系统',
    testingPhase: '工作台',
    logout: '退出登录',
    menu: {
        equipmentList: '设备列表',
        myReservations: '我的预约',
        adminDashboard: '管理后台'
    },
    equipmentList: {
        searchPlaceholder: '搜索设备...',
        filterType: '按类型筛选',
        total: '总设备数',
        available: '可用设备数',
        borrowed: '借出设备数',
        reserve: '预约',
        type: '类型',
        location: '位置',
        price: '价格',
        notePlaceholder: '添加备注（可选）',
        modalTitle: '预约设备',
        modalEquipmentLabel: '设备',
        cancel: '取消',
        confirm: '确认'
    },
    myReservations: {
        title: '个人中心',
        tabs: {
            reservations: '我的预约',
            loans: '当前借阅',
            history: '借阅历史'
        },
        columns: {
            id: 'ID',
            equipment: '设备',
            startDate: '开始日期',
            endDate: '结束日期',
            status: '状态',
            action: '操作',
            borrowTime: '借出时间',
            dueDate: '应还时间',
            returnTime: '归还时间',
            reason: '原因',
            notes: '备注'
        },
        returnModal: {
            title: '归还设备',
            conditionQuestion: '设备状态是否良好？',
            good: '良好',
            damaged: '损坏',
            describeDamage: '描述损坏情况...',
            confirm: '确认归还',
            cancel: '取消',
            button: '归还'
        }
    },
    admin: {
        maintenance: '维护/损坏',
        pending: '待审核',
        title: '管理后台',
        tabs: {
            approvals: '待审核申请',
            loanCenter: '借还中心',
            equipment: '设备管理',
            maintenance: '维护管理',
            history: '维修历史'
        },
        columns: {
            id: 'ID',
            user: '用户',
            equipment: '设备',
            start: '开始',
            end: '结束',
            action: '操作',
            damageNote: '故障描述',
            damageDate: '故障日期',
            repairNote: '维修备注',
            status: '状态',
            date: '日期'
        },
        buttons: {
            approve: '通过',
            reject: '拒绝',
            searchUser: '搜索用户',
            checkout: '出库',
            return: '入库',
            addEquipment: '添加设备',
            markRepaired: '标记为已修复'
        },
        headers: {
            readyPickup: '待取机 (已批准)',
            borrowed: '当前借出',
            addNew: '添加新设备'
        },
        form: {
            name: '设备名称',
            type: '类型',
            brand: '品牌',
            model: '型号',
            sn: '序列号',
            price: '价格',
            location: '位置',
            uIdPlaceholder: '用户数据库ID'
        },
        placeholders: {
            name: '例如：MacBook Pro 2023',
            type: '选择设备类型',
            brand: '例如：Apple',
            model: '例如：M2 Max',
            sn: '输入序列号',
            price: '输入价格 (元)',
            location: '例如：301室 A架'
        },
        repairModal: {
            title: '标记为已修复',
            noteLabel: '维修备注',
            notePlaceholder: '描述维修详情...',
            cancel: '取消',
            confirm: '确认'
        }
    },
    messages: {
        loadEquipmentsFailed: '加载设备列表失败',
        selectDates: '请选择借用日期',
        reservationSubmitted: '预约已提交',
        reserveFailed: '预约失败',
        fetchReservationsFailed: '获取预约记录失败',
        fetchLoansFailed: '获取借阅记录失败',
        returnSuccess: '归还成功',
        returnFailed: '归还失败',
        fetchPendingFailed: '获取待审核记录失败',
        approved: '已批准',
        approveFailed: '批准失败',
        rejected: '已拒绝',
        rejectFailed: '拒绝操作失败',
        equipmentAdded: '设备添加成功',
        addFailed: '添加失败',
        equipmentRepaired: '维修记录已更新',
        markRepairedFailed: '标记修复失败',
        loadMaintenanceFailed: '加载维护列表失败'
    }
}
