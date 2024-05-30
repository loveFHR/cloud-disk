<template>
    <div class="login">
        <div>
            {{ msg }}
        </div>
        <el-form
        size="small"
        ref="ruleFormRef"
        :model="ruleForm"
        status-icon
        :rules="rules"
        label-width="70px"
        class="demo-ruleForm"
    >
        <el-form-item label="E-mail" prop="email">
            <el-input v-model="ruleForm.email" />
        </el-form-item>
        <el-form-item label="密码" prop="password">
            <el-input v-model="ruleForm.password" type="password" />
        </el-form-item>
        <el-form-item>
            <el-button type="primary" @click="submitForm(ruleFormRef)"
            >登录</el-button>
        </el-form-item>
    </el-form>
    <div>
        <el-radio label="记住我" size="large" v-model="remember_me">记住我</el-radio>
        <el-link type="primary">注册</el-link>
        <el-link type="info">忘记密码？</el-link>
    </div>
    </div>
</template>

<script setup lang="ts">

import { reactive, ref } from 'vue'
import type { FormInstance, FormRules } from 'element-plus'
import useUser from '../store/user.ts'
import {$Login} from '../api/user'
import { useRouter } from 'vue-router'; 
const router=useRouter()
const userStore=useUser()
const msg=ref("")
const ruleFormRef = ref<FormInstance>()
const ruleForm=reactive({
    email:'',
    password:'',
})
const remember_me = ref(true)
// const validateEmail = (rule: any, value: any, callback: any) => {
//   if (value === '') {
//     callback(new Error('Please input the E-mail'))
//   } else {
//     callback()
//   }
// }
const validatePassword = (rule: any, value: any, callback: any) => {
  if (value === '') {
    callback(new Error('Please input the password'))
  } else {
    callback()
  }
}

const rules = reactive<FormRules<typeof ruleForm>>({
    // email: [{ validator: validateEmail, trigger: 'blur' }],
  password: [{ validator: validatePassword, trigger: 'blur' }],
})

const submitForm = (formEl: FormInstance | undefined) => {
  if (!formEl) return
  formEl.validate(async(valid) => {
    if (valid) {
        console.log("ruleForm",ruleForm);
        let r=await $Login(ruleForm)
        router.push('/admin')
        // if(r){
        //     let user=await $getInfo({username:ruleForm.username})
        //     router.push('/admin')
        // }
    } else {
      return false
    }
  })
}
</script>

<style scoped lang="scss">
.login{
    margin: 15vh auto;
    padding: 5vh 2vw;
    width: 27vw;
    height: 50vh;
    // background:linear-gradient(to bottom,rgb(144, 162, 255),rgb(182, 193, 255));
    background-color: #dae4ff;
    ::v-deep .el-form-item__label{
        color: rgb(98, 82, 219);
        font-weight: 700;
        font-size: large;
    }
}
</style>