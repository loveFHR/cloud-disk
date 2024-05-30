<script  setup>
import {$getAllGroups} from '../../api/department.ts'
import { ref } from 'vue'

defineProps({
    modelValue:{
        type:[Number,String]
    },
    label:{
      type:String
    }
})
const emit = defineEmits(['update:modelValue'])

const groupList = ref([])
const getAllGroups =async () => {
    const res = await $getAllGroups()
    groupList.value = res.data
}
getAllGroups()
</script>

<template>
  <el-select placeholder="请选择" :modelValue="modelValue" @update:modelValue="emit('update:modelValue',$event)">
    <el-option 
    v-for="item in groupList"
    :key="item.id"
    :label="item.groupName" 
    :value="item.id"></el-option>
    
  </el-select>
</template>
