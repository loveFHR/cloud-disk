import {defineStore} from 'pinia'
import { ref } from 'vue'
import { adminGetInfo } from '@/api/admin.js'

//管理员模块
export const useAdminStore = defineStore('admin',() => {
    const token = ref('')

    const admin = ref({})
    const setToken = (newToken) => {
        token.value = newToken
    }
    const removeToken = () => {
        token.value = ''
    }
    const getAdmin = async () => {
        const res = await adminGetInfo() //获取管理员数据
        console.log(res)
        admin.value = res.data.data
    }
    return {
        token,
        admin,
        setToken,
        removeToken,
        getAdmin
    },
    {
        persist: true // 持久化
    }
})