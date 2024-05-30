<template>
    <div class="lebar">
        <el-menu :default-active="filehome" class="el-menu-vertical-demo" @select="handleMenuSelect" @close="handleClose">
        <el-menu-item index="filehome">
            <el-icon><Menu /></el-icon>
            <span>全部文件</span>
        </el-menu-item>
        <el-menu-item index="picture">
            <el-icon><Picture /></el-icon>
            <span>图片</span>
        </el-menu-item>
        <el-menu-item index="document">
            <el-icon><document /></el-icon>
            <span>文档</span>
        </el-menu-item>
        <el-menu-item index="video">
            <el-icon><VideoPlay /></el-icon>
            <span>视频</span>
        </el-menu-item>
        <el-menu-item index="music">
            <el-icon><Headset /></el-icon>
            <span>音乐</span>
        </el-menu-item>
        <el-menu-item index="lese">
            <el-icon><ScaleToOriginal /></el-icon>
            <span>其他</span>
        </el-menu-item>
        <el-menu-item index="collect" style="margin-top: 4vh;">
            <el-icon><Star /></el-icon>
            <span>收藏夹</span>
        </el-menu-item>
        <el-menu-item index="recycle">
            <el-icon><Delete /></el-icon>
            <span>回收站</span>
        </el-menu-item>
        </el-menu>
    </div>

    <div class="container">
        <div class="fbtn">
            <div style="display: flex;">
                <div><upload/></div>
                <div><el-button type="primary" @click="share()" text>分享</el-button></div>
                <div><el-button type="primary" @click="download()" text>下载</el-button></div>
                <div><el-button type="primary" @click="collect()" text>收藏</el-button></div>
                <div><el-button type="primary" @click="mkdir()" text>新建文件夹</el-button></div>
                <div><el-button type="primary" @click="remove()" text>删除</el-button></div>
                <div><el-button type="primary" @click="copy()" text>复制到</el-button></div>
                <div><el-button type="primary" @click="rename()" text>重命名</el-button></div>
                <div><el-button type="primary" @click="move()" text>移动到</el-button></div>
            </div>
        </div>
        <div class="searchbox">
            <el-input
              v-model="SearchVal"
              placeholder="搜索"
              class="input-with-select">
              <template #append>
                <el-button :icon="Search" />
              </template>
            </el-input>
        </div>
    </div>
    
</template>

<script setup lang="ts">
    import { Search } from '@element-plus/icons-vue'
    import upload from '../components/fileop/upload.vue';
    import { ref} from 'vue'
    import { useRouter } from 'vue-router';

    const router = useRouter();
    let currentRoute = router.currentRoute.value.name;

    const handleMenuSelect = (index: string) => {
        currentRoute = index;
        router.push({ path: `/${index}` });
    };
    const handleClose = (key: string, keyPath: string[]) => {
    console.log(key, keyPath)
    }
    const SearchVal=ref("")
    const enterSearch=()=>{

    }
</script>

<style scoped>
.fbtn{
        margin-bottom: 3vh;
    }
    .searchbox{
        width: 15vw;
        float: right;
    }
.container{
    width: 60vw;
    padding-top: 3vh;
    padding-left: 5vw;
    display: flex;
}
.input-with-select  {
  background-color: var(--el-fill-color-blank);
}
.lebar{
    float: left;
    width: 12.5vw;
    height: 60vh;
    margin-left: 4vw;
    margin-top: 3vh;
}
</style>