export default {
    systemName: 'Equipment Loan System',
    testingPhase: 'Workbench',
    logout: 'Logout',
    menu: {
        equipmentList: 'Equipment List',
        myReservations: 'My Reservations',
        adminDashboard: 'Admin Dashboard'
    },
    equipmentList: {
        searchPlaceholder: 'Search equipment...',
        filterType: 'Filter by Type',
        total: 'Total Equipments',
        available: 'Available',
        borrowed: 'Borrowed',
        reserve: 'Reserve',
        type: 'Type',
        location: 'Loc',
        price: 'Price',
        notePlaceholder: 'Add a note (optional)',
        modalTitle: 'Reserve Equipment',
        modalEquipmentLabel: 'Equipment',
        cancel: 'Cancel',
        confirm: 'Confirm'
    },
    myReservations: {
        title: 'Personal Center',
        tabs: {
            reservations: 'My Reservations',
            loans: 'Current Loans',
            history: 'Borrow History'
        },
        columns: {
            id: 'ID',
            equipment: 'Equipment',
            startDate: 'Start Date',
            endDate: 'End Date',
            status: 'Status',
            action: 'Action',
            borrowTime: 'Borrow Time',
            dueDate: 'Due Date',
            returnTime: 'Return Time',
            reason: 'Reason',
            notes: 'Notes'
        },
        returnModal: {
            title: 'Return Equipment',
            conditionQuestion: 'Is the equipment in good condition?',
            good: 'Good',
            damaged: 'Damaged',
            describeDamage: 'Describe damage...',
            confirm: 'Confirm Return',
            cancel: 'Cancel',
            button: 'Return'
        }
    },
    admin: {
        maintenance: 'Maintenance/Damaged',
        pending: 'Pending Approvals',
        title: 'Admin Dashboard',
        tabs: {
            approvals: 'Pending Approvals',
            loanCenter: 'Loan Center',
            equipment: 'Equipment Management',
            maintenance: 'Maintenance',
            history: 'Repair History'
        },
        columns: {
            id: 'ID',
            user: 'User',
            equipment: 'Equipment',
            start: 'Start',
            end: 'End',
            action: 'Action'
        },
        buttons: {
            approve: 'Approve',
            reject: 'Reject',
            searchUser: 'Search User',
            checkout: 'Checkout',
            return: 'Return',
            addEquipment: 'Add Equipment',
            markRepaired: 'Mark as Repaired'
        },
        headers: {
            readyPickup: 'Ready for Pickup',
            borrowed: 'Currently Borrowed',
            addNew: 'Add New Equipment'
        },
        form: {
            name: 'Equipment Name',
            type: 'Type',
            brand: 'Brand',
            model: 'Model',
            sn: 'Serial Number',
            price: 'Price',
            location: 'Location',
            uIdPlaceholder: 'User Database ID'
        },
        placeholders: {
            name: 'e.g. MacBook Pro 2023',
            type: 'Select Equipment Type',
            brand: 'e.g. Apple',
            model: 'e.g. M2 Max',
            sn: 'Enter Serial Number',
            price: 'Price in CNY',
            location: 'e.g. Room 301, Shelf A'
        },
        repairModal: {
            title: 'Mark as Repaired',
            noteLabel: 'Repair Note',
            notePlaceholder: 'Describe the repair details...',
            cancel: 'Cancel',
            confirm: 'Confirm'
        }
    }
}
