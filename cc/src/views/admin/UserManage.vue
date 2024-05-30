<template>
  <div style="display: flex;margin: 2vh auto;width: 70vw;">
    <el-form inline>
      <el-form-item label="部门:">
        <SelectDepartment v-model="selectInfo.groupId"></SelectDepartment>
      </el-form-item>
      <el-form-item label="昵称:">
        <el-input v-model="selectInfo.name" placeholder="请输入昵称..."></el-input>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="onSelectUser">搜索</el-button>
        <el-button @click="onReset">重置</el-button>
      </el-form-item>
    </el-form>
    <div style="float: right;"><el-button type="danger" @click="onDeleteUserlist">批量删除</el-button></div>
  </div>
  <div style="width: 78vw; margin: 1vh auto;">
    <el-table :data="allUser"
    :header-cell-style="{'text-align':'center'}"
    :cell-style="{'text-align':'center'}"
      v-loading="loading"
      @selection-change="handleSelectionChange"
      style="width: 100%;">
      <el-table-column type="selection" width="50" />
      <!-- <el-table-column label="序号" width="100" type="index"></el-table-column> -->
      <!-- <el-table-column prop="id" label="id"></el-table-column> -->
      <el-table-column prop="userName" label="昵称" width="100"></el-table-column>
      <el-table-column prop="phone" label="手机" width="120"></el-table-column>
      <el-table-column prop="email" label="邮箱" width="170"></el-table-column>
      <el-table-column prop="all_cap" label="总容量" width="100">
        <template #default="{row}">
          <span>{{ (row.all_cap/102400).toFixed(2)+'GB' }}</span>
        </template>
      </el-table-column>
      <el-table-column prop="used_cap" label="已用容量" width="100">
        <template #default="{row}">
          <span>{{ row.used_cap/100<1024?Math.ceil(row.used_cap/100)+'MB':Math.ceil(row.used_cap/102400)+'GB' }}</span>
        </template>
      </el-table-column>
      <el-table-column prop="groupName" label="部门" width="110"></el-table-column>
      <el-table-column prop="createTime" label="创建时间" width="150"></el-table-column>
      <el-table-column prop="status" label="状态" width="100">
        <!-- <template #default="{row}">
          <el-switch v-model="row.status"
          active-text="开启" inactive-text="关闭"
          inactive-color="red"
          active-color="green"
          inline-prompt
          @change="handleUpdateStatus(row)"></el-switch>
        </template> -->
        <template #default="{row}">
          <!-- :type="buttonColor?'danger':'success'" -->
          <el-button v-if="row.status==false" @click="handleUpdateStatus(row)"
            type="success" plain round size="small" icon="CircleCheckFilled">开启</el-button>
          <el-button v-else @click="handleUpdateStatus(row)" 
            type="danger" plain round size="small" icon="CircleCloseFilled">禁用</el-button>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="120">
        <template #default="{ $index ,row }">
        <el-button 
        size="small" 
        :icon="Edit"
        type="primary"
        circle
        plain 
        @click="handleEdit(row)"></el-button>
        <!-- <el-button 
        size="small" 
        :icon="Connection"
        type="success"
        circle
        plain 
        @click="handleUpdateStatus($index, row)"></el-button> -->
        <el-button
          :icon="Delete"
          type="danger"
          circle
          plain
          size="small"
          @click="handleDelete($index, row)"></el-button
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
    title="编辑用户"
    width="30%">
    <el-form :model="userInfo" :rules="rules" label-width="100px" style="padding-right: 80px;">
      <!-- <el-form-item label="ID:">
        <el-input  value=""></el-input>
      </el-form-item> -->
      <!-- <el-form-item label="昵称:">
        <el-input  value=""></el-input>
      </el-form-item>
      <el-form-item label="邮箱:">
        <el-input  value=""></el-input>
      </el-form-item> -->
      <el-form-item type="number" label="手机号:" prop="phone">
        <el-input v-model="userInfo.phone" placeholder="请输入手机号..."></el-input>
      </el-form-item>
      <el-form-item type="number" label="容量:">
        <el-input v-model="userInfo.all_cap" placeholder="请输入容量..." style="width: 120px;" ></el-input>GB
      </el-form-item>
      <el-form-item label="部门:" prop="groupName">
        <!-- <el-input v-model="userInfo.groupName" placeholder="请输入部门" ></el-input> -->
        <SelectDepartment v-model="userInfo.groupName" :label="userInfo.groupName"></SelectDepartment>
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
      v-model:current-page="selectInfo.pageNum"
      v-model:page-size="selectInfo.pageSize"
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

<script setup lang="ts">
  import { ref} from 'vue'
  import { Edit,Delete,Connection} from '@element-plus/icons-vue'
  import { $adminGetAllUser,
    $adminUpdateUser,
    $adminDeleteById,
    $adminDeleteUserlist,
    $getUsersByName,
    $modifyUserStatus } from '../../api/admin'
  import SelectDepartment from '../../components/admin/SelectDepartment.vue'

  const allUser = ref([])
  const total = ref(0)
  const loading = ref(false) // 表格是否显示加载转圈状态
  const dialogVisible = ref(false)

  const selectInfo = ref({
    groupId : '',
    name : '',
    pageNum:1,
    pageSize:10
  })
  const userInfo  = ref({
      all_cap:0,
      id:'',
      username:'',
      email:'',
      phone:0,
      groupName:'',
      groupId:'',
      avatar:'',
      status:0
  })
  const getAllUser = async () =>{
    loading.value = true
    const res = await $adminGetAllUser(selectInfo.value)
    allUser.value = res.data.records
    //console.log(allUser.value)
    total.value = res.data.total
    loading.value = false
  }
  getAllUser()
  //分页
  const handleSizeChange = (size) => {
    selectInfo.value.pageNum = 1
    selectInfo.value.pageSize = size
    getAllUser()
  }
  const handleCurrentChange = (num) => {
    selectInfo.value.pageNum = num
    getAllUser()
  }
  const editGroupName = ref('')
  //点击编辑按钮
  const handleEdit = ( row) => {
    dialogVisible.value = true
    userInfo.value = { ...row }
    userInfo.value.all_cap = row.all_cap/102400
    editGroupName.value = userInfo.value.groupName
    //console.log(userInfo.value)
  }
  
  //提交更新用户信息
  const onSubmit =async () => { 
    userInfo.value.all_cap = userInfo.value.all_cap*102400
    //console.log(userInfo.value)
    if(userInfo.value.groupName!=editGroupName.value){
      userInfo.value.groupId = userInfo.value.groupName
    }
    //console.log(userInfo.value)
    const res = await $adminUpdateUser(userInfo.value)
    //console.log(res)
    if(res.code===200){
      dialogVisible.value = false
      ElMessage.success('修改成功')
      getAllUser()
    }else
      ElMessage.error(res.msg)  
  }
  const delInfo = ref({
    id: 0,
    list: [],
    name: ''
  })
  //删除
  const handleDelete =async ($index, row) => {
    await ElMessageBox.confirm('你确定要删除该用户吗？','温馨提示',{
      type:'warning',
      confirmButtonText:'确认',
      cancelButtonText:'取消'
    }) 
    delInfo.value.id = row.id
    //console.log(delInfo.value)
    const res = await $adminDeleteById(delInfo.value)
    if(res.code===200){
      ElMessage.success('删除成功')
      getAllUser()
    }else
      ElMessage.error(res.msg)
  }
  const selectionList =ref([])
  //点击复选框
  const handleSelectionChange = (val) => {
    selectionList.value = val
    // if (val.length > 0) {  
    //     val.forEach((row) => {  
    //       if (!delInfo.value.list.includes(row.id)) {  
    //         // delInfo.value.list.push(`${row.id}`)
    //         delInfo.value.list = val.map(item => item.id)
    //       }  
    //     })  
    //   } else {   
    //     delInfo.value.list = []
    //   } 
  }
  //批量删除
  const onDeleteUserlist =async () => {
    if(selectionList.value.length ==0 ){
      ElMessage.error('请先选择需要删除的数据');
    }else{
      await ElMessageBox.confirm('你确定要删除该用户吗？','温馨提示',{
      type:'warning',
      confirmButtonText:'确认',
      cancelButtonText:'取消'
    })  
    
    selectionList.value.forEach(item => {
      delInfo.value.list.push(item.id)
      //console.log(item.id)
    })

    const JSONUserInfo = JSON.parse(JSON.stringify(delInfo.value))

    const res = await $adminDeleteUserlist(JSONUserInfo)
    //console.log(res)
    if(res.code===200){
      delInfo.value.list = []
      ElMessage.success('删除成功')
      getAllUser()
    }else
      ElMessage.error(res.msg)
    } 
  }
  // //添加用户
  // const onAddUser = () => {
  //   dialogVisible.value = true
  // }

  const rules = {
    phone:[
      {required:true, message:'请输入手机号',trigger:'blur'}
    ],
    groupName:[]
  }
  //搜索用户
  const onSelectUser =async () => {
    selectInfo.value.pageNum = 1
    loading.value = true
    console.log(selectInfo.value)
    const res = await $getUsersByName(selectInfo.value)
    allUser.value = res.data.records
    total.value = res.data.total
    loading.value = false
  }
  //重置
  const onReset = () => {
    selectInfo.value.pageNum = 1
    selectInfo.value.groupId = ''
    selectInfo.value.name = ''
    getAllUser()
  }
  const modifyInfo = ref({
    avatar: '',
    email: '',
    groupId: 0,
    groupName: '',
    id: 0,
    phone: 0,
    status: 0,
    userName: ''
  })
  const buttonColor = ref(true)
  //修改用户状态
  const handleUpdateStatus =async (row) => {
    //console.log(row)
    modifyInfo.value.id = row.id
    //console.log(modifyInfo.value)
    const res = await $modifyUserStatus(modifyInfo.value)
    if(res.code===200){
      modifyInfo.value.id = 0
      buttonColor.value = !buttonColor.value
      ElMessage.success('修改成功')
      getAllUser()
    }
    else
      ElMessage.error(res.msg)
  }
</script>

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