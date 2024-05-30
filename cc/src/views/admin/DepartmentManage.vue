<script setup lang="ts">
  import { ref,reactive,onMounted} from 'vue'
  import { Delete} from '@element-plus/icons-vue'
  import {
    $getAllGroups,
    $addGroup,
    $getUsersByGroup,
    $deleteGroupByName,
    $deleteUserlist,
    $getGroupsByPage
  } from '../../api/department'
  const allGroup = ref([])
  const total = ref(50)
  const loading = ref(false) // 表格是否显示加载转圈状态
  const dialogVisible = ref(false)
  const pageInfo =ref({
    pageNum:1,
    pageSize:10,
    name:''
  })
  const handleSizeChange = (size) => {
    pageInfo.value.pageNum = 1
    pageInfo.value.pageSize = size
    getAllGroups()
  }
  const handleCurrentChange = (num) => {
    pageInfo.value.pageNum = num
    getAllGroups()
  }
  const formRef = ref()
  const groupInfo = ref({
    groupName:'',
    id:0,
    num:0
  })
  const delInfo = ref({
    id: '',
    list: [],
    name: ''
  })
  const getAllGroups =async () => {
    loading.value = true
    const res = await $getGroupsByPage(pageInfo.value)
    //const res2 = await $getAllGroups()
    allGroup.value = res.data.records
    //console.log(allGroup.value)
    total.value = res.data.total
    loading.value = false
  }
  getAllGroups()
  //点击添加按钮
  const onAddGroup = () =>{
    formRef.value = ''
    dialogVisible.value = true
  }
  //添加部门提交
  const onSubmit =async () => {
    await formRef.value.validate()
    const res = await $addGroup(groupInfo.value)
    if(res.code===200){
      dialogVisible.value = false
      ElMessage.success('添加成功')
      getAllGroups()
    }else
    ElMessage.error(res.msg)
  }
  //删除
  const handleDelete =async (row) => {
    if(row.num!=0){
      ElMessageBox.alert('该部门有成员,不允许删除','提示')
      return
    }
    await ElMessageBox.confirm('你确定要删除该部门吗？','温馨提示',{
      type:'warning',
      confirmButtonText:'确认',
      cancelButtonText:'取消'
    })
    delInfo.value.name = row.groupName
    const res = await $deleteGroupByName(delInfo.value)
    if(res.code===200){
      ElMessage.success('删除成功')
      getAllGroups()
    }else
      ElMessage.error(res.msg)
  }
  const selectionList =ref([])
  const multipleTableRef = ref()
  const handleSelectionChange = (val) => {
    selectionList.value = val
    //console.log(selectionList.value)

    // if (val.length > 0) {  
    //     val.forEach((row) => {
    //       if(row.num!=0){
    //         multipleTableRef.value!.toggleRowSelection(row, false)
    //         ElMessage.error('该部门有成员,不允许删除')
    //         return
    //       } 
    //     })  
    //   }   

    // if (val.length > 0) {   
    //     val.forEach((row) => {  
    //       if (!selectionList.value.includes(row.id)) {  
    //         selectionList.value.push(row.id)  
    //       }  
    //     })  
    //   } else {  
    //     selectionList.value = []  
    //   }  
  }
  //批量删除
  const onDeleteUserlist =async () => {
    if(selectionList.value.length ==0 ){
      ElMessage.error('请先选择需要删除的数据');
    }else{
      await ElMessageBox.confirm('你确定要删除这些部门吗？','温馨提示',{
      type:'warning',
      confirmButtonText:'确认',
      cancelButtonText:'取消'
    })  
    
    // selectionList.value.forEach(item => {
    //   //delInfo.value.list.push(item.id)
    //   //console.log(item.id)
    //   if(item.num>0){
    //     ElMessageBox.alert('你所选的部门有成员,不允许删除','提示')
    //     return
    //   }else{
    //     delInfo.value.list.push(item.id)
    //   }
    // })
    if(selectionList.value.filter(item => item.num > 0).length>0){
      ElMessageBox.alert('你所选的部门有成员,不允许删除','提示',{
        confirmButtonText: '确定',
        type: 'warning'
      })
      return
    }
    else{
      selectionList.value.forEach(item => delInfo.value.list.push(item.id))
    }

    if(delInfo.value.list.length===0){
      return
    }
    //console.log(delInfo.value.list)
    const res = await $deleteUserlist(delInfo.value)
    //console.log(res)
    if(res.code===200){
      delInfo.value.list = []
      ElMessage.success('删除成功')
      getAllGroups()
    }else
      ElMessage.error(res.msg)
     } 
  }
  //搜索
  const onSelectGroup = () => {
    pageInfo.value.pageNum = 1
    getAllGroups()
  }
  //重置
  const onReset = () => {
    pageInfo.value.pageNum = 1
    pageInfo.value.name = ''
    getAllGroups()
  }
</script>

<template>
  <div style="display: flex;margin: 2vh auto;width: 60vw;">
    <el-form inline>
      <el-form-item label="部门:">
        <el-input v-model="pageInfo.name" placeholder="请输入部门..."></el-input>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="onSelectGroup">搜索</el-button>
        <el-button @click="onReset">重置</el-button>
      </el-form-item>
    </el-form>
    <div style="float: right;">
      <el-button type="primary" @click="onAddGroup">添加部门</el-button>
      <el-button type="danger" @click="onDeleteUserlist">批量删除</el-button>
    </div>
  </div>
  <div style="width: 60vw; margin: 1vh auto;">
    <el-table :data="allGroup"
    :header-cell-style="{'text-align':'center'}"
    :cell-style="{'text-align':'center'}"
       ref="multipleTableRef"
       v-loading="loading" 
       style="width: 100%;"
       @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" />
      <el-table-column label="序号" width="80" type="index"></el-table-column>
      <!-- <el-table-column prop="id" label="id"></el-table-column> -->
      <el-table-column prop="groupName" label="部门" width="428"></el-table-column>
      <el-table-column prop="num" label="成员数量" width="200"></el-table-column>
      <el-table-column label="操作" width="100">
        <template #default="{ row }">
        <el-button
          :icon="Delete"
          type="danger"
          circle
          plain
          size="small"
          @click="handleDelete(row)"></el-button
        >
      </template>
      </el-table-column>
      <template #empty>
        <el-empty description="暂无数据..." />
      </template>
    </el-table>
    <!-- 弹窗 -->
    <el-dialog
    v-model="dialogVisible"
    title="添加部门"
    width="30%">
    <el-form ref="formRef" :model="groupInfo" label-width="100px" style="padding-right: 30px;">
      <el-form-item label="部门名称:" prop="groupName">
        <el-input v-model="groupInfo.groupName"  placeholder="请输入部门名称"></el-input>
      </el-form-item>    
    </el-form>
    <template #footer>
      <span class="dialog-footer">
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="onSubmit">
         确定
        </el-button>
      </span>
    </template>
  </el-dialog>
    <!-- 分页 -->
    <el-pagination small
      v-model:current-page="pageInfo.pageNum"
      v-model:page-size="pageInfo.pageSize"
      :page-sizes="[5, 10, 20, 50]"
      background
      layout="jumper, total, sizes, prev, pager, next"
      :total="total"
      @size-change="handleSizeChange"
      @current-change="handleCurrentChange"
      style="margin-top: 20px; justify-content: flex-end;"
    />
  </div>
</template>

<style scoped>
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