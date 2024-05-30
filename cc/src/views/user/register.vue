<template>
    <div style="overflow: hidden;margin: 4vh auto;display: flex; justify-content: center; align-items: center;"><img style="border-radius: 70%;width: 10vw;" src="../../assets/yp.svg" alt="YP"/></div>
    <div class="con">
        <div class="yp">洋盘</div>
    <div class="container">
        <el-form ref="ruleFormRef" :model="ruleForm" status-icon :rules="rules" label-width="70px" class="demo-ruleForm">
            <el-form-item label="用户名" prop="userName">
              <el-input v-model="ruleForm.userName" />
            </el-form-item>
            <el-form-item label="E-mail" prop="email">
              <el-input v-model="ruleForm.email" />
            </el-form-item>
            <el-form-item label="手机号码" prop="phone">
              <el-input v-model="ruleForm.phone" />
            </el-form-item>
            <el-form-item label="密码" prop="password">
              <el-input v-model="ruleForm.password" type="password" />
            </el-form-item>
            <el-form-item label="确认密码" prop="cfpassword">
              <el-input v-model="ruleForm.cfpassword" type="password" />
            </el-form-item>
            <el-form-item>
              <el-popover
                placement="bottom"
                :visible="visible"
                title="注册失败"
                :width="200"
                trigger="click"
                :content="msg">
                <template #reference>
                  <el-button class="m-2" type="primary" @click="submitForm(ruleFormRef)&&visible!=visible" style="width: 16vw;">注册</el-button>
                </template>
              </el-popover>
            </el-form-item>
            <el-form-item>
              <el-checkbox v-model="contract" label="" size="large" />
              <router-link :to="{name:'secret'}" style="text-decoration: none;"  target="_blank">同意《隐私政策》</router-link>
            </el-form-item>
          </el-form>
        </div>
    </div>
</template>
<script setup lang="ts">

import { reactive, ref ,defineEmits} from 'vue'
import type { FormInstance, FormRules } from 'element-plus'
import useUser from '../../store/user.ts'
import {$register,$Login} from '../../api/user'
import { useRouter } from 'vue-router';
  const router = useRouter();
const msg=ref("")
const visible = ref(false)
const contract=ref(false)
const ruleForm=reactive({
    email: "",
    password: "",
    phone: null,
    userName: "",
    cfpassword:"",
})
const userStore=useUser()
const ruleFormRef = ref<FormInstance>()

const validateEmail = (rule: any, value: any, callback: any) => {
    if (value === '') {callback(new Error('E-mail不能为空'))} else {callback()}
}
const validatePassword = (rule: any, value: any, callback: any) => {
    if (value === '') {callback(new Error('密码不能为空'))} else {callback()}
}
const validatePhone = (rule: any, value: any, callback: any) => {
    if (value === '') {callback(new Error('手机号码不能为空'))} else {callback()}
}
const validateUserName = (rule: any, value: any, callback: any) => {
    if (value === '') {callback(new Error('用户名不能为空'))} else {callback()}
}
const validateCfpassword = (rule: any, value: any, callback: any) => {
  if (value === '') {
        callback(new Error('请再次输入密码'))
    } else if (value !== ruleForm.password) {
        callback(new Error("两次输入的密码不匹配"))
    } else {
        callback()
    }
}

const rules = reactive<FormRules<typeof ruleForm>>({
    email: [{ validator: validateEmail, trigger: 'blur' }],
    password: [{ validator: validatePassword, trigger: 'blur' }],
    phone: [{ validator: validatePhone, trigger: 'blur' }],
    userName: [{ validator: validateUserName, trigger: 'blur' }],
    cfpassword: [{ validator: validateCfpassword, trigger: 'blur' }],
})

const submitForm = (formEl: FormInstance | undefined) => {
    if (!formEl) return
    formEl.validate(async(valid) => {
        if (valid) {
          if(!contract.value){
            ElMessage.error('请同意《隐私政策》后注册');
              return;
          }
            let r=await $register({
                  'email': ruleForm.email,
                  'password': ruleForm.password,
                  'phone': parseInt(ruleForm.phone),
                  'userName': ruleForm.userName,
            })
            visible.value=false;
            if(r===""){
              visible.value=false;
                let r1=await $Login({
                  'email':ruleForm.email,
                  'password':ruleForm.password
                })
                if(r1!=null){
                    userStore.setUserLogin(r1);
                    if(!r1.admin){
                        router.push('/');
                    }else{
                        router.push('/admin/user/manage');
                    }
                }
            }else{
              visible.value=true;
              msg.value=r;
            }
      }
    })
}
</script>
<style scoped lang="scss">
    .yp{
      font-family: "宋体", "SimSun";
      font-size: 33px;
      color: rgb(87, 176, 255);
      text-shadow: 3px 3px 5px rgba(212, 218, 255, 0.685);
      font-weight: 600;
      width: 24vw;
      text-align: center;
      padding-top: 2vh;
      padding-bottom: 2vh;
  }
  .con{
      border: 2px dodgerblue dotted;
      margin: 3vh auto;
      width: 24vw;
      height: 57vh;
      padding-left: 1vw;
      border-radius: 14px;
  }
    .container{
      display: flex;
      align-items: center; /* 垂直居中 */
    }
    .el-button--text {
      margin-right: 15px;
    }
    .el-select {
      width: 300px;
    }
    .el-input {
      width: 16vw;
    }
    </style>