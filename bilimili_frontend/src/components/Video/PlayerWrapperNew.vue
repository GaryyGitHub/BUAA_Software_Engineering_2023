<template>
    <div id="playerWrap" class="player-wrapper" :style="`height: ${size.height}px; width: ${size.width}px;`">
        <!-- 主体部分 -->
        <div class="player-area">
            <div class="player-primary-area">
                <!-- 视频播放区块 -->
                <div class="player-video-area" id="video-area" :data-screen="screenType" :data-ctrl-hidden="hideConsole" @mouseleave="hideConsole = true">
                    <!-- 视频组件 -->
                    <div class="player-video">
                        <div class="player-video-wrap">
                            <video ref="videoPlayer" :src="videoUrl"
                                   @loadedmetadata="videoIsReady"
                                   @timeupdate="timeUpdate"
                            ></video>
                        </div>
                    </div>
                    <div class="player-dm-wrap"></div>
                    <!-- 加载组件 -->
                    <div class="player-loading-wrap" :style="videoReady ? 'display: none;' : 'display: block;'">
                        <PlayerLoading></PlayerLoading>
                    </div>
                    <!-- 蒙版 -->
                    <div class="player-masking-wrap" @mousemove="showConsole" @click="handleClick" @dblclick="handleDblClick"></div>
                    <!-- 暂停提示标 -->
                    <div class="pause-svg" v-if="pause" :data-screen="screenType">
                        <img src="../../assets/img/play.svg" alt="" >
                    </div>
                    <!-- up主头像 -->
                    <div class="player-top-left-wrap">
                        <div v-if="user.uid">
                            <div class="player-uploader">
                                <img class="player-uploader-face" :src="user.avatar_url" alt="">
                            </div>
                        </div>
                    </div>
                    <!-- 控制器 -->
                    <div class="player-console-wrap" @mouseenter="showConsole">
                        <div class="player-console-mask"></div>
                        <div class="player-console-progress">
                            <div class="player-console-progress-line">
                                <PlayerProgress :currentPercentage="currentPer" @changeCurrent="changeCurrentPer"></PlayerProgress>
                            </div>
                            <div class="player-console-options">
                                <div class="player-console-bottom-left">
                                    <div class="player-console-controller-btn player-ctrl-play">
                                        <!-- 播放 -->
                                        <div class="player-ctrl-btn-icon player-ctrl-play-play" :style="pause ? '' : 'display: none;'" @click="playVideo">
                                            <span class="icon-svg">
                                                <svg t="1700644047374" class="icon" viewBox="0 0 1024 1024" version="1.1" xmlns="http://www.w3.org/2000/svg" p-id="4206" width="22" height="22"><path d="M847.52000001 429.80266667L344.50133334 101.152c-78.176-45.184-142.21866667-8.18133333-142.21866667 82.13333333v657.22666667c0 90.368 64.04266667 127.30666667 142.21866667 82.19733333L847.52000001 594.13333333c78.25066667-45.25866667 78.25066667-119.14666667 0-164.33066666z m0 0" fill="#FFFFFF" p-id="4207"></path></svg>
                                            </span>
                                        </div>
                                        <!-- 暂停 -->
                                        <div class="player-ctrl-btn-icon player-ctrl-play-pause" :style="pause ? 'display: none;' : ''" @click="pauseVideo">
                                            <span class="icon-svg">
                                                <svg t="1700644104462" class="icon" viewBox="0 0 1024 1024" version="1.1" xmlns="http://www.w3.org/2000/svg" p-id="4447" width="22" height="22"><path d="M735.9 64.9c-51.2 0-96 44.7-96 95.8v702.6c0 51.1 44.8 95.8 96 95.8s96-44.7 96-95.8V160.7c0-51.1-44.8-95.8-96-95.8z m-447.8 0c-51.2 0-96 44.7-96 95.8v702.6c0 51.1 44.8 95.8 96 95.8s96-44.7 96-95.8V160.7c-0.1-51.1-44.9-95.8-96-95.8z" p-id="4448" fill="#FFFFFF"></path></svg>
                                            </span>
                                        </div>
                                    </div>
                                    <div class="player-console-controller-btn player-ctrl-time">
                                        <div class="player-ctrl-time-label">
                                            <span class="current-time">{{ handleTime(currentTime) }}</span>
                                            <span class="divider">/</span>
                                            <span class="duration-time">{{ handleTime(duration) }}</span>
                                        </div>
                                    </div>
                                </div>
                                <div class="player-console-bottom-right">
                                    <!-- 清晰度 -->
                                    <div class="player-console-controller-btn player-ctrl-quality">1080P 高帧率</div>
                                    <!-- 倍速 -->
                                    <div class="player-console-controller-btn player-ctrl-playbackrate"
                                         @mouseenter="enterBtn('player-ctrl-playbackrate', 2)"
                                         @mouseleave="leaveBtn('player-ctrl-playbackrate', 2)"
                                    >
                                        <div class="player-ctrl-playbackrate-result">{{ playSpeedText }}</div>
                                        <ul class="player-ctrl-playbackrate-menu">
                                            <li class="player-ctrl-playbackrate-menu-item" :class="{'state-active': playSpeedText === '2.0x'}" @click="changePlaybackRate(2.0)">2.0x</li>
                                            <li class="player-ctrl-playbackrate-menu-item" :class="{'state-active': playSpeedText === '1.5x'}" @click="changePlaybackRate(1.5)">1.5x</li>
                                            <li class="player-ctrl-playbackrate-menu-item" :class="{'state-active': playSpeedText === '1.25x'}" @click="changePlaybackRate(1.25)">1.25x</li>
                                            <li class="player-ctrl-playbackrate-menu-item" :class="{'state-active': playSpeedText === '倍速'}" @click="changePlaybackRate(1.0)">1.0x</li>
                                            <li class="player-ctrl-playbackrate-menu-item" :class="{'state-active': playSpeedText === '0.75x'}" @click="changePlaybackRate(0.75)">0.75x</li>
                                            <li class="player-ctrl-playbackrate-menu-item" :class="{'state-active': playSpeedText === '0.5x'}" @click="changePlaybackRate(0.5)">0.5x</li>
                                        </ul>
                                    </div>
                                    <!-- 音量 -->
                                    <div class="player-console-controller-btn player-ctrl-volume"
                                         @mouseenter="enterBtn('player-ctrl-volume', 3)"
                                         @mouseleave="leaveBtn('player-ctrl-volume', 3)"
                                    >
                                        <div class="player-ctrl-btn-icon" @click="changeMutedState">
                                            <span class="icon-svg">
                                                <svg v-if="isMuted" t="1700670251969" class="icon" viewBox="0 0 1024 1024" version="1.1" xmlns="http://www.w3.org/2000/svg" p-id="4236" width="22" height="22"><path d="M225.680461 326.598406c-0.419556-0.019443-0.818645-0.019443-1.237177-0.019443L101.812315 326.578963c-22.753213 0-40.876989 18.24248-40.876989 40.777729l0 286.336424c0 22.534226 18.302855 40.777729 40.876989 40.777729l122.629945 0c0.079818 0 0.119727 0 0.198521 0l0 0.157589 300.289204 194.444551c7.125281 6.108115 16.405645 9.781784 26.526143 9.781784 22.573111 0 40.874943-18.301831 40.874943-40.878013 0-1.87572-0.119727-3.711532-0.360204-5.509481L591.970868 168.58151c0.239454-1.795902 0.360204-3.632737 0.360204-5.509481 0-22.574135-18.302855-40.876989-40.874943-40.876989-9.301853 0-17.884322 3.113921-24.750707 8.343015L225.680461 326.598406zM859.567485 510.524392l91.952248-91.951225c11.495822-11.517311 11.576663-30.558993-0.13917-42.274826-11.795651-11.795651-30.636764-11.755742-42.273802-0.140193l-91.953272 91.953272-91.950202-91.953272c-11.639085-11.616572-30.479175-11.655458-42.275849 0.140193-11.715833 11.715833-11.633968 30.757514-0.13917 42.274826l91.952248 91.951225-91.952248 91.953272c-11.494799 11.515265-11.576663 30.556946 0.13917 42.272779 11.796674 11.796674 30.636764 11.756765 42.275849 0.140193l91.950202-91.951225 91.953272 91.951225c11.636015 11.617595 30.477129 11.657504 42.273802-0.140193 11.715833-11.714809 11.634991-30.757514 0.13917-42.272779L859.567485 510.524392z" fill="#FFFFFF" p-id="4237"></path></svg>
                                                <svg v-else t="1700670170727" class="icon" viewBox="0 0 1024 1024" version="1.1" xmlns="http://www.w3.org/2000/svg" p-id="3989" width="22" height="22"><path d="M218.495828 334.609871c-0.390903-0.019443-0.773619-0.019443-1.164522-0.019443L100.022553 334.590428c-21.744233 0-39.087227 17.448394-39.087227 39.001269l0 273.866415c0 21.551852 17.505699 38.999223 39.087227 38.999223l117.308753 0c0.057305 0 0.113587 0 0.171915 0l0 0.153496 287.22056 185.975668c6.824429 5.842055 15.691377 9.354042 25.370831 9.354042 21.590737 0 39.096437-17.505699 39.096437-39.095413 0-1.794879-0.124843-3.551896-0.354064-5.270027L568.836985 183.473685c0.229221-1.718131 0.354064-3.475148 0.354064-5.269004 0-21.590737-17.505699-39.096437-39.096437-39.096437-8.895601 0-17.105586 2.977821-23.682375 7.979742L218.495828 334.609871z" fill="#FFFFFF" p-id="3990"></path><path d="M757.858012 953.491133l0.085958 0.075725c123.876332-107.514689 202.211445-266.13329 202.211445-443.041442 0-177.214121-78.603219-336.062965-202.851011-443.61654l-0.11461 0.11461c-4.963035-3.817955-11.17655-6.109138-17.925255-6.109138-16.197914 0-29.322839 13.133112-29.322839 29.321816 0 6.757914 2.28095 12.981662 6.109138 17.926278l-0.333598 0.342808c0.821715 0.706081 1.641383 1.393743 2.462075 2.119267 1.173732 1.202385 2.452865 2.329045 3.817955 3.321652 110.054535 96.710622 179.51349 238.550071 179.51349 396.578224 0 158.02713-69.458955 299.866578-179.51349 396.577201-1.36509 0.99363-2.644223 2.118244-3.817955 3.321652-0.600681 0.533143-1.212618 1.048889-1.822508 1.564635l0.229221 0.230244c-4.152577 5.058203-6.643304 11.530614-6.643304 18.593474 0 16.188704 13.124925 29.321816 29.322839 29.321816C746.317165 960.134437 752.798786 957.651896 757.858012 953.491133z" fill="#FFFFFF" p-id="3991"></path><path d="M713.998085 729.35433l0.23843 0.24764c55.380308-56.43022 89.532129-133.76454 89.532129-219.077577 0-85.409229-34.228569-162.800853-89.704045-219.249493l-0.268106 0.267083c-4.886287-3.64604-10.966773-5.821589-17.543561-5.821589-16.197914 0-29.322839 13.133112-29.322839 29.321816 0 6.566556 2.166339 12.657274 5.822612 17.544585l-0.162706 0.170892c0.773619 0.782829 1.547239 1.584078 2.310625 2.38635 0.075725 0.076748 0.152473 0.171915 0.23843 0.248663 43.3626 45.587268 69.983911 107.248629 69.983911 175.132716 0 67.884087-26.621311 129.544425-69.983911 175.131693-0.085958 0.077771-0.162706 0.171915-0.23843 0.24764-0.706081 0.74599-1.422396 1.471514-2.13871 2.214435l0.144286 0.134053c-3.751441 4.926196-5.976108 11.092639-5.976108 17.754363 0 16.188704 13.124925 29.321816 29.322839 29.321816C702.925912 735.328391 709.072913 733.113957 713.998085 729.35433z" fill="#FFFFFF" p-id="3992"></path></svg>
                                            </span>
                                        </div>
                                        <div class="player-ctrl-volume-box">
                                            <div class="player-ctrl-volume-number">{{ volume }}</div>
                                            <SliderColumn class="player-ctrl-volume-progress" :currentPer="volume / 100" :sliderHeight="60" v-model:order="volumeOrder" @changeCurrent="changeVolume"></SliderColumn>
                                        </div>
                                    </div>
                                    <!-- 全屏 -->
                                    <div class="player-console-controller-btn player-ctrl-full">
                                        <div class="player-ctrl-btn-icon" @click="changeFullScreen">
                                                <span class="icon-svg">
                                                    <svg v-if="screenType === 'normal'" t="1700682460725" class="icon" viewBox="0 0 1024 1024" version="1.1" xmlns="http://www.w3.org/2000/svg" p-id="4481" width="22" height="22"><path d="M992 392h-79.99999969V231.99999969h-160.00000031V152h240v240z m-240 480v-79.99999969h160.00000031v-160.00000031h79.99999969v240h-240zM32 632h79.99999969v160.00000031h160.00000031v79.99999969H32v-240zM272 152v79.99999969H111.99999969v160.00000031H32V152h240z m559.99999969 160.00000031H192.00000031v399.99999938h639.99999938V312.00000031z" fill="#FFFFFF" p-id="4482"></path></svg>
                                                    <svg v-if="screenType === 'full'" t="1700682742461" class="icon" viewBox="0 0 1024 1024" version="1.1" xmlns="http://www.w3.org/2000/svg" p-id="4722" width="22" height="22"><path d="M896 128H128a64.07 64.07 0 0 0-64 64v640a64.07 64.07 0 0 0 64 64h768a64.07 64.07 0 0 0 64-64V192a64.07 64.07 0 0 0-64-64zM448 672a32 32 0 0 1-64 0v-50.75L246.63 758.63a32 32 0 0 1-45.26-45.26L338.75 576H288a32 32 0 0 1 0-64h128a31.83 31.83 0 0 1 22.35 9.1l0.43 0.43A31.83 31.83 0 0 1 448 544z m374.63-361.37L685.25 448H736a32 32 0 0 1 0 64H608a31.83 31.83 0 0 1-22.35-9.1c-0.14-0.14-0.29-0.28-0.43-0.43A31.83 31.83 0 0 1 576 480V352a32 32 0 0 1 64 0v50.75l137.37-137.38a32 32 0 0 1 45.26 45.26z" fill="#FFFFFF" p-id="4723"></path></svg>
                                                </span>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                    <!-- 隐蔽进度条 -->
                    <div class="player-hidden-progress-wrap">
                        <div class="player-hidden-progress-duration-wrap">
                            <div class="player-progress-duration">
                                <div class="player-progress-current" :style="`transform: scaleX(${currentPer});`"></div>
                            </div>
                        </div>
                    </div>
                </div>
                <!-- 发送弹幕区域 -->
                <div class="player-dm-sending-area-outside">
                    <div class="player-video-current-state">
                        <div class="player-video-current-state-content">
                            <a>1</a>  人正在观看
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</template>

<script>
import PlayerLoading from './PlayerLoadingNew.vue';
import PlayerProgress from './PlayerProgressNew.vue';
import SliderColumn from './SliderColumn.vue';


let mouseTimer;
let menuShowTimer = [null, null, null, null];
let menuHideTimer = [null, null, null, null];

export default {
    name: "PlayerWrapper",
    components: {
        PlayerLoading,
        SliderColumn,
        PlayerProgress,
    },
    data() {
        return {
            videoWidth: 704,
            videoHeight: 442,
            size: {
                width: 704,
                height: 442,
            },
            videoReady: false,
            pause: false,
            playSpeedText: '倍速',
            screenType: 'normal',
            hideConsole: true,
            isMuted: false,
            volumeOrder: false,
            volume: 35,
            currentPer: 0,
            currentTime: 0,
        }
    },
    props: {
        // UP主资料
        user: {
            type: Object,
            default: () => {},
        },
        // 视频的播放地址
        videoUrl: {
            type: String,
            default: () => '',
        },
        // 视频总时长
        duration: {
            type: Number,
            default: () => 0.0,
        },
        // 当前观看人数
        population: {
            type: Number,
            default: () => 0,
        },
    },
    methods: {
        //////// 请求 /////////
        // 增加一个播放量
        async increasePlay() {
            const formData = new FormData();
            formData.append("vid", Number(this.$route.params.vid));
            if (this.$store.state.user.uid) {
                // 如果用户登录了，就算该用户观看了视频
                const res = await this.$post("/video/play/user", formData, {
                    headers: { Authorization: "Bearer " + localStorage.getItem("teri_token") }
                });
                if (!res.data.data) return;
                const data = res.data.data;
                const atv = {
                    love: data.love === 1,
                    unlove: data.unlove === 1,
                    coin: data.coin,
                    collect: data.collect === 1
                };
                this.$store.commit("updateAttitudeToVideo", atv);
            }
        },

        //////// 事件 /////////
        // 根据窗口大小改变播放器的宽高
        changeWindowSize() {
            const MAX_WIDTH_DIFF = 112;
            const MIN_HEIGHT_SMALL = 424;
            const MIN_HEIGHT_LARGE = 434;
            const BOTTOM_HEIGHT_SMALL = 46;
            const BOTTOM_HEIGHT_LARGE = 56;
            //
            const windowInnerWidth = window.innerWidth;
            const windowInnerHeight = window.innerHeight;
            //
            // 计算右侧容器宽度和底部高度
            let rightWidth = windowInnerWidth < 1681 ? 380 : 441;
            let bottomHeight = windowInnerWidth < 1681 ? BOTTOM_HEIGHT_SMALL : BOTTOM_HEIGHT_LARGE;

            // 计算初始高度，使其为屏高的70%
            let height = (windowInnerHeight - 64) * 0.7;

            // 根据高度计算初始宽度
            let width = (height - bottomHeight) * (16 / 9);

            // 限制宽度范围
            if (windowInnerWidth - width - rightWidth < MAX_WIDTH_DIFF) {
                width = windowInnerWidth - rightWidth - MAX_WIDTH_DIFF;
                height = width * (9 / 16) + bottomHeight;
            }

            // 限制高度范围
            const minHeight = windowInnerWidth < 1681 ? MIN_HEIGHT_SMALL : MIN_HEIGHT_LARGE;
            height = Math.max(minHeight, height);
            height = Math.min(1010, height);

            //根据最终高度计算最终宽度
            width = (height - bottomHeight) * (16 / 9);

            this.size.width = width;
            this.size.height = height;
            this.videoWidth = width;
            this.videoHeight = height - bottomHeight;
            this.$emit('resize', this.size);
        },

        // 监听键盘按键触发对应事件
        handleKeyboard(event) {
            // console.log(event);
            // 检查当前焦点元素
            var activeElement = document.activeElement;
            var isInputField = activeElement.tagName == 'INPUT' || activeElement.tagName == 'TEXTAREA';

            if (isInputField) return;
            switch (event.keyCode) {
                case 32: // 空格键 播放/暂停
                    event.preventDefault();
                    this.changePlayState();
                    break;
                case 77: // M键 静音
                    event.preventDefault();
                    this.changeMutedState();
                    break;
                case 39: // →键 快进5秒
                    event.preventDefault();
                    this.seekVideo(5);
                    break;
                case 37: // ←键 回退5秒
                    event.preventDefault();
                    this.seekVideo(-5);
                    break;
                case 38: // ↑键 音量+10
                    event.preventDefault(); // 阻止默认行为（滚动页面）
                    this.adjustVolume(10);
                    break;
                case 40: // ↓键 音量-10
                    event.preventDefault(); // 阻止默认行为（滚动页面）
                    this.adjustVolume(-10);
                    break;
                default:
                    break;
            }
        },

        adjustVolume(amount) {
            let volume = (this.volume + amount) / 100;
            volume = Math.min(1, volume);
            volume = Math.max(0, volume);
            this.changeVolume(volume);
            this.volumeOrder = true;
        },

        seekVideo(seconds) {
            this.$refs.videoPlayer.currentTime += seconds;
        },

        // 视频已经加载好了可以播放
        async videoIsReady() {
            this.videoReady = true;
            if (!this.pause) {
                try {
                    await this.$refs.videoPlayer.play();
                    this.pause = false;
                } catch (err) {
                    this.pause = true;
                }
            }
        },

        showConsole() {
            this.hideConsole = false;
        },

        handleClick() {
            clearTimeout(mouseTimer);
            mouseTimer = setTimeout(() => {
                this.changePlayState();
            }, 300);
        },

        handleDblClick() {
            clearTimeout(mouseTimer);
            this.changeFullScreen();
        },

        changePlayState() {
            if (this.pause) {
                this.playVideo();
            } else {
                this.pauseVideo();
            }
        },

        playVideo() {
            this.pause = false;
            this.$refs.videoPlayer.play();
        },

        pauseVideo() {
            this.pause = true;
            this.$refs.videoPlayer.pause();
        },

        changeMutedState() {
            let Volume = this.$refs.videoPlayer.volume;
            if (Volume > 0) {
                this.volume = 0;
                this.isMuted = true;
            } else {
                this.volume = localStorage.getItem('volume');
                this.isMuted = false;
            }
            this.$refs.videoPlayer.volume = this.volume / 100;
            this.volumeOrder = true;
        },

        changeVolume(per) {
            let volume = Math.floor(per * 100);
            this.volume = volume;
            this.$refs.videoPlayer.volume = volume / 100;
            this.isMuted = (volume === 0);
            localStorage.setItem("volume", JSON.stringify(volume));
        },

        changeFullScreen() {
            if (this.screenType !== 'full') {
                this.fullSrceen();
            } else {
                this.exitFullscreen();
            }
        },

        fullSrceen() {
            this.screenType = 'full';
            const videoArea = document.querySelector('.player-video-area');
            videoArea.requestFullscreen();
        },

        exitFullscreen() {
            document.exitFullscreen();
            this.screenType = 'normal';
        },

        handleVideoResize() {
            const videoArea = document.querySelector('.player-video-area');
            if (videoArea.clientWidth < window.innerWidth && videoArea.clientHeight < window.innerHeight && this.screenType !== 'normal') {
                this.screenType = 'normal';
            }
        },

        changeCurrentPer(curr) {
            this.currentPer = curr;
            let time = curr * this.duration;
            this.currentTime = time;
            this.$refs.videoPlayer.currentTime = time;
        },

        timeUpdate() {
            const time = this.$refs.videoPlayer.currentTime;
            this.currentTime = time;
            this.currentPer = time / this.duration;
        },

        changePlaybackRate(num) {
            if (num === 1.0) {
                this.playSpeedText = '倍速';
            } else {
                this.playSpeedText = `${num}x`;
            }
            this.$refs.videoPlayer.playbackRate = num;
        },

        enterBtn(name, index) {
            clearTimeout(menuHideTimer[index]);
            menuShowTimer[index] = setTimeout(() => {
                const obj = document.querySelector(`.${name}`);
                obj.classList.add('state-show');
            }, 200);
        },

        leaveBtn(name, index) {
            clearTimeout(menuShowTimer[index]);
            menuHideTimer[index] = setTimeout(() => {
                const obj = document.querySelector(`.${name}`);
                obj.classList.remove('state-show');
            }, 200);
        },

        handleTime(time) {
            time = parseInt(time);
            time = Math.floor(time);
            let m = Math.floor(time / 60);
            let s = Math.floor(time % 60);
            m = m < 10 ? '0' + m : m;
            s = s < 10 ? '0' + s : s;
            return m + ':' + s;
        },
    },

    created() {
        // 同步音量
        if (localStorage.getItem("volume")) {
            this.volume = JSON.parse(localStorage.getItem("volume"));
        } else {
            localStorage.setItem("volume", JSON.stringify(this.volume));
        }
    },
    mounted() {
        this.changeWindowSize();
        window.addEventListener('resize', this.changeWindowSize);
        window.addEventListener('resize', this.handleVideoResize);
        document.addEventListener('keydown', (e) => this.handleKeyboard(e));
        // 默认播放音量不等于this.volume，所以挂载时更新同步一下音量
        this.$refs.videoPlayer.volume = this.volume / 100;
    },
    beforeUnmount() {
        window.removeEventListener('resize', this.changeWindowSize);
        window.removeEventListener('resize', this.handleVideoResize);
        document.removeEventListener('keydown', (e) => this.handleKeyboard(e));

    },
    watch: {
        // 更换视频时初始化
        "videoUrl"() {
            this.videoReady = false;
        },
        "$store.state.isLogin"(curr) {
            if (curr) {
                this.increasePlay();
            }
        }
    }
}
</script>

<style scoped>
.player-wrapper {
    position: relative;
}

.player-area {
    position: relative;
    height: 100%;
    width: 100%;
}

.player-primary-area {
    display: flex;
    flex-direction: column;
    flex-wrap: nowrap;
    height: 100%;
    width: 100%;
}

.player-video-area {
    background-color: #000;
    flex: 1;
    overflow: hidden;
    position: relative;
}

.player-video-area[data-ctrl-hidden=true] {
    cursor: none;
}

.player-video-area * {
    user-select: none;
}

.player-video {
    align-items: center;
    display: flex;
    flex: none;
    height: 100%;
    justify-content: center;
    position: relative;
    width: 100%;
}

.player-video-wrap {
    box-sizing: border-box;
    display: block;
    height: 100%;
    width: 100%;
}

.player-video-wrap video {
    content-visibility: visible;
    display: block;
    height: 100%;
    margin: auto;
    width: 100%;
}

.player-dm-wrap {
    cursor: pointer;
    height: 100%;
    left: 0;
    mask-position: center;
    overflow: hidden;
    pointer-events: none;
    position: absolute;
    top: 0;
    user-select: none;
    width: 100%;
    align-items: center;
    display: flex;
    justify-content: center;
    z-index: 2;
}


.player-loading-wrap {
    background-color: #000;
    height: 100%;
    left: 0;
    position: absolute;
    top: 0;
    width: 100%;
    z-index: 57;
}

.player-masking-wrap {
    width: 100%;
    height: 100%;
    position: absolute;
    top: 0;
    left: 0;
    z-index: 58;
}

.pause-svg {
    width: 15%;
    height: 15%;
    position: absolute;
    opacity:0.3;
    bottom: 30vh;
    right: 26vw;
}

.pause-svg[data-screen=full], .pause-svg[data-screen=web] {
    bottom: 49vh;
    right: 42vw;
}

.player-top-left-wrap {
    color: #fff;
    display: flex;
    left: 12px;
    margin-top: 18px;
    pointer-events: none;
    position: absolute;
    top: 0;
    transition: all .2s ease-in-out;
    width: 100%;
    z-index: 59;
}

.player-video-area[data-ctrl-hidden=true] .player-top-left-wrap {
    opacity: 0;
    visibility: hidden;
}

.player-video-area[data-ctrl-hidden=false] .player-top-left-wrap {
    opacity: 1;
    visibility: visible;
}

.player-uploader {
    align-items: center;
    background-color: rgba(0,0,0,.4);
    border-radius: 26px;
    cursor: pointer;
    display: flex;
    flex: 0;
    font-size: 12px;
    font-weight: 400;
    height: 38px;
    line-height: 32px;
    margin-bottom: 8px;
    pointer-events: all;
    text-align: center;
    width: 38px;
    z-index: 2;
}

.player-uploader:hover {
    background-color: rgba(0,0,0,.6);
}

.player-uploader-face {
    border-radius: 50%;
    flex: none;
    height: 32px;
    margin-left: 3px;
    vertical-align: bottom;
    width: 32px;
}

.player-console-wrap {
    bottom: 0;
    left: 0;
    position: absolute;
    width: 100%;
    z-index: 75;
}

.player-console-mask {
    background: url(../../assets/img/shadow.png) repeat-x bottom;
    bottom: 0;
    height: 100px;
    left: 0;
    pointer-events: none;
    position: absolute;
    transition: opacity .2s ease-in-out;
    width: 100%;
    z-index: -1;
}

.player-console-progress-line {
    bottom: 44px;
    left: 0;
    opacity: 1;
    position: absolute;
    right: 0;
    transition: opacity .2s ease-out;
    visibility: visible;
}

.player-console-options {
    display: flex;
    height: 35px;
    justify-content: space-between;
    line-height: 22px;
    margin: 30px 0 0;
    transition: all .2s ease-out;
    width: 100%;
}

.player-console-options, .player-console-progress-line {
    box-sizing: border-box;
    padding: 0 12px;
}

.player-console-controller-btn {
    fill: #fff;
    color: #fff;
    font-size: 0;
    height: 22px;
    line-height: 22px;
    outline: 0;
    position: relative;
    text-align: center;
    width: 36px;
    z-index: 2;
    opacity: 0.8;
}

.player-console-controller-btn:hover {
    opacity: 1;
}

.player-ctrl-btn-icon {
    cursor: pointer;
    width: 100%;
}

.icon-svg {
    display: inline-flex;
    user-select: none;
    height: 100%;
    width: 100%;
}

.player-ctrl-btn-icon .icon-svg {
    height: 22px;
    opacity: .9;
    vertical-align: middle;
}

.icon-svg svg {
    transition: fill .15s ease-in-out;
    height: 100% ;
    width: 100% ;
}

.player-console-bottom-left {
    display: inline-flex;
    flex: none;
}

.player-video-area[data-ctrl-hidden=false] .player-hidden-progress-wrap, .player-video-area[data-ctrl-hidden=true] .player-console-progress-line {
    opacity: 0;
    visibility: hidden;
}

.player-video-area[data-ctrl-hidden=false] .player-console-progress-line {
    opacity: 1;
    visibility: visible;
}

.player-video-area[data-ctrl-hidden=true] .player-console-options, .player-video-area[data-ctrl-hidden=true] .player-console-mask {
    opacity: 0;
}

.player-video-area[data-ctrl-hidden=false] .player-console-options, .player-video-area[data-ctrl-hidden=false] .player-console-mask {
    opacity: 1;
    transition: opacity .2s ease-in;
}

.player-ctrl-time {
    font-size: 12px;
    margin-right: 10px;
    min-width: 90px;
}

.player-ctrl-time-label {
    height: 100%;
    position: absolute;
    text-align: center;
    white-space: nowrap;
    width: 100%;
}

.divider {
    padding: 0 4px;
}

.player-console-bottom-right {
    display: flex;
    justify-content: flex-end;
}

.player-video-area[data-screen=full] .player-console-bottom-right {
    min-width: 378px;
}

.player-ctrl-quality {
    flex: none;
    font-size: 14px;
    margin-right: 10px;
    width: auto;
    cursor: pointer;
    font-weight: 600;
}

.player-video-area[data-screen=full] {
    visibility: visible;
}

.player-ctrl-playbackrate {
    font-size: 14px;
    width: 50px;
}

.player-ctrl-playbackrate-result {
    cursor: pointer;
    font-weight: 600;
    width: 100%;
}

.player-ctrl-playbackrate-menu {
    background-color: hsla(0,0%,8%,.9);
    border-radius: 2px;
    bottom: 41px;
    box-sizing: border-box;
    display: none;
    left: 50%;
    margin: 0;
    padding: 0;
    position: absolute;
    text-align: center;
    transform: translateX(-50%);
    width: 70px;
}

.player-ctrl-playbackrate.state-show .player-ctrl-playbackrate-menu {
    display: block;
}

.player-ctrl-playbackrate-menu-item {
    cursor: pointer;
    height: 36px;
    line-height: 36px;
    position: relative;
}

.player-ctrl-playbackrate-menu-item:hover {
    background-color: hsla(0,0%,100%,.1);
}

.player-ctrl-playbackrate-menu-item.state-active {
    color: #FF6699;
}

.player-ctrl-setting.state-show {
    display: block;
}

.player-ctrl-volume-box {
    background: hsla(0,0%,8%,.9);
    border-radius: 2px;
    bottom: 41px;
    height: 100px;
    left: 50%;
    margin-left: -16px;
    position: absolute;
    width: 32px;
}

.player-ctrl-volume.state-show .player-ctrl-volume-box {
    display: block;
}

.player-ctrl-volume-number {
    color: #e5e9ef;
    font-size: 12px;
    height: 28px;
    line-height: 28px;
    margin-bottom: 2px;
    text-align: center;
    width: 100%;
}

.player-ctrl-volume-progress {
    height: 60px;
}

.player-hidden-progress-wrap {
    bottom: 0;
    height: 2px;
    left: 0;
    position: absolute;
    right: 0;
    transition: opacity .4s ease-in;
}

.player-video-area[data-ctrl-hidden=true] .player-hidden-progress-wrap {
    opacity: 1;
    visibility: visible;
}

.player-hidden-progress-duration-wrap {
    height: 100%;
    position: relative;
    width: 100%;
}

.player-progress-duration {
    background-color: hsla(0,0%,100%,.2);
    border-radius: 1.5px;
    bottom: 0;
    left: 0;
    overflow: hidden;
    position: absolute;
    right: 0;
    top: 0;
}

.player-progress-schedule-buffer, .player-progress-current {
    bottom: 0;
    left: 0;
    position: absolute;
    right: 0;
    top: 0;
    transform: scaleX(0);
    transform-origin: 0 0;
}

.player-progress-current {
    background-color: #FF6699;
}

.player-dm-sending-area-outside {
    background: #fff;
    display: flex;
    align-items: center;
    flex: none;
    height: 46px;
    align-items: center;
    justify-content: center;
    font-size: 17px;
}

.player-video-current-state {
    align-items: center;
    color: #404245;
    display: flex;
    flex-shrink: 1;
    height: 16px;
    line-height: 18px;
    margin-right: 24px;
    overflow: hidden;
    position: relative;
}

.player-video-current-state-content {
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
}

.player-video-current-state-content a {
    color: #FF6699;
    font-weight: 600;
}

.player-video-area[data-screen=full] {
    background-color: hsla(0,0%,100%,.3);
    color: hsla(0,0%,100%,.6);
    margin: 0 auto;
    padding: 0;
    --el-text-color-placeholder: rgba(255,255,255,0.6);
}

ol, ul {
    list-style: none;
    outline: none;
}
</style>