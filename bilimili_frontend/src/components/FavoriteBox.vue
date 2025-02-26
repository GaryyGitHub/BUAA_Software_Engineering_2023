<!-- 收藏功能 -->
 <!-- 父组件需要做的：传过来props参数alreadySelected（**该用户**收藏了这个视频的所有收藏夹**数组**，按fid升序），vid（视频id） -->
  <!-- 还要定义一个回调函数，用@favorite传递 -->
<template>
  <div class="box">
    <div class="box-title">添加到收藏夹</div>
    <div class="box-content">
      <!-- 需要显示输入框时，ban掉其他地方 -->
      <div class="check-box" >
        <el-checkbox-group :class="{'ban-operate': showInput}" v-model="selectList" v-for="(item, index) in favList" :key="index">
          <el-checkbox :value="item.fid" border>
            <template v-slot:default>
              <span >{{ item.title }}</span>
              <span class="slot-text">{{ item.count }}</span>
            </template>
          </el-checkbox>
        </el-checkbox-group>
        <div class="add">
          <!-- 新建收藏夹的按钮 -->
          <div class="add-new" v-if="!showInput" @click="(event) => enableShowInput(event)">
            <el-icon :size="18"><Plus /></el-icon><div style="margin-left: 20px;">新建收藏夹</div>
          </div>
          <el-input v-else class="input-box" v-model="newFavTitle"  placeholder="收藏夹名称，最多20字">
            <template #suffix><el-icon :size="22" class="ok-icon" @click="createNewFav"><Select /></el-icon>
            </template>
          </el-input>
        </div>
      </div>
      <div class="confirm">
        <div class="confirm-botton" :class="{'disable': showInput || neverChanged }" @click="confirm">确定</div>
      </div>
    </div>
  </div>
</template>

<script setup>
// 引用该组件时，需要传入props参数：vid，lastSelected（**该用户**收藏了这个视频的所有收藏夹**数组**，按fid升序）
import { ref, computed, onMounted, defineProps, onBeforeUnmount, defineEmits } from 'vue'
import { useStore } from 'vuex';
import { get, post } from '@/network/request'
import { ElMessage } from 'element-plus'

const store = useStore()
// 使用父组件的方法
const emit = defineEmits(['favorite'])

// 当前用户
const uidVisiting = computed(() => {
  return store.state.user.uid;
});

// 勾选的收藏夹列表有没有变化
const neverChanged = computed(() => {
  var temp = [].concat(selectList.value)
  temp.sort()
  return (JSON.stringify(temp) === JSON.stringify(props.alreadySelected))
})

const favList = ref([]) // 收藏夹列表
const selectList = ref() // 选择要放进去的收藏夹列表
const newFavTitle = ref('') // 新建收藏夹的标题

// 查询当前用户的收藏夹列表
const getFavList = async() => {
  const res = await get("/favorite/get-all/visitor", {
    params: { uid: uidVisiting.value }
  })
  // 优先显示默认收藏夹
  const defaultFav = res.data.data.find(item => item.type === 1);
  const list = res.data.data.filter(item => item.type !== 1);
  list.unshift(defaultFav);
  favList.value = list
  console.log('favList', favList.value)
}

// 建立新的收藏夹，向后端通信，在前端展示
const createNewFav = async() => {
  if (newFavTitle.value.length > 20) {
    ElMessage.error("名称超过20字，请重新输入");
    return
  }
  if (newFavTitle.value.length === 0) {
    ElMessage.error("名称不能为空，请重新输入");
    return
  }
  const formData = new FormData();
  formData.append("title", newFavTitle.value);
  formData.append("desc", "");
  formData.append("visible", 1);
  const res = await post("/favorite/create", formData, {
    headers: { Authorization: "Bearer " + localStorage.getItem("teri_token") }
  })
  if(!res.data) return
  selectList.value = props.alreadySelected
  newFavTitle.value = ''
  showInput.value = false
  await getFavList()    // 要更新收藏夹列表，因为有了新建的！！！
  ElMessage.success("新建收藏夹成功")
}

// 确认收藏
const confirm = async() => {
  if(neverChanged.value) return
  // alreadySelected中有，但selectList中没有的，需要从后端删除
  const removeList = props.alreadySelected.filter(item => !selectList.value.includes(item));
  // selectList中有，但alreadySelected中没有的，需要从后端增加
  const addList = selectList.value.filter(item => !props.alreadySelected.includes(item));
  
  console.log('removeList', removeList)
  console.log('addList', addList)

  // 之前没收藏，现在加入收藏夹了，就是收藏了
  const isFavorited = props.alreadySelected.length === 0 && addList.length > 0
  // 之前有收藏，现在没有增加收藏夹，反而取消的收藏夹数等于之前的收藏夹数，就是取消收藏了
  const notFavorited = props.alreadySelected.length > 0 && addList.length === 0 && removeList.length === props.alreadySelected.length
  // 此处有更新store.state.attitudeToVideo的代码，暂时不写，详见AddToFavorite.vue的submit函数，需要再写

  const formData = new FormData();
  formData.append("vid", props.vid);
  formData.append("adds", addList.join(','));
  formData.append("removes", removeList.join(','));
  const res = await post("/video/collect", formData, {
    headers: { Authorization: "Bearer " + localStorage.getItem("teri_token") }
  })
  if(!res.data) return
  // @favorite事件调用父组件的updateFavorites函数，更新收藏夹列表
  const info = {
    fids: selectList.value,
    num: isFavorited ? 1 : notFavorited ? -1 : 0
  }
  emit('favorite', info)
}

// 从父组件传入的props参数
const props = defineProps({
  alreadySelected: {
    type: Array,
  },
  vid: {
    type: Number,
  }
})

const showInput = ref(false) // 是否显示新建收藏夹输入框

const enableShowInput = (event) => {
  showInput.value = true
  // 阻止事件冒泡，防止点击到收藏夹按钮什么的
  event.stopPropagation()
}

// 点击除了提交按钮和新建收藏夹输入框以外的部分区域，就不显示输入框了
const cancelPropagate = (event) => {
  const addNew = document.querySelector('.add')
  const confirm = document.querySelector('.confirm')
  if (!addNew.contains(event.target) && !confirm.contains(event.target)) {
    showInput.value = false
  }
}

// created
// 把传入的alreadySelected数组，转化成selectList数组，方便v-model双向绑定
onMounted(async() => {
  selectList.value = props.alreadySelected
  console.log('selectList', selectList.value)
  await getFavList()
  window.addEventListener('click', cancelPropagate)
})

onBeforeUnmount(() => {
  window.removeEventListener('click', cancelPropagate)
})

</script>

<style scoped>
.box {
  width: 400px;
  background-color: white;
}

.box-title {
  position: relative;
  height: 50px;
  font-size: 20px;
  text-align: center;
  color: black;
}

.check-box {
  height: 350px;
  overflow: auto;
  display: flex;
  flex-direction: column;
  align-items: center;
  border-bottom: 1px solid #e4e4e4;
}

.check-box /deep/ .el-checkbox {
  /* background-color: #bc0000; */
  width: 300px;
  height: 40px;
  transition: background-color .3s ease;
  transition: border-color .3s ease;
}

.check-box /deep/ .el-checkbox:hover  {
  border-color: #166fe8;
}

.check-box /deep/ .el-checkbox.is-checked {
  background-color: #e4e4e486;
  border-color: #166fe8;
}

.check-box /deep/ .el-checkbox__input {
  border-radius: 4px;
}

.check-box /deep/ .el-checkbox__input.is-checked+.el-checkbox__label {
  color: #166fe8;
}

.check-box /deep/ .el-checkbox .el-checkbox__label {
  color:black;
  font-size: 15px;
  line-height: 24px;
  margin-left: 20px;
  width: 270px;
  display: flex;
  justify-content: space-between;
}

.slot-text {
  font-size: 14px;
  color: #999;
}

.ban-operate {
  opacity: 0.3;
  background-color: #fff;
  /* background: #651414; */
}

.add {
  min-height: 40px;
  min-width: 300px;
  margin-top: 20px;
  margin-bottom: 20px;
}

.add-new {
  font-size: 16px;
  display: flex;
  align-items: center;
  justify-content: center;
  width: 300px;
  height: 40px;
  border-radius: 10px;
  border: 1px solid#f25d8e;
  cursor: pointer;
  color: black;
  background-color: white;
  transition: background-color 0.3s ease;
  transition: color 0.2s ease;
}

.add-new:hover {
  background-color:#f25d8e;
  color: white;
}

.input-box {
  width: 100%;
}

.input-box >>> .el-input__inner {
  padding-left: 5px;
  font-family: 'Harmony Font';
  height: 40px;
  font-size: 15px !important;
}

.input-box >>> .el-input__wrapper {
  border: 1px solid #166fe8;
  border-radius: 15px;
  padding-right: 0;
  padding-top: 0;
  padding-bottom: 0;
}

.ok-icon {
  height: 40px;
  width: 40px;
  border: 1px solid #166fe8;
  border-radius: 15px;
  cursor: pointer;
}

.confirm {
  margin-top: 20px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
}

.confirm-botton {
  font-size: 16px;
  display: flex;
  align-items: center;
  justify-content: center;
  width: 100px;
  height: 40px;
  border-radius: 10px;
  border: 1px solid#f25d8e;
  cursor: pointer;
  color: black;
  background-color: white;
  transition: background-color 0.3s ease;
  transition: color 0.2s ease;
}

.confirm-botton:hover {
  background-color:#f25d8e;
  color: white;
}

.disable {
  background-color: #e4e4e4;
  color: #999;
  border: 1px solid #999;
}

.disable:hover {
  background-color: #e4e4e4;
  color: #999;
  text-decoration: line-through;
}
</style>