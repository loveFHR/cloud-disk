<template>
    <div class="container" >
        <el-input v-model="token" class="w-50 m-2" placeholder="请输入分享令牌" style="width: 19vw;">
            <template #prefix>
                <el-icon class="el-input__icon"><search /></el-icon>
            </template>
        </el-input>
        <el-button type="primary" plain @click="getShare" style="margin:0 1vw;">获取分享</el-button>
        <div><el-button type="primary" @click="move" text>剪切</el-button></div>
        <div style="float: right;margin: 8px;">分享人：{{userName}}</div>
    </div>
    <div class="table">
        <el-table @select="sel" @select-all="selall" :data="tableData"
         height="450" style="width: 100%"
        @selection-change="handleSelectionChange"
        :default-sort="{ prop: 'changeDate', order: 'descending' }"
        >
          <el-table-column type="selection"  width="35px" />
          <el-table-column  label="" width="40px" >
            <template #default="scope">
              <span v-if="scope.row.isDir">
                 <img src="../../assets/foldor.svg" width="20px" height="20px">
               </span>
               <span v-else-if="scope.row.type==='.jpg'|| scope.row.type==='.png'||scope.row.type==='.jpeg'">
                <img src="../../assets/photo.svg" width="20px" height="20px">
               </span>
               <span v-else-if="scope.row.type==='.dll'">
                <img src="../../assets/dll.svg" width="20px" height="20px">
               </span>
               <span v-else-if="scope.row.type==='.exe'">
                <img src="../../assets/exe.svg" width="20px" height="20px">
               </span>
               <span v-else-if="scope.row.type==='.mp4'">
                <img src="../../assets/videos.svg" width="20px" height="20px">
               </span>
               <span v-else-if="scope.row.type==='.pdf'">
                <img src="../../assets/pdf .svg" width="20px" height="20px">
               </span>
               <span v-else-if="scope.row.type==='.zip'">
                <img src="../../assets/zip.svg" width="20px" height="20px">
               </span>
               <span v-else-if="scope.row.type==='.audio'||scope.row.type==='.mp3'">
                <img src="../../assets/mp3.svg" width="20px" height="20px">
               </span>
               <span v-else-if="scope.row.type==='.ppt'">
                <img src="../../assets/PPT.svg" width="20px" height="20px">
               </span>
               <span v-else-if="scope.row.type==='.svg'">
                <img src="../../assets/SVG.svg" width="20px" height="20px">
               </span>
               <span v-else-if="scope.row.type==='.txt'">
                <img src="../../assets/txt.svg" width="20px" height="20px">
               </span>
               <span v-else-if="scope.row.type==='.doc'">
                <img src="../../assets/doc.svg" width="20px" height="20px">
               </span>
               <span v-else-if="scope.row.type==='.docx'">
                <img src="../../assets/docx.svg" width="20px" height="20px">
               </span>
               <span v-else>
                <img src="../../assets/file.svg" width="20px" height="20px">
               </span>
              </template>
           </el-table-column>
            <el-table-column prop="fileName" label="文件名" width="300" />
            <el-table-column prop="changeDate" sortable label="修改时间" width="160" /> 
            <el-table-column prop="size" label="文件大小" width="160" />
           <el-table-column  prop="type" label="文件类型" />
        </el-table>
    </div>
</template>
<script lang="ts" setup>
import { ref } from 'vue'
import {$getshare,$download} from '../../api/fileop'
import { ElMessage } from 'element-plus'
import useUser from '../../store/user.ts'
import useMovefile from '../../store/movefile.ts'
const movefileStore = useMovefile()
const userStore=useUser()
const token=ref('');
const userName=ref('');
const share_sel=ref([])
const tableData=ref([])
const sel=(selection, row)=>{
    share_sel.value=selection;
}
const selall=(selection)=>{
    share_sel.value=selection;
}
function fileSize(size) {
  if (size < 1024) {
    return size + " B";
  } else if (size < 1024 * 1024) {
    return (size / 1024).toFixed(2) + " KB";
  } else if (size < 1024 * 1024 * 1024) {
    return (size / (1024 * 1024)).toFixed(2) + " MB";
  } else if (size < 1024 * 1024 * 1024 * 1024) {
    return (size / (1024 * 1024 * 1024)).toFixed(2) + " GB";
  } else {
    return (size / (1024 * 1024 * 1024 * 1024)).toFixed(2) + " TB";
  }
}
function fileType(type,isDir) {
  if(isDir) return "文件夹";
  const typeMap = {
    '.jpg': 'jpg图片文件',
    '.png': 'png图片文件',
    '.jpeg': 'jpeg图片文件',
    '.txt': '文本文件',
    '.pdf': 'PDF文件',
    '.docx': 'docx文档',
    '.doc': 'dox文档',
    '.xls': 'Excel电子表格',
    '.ppt': 'ppt演示文稿',
    '.pptx': 'pptx演示文稿',
    '.mp3': 'MP3音频文件',
    '.mp4': 'MP4视频文件',
    '.zip': '压缩文件',
    '.exe': 'exe文件',
    '.dll': 'dll文件',
  };
  if (typeMap.hasOwnProperty(type)) {
    return typeMap[type];
  } else {
    return '其他文件类型';
  }
}
async function getShare1(){
    if(userStore.user.shareToken){
        token.value=userStore.user.shareToken;
    }else{
        return;
    }
    let r=await $getshare({
        'shareToken':token.value,
    });
    console.log("sharer",r);

    if(r!==null){
        userName.value=r.user.username;
        for(const file of r.sharedFiles){
            tableData.value.push({
                id:file.id,
                fileName:file.fileName,
                changeDate:file.changeDate,
                size:fileSize(file.size),
                type:fileType(file.type),
            });
        }
    }
}
getShare1();
const getShare=async()=>{
    let r=await $getshare({
        'shareToken':token.value,
    });
    if(r===null){
        ElMessage.error('令牌无效，获取分享失败');
    }else{
        tableData.value=[];
        userStore.setShareToken(token.value);
        userName.value=r.user.username;
        for(const file of r.sharedFiles){
            tableData.value.push({
                id:file.id,
                isDir:file.isDir,
                fileName:file.fileName,
                changeDate:file.changeDate,
                size:fileSize(file.size),
                type:fileType(file.type,file.isDir),
            });
        }
    }
}

const move=async()=>{
    movefileStore.clearMovefile();
    for(const f of share_sel.value){
        movefileStore.setMovefile(f.id);
    }
    movefileStore.setIsShare(true);
    console.log("movfile",movefileStore.movefile);
    ElMessage({
        message: '剪切成功，请到文件夹下粘贴',
        type: 'success',
    })
}
</script>
<style scoped>
.container{
display: flex;
margin:2vh auto;
text-align: center;
width: 60vw;
/* border: 2px black dotted; */
}
.table{
    width: 64vw;
    margin: 2vh auto;
}
::v-deep .el-table {
    background-color:  transparent !important;
    color: #303034 !important;
  }
  
::v-deep .el-table tbody tr:hover>td { 
  background-color: #7ba9f9 !important;
  color: white !important;
}
  ::v-deep .el-table__expanded-cell {
    background-color: transparent !important;
  }
  ::v-deep .el-table tr,
  ::v-deep .el-table td {
    background-color: transparent;
  }
  ::v-deep .el-table th{
    background-color: #d8e6ff;
    font-weight: 700;
    color: #343539;
    font-size: 15px;
  }
</style>