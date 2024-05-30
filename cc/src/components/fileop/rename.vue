<template>
<el-button type="primary" text @click="openLog">
    重命名
</el-button>
<el-dialog v-model="dlogin" title="重命名" style="width: 34vw;height: 55vh;background-color: #ebf5ff; border-radius: 12px;" @close="closeall">
    <el-table :data="tableData" height="290">
        <el-table-column label="请修改文件名" width="450">
            <template #default="{ row }">
                <el-input v-model="row.filename" style="width: 20vw;"></el-input>
                <el-icon v-if="row.ch"><CircleCheckFilled /></el-icon>
                <span v-else style="color: rgb(240, 37, 37);font-size: small;">{{row.msg}}</span>
            </template>
        </el-table-column>
    </el-table>
    <el-button type="primary" @click="rename">确认</el-button>
</el-dialog>
    
</template>

<script setup lang="ts">

import { ref,watch,defineProps,defineEmits} from 'vue'
import {$rename} from '../../api/fileop'
import { ElMessage, ElMessageBox } from 'element-plus'
const emits=defineEmits(['load']);

const dlogin = ref(false)
const formLabelWidth = '140px'
const openLog=()=>{
dlogin.value=true;
    if(props.rename_sel.length>0){
        dlogin.value=true;
    }
}

const tableData=ref([])
const btableData=ref([])

const props=defineProps({
    rename_sel:Array<any>,
    path1:String,
});
const path1=ref('');
watch(()=>props.path1,()=>{
    path1.value=props.path1;
})
const clipFileName = (fileName) => {
    const lastSlashIndex = fileName.lastIndexOf('/');
    return fileName.substring(lastSlashIndex + 1);
}
watch(()=>props.rename_sel,()=>{
    console.log(props.rename_sel);
    tableData.value = [];
    btableData.value=[];
    for (const item of props.rename_sel) {
        const filename = clipFileName(item.fileName);
        const lastDotIndex = filename.lastIndexOf('.');
        tableData.value.push({
            filename: filename,
            id: item.id,
            ch:false,
            msg:"",
        });
        btableData.value.push({
            filename:filename,
        });
    }
})
function checkFileName(filename) {
    return filename.includes('/');
}

const rename= async () =>{
    for(let i=0;i<tableData.value.length;i++){
        const f1 = tableData.value[i].filename;
        const f2=btableData.value[i].filename;
        if(f1===f2) continue;
        const lastDotIndex = f1.lastIndexOf('.');
        let f1_x = f1;
        let f1_y ='';
        if(lastDotIndex!==-1){
            f1_x = f1.slice(0, lastDotIndex);
            f1_y = f1.slice(lastDotIndex + 1);
        }
        const lastDotIndex1 = f2.lastIndexOf('.');
        let f2_x = f2;
        let f2_y ='';
        if(lastDotIndex1!==-1){
            f2_x = f2.slice(0, lastDotIndex1);
            f2_y = f2.slice(lastDotIndex1 + 1);
        }
        const id=tableData.value[i].id;
        let msg=""
        if(f1_x!==f2_x&&f1_y===f2_y){
            if(checkFileName(f1)){
                msg='文件名不能含\'/\'';
            }else{
                let r=await $rename({
                    'filename':f1,
                    'id':id,
                });
                if(r==="success"){
                    btableData.value[i].filename=f1;
                }else{
                    msg=r
                }
            }
        }else if(f1_y!==f2_y){
            await ElMessageBox.confirm(
                f1+'如果改变文件扩展名，可能使文件不可用。确定要更改吗',
                '重命名',
                {
                    confirmButtonText: '确认',
                    cancelButtonText: '取消',
                    type: 'warning',
                }
            )
            .then(async () => {
                if(checkFileName(f1)){
                    msg='文件名不能含\'/\'';
                }else{
                    let r1=await $rename({
                        'filename':f1,
                        'id':id,
                    });
                    if(r1==="success"){
                        btableData.value[i].filename=f2;
                    }else{
                        msg=r1
                    }
                }
            })
            .catch(() => {
                
            })
        }
        if(msg===""){
            tableData.value[i].ch=true;
        }else{
            tableData.value[i].msg=msg;
        }
    }
    emits('load',path1.value,false);
}

</script>
<style scoped lang="scss">

</style>