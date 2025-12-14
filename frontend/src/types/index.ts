export interface User {
    userId: number;
    username: string;
    fullName: string;
    role: 'admin' | 'user';
    employeeStudentId: string;
}

export interface Equipment {
    equipmentId: number;
    equipmentName: string;
    typeId: number;
    typeName?: string;
    brand: string;
    model: string;
    serialNumber: string;
    price: number;
    storageLocation: string;
    status: '可用' | '借出' | '维护中' | '损坏';
    damageDescription?: string;
    damageTime?: string;
    resolutionNotes?: string;
}

export interface Reservation {
    reservationId: number;
    userId: number;
    userName?: string;
    equipmentId: number;
    equipmentName?: string;
    scheduledBorrowDate: string;
    scheduledReturnDate: string;
    status: '待审核' | '已批准' | '已拒绝' | '已过期';
    reservationTime: string;
    notes?: string;
}

export interface BorrowRecord {
    recordId: number;
    userId: number;
    userName?: string;
    equipmentId: number;
    equipmentName?: string;
    actualBorrowTime: string;
    expectedReturnTime: string;
    actualReturnTime?: string;
    borrowStatus: '使用中' | '已归还' | '逾期' | '损坏';
    notes?: string;
}
