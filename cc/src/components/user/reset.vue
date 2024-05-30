<template>
    <el-button link @click="dreg=true">
        忘记密码？
    </el-button>
    <el-dialog v-model="dreg" title="重置密码" style="width: 32vw;height: 60vh;" @close="closeReg">
        <el-form ref="ruleFormRef" :model="ruleForm" status-icon :rules="rules" label-width="70px" class="demo-ruleForm">
            <el-form-item label="E-mail" prop="email">
              <el-input v-model="ruleForm.email" />
            </el-form-item>
            <el-form-item label="新密码" prop="password">
              <el-input v-model="ruleForm.password" type="password" />
            </el-form-item>
            <el-form-item label="确认新密码" prop="cfpassword">
              <el-input v-model="ruleForm.cfpassword" type="password" />
            </el-form-item>
            <el-form-item label="验证码" prop="verifyCode">
                <el-input v-model="ruleForm.verifyCode" style="width: 13vw;"></el-input>
                <el-button type="primary" :loading="checkCodeBtn.loading" :disabled="checkCodeBtn.disabled"
                        @click="getCheckCode" style="margin-left: 5px">{{ checkCodeBtn.text }}</el-button>
            </el-form-item>
            <el-form-item>
              <el-popover
                placement="bottom"
                :visible="visible"
                title="重置密码失败"
                :width="200"
                trigger="click"
                :content="msg">
                <template #reference>
                  <el-button class="m-2" type="primary" @click="submitForm(ruleFormRef)&&visible!=visible" style="width: 71%;margin-left: 5vw;">重置密码</el-button>
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
import {$sendVerifycode,$Login,$reset} from '../../api/user'
import type { ElMessage } from 'element-plus'

const dreg = ref(false)
const msg=ref("")
const visible = ref(false)
const formLabelWidth = '140px'

const emits=defineEmits(['closeLog']);

const closeReg=()=>{
    ruleForm.email="";
    ruleForm.password="";
    ruleForm.cfpassword="";
    ruleForm.verifyCode="";
    visible.value=false
    emits('closeLog')
}
const userStore=useUser()
const ruleFormRef = ref<FormInstance>()
const ruleForm=reactive({
    email: "",
    password: "",
    cfpassword:"",
    verifyCode:"",
})
const validateEmail = (rule: any, value: any, callback: any) => {
    if (value === '') {callback(new Error('E-mail不能为空'))} else {callback()}
}
const validatePassword = (rule: any, value: any, callback: any) => {
    if (value === '') {callback(new Error('密码不能为空'))} else {callback()}
}
const validateCfpassword = (rule: any, value: any, callback: any) => {
    if (value === '') {
        console.log("value",ruleForm.password);

        callback(new Error('确认密码不能为空'))
    } 
    else {
        if(value!=ruleForm.password) {
            callback(new Error('密码与确认密码不一致'))
        }
        else callback()
    }
}
const validateCode = (rule: any, value: any, callback: any) => {
    if (value === '') {callback(new Error('验证码不能为空'))} else {callback()}
}

let checkCodeBtn = reactive<any>({
    text: '获取验证码',
    loading: false,
    disabled: false,
    duration: 60,
    timer: null
})
const f=ref(false);
// 根据用户名获取验证码
const getCheckCode = async () => {
    if(checkCodeBtn.duration !== 60) return
    if (ruleForm.email === null || ruleForm.email.trim() === '') {
        return;
    }
    let r=await $sendVerifycode(ruleForm.email)
    console.log(r);
    if(r!=="") {
        ElMessage.error(r)
        return;
    }
    f.value=true;
    // 倒计时期间按钮不能单击
    if (checkCodeBtn.duration !== 60) {
        checkCodeBtn.disabled = true
    }
    // 清除掉定时器
    checkCodeBtn.timer && clearInterval(checkCodeBtn.timer)
    // 开启定时器
    checkCodeBtn.timer = setInterval(() => {
    const tmp = checkCodeBtn.duration--
    checkCodeBtn.text = `${tmp}秒`
    if (tmp <= 0) {
        // 清除掉定时器
        clearInterval(checkCodeBtn.timer)
        checkCodeBtn.duration = 60
        checkCodeBtn.text = '重新获取'
        // 设置按钮可以单击
        checkCodeBtn.disabled = false
    }
    // console.info(checkCodeBtn.duration)
    }, 1000)
}

const rules = reactive<FormRules<typeof ruleForm>>({
    email: [{ validator: validateEmail, trigger: 'blur' }],
    password: [{ validator: validatePassword, trigger: 'blur' }],
    cfpassword: [{ validator: validateCfpassword, trigger: 'blur' }],
    verifyCode: [{ validator: validateCode, trigger: 'blur' }],
})

const submitForm = (formEl: FormInstance | undefined) => {
    if (!formEl) return
    formEl.validate(async(valid) => {
        if (valid) {
            if(!f.value) return;
            let r=await $reset({
                "code": ruleForm.verifyCode,
                "email": ruleForm.email,
                "password": ruleForm.password,
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
