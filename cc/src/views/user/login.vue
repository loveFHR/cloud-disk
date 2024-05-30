<template>
    <div style="overflow: hidden;margin: 4vh auto;display: flex; justify-content: center; align-items: center;"><img style="border-radius: 70%;width: 10vw;" src="../../assets/yp.svg" alt="YP"/></div>
    <div class="con">
        <div class="yp">洋盘</div>
    <div class="container">
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
                <el-button class="m-2" type="primary" @click="submitForm(ruleFormRef)&&visible!=visible" style="width:16vw;">登录</el-button>
              </template>
            </el-popover>
          </el-form-item>
          <el-form-item>
            <el-checkbox v-model="remember_me" label="记住我" size="large" />
            <el-checkbox v-model="contract" label="" size="large" />
            <router-link :to="{name:'secret'}" style="text-decoration: none;"  target="_blank">同意《隐私政策》</router-link>
          </el-form-item>
          <el-form-item>
              <div style="margin-left: 1vw;"><router-link :to="{name:'register'}" style="text-decoration: none;">注册</router-link></div>
              <div style="margin: 0 4vw;"></div>
              <div><router-link :to="{name:'reset'}" style="text-decoration: none;">忘记密码？</router-link></div>
          </el-form-item>
      </el-form>
    </div>
</div>
  </template>
  
  <script setup lang="ts">
  
  import { reactive, ref } from 'vue'
  import type { FormInstance, FormRules } from 'element-plus'
  import useUser from '../../store/user.ts'
  import {$Login,$getInfo} from '../../api/user'
  import { useRouter } from 'vue-router';
  const router = useRouter();
  const userStore=useUser()
  const msg=ref("")
  const contract=ref(false)
  const visible = ref(false)
  const ruleFormRef = ref<FormInstance>()
  const remember_me = ref(false)
  const ruleForm=reactive({
      email:'',
      password:'',
  })
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
        visible.value=false;
        if(!contract.value){
        ElMessage.error('请同意《隐私政策》后登录');
          return;
        }
          let r=await $Login(ruleForm)
          if(r.id){
            visible.value=false;
            userStore.setUserLogin(r);
            if(!r.admin){
                router.push('/');
            }else{
                router.push('/admin/user/manage');
            }
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
      height: 50vh;
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