<script setup lang="ts">
import {
  Management,
  Promotion,
  UserFilled,
  User,
  Crop,
  EditPen,
  SwitchButton,
  CaretBottom
} from '@element-plus/icons-vue'
import { useRouter } from 'vue-router'
// import {getAdmin} from '@/store/admin.js'
// onMounted(() => {
//     getAdmin()
// })
</script>

<template>
    <el-container class="layout-container">
      <el-aside width="200px">
        <div class="el-aside__logo"></div>
        <!-- 高亮 -->
        <el-menu
          active-text-color="#ffd04b" 
          background-color="#232323"
          :default-active="$route.path"
          text-color="#fff"
          router
        >
          <el-menu-item index="/admin/user/manage">
            <el-icon><Management /></el-icon>
            <span>用户管理</span>
          </el-menu-item>
          <el-menu-item index="/admin/department/manage">
            <el-icon><Promotion /></el-icon>
            <span>部门管理</span>
          </el-menu-item>
  
          <!-- <el-sub-menu index="/user"> -->
            <!-- 多级菜单的标题 - 具名插槽 title -->
            <!-- <template #title>
              <el-icon><UserFilled /></el-icon>
              <span>个人中心</span>
            </template> -->
  
            <!-- 展开的内容 - 默认插槽 -->
            <!-- <el-menu-item index="#">
              <el-icon><User /></el-icon>
              <span>基本资料</span>
            </el-menu-item>
            <el-menu-item index="#">
              <el-icon><Crop /></el-icon>
              <span>更换头像</span>
            </el-menu-item>
            <el-menu-item index="#">
              <el-icon><EditPen /></el-icon>
              <span>重置密码</span>
            </el-menu-item>
          </el-sub-menu> -->
        </el-menu>
      </el-aside>
      <el-container>
        <!-- <el-header>
          <div>
            当前用户:<strong>杨东太帅啦</strong>
          </div>
          <el-dropdown placement="bottom-end" @command="handleCommand"> -->
            <!-- 展示给用户，默认看到的 -->
            <!-- <span class="el-dropdown__box">
              <el-avatar src="avatar" />
              <el-icon><CaretBottom /></el-icon>
            </span> -->
  
            <!-- 折叠的下拉部分 -->
            <!-- <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="profile" :icon="User"
                  >基本资料</el-dropdown-item
                >
                <el-dropdown-item command="avatar" :icon="Crop"
                  >更换头像</el-dropdown-item
                >
                <el-dropdown-item command="password" :icon="EditPen"
                  >重置密码</el-dropdown-item
                >
                <el-dropdown-item command="logout" :icon="SwitchButton"
                  >退出登录</el-dropdown-item
                >
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </el-header> -->
        <el-main>
          <router-view></router-view>
        </el-main>
        <!-- <el-footer> ©2023 Created by FHR</el-footer> -->
      </el-container>
    </el-container>
  </template>
  
  <style lang="scss" scoped>
  .layout-container {
    height: 100vh;
    .el-aside {
      background-color: #232323;
      &__logo {
        height: 120px;
        background: url('@/assets/logo.png') no-repeat center / 120px auto;
      }
      .el-menu {
        border-right: none;
      }
    }
    .el-header {
      background-color: #fff;
      display: flex;
      align-items: center;
      justify-content: space-between;
      .el-dropdown__box {
        display: flex;
        align-items: center;
        .el-icon {
          color: #999;
          margin-left: 10px;
        }
  
        &:active,
        &:focus {
          outline: none;
        }
      }
    }
    .el-footer {
      display: flex;
      align-items: center;
      justify-content: center;
      font-size: 14px;
      color: #666;
    }
  }
  </style>