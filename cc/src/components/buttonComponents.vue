<template>
    <div class="container">
        <div class="fbtn">
            <div style="display: flex;">
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
                <div><mkdir :path1="path1" @load="load"/></div>
                <div><el-button type="primary" @click="move" text>剪切</el-button></div>
                <div><el-button type="primary" @click="paste" text>粘贴</el-button></div>
            </div>
        </div>
    </div>
    <div>
        <el-progress :percentage="percentage" v-if="isPer"/>
    </div>
</template>

<script setup lang="ts">
import { Search } from '@element-plus/icons-vue'
import upload from '../components/fileop/upload.vue';
import { ref,watch,defineProps,defineEmits} from 'vue'
import {$collect,$delete,$download,$move,$share_status,$share_store,$search} from '../api/fileop'
import rename from '../components/fileop/rename.vue';
import mkdir from '../components/fileop/mkdir.vue';
import share from '../components/fileop/share.vue';
import useUser from '../store/user.ts'
import useMovefile from '../store/movefile.ts'
const movefileStore = useMovefile()
const userStore=useUser()

const emits=defineEmits(['load1']);

const load=(path:String,f:Boolean)=>{
    emits('load1',path,f);
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
const enterSearch=()=>{

}
const props=defineProps({
    collect_sel:Array<any>,
    path1:String,
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
watch(()=>props.path1,()=>{
    path1.value=props.path1;
})
const collect = async ()=>{
    if(ids.value.length===0) return;
    let r=await $collect(ids.value);
    if(r==="success"){
        ids.value=[]
        emits('load1',path1.value,false)
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
        emits('load1',path1.value,false)
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

const move=()=>{
    movefileStore.clearMovefile();
    for(const f of rename_sel.value){
        movefileStore.setMovefile(f.id);
    }
    movefileStore.setIsShare(false);
    ElMessage({
        message: '剪切成功，请到文件夹下粘贴',
        type: 'success',
    })
}
const paste=async()=>{
    if(typeof movefileStore.movefile.files ==='undefined'||movefileStore.movefile.files.length===0){
        return;
    }
    console.log("movf",movefileStore.movefile.is_share);
    if(movefileStore.movefile.is_share){
        let r1=await $share_store({
            ids:movefileStore.movefile.files,
            path:path1.value,
            token:userStore.user.shareToken,
        });
        if(r1){
            emits('load1',path1.value,true)
            ElMessage({
                message: '粘贴成功',
                type: 'success',
            })
        }else{
            ElMessage.error(msg+' 粘贴失败');
        }
    }else{
        let f=true;
        let msg=""
        for(const id of movefileStore.movefile.files){
            let r=await $move({
                id:id,
                path:path1.value,
            });
            if(r!==""){
                f=false;
                msg=r;
            }
        }
        if(f){
            emits('load1',path1.value,true)
            ElMessage({
                message: '粘贴成功',
                type: 'success',
            })
        }else{
            ElMessage.error(msg+' 粘贴失败');
        }
    }
    movefileStore.clearMovefile();
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
        emits('load1',path1.value,false)
    }
}
</script>

<style scoped>
.fbtn{
    margin-bottom: 3vh;
}
.searchbox{
    width: 16vw;
    float: right;
}
.container{
    /* border: 2px black dashed; */
    width: 46vw;
    height: 5vh;
    display: flex;
    margin: 5px;
}
.input-with-select  {
  background-color: var(--el-fill-color-blank);
}
</style>