<template>
<div class="container">
    <lebar/>
    <div>
      <!-- <div style="padding: 0px">
      <a style="color: #666" href="../filehome">全部文件</a><el-icon v-if="length>0"><ArrowRight /></el-icon>
      <a style="color: #666" href="">{{  }}<el-icon v-if="length>1"><ArrowRight /></el-icon></a>
      </div> -->
      <div class="breadcrumbs" style="margin-bottom: 1vh;font-size: 15px;margin-top: 2vh;">
        <a style="color:  #040669;cursor:pointer" @click="loadFiles()" >全部文件</a>
        <span v-for="(crumb, index) in breadcrumbs" :key="index">
          <el-icon v-if="index !== 0" size="12px"><ArrowRight /></el-icon>
          <a
            style="color: #061b76; cursor: pointer; "
            v-if="index !== breadcrumbs.length - 1"
            @click="navigateToPath(crumb.path)"
          >
            {{ "  "+crumb.name }}
          </a>
          <span
            style="color: #19047a; cursor: pointer; "
            v-else
            @click="navigateToPath(crumb.path)"
          >
            {{ crumb.name }}
          </span>
        </span>
      </div>
      <el-table 
       @select="sel" @select-all="selall" :row-style="{height:2.5+'vh'}" 
      :data="files" height="64.7vh" style="width: 100%" 
      >
        <el-table-column  label="" width="50" >
          <template #default="scope">
            <span v-if="scope.row.isDir">
              <img src="../../assets/foldor.svg" width="20px" height="20px">
            </span>
            <span v-else-if="scope.row.type==='.jpg'|| scope.row.type==='.png'||scope.row.type==='.mp3'">
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
            <span v-else>
             <img src="../../assets/file.svg" width="20px" height="20px">
            </span>
            </template>
         </el-table-column>
          <el-table-column prop="fileName" label="文件名" width="380" >
          <template #default="scope">
            <span v-if="scope.row.isDir"
             style=" cursor:pointer"
            >
            <div @dblclick="path=scope.row.fileName,loadFilesAndBreadcrumbs(scope.row.fileName)">
            {{ clipFileName(scope.row.fileName) }}</div>
          </span>
          <span v-else-if="scope.row.type==='.jpg'|| scope.row.type==='.png'"  style=" cursor:pointer">
            <a  @click="previewImg(scope.row.id)">
              {{ scope.row.fileName }}</a>
          </span>
          <span v-else-if="scope.row.type==='.mp3'"  style=" cursor:pointer">
            <div @click=" previewMP3(scope.row.id)">
              {{ scope.row.fileName }}</div>
          </span>
          <span v-else-if="scope.row.type==='.mp4'"  style=" cursor:pointer">
            <div @click=" previewMP4(scope.row.id)">
              {{ scope.row.fileName }}</div>
          </span>
          </template>
         </el-table-column>
          <el-table-column prop="changeDate" sortable label="修改时间" width="160" /> 
          <el-table-column  label="文件大小" width="120" >
            <template #default="{row}">
              <span v-if="!row.isDir">{{ row.size<1024?row.size+'KB': row.size/1024+'MB'}}</span>
            </template>
          </el-table-column>
          <el-table-column  prop="typename" label="文件类型" width="130" />
          <el-table-column label="操作" width="110">
            <template #default="scope">
              <el-button
                size="small"
                type="primary" plain
                @click="handleDelete( scope.row.id)">取消收藏</el-button>      
            </template>
          </el-table-column>
      </el-table>
      <div class="demo-pagination-block" style="float: right;margin-top: 5px;" >
        <el-pagination small
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :page-sizes="[2,5,8,10]"
          :small="small"
          :disabled="disabled"
          :background="background"
          layout="sizes, prev, pager, next"
          :total="total "
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    <el-dialog v-model=dialogVisible 
    title="预览" width="30%"
    destroy-on-close 
    center
    draggable>
      <span>
          <img :src="previewImgUrl" style="max-width: 100%; max-height: 100%; height: auto;" alt="">
      </span>
    </el-dialog>
  </div>
</div>
</template>
<script lang="ts" setup>
  import { onMounted,ref} from 'vue'
  import { $QueryCollectfile,$cancelCollectfile } from '../../api/collect'
  import { $queryPreviewfile,$queryAllfile } from '../../api/file'
  import {baseURL_view} from '../../config/baseURL'
  import { ElMessageBox,ElNotification } from 'element-plus';
  import lebar from '../../components/lebar.vue'

  //当前目录 
  const path1=ref();
  onMounted(()=>{
      loadFiles()
  })
  
   //剪辑文件名
   const clipFileName = (fileName:any) => {
  const lastSlashIndex = fileName.lastIndexOf('/');
  return fileName.substring(lastSlashIndex + 1);
};
function fileType(type,isDir) {
  if(isDir) return "文件夹";
  const typeMap = {
    '.jpg': 'jpg图片文件',
    '.png': 'png图片文件',
    '.jpeg': 'jpeg图片文件',
    '.txt': '文本文件',
    '.pdf': 'PDF文件',
    '.docx': 'docx文档',
    '.doc': 'Word文档',
    '.xls': 'Excel电子表格',
    '.ppt': 'PowerPoint演示文稿',
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

  //分页
  const small = ref(false)
  //当前页
  let currentPage = (1)
  //每页显示多少条
  let pageSize = (10)
  //总条数
  const total = ref(0)
  const background = ref(true)
  const disabled = ref(false)
  const handleSizeChange = (size: number) => {
    pageSize = size
    currentPage=1
    if(path1.value=="")
    loadFiles();
  else
  loadFilesAndBreadcrumbs(path1.value);
  }

const handleCurrentChange = (num: number) => {
currentPage = num
if(path1.value=="")
    loadFiles();
  else
  loadFilesAndBreadcrumbs(path1.value);
}
 
let previewImgUrl=("")
//预览图片
let dialogVisible = ref(false)
const previewImg=async(url:string)=>{
const response = await $queryPreviewfile(url)
previewImgUrl=baseURL_view+response.data;
dialogVisible.value = true
}
//预览音频
let previewMP3Url=("")
const previewMP3=async(url:string)=>{
const response = await $queryPreviewfile(url)
previewMP3Url=baseURL_view+response.data;
window.open(previewMP3Url)
}

//预览视频
let previewMP4Url=("")
const previewMP4=async(url:string)=>{
const response = await $queryPreviewfile(url)
previewMP4Url=baseURL_view+response.data;
window.open(previewMP4Url)
}

let  files=ref([])

//加载所有文件
const loadFiles = async () => {
  path1.value=""
  let listall=[]
try {
  const post = {
    pageNum: currentPage,
    pageSize: pageSize ,
  }
  const response = await $QueryCollectfile(post)
  if(response.data.total!=0)
  {listall = response.data.records;
    
    const listTrue = listall.filter(records => records.isDir === true);
    const listFalse = listall.filter(records => records.isDir === false);

    files.value = listTrue.concat(listFalse);
    
    for(const f of files.value)
    {
      f.typename=fileType(f.type,f.isDir);
    }}
    else
    files.value=[]
    total.value = response.data.total;
  breadcrumbs = ref()
} catch (error) {
  console.error('Error loading files:', error)
}
}

let breadcrumbs = ref();

// 加载文件及更新面包屑导航
const loadFilesAndBreadcrumbs = async (path:string) => {
  path1.value=path
  let listall=[]
 
try {
  const post = {
    curr: currentPage,
    size: pageSize,
    path: path
  };
  const response = await $queryAllfile(post);
  if(response.data.total!=0)
  {listall = response.data.records;
    
    const listTrue = listall.filter(records => records.isDir === true);
    const listFalse = listall.filter(records => records.isDir === false);

    files.value = listTrue.concat(listFalse);
    for(const f of files.value)
    {
      f.typename=fileType(f.type,f.isDir);
    }
    }
    else
    {
      files.value=[]
    }
  
  // 更新面包屑导航

  breadcrumbs.value = generateBreadcrumbs(path);

  // 更新文件列表
  total.value = response.data.total;
} catch (error) {
  console.error('Error loading files:', error);
}
};

// 生成面包屑导航数据
const generateBreadcrumbs = (path:string) => {
const crumbs = [];
const pathSegments = path.split('/');

for (let i = 0; i < pathSegments.length; i++) {
  const crumbPath = pathSegments.slice(0, i + 1).join('/');
  const crumbName = pathSegments[i];
  crumbs.push({ path: crumbPath, name: crumbName });
}

return crumbs;
};
// 导航到指定路径
const navigateToPath = (path:string) => {
currentPage = 1;
loadFilesAndBreadcrumbs(path);
};
const collect_sel=ref([])
const sel=(selection:any)=>{
  collect_sel.value=selection;
}
const selall=(selection:any)=>{
  collect_sel.value=selection;
}
 //取消收藏
 const handleDelete=(id:number)=>{
    ElMessageBox.confirm(
        '确定取消收藏吗?',
        '提示',
        {
            confirmButtonText: '确定',
            cancelButtonText: '取消',   
            type: 'warning'
        }
    )
    .then(async()=>{
     
      //console.log(row)
      const ret= await $cancelCollectfile({id})
      if(ret.code===200)
      {
        ElMessage.success({
            message: '取消收藏成功',
            type:'success'
          })
          //重新加载文件
          loadFiles()
        }
        else{
          ElMessage.error({
                message: '取消收藏失败',
                type:'error'
              });
      }
    })
  }  

</script>
<style lang="scss" scoped>



.container{
  width:80vw;
    margin: 0vh auto;
    display: flex;
}
.pagination-block{
margin-top: 10px;
float: right;
}
img 
{ width: 100% ; height: 100% ; object-fit :cover; } 
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
.breadcrumbs:hovar{
cursor:pointer
}
.breadcrumbs{
  margin-left:10px
}
</style>