<template>
    <el-card class="page-container">
      <template #header>
          <div class="header">
            <span>{{title}}</span>
            <slot name="extra"></slot>
          </div>
      </template>
      <slot></slot>
    </el-card>
  </template>
  
  <script lang="ts" setup>
  defineProps({
    title:{
        required:true,
        type:String
        
    }
  })
  </script>
  
  <style lang="scss" scoped>
    .page-container {
      min-height: 100%;
      box-sizing: border-box;
      .header {
        display: flex;
        justify-content: space-between;
        align-items: center;
      }
    }
  </style>