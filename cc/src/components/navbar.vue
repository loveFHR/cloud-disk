<template>
    <div class="container" v-if="userStore.user.id">
        <div class="logo">
            <div style="overflow: hidden;margin-top: 1vh;margin-left: 1vw;"><img style="border-radius: 70%;width: 6.5vw;" src="../assets/yp.svg" alt="YP"/></div>
        </div>
        <div class="upbar">
        <el-menu background-color='rgb(232, 243, 253)'
                :default-active="1"
                class="el-menu-demo"
                mode="horizontal"
                :ellipsis="false"
                @select="handleSelect"
            >
                <el-menu-item index="0">
                    <div class="yp">洋盘</div>
                </el-menu-item>

                <el-menu-item index="1" style="width: 4vw;" v-if="!userStore.user.admin"><router-link :to="{name:'home'}" style="color: rgb(101, 141, 229); font-size: 17px; font-weight: 700;">主页</router-link></el-menu-item>
                <el-menu-item index="2" style="width: 4vw;margin-left: 1vw;" v-if="!userStore.user.admin"><router-link :to="{name:'shareLook'}" style="color: rgb(101, 141, 229); font-size: 17px; font-weight: 700;">分享</router-link></el-menu-item>
                <el-menu-item index="3" style="width: 5vw;margin-left: 1vw;" v-if="!userStore.user.admin"><router-link :to="{name:'department'}" style="color: rgb(101, 141, 229); font-size: 17px; font-weight: 700;">部门文件</router-link></el-menu-item>
                <el-menu-item index="4" style="width: 5vw;margin-left: 1vw;" v-if="userStore.user.admin"><router-link :to="{name:'usermanage'}" style="color: rgb(101, 141, 229); font-size: 17px; font-weight: 700;">用户管理</router-link></el-menu-item>
                <el-menu-item index="5" style="width: 5vw;margin-left: 1vw;" v-if="userStore.user.admin"><router-link :to="{name:'departmentmanage'}" style="color: rgb(101, 141, 229); font-size: 17px; font-weight: 700;">部门管理</router-link></el-menu-item>
                <div class="flex-grow" />
                <el-sub-menu v-if="userStore.user.id">
                    <template #title>
                        <div style="margin-right: 1vw;">
                            <el-avatar :size="55" :src="userStore.user.avatar" />
                        </div>
                        <span>{{userStore.user.userName}}</span>
                    </template>
                    <el-menu-item ><router-link :to="{name:'account'}" index="2-1" style="color: rgb(101, 141, 229); font-size: 17px; font-weight: 700;">个人中心</router-link></el-menu-item>
                    <el-menu-item index="2-2" @click="logout"><span>登出</span></el-menu-item>
                </el-sub-menu>
            </el-menu>
        </div>
    </div>
</template>

<script setup lang="ts">
import useUser from '../store/user.ts'
import {useRoute} from 'vue-router'
import { ref } from 'vue'

const userStore=useUser()
console.log("user",userStore.user);
const activeIndex = ref('1')
const handleSelect = (key: string, keyPath: string[]) => {
  console.log(key, keyPath)
}

const logout=()=>{
    location.reload();
    localStorage.removeItem("token")
    userStore.clearUser();
}
</script>

<style scoped>
.container{
    /* border: 2px black  dotted; */
    display: flex;
    justify-content: center;
    margin: 1vh auto;
    width: 80vw;
    margin-bottom: 1.5vh;
    background-color: rgb(199, 225, 255);
    border-radius: 13px;
}
.upbar{
    width: 69vw;
    height: 6vh;
    margin: 1.5vh auto ;
    background-color: rgb(232, 243, 253);
}
.flex-grow {
  flex-grow: 1;
}
.yp{
    font-family: "Doodle Fonts", cursive;
    font-size: 33px;
    color: rgb(83, 111, 255);
    text-shadow: 3px 3px 5px rgba(194, 190, 255, 0.815);
    font-weight: 600;
}
.el-menu-item a {
  text-decoration: none;
}
span{
    font-size: 17px;
  font-weight: 600;
  color: rgb(101, 137, 213);
}
.upbar >>> .el-menu-item:hover {
  background: #aad0fc !important;
}
.upbar >>> .el-submenu__title:hover {
    background: #ff95c8 !important;
}
</style>