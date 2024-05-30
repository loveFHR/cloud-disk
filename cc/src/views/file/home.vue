<template>
  <div class="container">
  <lebar/>
  <div>
    <div style="display: flex;align-items: center;"><div style="margin-left: 2vw;"><buttonComponents :collect_sel="collect_sel" :path1="path1" @load1="load1"/>  </div>
    <div class="searchbox">
      <el-input
        v-model="SearchVal"
        placeholder="关键字/文件后缀(.txt)"
        class="input-with-select">
        <template #append>
          <el-button icon="Search" @click="search"/>
        </template>
      </el-input>
    </div></div>
    <!-- <div style="padding: 0px">
    <a style="color: #666" href="../filehome">全部文件</a><el-icon v-if="length>0"><ArrowRight /></el-icon>
    <a style="color: #666" href="">{{  }}<el-icon v-if="length>1"><ArrowRight /></el-icon></a>
    </div> -->
    <div class="breadcrumbs" style="margin-bottom: 1vh;font-size: 15px;">
      <a style="color: #274c8a;cursor:pointer" @click="loadFiles()" >全部文件</a>
      <span v-for="(crumb, index) in breadcrumbs" :key="index">
        <el-icon v-if="index !== 0" size="12px"><ArrowRight /></el-icon>
        <a
          style="color: #1f4b78; cursor: pointer; "
          @click="navigateToPath(crumb.path)"
        >
          {{ "  "+crumb.name }}
        </a>
      </span>
    </div>
    <div style="padding: 8px;border-radius: 13px;">
        <el-table :row-style="{height:2.5+'vh'}" 
         @select="sel" @select-all="selall"
        :data="files" height="64.7vh" style="width: 100%;" 
        >
          <el-table-column type="selection"  width="35px" />
          <el-table-column  label="" width="30px" >
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
           <el-table-column prop="fileName" label="文件名" width="440">
  <template #default="scope">
    <div style="display: flex; align-items: center;">
      <div style="cursor: pointer; text-align: right; margin-right: 5px;">
        <el-icon v-if="scope.row.collection===0" size="20px" color='#dadad4'><StarFilled  /></el-icon>
        <el-icon v-if="scope.row.collection===1" size="20px" color="#fadd06"><StarFilled /></el-icon>
        <el-icon v-if="scope.row.share===0" size="20px" color='#dadad4'><Promotion /></el-icon>
        <el-icon v-if="scope.row.share===1" size="20px" color="#ff95c8"><Promotion /></el-icon>
      </div>
      <span v-if="scope.row.isDir" style="cursor: pointer;">
        <div @dblclick="path1=scope.row.fileName,loadFilesAndBreadcrumbs(scope.row.fileName)">
          {{ clipFileName(scope.row.fileName) }}
        </div>
      </span>
      <span v-else-if="scope.row.type==='.jpg' || scope.row.type==='.png'" style="cursor: pointer;">
        <a @click="previewImg(scope.row.id)">
          {{ scope.row.fileName }}
        </a>
      </span>
      <span v-else-if="scope.row.type==='.mp3'" style="cursor: pointer;">
        <div @click="previewMP3(scope.row.id)">
          {{ scope.row.fileName }}
        </div>
      </span>
      <span v-else-if="scope.row.type==='.mp4'" style="cursor: pointer;">
        <div @click="previewMP4(scope.row.id)">
          {{ scope.row.fileName }}
        </div>
      </span>
      <span v-else>
        <div>
          {{ scope.row.fileName }}
        </div>
      </span>
    </div>
  </template>
</el-table-column>


            <el-table-column prop="changeDate" sortable label="修改时间" width="180" /> 
            <el-table-column  label="文件大小" width="120" >
              <template #default="{row}">
                <span v-if="!row.isDir"
                style="text-align: left"
                
                >
                <div v-if="Math.floor(row.size / 1024)<1000">
                  {{ Math.floor(row.size / 1024) + 'KB'  }}
                </div>
                <div v-else-if="Math.floor(row.size / 1024 )>1000&&Math.floor(row.size / 1024 / 1024)<1000">
                  {{ Math.floor(row.size / 1024/1024) + 'MB'  }}
                </div>
                <div v-else>
                  {{ (row.size / 1024/1024/1024) + 'GB'  }}
                </div>
                </span>
                </template>
            </el-table-column>
            <el-table-column  prop="typename" label="文件类型" width="130" >

              </el-table-column>
        </el-table>
      </div>
        <div class="demo-pagination-block" style="float: right;margin-top: 5px;">
          <el-pagination small
            v-model:current-page="currentPage"
            v-model:page-size="pageSize"
            :page-sizes="[2,5,8,10]"
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
        <span  >
          <div  v-if="type==='img'">
            <img :src="previewImgUrl" style="max-width: 100%; max-height: 100%; height: auto; transform: scale(1.2);" alt="">
          </div>
        </span>
      </el-dialog>
  </div>
</div>
</template>
<script lang="ts" setup>
    import { onMounted,ref} from 'vue'
    import { $queryAllfile,$queryPreviewfile } from '../../api/file'
    import {baseURL_view} from '../../config/baseURL'
    import lebar from '../../components/lebar.vue'
    import buttonComponents from '../../components/buttonComponents.vue'
import {$search} from '../../api/fileop'
    onMounted(()=>{
        loadFiles()
    })
    const path1=ref('');
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
  
    //剪辑文件名
    const clipFileName = (fileName:any) => {
  const lastSlashIndex = fileName.lastIndexOf('/');
  return fileName.substring(lastSlashIndex + 1);
};

   //当前目录
   const path=ref();
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
 let type=""
//预览图片
let dialogVisible = ref(false)
const previewImg=async(url:string)=>{
  const response = await $queryPreviewfile(url)
  previewImgUrl=baseURL_view+response.data;
  type="img";
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

// 加载所有文件
const loadFiles = async () => {
  path1.value="";
  let listall = [];

  try {
    const post = {
      curr: currentPage,
      size: pageSize,
      path: null
    };

    const response = await $queryAllfile(post);
    listall = response.data.records;
    
    const listTrue = listall.filter(records => records.isDir === true);
    const listFalse = listall.filter(records => records.isDir === false);

    files.value = listTrue.concat(listFalse);
    console.log(files);
    total.value = response.data.total;
    breadcrumbs = ref();
    for(const f of files.value){
      f.typename=fileType(f.type,f.isDir);
    }
  } catch (error) {
    console.error('Error loading files:', error);
  }
};


let breadcrumbs = ref();

// 加载文件及更新面包屑导航
const loadFilesAndBreadcrumbs = async (path:string) => {
  path1.value=path;
  let listall = [];
  try {
    const post = {
      curr: currentPage,
      size: pageSize,
      path: path
    };
    const response = await $queryAllfile(post);

    // 更新面包屑导航
    breadcrumbs.value = generateBreadcrumbs(path);

    // 更新文件列表
    listall = response.data.records;
    
    const listTrue = listall.filter(records => records.isDir === true);
    const listFalse = listall.filter(records => records.isDir === false);

    files.value = listTrue.concat(listFalse);
    for(const f of files.value)
    {
      f.typename=fileType(f.type,f.isDir);
    }
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

const load1=async(path:String,f:Boolean)=>{
  let listall = [];
  try {
    const response = await $queryAllfile({ 
      curr: 1,
      size: pageSize,
      path: path
    });
    total.value = response.data.total;
    if(f){
      currentPage=Math.ceil(total.value/pageSize);
    }
    const res = await $queryAllfile({
      curr: currentPage,
      size: pageSize,
      path: path
    });
    listall = res.data.records;
    
    const listTrue = listall.filter(records => records.isDir === true);
    const listFalse = listall.filter(records => records.isDir === false);

    files.value = listTrue.concat(listFalse);
    for(const f of files.value){
      f.typename=fileType(f.type,f.isDir);
    }
  } catch (error) {
    console.error('Error loading files:', error);
  }
}

const SearchVal=ref('')
const search=async()=>{
    if(SearchVal.value===null||SearchVal.value.trim()===''){
        loadFiles();
    }
    const lastDotIndex = SearchVal.value.lastIndexOf('.');
    let name = SearchVal.value;
    let type = "";
    if(lastDotIndex!==-1){
      name = SearchVal.value.slice(0, lastDotIndex);
      type = SearchVal.value.slice(lastDotIndex + 1);
    } 
    currentPage=1;
    
    let listall = [];
  try {
    let r=await $search({
        curr:currentPage,
        size:pageSize,
        name,
        type,
    });
    listall = r.records;
    
    const listTrue = listall.filter(records => records.isDir === true);
    const listFalse = listall.filter(records => records.isDir === false);

    files.value = listTrue.concat(listFalse);
    console.log(files);
    total.value = r.total;
    breadcrumbs = ref();
    for(const f of files.value){
      f.typename=fileType(f.type,f.isDir);
    }
  } catch (error) {
    console.error('Error loading files:', error);
  }
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

.input-with-select  {
  background-color: var(--el-fill-color-blank);
}

.breadcrumbs:hovar{
  cursor:pointer
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