<template>
    <div class="container">
        <div><upload :path1="path1" @percent="percent" @load="load"/></div>
        <div><share :rename_sel="rename_sel"/></div>
        <el-popover
            placement="top-start"
            title="部门共享"
            :width="200"
            trigger="hover"
            content="更改部门共享状态(共享->未共享,未共享->共享)"
        >
        <template #reference>
            <el-button type="primary" @click="departmentShare" text>部门共享</el-button>
            </template>
        </el-popover>
        <div><el-button type="primary" @click="download" text>下载</el-button></div>
        <div><el-button type="primary" @click="collect" text>收藏</el-button></div>
        <div><el-button type="primary" @click="remove" text>删除</el-button></div>
        <div><rename :rename_sel="rename_sel" :path1="path1" @load="load" /></div>
    </div>
    <div>
        <el-progress :percentage="percentage" v-if="isPer"/>
    </div>
</template>

<script setup lang="ts">
import upload from '../components/fileop/upload.vue';
import { ref,watch,defineProps,defineEmits} from 'vue'
import {$collect,$delete,$download,$move,$share_status} from '../api/fileop'
import rename from '../components/fileop/rename.vue';
import share from '../components/fileop/share.vue';

const emits=defineEmits(['load1']);
const load=(path:String,f:Boolean)=>{
    emits('load1');
}
const percentage=ref(0)
const isPer=ref(false)
const percent=(per)=>{
    if(per<0) {
        isPer.value=false;
        percentage.value=0;
    }else if(per===0){
        isPer.value=true;
        percentage.value=0;
    }else{
        percentage.value=per;
    }
}
const props=defineProps({
    collect_sel:Array<any>,
})
const ids=ref([])
const rename_sel=ref([])
const path1=ref('');
watch(()=>props.collect_sel,()=>{
    rename_sel.value=props.collect_sel;
    for(let i=0;i<props.collect_sel.length;i++){
        ids.value.push(props.collect_sel[i].id)
    }
})
const collect = async ()=>{
    if(ids.value.length===0) return;
    let r=await $collect(ids.value);
    if(r==="success"){
        ids.value=[]
        emits('load1')
        ElMessage({
        message: '收藏成功',
        type: 'success',
      })
    }else{
        ElMessage.error(r);
    }
}
const remove=async()=>{
    if(ids.value.length===0) return;
    let r=await $delete(ids.value);
    if(r==="success"){
        ids.value=[]
        emits('load1')
        ElMessage({
        message: '已放入回收站',
        type: 'success',
      })
    }else{
        ElMessage.error(r);
    }
}
const download=async()=>{
    for(const file of rename_sel.value){
        if(file.isDir){
            ElMessage.error('不能下载文件夹');
            continue;
        }
        const id=file.id;
        const filename=file.fileName;
        let r=await $download(id);
        console.log(r);
        let blobURL=URL.createObjectURL(r);
        let link=document.createElement("a");
        link.href=blobURL;
        link.download=filename;
        link.style.display='none';
        link.click();
    }
}

const departmentShare=async()=>{
    if(userStore.user.groupName==='暂无'){
        ElMessage.error('未加入部门，无法共享');
        return;
    }
    if(rename_sel.value.length===0) return;
    let f=true;
    for(const file of rename_sel.value){
        const id=file.id;
        let r=await $share_status({
            id,
            isSharing:1-file.share,
        });
        if(!r){
            f=false;
        }
    }
    if(f){
        ElMessage({
            message: '更改共享状态成功',
            type: 'success',
        })
        emits('load1')
    }
}
</script>

<style scoped>
.container{
    /* border: 2px black dashed; */
    width: 31vw;
    height: 5vh;
    display: flex;
    margin: 5px;
}
</style>