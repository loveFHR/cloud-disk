<template>
    <div style="overflow: hidden;margin: 4vh auto;display: flex; justify-content: center; align-items: center;"><img style="border-radius: 70%;width: 10vw;" src="../../assets/yp.svg" alt="YP"/></div>
    <div class="con">
        <div class="yp">洋盘</div>
        <div class="container">
            <el-form ref="ruleFormRef" :model="ruleForm" status-icon :rules="rules" label-width="70px" class="demo-ruleForm">
                <el-form-item label="E-mail" prop="email">
                <el-input v-model="ruleForm.email" />
                </el-form-item>
                <el-form-item label="新密码" prop="password">
                <el-input v-model="ruleForm.password" type="password" />
                </el-form-item>
                <el-form-item label="确认密码" prop="cfpassword">
                <el-input v-model="ruleForm.cfpassword" type="password" />
                </el-form-item>
                <el-form-item label="验证码" prop="verifyCode">
                    <el-input v-model="ruleForm.verifyCode" style="width: 10vw;"></el-input>
                    <el-button type="primary" :loading="checkCodeBtn.loading" :disabled="checkCodeBtn.disabled"
                            @click="getCheckCode" style="margin-left: 5px;width: 5.6vw;">{{ checkCodeBtn.text }}</el-button>
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
                    <el-button class="m-2" type="primary" @click="submitForm(ruleFormRef)&&visible!=visible" style="width: 16vw;">重置密码</el-button>
                    </template>
                </el-popover>
                </el-form-item>
            </el-form>
        </div>
    </div>
</template>
<script setup lang="ts">
import { reactive, ref ,defineEmits} from 'vue'
import type { FormInstance, FormRules } from 'element-plus'
import useUser from '../../store/user.ts'
import {$sendVerifycode,$Login,$reset} from '../../api/user'
import type { ElMessage } from 'element-plus'
import { useRouter } from 'vue-router';
  const router = useRouter();
const msg=ref("")
const visible = ref(false)

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
    height: 45vh;
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