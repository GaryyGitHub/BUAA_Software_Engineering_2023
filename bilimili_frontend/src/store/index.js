import { createStore } from 'vuex'
import axios from 'axios'
import { ElMessage } from 'element-plus'

export default createStore({
  state: {
    // 是否登录
    isLogin: false,
    // 用户信息
    user: {},
    // 实时通讯websocket
    ws: null,
    isLoading: false,
    channels: [],
  },
  mutations: {
    // 获取初始化数据
    getInitData(state) {
      state.isLogin = false
      state.user = {}
    },
    // 修改登录状态
    updateIsLogin(state, isLogin) {
      state.isLogin = isLogin
    },
    // 更新当前用户信息
    updateUser(state, user) {
      state.user = user
    },
    // 处理websocket事件
    setWebSocket(state, ws) {
      state.ws = ws;
    },
    updateChannels(state, channels) {
      state.channels = channels;
      // console.log("vuex中的分区: ", state.channels);
    },
  },
  actions: {
    async getPersonalInfo(context) {
      // 异步获取用户信息
      const res = await axios.get('/api/user/personal/info', {
        headers: {
          Authorization: "Bearer " + localStorage.getItem("teri_token")
        }
      // })
      // console.log('nextline!!',context)
      // console.log('看这里！！！！',res);
      }).catch(() => {
          context.commit('getInitData')
          // 关闭ws
          if(context.state.ws) {
            context.state.ws.close()
            context.commit('setWebSocket', null)
          }
          localStorage.removeItem("teri_token")
          ElMessage.error('请先登录')
        })
        console.log('userInfo', res);
        // 这里有问题！总是报错
      // if (res.data.code === 200) {
        context.commit('updateIsLogin', true)
        context.commit('updateUser', res.data.data)
      // }
    },

    connectWebSocket({ commit, state }) {
      return new Promise((resolve) => {
        if (state.ws) {
          state.ws.close()
          commit('setWebSocket', null)
        }
        // const ws = new WebSocket('ws://116.62.87.161:7071/im')
        // const ws = new WebSocket('ws://10.192.169.195:7071/im')
        const ws = new WebSocket('ws://bilimili-backend:7071/im')
        commit('setWebSocket', ws)
        ws.addEventListener('open', () => {
          resolve()
        })
        ws.addEventListener('message', e => commit('handleWebSocketMessage', e))
        ws.addEventListener('close', () => commit('getInitData'))
      })
    },

    async closeWebSocket({ commit, state }) {
      if (state.ws) {
        await state.ws.close()
        commit('setWebSocket', null)
      }
    },

    logout(context) {
      context.commit('getInitData')
      if(context.state.ws) {
        context.state.ws.close()
        context.commit('setWebSocket', null)
      }
      // axios.get("/api/user/account/logout", {
      //   headers: {
      //     Authorization: "Bearer " + localStorage.getItem("teri_token"),
      //   }
      // })
      // 清除本地存储的token
      localStorage.removeItem("teri_token")
      console.log("logout success, loginState:", context.state.isLogin, "user: ", context.state.user)
    },
  }
})