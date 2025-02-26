<template>
  <div class="navbar-middle">
    <el-input clearable v-model="searchText" class="search-box" size="large" placeholder="要搜些什么呢..."
        @keyup.enter="handleSearch()"
        @focus="showPop()" >
        <!-- 在搜索框的后面插入搜索icon  -->
        <template #append>
            <el-icon :size="25" style="cursor: pointer;" @click="handleSearch()"><Search /></el-icon>
        </template>
      </el-input>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import { defineProps } from 'vue';

const props = defineProps({
  keyword: {
    type: String,
    default: ''
  }
}); // 接收路由中传过来的keyword
const searchText = ref('');
const router = useRouter();

const handleSearch = () => {
  let searchTextValue = searchText.value.trim();
  console.log('搜索内容：' + searchTextValue);
  searchText.value = '';       // 清空搜索框内容
  if(searchTextValue === ''){  // 如果搜索框内容为空，则跳转到搜索默认页面
    openNewPage("/search")
  } else {
    openNewPage(`/search?keyword=${searchTextValue}`)
  }
};

// 展示推荐内容，暂时不做
const showPop = () => {
  console.log('需要显示推荐内容');
};

const openNewPage = (route) => {
  window.open(router.resolve(route).href, '_self');
};

onMounted(() => {
  // 页面加载完成后，将keyword赋值给搜索框
  searchText.value = props.keyword;
});

</script>

<style scoped>
.navbar-middle {
  width: 700px;
  background-color: #bcbcbc;
  padding: 3px;
  border-radius: 10px;
  transition: background-color 0.2s ease;
}

.navbar-middle:focus-within {
  background-color: rgb(19, 29, 70);
}

.search-box /deep/ .el-input__wrapper {
  height: 45px;
  width: 700px;
  font-size: 16px;
  border-radius: 10px;
  border:0;
  box-shadow: 0 0 0 0px;
}

.search-box /deep/ .el-input__inner {
  font-family: 'Harmony Font';
  font-size: 18px;
}

.search-box /deep/ .el-input-group__append {
  background-color: white;
  border-radius: 10px;
  border:0;
  box-shadow: 0 0 0 0px;
  transition: background-color 0.2s ease;
}

.search-box /deep/ .el-input-group__append:hover {
  background-color: #dedede;
}
</style>