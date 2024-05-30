<template>
  <div class="container">
    <lebar/>
    <div>
    <div style="margin-left: 3vw;"><abuttonComponents :collect_sel="collect_sel" @load1="loadFiles"/> </div>
      <el-table 
          @select="sel" @select-all="selall" :row-style="{height:2.5+'vh'}" 
          :data="files" height="64.7vh" style="width: 100%" 
          :default-sort="{ prop: 'changeDate', order: 'descending' }"
          >
            <el-table-column type="selection"  width="35px" />  
            <el-table-column  label="" width="50px" >
              <template #default="scope">
               <span v-if="scope.row.type==='.audio'||scope.row.type==='.mp3'">
                <img src="../../assets/mp3.svg" width="20px" height="20px">
               </span>
              </template>
             </el-table-column>
              <el-table-column prop="fileName" label="文件名" width="440" >
                <template #default="scope">
    <div style="display: flex; align-items: center;">
      <div style="cursor: pointer; text-align: right; margin-right: 5px;">
        <el-icon v-if="scope.row.collection===0" size="20px" color='#dadad4'><StarFilled  /></el-icon>
        <el-icon v-if="scope.row.collection===1" size="20px" color="#fadd06"><StarFilled /></el-icon>
        <el-icon v-if="scope.row.share===0" size="20px" color='#dadad4'><Promotion /></el-icon>
        <el-icon v-if="scope.row.share===1" size="20px" color="#ff95c8"><Promotion /></el-icon>
      </div>
      <span v-if="scope.row.type==='.mp3'" style="cursor: pointer;">
        <div @click="previewMP3(scope.row.id)">
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
                <template #default="scope">
                  <span v-if="!scope.row.isDir"
                  style="text-align: left"
                  >
                  <div v-if="Math.floor(scope.row.size / 1024)<1000">
                    {{ Math.floor(scope.row.size / 1024) + 'KB'  }}
                  </div>
                  <div v-else-if="Math.floor(scope.row.size / 1024 )>1000&&Math.floor(scope.row.size / 1024 / 1024)<1000">
                    {{ Math.floor(scope.row.size / 1024/1024) + 'MB'  }}
                  </div>
                  <div v-else>
                    {{ Math.floor(scope.row.size / 1024/1024/1024) + 'GB'  }}
                  </div>
                  </span>
                  </template>
              </el-table-column>
              <el-table-column  prop="typename" label="文件类型" width="130" >

              </el-table-column>
          </el-table>
          <div class="demo-pagination-block" style="float: right;margin-top: 5px;">
     
      <el-pagination
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
  </div>
  </div>
  </template>
  <script lang="ts" setup>
      import { onMounted,ref } from 'vue'
      import { $queryTypefile,$queryPreviewfile } from '../../api/file'
      import abuttonComponents from '../../components/abuttonComponents.vue'
      import lebar from '../../components/lebar.vue'
      import {baseURL_view} from '../../config/baseURL'
      onMounted(()=>{
          loadFiles()
          
      })
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
        loadFiles();
      }
  
  const handleCurrentChange = (num: number) => {
    currentPage = num
    loadFiles();
  }
     
  
  //预览音频
  let previewMP3Url=("")
    const previewMP3=async(url:string)=>{
    const response = await $queryPreviewfile(url)
    previewMP3Url=baseURL_view+response.data;
    window.open(previewMP3Url)
  }
  
  let  files=ref([])

  //加载所有文件
  const loadFiles = async () => {
    try {
      const post = {
        curr: currentPage,
        size: pageSize ,
        type: '.mp3'
      }
      const response = await $queryTypefile(post)
      if(response.total!=0)
    {  files.value = response.records
      for(const f of files.value)
    {
      f.typename=fileType(f.type,f.isDir);
    }
    }
    else 
    files.value=[]
      total.value = response.total
    } catch (error) {
      console.error('Error loading files:', error)
    }
  }
  
  const collect_sel=ref([])
  const sel=(selection:any)=>{
    collect_sel.value=selection;
  }
  const selall=(selection:any)=>{
    collect_sel.value=selection;
  }
  </script>
  <style lang="scss" scoped>
      .fbtn{
          margin-bottom: 3vh;
          
      }
      .searchbox{
          width: 15vw;
          float: right;
      }
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
  img 
  { width: 100% ; height: 100% ; object-fit :cover; } 
  
  </style>