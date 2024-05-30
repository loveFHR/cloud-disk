<template>
    <div class="lebar">
        <el-menu :default-active="currentRoute" class="el-menu-vertical-demo" @select="handleMenuSelect" @close="handleClose"
        router>
        <el-menu-item index="">
            <el-icon><Menu /></el-icon>
            <span>全部文件</span>
        </el-menu-item>
        <el-menu-item index="picture">
            <el-icon><Picture /></el-icon>
            <span>图片</span>
        </el-menu-item>
        <el-menu-item index="document">
            <el-icon><document /></el-icon>
            <div>文档</div>
        </el-menu-item>
         
        <el-menu-item index="video">
            <el-icon><VideoPlay /></el-icon>
            <span>视频</span>
        </el-menu-item>
        <el-menu-item index="music">
            <el-icon><Headset /></el-icon>
            <span>音乐</span>
        </el-menu-item>

        <el-menu-item index="other">
            <el-icon><ScaleToOriginal /></el-icon>
            <span>其他</span>
        </el-menu-item>
        <el-menu-item index="collect" style="margin-top: 2vh;">
            <el-icon><Star /></el-icon>
            <span>收藏夹</span>
        </el-menu-item>
        <el-menu-item index="recycle">
            <el-icon><Delete /></el-icon>
            <span>回收站</span>
        </el-menu-item>
        <el-menu-item index="department">
            <el-icon><User /></el-icon>
            <span>部门文件</span>
        </el-menu-item>
        </el-menu>
    </div>
</template>

<script setup lang="ts">
   
    import { useRouter } from 'vue-router';
    import { ref } from 'vue';
    import { $queryDelecteOneFile } from '../../api/recycle';
    import { ElMessageBox,ElNotification } from 'element-plus';
   

    const id=ref(['ids'])
    const router = useRouter();
    let currentRoute = router.currentRoute.value.name;

    const handleMenuSelect = (index: string) => {
        currentRoute = index;
        router.push({ path: `/${index}` });
    };
    const handleClose = (key: string, keyPath: string[]) => {
    console.log(key, keyPath)
    }
    //清空回收站
  const DelecteFile=(id:number)=>{
    ElMessageBox.confirm(
        '确定删除吗?',
        '提示',
        {
            confirmButtonText: '确定',
            cancelButtonText: '取消',   
            type: 'warning'
        }
    )
    .then(async()=>{
      let ret= await $queryDelecteOneFile({id})
      console.log(ret);
      if(ret.success)
      {
          ElNotification.success({
            title: '成功',
            message: '删除成功',
            type:'success'
          })
          //重新加载文件
       
        }
        else{
              ElNotification.error({
                title: '失败',
                message: '删除失败',
                type:'error'
              });
      }
    })
  }  

</script>

<style scoped>
.lebar{
    /* border: 2px black dotted; */
    float: left;
    width: 12.5vw;
    height: 60vh;
    margin-left: 3vw;
}
</style>