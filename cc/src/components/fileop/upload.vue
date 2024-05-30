<template>
  <el-upload
    class="upload-demo"
    action=""
    multiple
    :show-file-list="false" 
    :auto-upload="false"
    :on-change="handleChange"
  >
    <el-button type="primary" text>上传</el-button>
  </el-upload>
</template>
<script lang="ts" setup>
import { ref,watch,defineProps ,defineEmits} from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {$upload,$checkfile,$uploadchunk,$checkchunk,$mergechunks} from '../../api/fileop'
import {$queryAllfile} from '../../api/file'
import type { UploadProps, UploadUserFile } from 'element-plus'
import SparkMD5 from "spark-md5";
const emits=defineEmits(['percent','load']);
const path1=ref('')
const props=defineProps({
    path1:String,
});
watch(()=>props.path1,()=>{
    path1.value=props.path1;
    console.log("pa",path1.value);
})

const chmb=5;
const getFileMd5 = (file: File, chunkCount: number, chunkSize: number) => {
  return new Promise((resolve, reject) => {
    const blobSlice = File.prototype.slice
    const chunks = chunkCount
    let currentChunk = 0
    const spark = new SparkMD5.ArrayBuffer()
    const fileReader = new FileReader()
    fileReader.onload = e => {
      spark.append(e.target?.result)
      currentChunk ++
      if(currentChunk < chunks){
        loadNext()
      } else {
        const md5 = spark.end()
        resolve(md5)
      }
    }
    fileReader.onerror = e => {
        reject(e)
    }
    function loadNext () {
        const start = currentChunk * chunkSize
        let end = start + chunkSize
        if(end > file.size){
            end = file.size
        }
        fileReader.readAsArrayBuffer(blobSlice.call(file, start, end))
    }
    loadNext()
  })
}
let c=0;
const uploadLargeFile = async (file) => {
  const chunkSize = chmb * 1024 * 1024; // 分片大小
	const fileSize = file.size // 文件大小
	let chunkCount = Math.ceil(fileSize / chunkSize) // 分片数量
	if(chunkSize > fileSize){ // 文件过小就一片
	    chunkCount = 1
	}
	// 文件md5，给文件一个唯一标识
	const fileMd5 = await getFileMd5(file, chunkCount, chunkSize);
  let cnt=chunkCount;
	// 上传分片
  emits('percent',0);
	for( let i=0;i<chunkCount;i++) {
    	const start = i * chunkSize //分片开始
    	const end = Math.min(fileSize, start + chunkSize) // 分片结束
    	const _chunkFile = file.slice(start, end) // 分片文件
    	// 定义分片上传接口参数
    	let bfile=new File([_chunkFile],file.name);
	    // 检查分片文件是否上传-没有上传则上传
	    let r=await $checkchunk({
        'chunk':i,
        'fileMd5':fileMd5,
      });
	    if(!r){
	      let r1=await $uploadchunk({
          'chunk':i,
          'file':bfile,
          'fileMd5':fileMd5,
        });
        if(r1){
	        cnt--;c++;
          emits('percent',(c*100/chunkCount).toFixed(2));
        }
	    }else{
        cnt--;
      }
    }
    if(cnt<=0){
      let r2=await $mergechunks({
        status:0,
        parentPath:path1.value,
        chunkTotal:chunkCount,
        fileMd5:fileMd5,
        fileName:file.name,
      });
      emits('percent',-1);
      if(r2){
        c=0;
      emits('load',path1.value,false);
        ElMessage({
          message: '上传成功',
          type: 'success',
        });
      }else{
        ElMessage.error('上传失败');
      }
    }
};

const uploadSmallFile = async (file) => {
    let r1 = await $upload({
      parentPath: path1.value,
      filedata: file,
      status: 0,
    });
    if (r1) {
      emits('load',path1.value,false);
      ElMessage({
        message: '上传成功',
        type: 'success',
      });
    } else {
      ElMessage.error('上传失败');
    }
};
// const uploadF = async (upfile) => {
//   const file = upfile.file;
//   const fileSize = file.size;
//   let r = await $checkfile({
//     filedata: file,
//     parentPath: path1.value,
//     status: 0,
//   });
//   if(!r){
//     if (fileSize > chmb * 1024 * 1024) {
//       await uploadLargeFile(file);
//     } else {
//       await uploadSmallFile(file);
//     }
//   }else{
//     ElMessage({
//         message: '上传成功',
//         type: 'success',
//       });
//   }
// };
const handleChange = async (upfile) => {
  const file = upfile.raw;
  const fileSize = file.size;
  let r = await $checkfile({
    filedata: file,
    parentPath: path1.value,
    status: 0,
  });
  if(!r){
    if (fileSize > chmb * 1024 * 1024) {
      await uploadLargeFile(file);
    } else {
      await uploadSmallFile(file);
    }
  }else{
    // ElMessage({
    //     message: '上传成功',
    //     type: 'success',
    //   });
  }
};
</script>
