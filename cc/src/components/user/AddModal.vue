<template>
    <el-dialog v-model="dvis" width="30%" :title="info?.name ? 'xg':'xz'" @close="$emit('closeAdd')" draggable>
        <el-form
        v-model="form"
        :model="sizeForm"
        label-width="auto"
        :label-position="labelPosition"
        :size="size">
            <el-form-item label="date">
                <el-col :span="11">
                <el-date-picker :disabledDate="disabledDate" v-model="form.date" type="date" label="Pick a date" placeholder="Pick a date" style="width: 100%"/>
                </el-col>
            </el-form-item>
            <el-form-item label="name">
                <el-input v-model="form.name" />
            </el-form-item>
            <el-form-item label="address">
                <el-input v-model="form.address" />
            </el-form-item>
            <el-form-item label="order">
                <el-input v-model.number="form.order" />
            </el-form-item>
        </el-form>
        <template #footer>
            <span class="dialog-footer">
              <el-button @click="closeAdd()">Cancel</el-button>
              <el-button type="primary" @click="save()">
                Confirm
              </el-button>
            </span>
          </template>
    </el-dialog>
</template>
<script lang="ts" setup>
    import {ref,computed,Ref,watch} from 'vue'
    import User from '../../class/User'
    const props=defineProps({
        is_show:Boolean,
        info:User
    })
    const dvis=computed(()=>props.is_show)
    const form:Ref<User>=ref<User>({
        id:"",
        date:"",
        name:"",
        address:"",
        order:0
    })
    watch(()=>props.info,(new_info)=>{
        console.log("info",props.info,new_info);
        if(new_info){
            form.value={
                id:new_info.id,
                date:new_info.date,
                name:new_info.name,
                address:new_info.address,
                order:new_info.order
            }
        }
        console.log(form.value);
    })
    const disabledDate=(time:any)=>{
        const _maxTime=Date.now()-24*60*60*1000*1
        return time.getTime() <= _maxTime
    }
    const emits=defineEmits(["closeAdd","success"])
    const closeAdd=()=>{
        emits("closeAdd")

    }
    const save=()=>{
        emits("closeAdd")
    }
</script>