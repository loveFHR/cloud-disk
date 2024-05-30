<template>
    <el-button type="primary" text @click="openLog">
        分享
    </el-button>
    <el-dialog v-model="dlogin" title="分享" style="width: 31vw;height: 38vh;background-color: #ebf5ff; border-radius: 12px;" @close="close">
        <span>有效期</span>
        <el-radio-group v-model="radio" @change="handleChange">
            <el-radio :label="1">1天</el-radio>
            <el-radio :label="7">7天</el-radio>
            <el-radio :label="30">30天</el-radio>
            <el-radio :label="365">365天</el-radio>
            <el-radio :label="-1">永久</el-radio>
            <el-radio :label="0">自定义</el-radio>
        </el-radio-group>
        <div class="block" v-if="is_sel">
            <el-date-picker
                v-model="date"
                type="datetime"
                placeholder="分享截止日期"
                :disabledDate="disabledDateFn"></el-date-picker>
        </div>
        <div>
            <el-input v-if="token" v-model="token" style="width: 19vw;" :readonly="true" @focus="onFocus($event)"></el-input>
            <el-button type="primary" style="float: right;" @click="share">获取分享令牌</el-button>
        </div>
    </el-dialog>
</template>
<script setup lang="ts">
import { ref,watch,defineProps} from 'vue'
import {$share} from '../../api/fileop'
import { ElMessage, ElMessageBox } from 'element-plus'
const dlogin = ref(false)
const openLog=()=>{
    if(props.rename_sel.length>0){
        dlogin.value=true;
    }
}
const radio = ref(1)
const date = ref<Date | null>(null);
const is_sel=ref(false)
const token=ref('')
const ids=ref([])
const close=()=>{
    token.value="";
}
const onFocus=(event)=>{
    event.currentTarget.select();
}
const disabledDateFn = (time: any) => {
    return time.getTime() <Date.now();
}
const props=defineProps({
    rename_sel:Array<any>
});
watch(()=>props.rename_sel,()=>{
    console.log(props.rename_sel);
    ids.value=[];
    for (const item of props.rename_sel) {
        ids.value.push(item.id);
    }
})
const handleChange=()=>{
    if(radio.value===0){
        is_sel.value=true;
    }else{
        is_sel.value=false;
    }
}
const share=async()=>{
    if(radio.value===0){
        if(date.value===null) return;
        let timeOut=Math.ceil((date.value.getTime()-Date.now())/1000);
        let r=await $share({
            ids:ids.value,
            timeOut,
        });
        if(r.msg==="success"){
            token.value=r.token;
        }else{
            ElMessage.error(r.msg);
        }
    }else if(radio.value===-1){
        let r=await $share({
            ids:ids.value,
        });
        if(r.msg==="success"){
            token.value=r.token;
        }else{
            ElMessage.error(r.msg);
        }
    }else{
        let timeOut=radio.value*24*60*60;
        let r=await $share({
            ids:ids.value,
            timeOut,
        });
        if(r.msg==="success"){
            token.value=r.token;
        }else{
            ElMessage.error(r.msg);
        }
    }
}
</script>
<style scoped lang="scss">
div{
    margin-bottom: 2vh;
}
span{
    font-size: larger;
    margin-bottom: 2vh;
}
.demo-datetime-picker {
  display: flex;
  width: 100%;
  padding: 0;
  flex-wrap: wrap;
}
.demonstration {
  display: block;
  color: var(--el-text-color-secondary);
  font-size: 14px;
  margin-bottom: 20px;
}
</style>