<template>
    <div class="lebar">
          <el-menu :default-active="$route.path"  active-text-color="rgb(80, 122, 248)" 
          background-color="rgb(212, 223, 255)"
          text-color="rgb(60, 62, 68)" router>
            <el-menu-item index="/">
                <el-icon><Menu /></el-icon>
                <span>全部文件</span>
            </el-menu-item>
            <el-menu-item index="/picture">
                <el-icon><Picture /></el-icon>
                <span>图片</span>
            </el-menu-item>
            <el-menu-item index="/document">
                <el-icon><document /></el-icon>
                <span>文档</span>
            </el-menu-item>
            <el-menu-item index="/video">
                <el-icon><VideoPlay /></el-icon>
                <span>视频</span>
            </el-menu-item>
            <el-menu-item index="/music">
                <el-icon><Headset /></el-icon>
                <span>音乐</span>
            </el-menu-item>
            <el-menu-item index="/collect" style="margin-top: 1vh;">
                <el-icon><Star /></el-icon>
                <span>收藏夹</span>
            </el-menu-item>
            <el-menu-item index="/recycle">
                <el-icon><Delete /></el-icon>
                <span>回收站</span>
            </el-menu-item>
            <div>
                <Cap/>
            </div>
        </el-menu>
    </div>
    
</template>

<script setup lang="ts">
import { ref} from 'vue'
import Cap from './user/Cap.vue'
</script>

<style  lang="scss" scoped>
.lebar{
    /* border: 2px black dotted; */
    float: left;
    width: 11.5vw;
    height: 75vh;
    margin-right: 1.7vw;
    margin-top: 1vh;
    background-color: rgb(212, 223, 255);
    padding: 3px;
    border-radius: 13px;
}
.el-menu-item:hover{
    background: #98a8f7!important;
    color: #fff!important;
}
.cap{
    margin-top: 1vh;
}
</style>