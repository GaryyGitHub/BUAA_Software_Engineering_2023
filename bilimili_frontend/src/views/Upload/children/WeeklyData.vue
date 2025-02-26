<!-- 投稿页-周报 -->
<template>
  <div class="container">
    <div class="title">
      <span class="title-text">数据概览</span>
      <span class="tip-text">这里展示了你所有投稿视频的相关数据，每周会定时发送周报，请及时查看</span>
    </div>
    <div class="content">
      <div class="box" @mouseover="handleMouseOver(1)" @mouseout="handleMouseOut(1)">
        <div class="text" :style="{color: textColor(1)}">播放量</div>
        <div class="text-data" :style="{color: textDataColor(1)}">{{viewSum}}</div>
      </div>
      <div class="box" @mouseover="handleMouseOver(2)" @mouseout="handleMouseOut(2)">
        <div class="text" :style="{color: textColor(2)}">获赞量</div>
        <div class="text-data" :style="{color: textDataColor(2)}">{{likeSum}}</div>
      </div>
      <div class="box" @mouseover="handleMouseOver(3)" @mouseout="handleMouseOut(3)">
        <div class="text" :style="{color: textColor(3)}">收藏量</div>
        <div class="text-data" :style="{color: textDataColor(3)}">{{favoriteSum}}</div>
      </div>
      <div class="box" @mouseover="handleMouseOver(4)" @mouseout="handleMouseOut(4)">
        <div class="text" :style="{color: textColor(4)}">关注量</div>
        <div class="text-data" :style="{color: textDataColor(4)}">{{followSum}}</div>
      </div>
    </div>
    <div class="chart-background">
      <div ref="chart" class="chart-content"></div>
    </div>
  </div>
</template>

<script setup>
import { computed, watch, ref, onMounted, } from 'vue';
import { useStore } from 'vuex';
import { get } from '@/network/request'
import * as echarts from 'echarts';

const store = useStore();

const uidVisiting = computed(() => {
  return store.state.user.uid;
});

const viewSum = ref(0)
const likeSum = ref(0)
const favoriteSum = ref(0)
const followSum = ref(0)

const favorite = ref([])
const follow = ref([])
const view = ref([])
const like = ref([])

// 获取数据相关
const getData = async() => {
  const res = await get('/user_record', {
    params: { uid: uidVisiting.value },
    headers: { Authorization: "Bearer " + localStorage.getItem("teri_token") }
  })
  console.log('getData', res)
  if(!res.data.data) return
  favorite.value = res.data.data.favorite
  follow.value = res.data.data.follow
  view.value = res.data.data.view
  like.value = res.data.data.like
}

const calculateTotals = () => {
  favoriteSum.value = favorite.value.reduce((acc, cur) => acc + cur, 0)
  followSum.value = follow.value.reduce((acc, cur) => acc + cur, 0)
  viewSum.value = view.value.reduce((acc, cur) => acc + cur, 0)
  likeSum.value = like.value.reduce((acc, cur) => acc + cur, 0)
}

// 图表相关
const chart = ref(null);
const drawChart = () => {
  const myChart = echarts.init(chart.value);
  const currentDate = new Date();
  const days = [];
  for (let i = 1; i <= 7; i++) {
    const date = new Date(currentDate);
    date.setDate(currentDate.getDate() - i);
    days.unshift(date.toLocaleDateString('zh-CN', { month: '2-digit', day: '2-digit' }));
  }
  const data = {
    days: days,
    views: view.value,
    favorites: favorite.value,
    follows: follow.value,
    likes: like.value
  };
  console.log('data',data)
  const option = {
    tooltip: {
      trigger: 'axis'
    },
    legend: {
      data: ['观看量', '收藏量', '关注量', '获赞量']
    },
    xAxis: {
      type: 'category',
      data: data.days
    },
    yAxis: {
      type: 'value'
    },
    series: [
      {
        name: '观看量',
        type: 'line',
        data: data.views,
        itemStyle: {
          color: '#001627',
          borderWidth: 20
        }
      },
      {
        name: '收藏量',
        type: 'line',
        data: data.favorites,
        itemStyle: {
          color: '#004B76',
          borderWidth: 20
        }
      },
      {
        name: '关注量',
        type: 'line',
        data: data.follows,
        itemStyle: {
          color: '#008AC5',
          borderWidth: 20
        }
      },
      {
        name: '获赞量',
        type: 'line',
        data: data.likes,
        itemStyle: {
          color: '#40C5F1',
          borderWidth: 20
        }
      }
    ]
  };

  myChart.setOption(option);

  myChart.on('mousemove', params => {
    const pointInPixel = [params.offsetX, params.offsetY];
    const pointInGrid = myChart.convertFromPixel('grid', pointInPixel);
    const dataIndex = Math.round(pointInGrid[0]);
    const value = [];
    myChart.getOption().series.forEach(series => {
      value.push(series.data[dataIndex]);
    });
    myChart.dispatchAction({
      type: 'showTip',
      seriesIndex: 0,
      dataIndex: dataIndex,
      position: pointInGrid,
      data: value
    });
  });
};

// 美观相关
const isHovered = ref({1: false, 2: false, 3: false, 4: false})

const handleMouseOver = (index) => {
  isHovered.value[index] = true
}

const handleMouseOut = (index) => {
  isHovered.value[index] = false
}

const textColor = computed(() => {
  return (num) => {
    return isHovered.value[num] ? 'white' : '#61666D'
  }
})

const textDataColor = computed(() => {
  return (num) => {
    return isHovered.value[num] ? 'white' : 'black'
  }
})

onMounted(async() => {
  console.log(uidVisiting.value)
  await getData()
  calculateTotals()
  console.log(viewSum.value, likeSum.value, favoriteSum.value, followSum.value)
  drawChart()
})

watch(uidVisiting, async() => {
  console.log(uidVisiting.value)
  await getData()
  calculateTotals()
  console.log(viewSum.value, likeSum.value, favoriteSum.value, followSum.value)
  drawChart()
})

</script>

<style scoped>
.container {
  margin-left: 300px;
  margin-right: 90px;
  border-radius: 20px;
  border: 1px solid #999;
  background-color: #fff;
}

.title {
  width: 1200px;
  margin: 30px 40px 20px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  height: 30px;
}

.title-text {
  font-size: 24px;
  font-weight: bold;
  color: black;
  text-align: left;
  display: flex;
  align-items: center;
}

.tip-text {
  margin-right: 40px;
  font-size: 14px;
  color: #999;
  text-align: left;
  display: flex;
  align-items: center;
}

.content {
  display: flex;
  justify-content: center;
  height: 25vh;
}

.box {
  width: 200px;
  height: 100px;
  background-color: #eeeeee;
  border-radius: 20px;
  display: flex;
  justify-content: center;
  margin-left:15px;
  margin-top:20px;
  flex-direction: column;
  align-items: center;
  cursor: pointer;
  transition: background-color 0.3s ease;
}

.box:hover {
  background-color: #166fe8;
}

.text {
  /* text-align-last:justify; */
  padding-right:110px;
  margin-top: 8px;
  text-align: left;
  color: #999;
  font-size: 20px;
}

.text-data {
  padding-right:123px;
  margin-top: 10px;
  text-align: left;
  margin-bottom: 15px;
  font-stretch: extra-condensed;
  font-weight: bold;
  color: black;
  font-size:24px;
}

.chart-background {
  display: flex;
  background-color: white;
  justify-content: center;
  align-items: center;
  border-top-left-radius: 40px;
  border-top-right-radius: 40px;
  margin-top: -40px;
}

.chart-content {
  width: 1000px;
  height: 500px;

}

</style>