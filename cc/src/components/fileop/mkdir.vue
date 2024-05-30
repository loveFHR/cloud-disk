<template>
    <el-button type="primary" text @click="openLog" >
        新建文件夹
    </el-button>
    <el-dialog v-model="dlogin" title="新建文件夹" style="width: 29vw; height: 26vh; background-color: #ebf5ff; border-radius: 12px;" @close="closeall" draggable @click="closeMk">
        <div>
            <el-input v-model="filename" style="width: 20vw;"></el-input>
            <el-button type="primary" @click="mkdir" style="margin-left: 1vw;">确认</el-button>
            <span style="color: rgb(243, 74, 74);margin: 6px;">{{msg}}</span>
        </div>
    </el-dialog>
</template>

<script setup lang="ts">
import { ref,watch,defineProps,defineEmits} from 'vue'
import {$rename,$createDir} from '../../api/fileop'
import { ElMessage, ElMessageBox } from 'element-plus'

const emits=defineEmits(['load']);

const dlogin = ref(false)
const formLabelWidth = '140px'
const openLog=()=>{
dlogin.value=true;
}
const filename=ref("");
const msg=ref("");
const path1=ref('')
const props=defineProps({
    path1:String,
});
const closeMk=()=>{
    msg.value="";
    filename.value="";
}
watch(()=>props.path1,()=>{
    path1.value=props.path1;
})
function checkFileName(filename) {
    return filename.includes('/');
}
const mkdir= async () =>{
    if(checkFileName(filename.value)){
        msg.value='文件夹名不能含\'/\'';
        return;
    }
    let r=await $createDir({
        'name':filename.value,
        'path':path1.value,
    });
    msg.value=r;
    if(r==="新建文件夹成功"){
        emits('load',path1.value,true);
    }
}

</script>
<style scoped lang="scss">
  ::v-deep .el-card__body {
    background-color: #d9e2ff !important;
    border-radius: 13px !important;
  }
</style>