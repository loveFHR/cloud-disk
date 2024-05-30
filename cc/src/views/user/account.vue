<template>
    <div class="container">
        <el-card class="box-card" body-style="margin: 2vh auto">
            <template #header>
                <div class="card-header">
                    <span>个人中心</span>
                    <el-button class="button" @click="modify">{{mbtn}}</el-button>
                </div>
            </template>
            <div style="margin-right: 4vw ;margin-left: 3vw; margin-top: 2vh;">
                <div style="float: right;">
                  <div v-if="!md">
                    <img :src="userStore.user.avatar" style="width: 17vw; border: 2px solid #d8e4f0; box-shadow: 0 2px 4px rgba(133, 203, 233, 0.2);"/>
                  </div>
                    <div v-else>
                        <div>上传头像</div>
                        <el-upload
                          class="avatar-uploader"
                          :show-file-list="false"
                          :before-upload="beforeAvatarUpload"
                          auto-upload="false"
                          :http-request="updateAvatar"
                        >
                          <img v-if="imageUrl" :src="imageUrl" class="avatar" />
                          <el-icon v-else class="avatar-uploader-icon"><Plus /></el-icon>
                        </el-upload>
                    </div>
                </div>
                <div :class="item">
                    <div>用户名：</div>
                    <div>{{userStore.user.userName}}</div>
                </div>
                <div :class="item">
                    身份：{{identity}}
                </div>
                <div :class="item">
                    <div>E-mail：</div>
                    <div v-if="!md">{{userStore.user.email}}</div>
                    <div style="width: 12vw" v-else><el-input v-model="email" class="w-50 m-2"/></div>
                </div>
                <div :class="item">
                    <div>手机号码：</div>
                    <div v-if="!md">{{userStore.user.phone}}</div>
                    <div style="width: 12vw" v-else><el-input v-model="phone" class="w-50 m-2"/></div>
                </div>
                <div :class="item">
                    所属部门：{{userStore.user.groupName}}
                </div>
                <div :class="item">
                  容量使用情况：{{used}}G/{{total}}G
                </div>
                <div v-if="!md"><el-progress :percentage="cap" :stroke-width="20" striped striped-flow/></div>
                <div :class="item" v-if="md">
                  <div>设置新密码：</div>
                  <div style="width: 12vw; margin-right: 1vw;"><el-input v-model="password1" class="w-50 m-2" type="password"/></div>
                  <div>确认新密码：</div>
                  <div style="width: 12vw; margin-right: 1vw;"><el-input v-model="password2" class="w-50 m-2" type="password"/></div>
                </div>
                <div :class="item" v-if="md">
                  <div>请输入原密码确认修改：</div>
                  <div style="width: 12vw; margin-right: 1vw;"><el-input v-model="cfpassword" class="w-50 m-2" type="password"/></div>
                  <el-button type="primary" @click="modify1">确认修改</el-button>
                </div>
            </div>
        </el-card>
    </div>
</template>
<script lang="ts" setup>
import useUser from '../../store/user.ts'
import { computed,ref } from 'vue';
import {$getInfo,$confirmPassword,$updateEmail,$updatePhone,$updatePassword,$uploadAvatar,$updateAvatar,$getUserCapInfo} from '../../api/user'
import { Plus } from '@element-plus/icons-vue'
import type { UploadProps,ElMessage } from 'element-plus'
import { baseURL_img } from '../../config/baseURL';

const userStore=useUser()
const cap=ref(0.0)
const used=ref(0)
const total=ref(0)
async function fetchData() {
    let r = await $getInfo({});
    userStore.setUserInfo(r);
    let r1=await $getUserCapInfo({});
    used.value=r1.used_cap;
    total.value=r1.all_cap;
    cap.value=(parseFloat(used.value)/parseFloat(total.value)*100).toFixed(2);
    used.value=(r1.used_cap/1024/100).toFixed(2);
    total.value=(r1.all_cap/1024/100).toFixed(2);
}
fetchData();
const identity = computed(() => userStore.user.admin===true?"管理员":"员工");
const md=ref(false)
const email=ref("")
const phone=ref("")
const password1=ref("")
const password2=ref("")
const cfpassword=ref("")
const mbtn=ref("修改信息")
const item=ref("item1")
const modify=()=>{
    if(md.value){
        md.value=false;
        mbtn.value="修改信息";
        item.value="item1"
    }else{
        md.value=true;
        mbtn.value="取消修改";
        item.value="item2"
        email.value=userStore.user.email;
        phone.value=userStore.user.phone;
        password1.value="";
        password2.value="";
        cfpassword.value="";
    }
}
const imageUrl = ref('')
const imageUrl1=ref('')

// const handleAvatarSuccess: UploadProps['onSuccess'] = (response,uploadFile) => {
//     // console.log("response",response);
//     // console.log("ufile",uploadFile);
//   // imageUrl.value = URL.createObjectURL(uploadFile.raw!)
// }

const beforeAvatarUpload: UploadProps['beforeUpload'] = (rawFile) => {
  if (rawFile.type !== 'image/jpeg'&&rawFile.type !== 'image/png') {
     ElMessage.error('格式错误')
    return false
  } else if (rawFile.size / 1024 / 1024 > 2) {
     ElMessage.error('图片大小不能超过2MB')
    return false
  }
  return true
}
const updateAvatar = async (file) => {
  let r=await $uploadAvatar({'avatar':file.file})
  if(r.msg==="success"){
    imageUrl.value=baseURL_img+r.url;
    imageUrl1.value=r.url;
  }
};
const modify1 = async () => {
  let cf = await $confirmPassword({
    "password": cfpassword.value,
  });
  if (cf==="") {
    let f=true;
    if(userStore.user.email!=email.value){
      let r=await $updateEmail({
        'email1':userStore.user.email,
        'email2':email.value,
      })
      if(r===""){
        userStore.setEmail(email.value);
        console.log("success");
      }else{
        ElMessage.error(r+' 邮箱修改失败')
        f=false;
      }
    }
    if(userStore.user.phone!=phone.value){
      let r=await $updatePhone({
        'phone1':userStore.user.phone,
        'phone2':phone.value,
      })
      if(r===""){
        userStore.setPhone(phone.value);
        console.log("success1");
      }else{
        ElMessage.error(r+' 手机号码修改失败')
        f=false;
      }
    }
    if(password1.value){
      let r=await $updatePassword({
        'password1':password1.value,
        'password2':password2.value,
      })
      if(r===""){
        console.log("success2");
      }else{
        ElMessage.error(r+' 密码修改失败')
        f=false;
      }
    }
    if(imageUrl1.value){
      console.log("url",imageUrl1.value);
      let r1=await $updateAvatar({url:imageUrl1.value})
      if(r1==="success"){
        userStore.setAvatar(imageUrl.value);
      }else{
        ElMessage.error(r1+' 头像修改失败')
        f=false;
      }
    }
    if(f){
      modify();
      ElMessage({
        message: '修改成功',
        type: 'success',
      })
    }
  }else{
    ElMessage.error(cf+' 修改失败')
  }
};
</script>

<style scoped>
.card-header {
display: flex;
justify-content: space-between;
align-items: center;
}
.item1 {
margin-top: 6vh;
margin-bottom: 6vh;
display: flex;
align-items: center;
font-size: 16px;
}
.item2 {
margin-top: 5.4vh;
margin-bottom: 4.99vh;
display: flex;
align-items: center;
font-size: 16px;
}

.box-card {
width: 62vw;
margin: 0 auto; /* 水平居中 */
}
.avatar-uploader .avatar {
  width: 178px;
  height: 178px;
  display: block;
}
.el-card ::v-deep .el-card__header {
  background-color: rgb(236, 241, 253);
}
.el-card ::v-deep .el-card__body {
  background-color: rgba(249, 247, 255, 0.938);
}
</style>

<style>
    .avatar-uploader .el-upload {
      border: 1px dashed rgb(128, 179, 255);
      border-radius: 6px;
      cursor: pointer;
      position: relative;
      overflow: hidden;
      transition: rgb(211, 215, 255);
    }
    
    .avatar-uploader .el-upload:hover {
      border-color: var(--el-color-primary);
    }
    
    .el-icon.avatar-uploader-icon {
      font-size: 28px;
      color: #6fa8f8;
      width: 178px;
      height: 178px;
      text-align: center;
    }
    </style>