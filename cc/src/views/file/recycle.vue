<template>
  <div class="container">
    <lebar/>
    <div>
      <div class="fbtn">
        <div><el-button type="primary" @click="RecycleAllFile(ids)" text>恢复</el-button></div>
        <div><el-button type="primary" @click=" DelecteAllFile(ids)" text >删除</el-button></div>
        <div><el-button style="right: 0px; position: relative;" type="primary" @click="DelecteAll()" text >清空回收站</el-button></div>
      </div>
        <el-table 
        :data="files" height="64.7vh" style="width: %100" 
        :row-style="{height:2.5+'vh'}" 
        @selection-change="handleSelectionChange"
        @cell-click="handleCancelSelection">
        <el-table-column type="selection"  width="35px" />
          <el-table-column  label="" width="30px" >
            <template #default="scope">
              <span v-if="scope.row.dir">
                <img src="../../assets/foldor.svg" width="20px" height="20px">
              </span>
              <span v-else>
               <img src="../../assets/file.svg" width="20px" height="20px">
              </span>
            </template>
           </el-table-column>
            <el-table-column  label="文件名" width="400"  >
              <template #default="scope">
            <span v-if="scope.row.dir"

            >
            <div>{{ clipFileName(scope.row.fileName) }}</div>
          </span>
          <span v-else>
            <a >
              {{ scope.row.fileName }}</a>
          </span>
         
          </template>

           </el-table-column>
            <el-table-column prop="deleteTime" sortable label="修改时间" width="170px" /> 
            <el-table-column label="操作" width="180px">
          <template #default="scope">
            <el-button size="small" @click="RecycleFile(scope.row.id)"
              >还原</el-button>
            <el-button
              size="small"
              type="danger"
              @click="DelecteFile(scope.row.id)">删除</el-button>
          </template>
      </el-table-column>
      </el-table>
      <div class="pagina" style="float: right;margin-top: 5px;">
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
        @current-change="handleCurrentChange"/>
      </div>
    </div>
</div>
</template>
<script lang="ts" setup>
    import { onMounted,ref} from 'vue'
    import { $queryRecycleFile,$queryDelecteOneFile,$queryRecycleOneFile,$queryDelecteAllOneFile } from '../../api/recycle'
    import { ElMessageBox,ElNotification } from 'element-plus';
    import HomeComponents from '../../components/lebar.vue'
    const ids = ref([]);
    const idsfile=ref([])
     //剪辑文件名
   const clipFileName = (fileName:any) => {
  const lastSlashIndex = fileName.lastIndexOf('/');
  return fileName.substring(lastSlashIndex + 1);
};
// 获取复选框
const handleSelectionChange = (val) => {
  // 清空已选的文件 IDs
  ids.value = [];

  if (val.length > 0) {
    val.forEach((row) => {
      // 将选中的文件的 ID 加入 ids 数组
      ids.value.push(row.id);
      console.log('ids:', ids.value);
    });
  }
};
// 取消选择的时候
const handleCancelSelection = (row) => {
  const index = ids.value.indexOf(row.id);
  if (index !== -1) {
    // 从 ids 数组中移除取消选择的文件的 ID
    ids.value.splice(index, 1);
    console.log('ids:', ids.value);
  }
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
      loadFiles();
    }

const handleCurrentChange = (num: number) => {
  currentPage = num
  loadFiles();
}
const  files=ref([]);
//加载所有文件
// 定义 tableData 的类型
// interface file {
//   id: number;
//   fileName: string;
//   dir:boolean;
//   deleteTime:string;
// }
const loadFiles = async () => {
 let listall=[];
  try {
    const post = {
      page: currentPage,
      size: pageSize ,
      path: null
    }

    const response = await  $queryRecycleFile(post);
 
     listall = response.data.records;
    
    const listTrue = listall.filter(records => records.dir === true);
    const listFalse = listall.filter(records => records.dir === false);
  
    files.value = listTrue.concat(listFalse);
    for(const f of files.value)
    {
      f.typename=fileType(f.type,f.isDir);
    }
    total.value = response.data.total;
  } catch (error) {
    console.error('Error loading files:', error)
  }
}
    onMounted(()=>{
        loadFiles()
    })

     //清除单个文件
  const DelecteFile=(fileids:number)=>{
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

      const post=[fileids]

      let ret= await $queryDelecteOneFile(post)
      console.log(ret);
      if(ret.code==200)
      {
        ElMessage.success({
            message: '删除成功',
            type:'success'
          })
          //重新加载文件
          loadFiles()
        }
        else{
          ElMessage.error({
                message: '删除失败',
                type:'error'
              });
      }
    })
  }  

   //清除多个文件
   const DelecteAllFile=(fileids:Array<number>)=>{
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

      
   console.log('fiesids',fileids);
      let ret= await $queryDelecteOneFile(fileids)
      if(ret.code==200)
      {
        ElMessage.success({
            message: '删除成功',
            type:'success'
          })
          //重新加载文件
          loadFiles()
        }
        else{
          ElMessage.error({
                message: '删除失败',
                type:'error'
              });
      }
    })
  }  
     //恢复一个文件
     const RecycleFile=(fileid:number)=>{
    ElMessageBox.confirm(
        '确定还原文件吗?',
        '提示',
        {
            confirmButtonText: '确定',
            cancelButtonText: '取消',   
            type: 'warning'
        }
    )
    .then(async()=>{
     const post=[fileid]
      let ret= await $queryRecycleOneFile(post)
      console.log(ret);
      if(ret.code==200)
      {
        ElMessage.success({
            message: '还原成功',
            type:'success'
          })
          //重新加载文件
          loadFiles()
        }
        else{
          ElMessage.error({
                message: '还原失败',
                type:'error'
              });
      }
    })
  }  
  
  //恢复多个文件
     const RecycleAllFile=(fileid:Array<number>)=>{
    ElMessageBox.confirm(
        '确定还原文件吗?',
        '提示',
        {
            confirmButtonText: '确定',
            cancelButtonText: '取消',   
            type: 'warning'
        }
    )
    .then(async()=>{
      let ret= await $queryRecycleOneFile(fileid)
      console.log(ret);
      if(ret.code==200)
      {
        ElMessage.success({
            message: '还原成功',
            type:'success'
          })
          //重新加载文件
          loadFiles()
        }
        else{
          ElMessage.error({
               
                message: '还原失败',
                type:'error'
              });
      }
    })
  }  
  //清空回收站
  const DelecteAll=()=>{
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

      let ret= await  $queryDelecteAllOneFile()
      console.log(ret);
      if(ret.code===200)
      {
        ElMessage.success({
            message: '删除成功',
            type:'success'
          })
          //重新加载文件
          loadFiles()
        }
        else{
          ElMessage.error({
               
                message: '删除失败',
                type:'error'
              });
      }
    })
  }  


  
</script>
<style lang="scss" scoped>
    .fbtn{
      margin-left: 3vw;
        margin-bottom: 1vh;
        width:800px;
        display: flex;
    }
    .container{
      width:80vw;
      margin: 0vh auto;
      display: flex;
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