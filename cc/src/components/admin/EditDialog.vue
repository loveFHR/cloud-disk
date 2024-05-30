<script setup lang="ts">
import { ref } from 'vue'
const dialogVisible = ref(false)
const open = (row) => {
  dialogVisible.value = true
}
defineExpose({
  open
})
</script>

<template>
    <div>
        <el-dialog style="background-color: #ebf5ff; border-radius: 12px;"
    v-model="dialogVisible"
    title="添加用户"
    width="30%">
    <span>对话框内容主体</span>
    <template #footer>
      <span class="dialog-footer">
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="dialogVisible = false">
         确定
        </el-button>
      </span>
    </template>
  </el-dialog>
    </div>
</template>