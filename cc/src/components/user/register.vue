<template>
    <el-button link @click="dreg=true">
        注册
    </el-button>
    <el-dialog v-model="dreg" title="注册" style="width: 32vw;height: 60vh;" @close="closeReg">
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
                  <el-button class="m-2" type="primary" @click="submitForm(ruleFormRef)&&visible!=visible" style="width: 71%;margin-left: 5vw;">注册</el-button>
                </template>
              </el-popover>
            </el-form-item>
          </el-form>
    </el-dialog>
</template>
<script setup lang="ts">

import { reactive, ref ,defineEmits} from 'vue'
import type { FormInstance, FormRules } from 'element-plus'
import useUser from '../../store/user.ts'
import {$register,$Login} from '../../api/user'

const dreg = ref(false)
const msg=ref("")
const visible = ref(false)
const formLabelWidth = '140px'

const emits=defineEmits(['closeLog']);

const ruleForm=reactive({
    email: "",
    password: "",
    phone: null,
    userName: "",
    cfpassword:"",
})

const closeReg=()=>{
  ruleForm.email="";
  ruleForm.password="";
  ruleForm.phone="";
  ruleForm.userName="";
  ruleForm.cfpassword="";
  visible.value=false;
  emits('closeLog')
}

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
                    console.log("userstore",userStore.user);
                }
            }else{
              visible.value=true;
              msg.value=r;
            }
      }
    })
}
</script>
<style scoped>
.demo-ruleForm .el-form-item {
  margin-bottom: 2.5vh;
}
.el-button--text {
  margin-right: 15px;
}
.el-select {
  width: 300px;
}
.el-input {
  width: 300px;
}
.dialog-footer button:first-child {
  margin-right: 10px;
}
</style>