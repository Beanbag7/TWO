import { createRouter, createWebHistory } from 'vue-router'

const routes = [
    {
        path: '/login',
        name: 'Login',
        component: () => import('../views/Login.vue')
    },
    {
        path: '/',
        name: 'Home',
        component: () => import('../views/Home.vue'), // Main Layout
        redirect: '/equipment',
        children: [
            {
                path: 'equipment',
                name: 'EquipmentList',
                component: () => import('../views/EquipmentList.vue')
            },
            {
                path: 'my-reservations',
                name: 'MyReservations',
                component: () => import('../views/MyReservations.vue')
            },
            // Admin routes
            {
                path: 'admin',
                name: 'AdminDashboard',
                component: () => import('../views/AdminDashboard.vue')
            }
        ]
    }
]

const router = createRouter({
    history: createWebHistory(),
    routes
})

router.beforeEach((to, _from, next) => {
    const user = localStorage.getItem('user')
    if (to.path !== '/login' && !user) {
        next('/login')
    } else {
        next()
    }
})

export default router
