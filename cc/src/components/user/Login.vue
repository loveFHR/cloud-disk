<template>
  <el-button link @click="openLog">
    登录
  </el-button>

  <el-dialog v-model="dlogin" title="登录" style="width: 32vw;height: 60vh;" @close="closeall">
      <el-form
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
          <el-popover
            placement="bottom"
            :visible="visible"
            title="登录失败"
            :width="200"
            trigger="click"
            :content="msg"
          >
            <template #reference>
              <el-button class="m-2" type="primary" @click="submitForm(ruleFormRef)&&visible!=visible" style="width: 85%;">登录</el-button>
            </template>
          </el-popover>
        </el-form-item>

        <el-form-item><el-checkbox v-model="remember_me" label="记住我" size="large" /></el-form-item>
        <el-form-item style="margin: 0 auto;">
            <div style="margin-left: 2.5vw;"><register @closeLog="closeLogin" /></div>
            <div style="margin: 0 4vw;"></div>
            <div><reset @closeLog="closeLogin" /></div>
        </el-form-item>
    </el-form>
  </el-dialog>
    
</template>

<script setup lang="ts">

import { reactive, ref } from 'vue'
import type { FormInstance, FormRules } from 'element-plus'
import useUser from '../../store/user.ts'
import {$Login,$getInfo} from '../../api/user'
import register from './register.vue'
import reset from './reset.vue'

const dlogin = ref(false)
const formLabelWidth = '140px'
const openLog=()=>{
  dlogin.value=true;
}
const closeLogin=()=>{
  dlogin.value=false
}
const userStore=useUser()

async function fetchData() {
  let t = localStorage.getItem("token");
  if (t != null) {
    userStore.setToken(t);
    let r = await $getInfo({});
    userStore.setUserInfo(r);
  }
}
fetchData();

const msg=ref("")
const visible = ref(false)
const ruleFormRef = ref<FormInstance>()
const remember_me = ref(false)
const ruleForm=reactive({
    email:'',
    password:'',
})
const closeall=()=>{
  ruleForm.email="";
  ruleForm.password="";
  visible.value=false;
}
const validateEmail = (rule: any, value: any, callback: any) => {
  if (value === '') {
    callback(new Error('E-mail不能为空'))
  } else {
    callback()
  }
}
const validatePassword = (rule: any, value: any, callback: any) => {
  if (value === '') {
    callback(new Error('密码不能为空'))
  } else {
    callback()
  }
}

const rules = reactive<FormRules<typeof ruleForm>>({
    email: [{ validator: validateEmail, trigger: 'blur' }],
  password: [{ validator: validatePassword, trigger: 'blur' }],
})

const submitForm = (formEl: FormInstance | undefined) => {
  if (!formEl) return
  formEl.validate(async(valid) => {
    if (valid) {
        let r=await $Login(ruleForm)
        visible.value=false;
        if(r.id){
          visible.value=false;
          userStore.setUserLogin(r);
          console.log(r.token);
          if(remember_me.value===true){
            localStorage.setItem("token",r.token);
          }else{
            localStorage.removeItem("token");
          }
        }else{
          visible.value=true;
          msg.value=r.msg;
        }
    }
  })
}
</script>
<style scoped lang="scss">
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