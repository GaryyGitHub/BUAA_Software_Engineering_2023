<template>
    <div class="slider-column v-ui v-ui-area">
        <div class="track track-vertical">
            <div class="bar-wrap">
                <div class="bar" :style="`transform: scaleY(${barScaleY});`"></div>
            </div>
            <div class="dot-wrap" :style="`transform: translateY(-${barOffsetY}px);`">
                <div class="dot"></div>
            </div>
        </div>
    </div>
</template>

<script>
export default {
    name: "SliderColumn",
    data() {
        return {
            isMoving: false,
            barOffsetY: 0,
            barScaleY: 0,
        }
    },
    props: {
        // 当前进度百分比 小数0~1
        currentPercentage: {
            type: Number,
            default: () => 0,
        },
        // 滑块总高度
        sliderHeight: {
            type: Number,
            default: () => 12,
        },
        // 更新信号
        order: {
            type: Boolean,
            default : () => false,
        },
    },
    methods: {
        // 初始化滑块位置
        initPosition() {
            this.barScaleY = this.currentPercentage;
            this.barOffsetY = (this.sliderHeight - 12) * this.currentPercentage;
            this.$emit("update:order", false);
        },

        // 初始化拖动
        initDrag() {
            const slider = document.querySelector('.slider-column');
            let offsetY, currPer;

            // 计算当前比例的辅助函数
            const updatePercentage = (clientY) => {
                offsetY = slider.getBoundingClientRect().bottom - clientY;
                currPer = offsetY / slider.getBoundingClientRect().height;
                currPer = Math.max(0, Math.min(1, currPer)); // 边界值判定
                this.barScaleY = currPer;
                this.barOffsetY = (slider.getBoundingClientRect().height - 12) * currPer;
                this.$emit("changeCurrent", currPer); // 更新
            };

            // 鼠标按下事件处理程序
            slider.addEventListener("mousedown", (e) => {
                this.isMoving = true;
                updatePercentage(e.clientY);
            });

            // 鼠标移动事件处理程序
            document.addEventListener("mousemove", (e) => {
                if (this.isMoving) {
                    updatePercentage(e.clientY);
                }
            });

            // 鼠标释放事件处理程序
            document.addEventListener("mouseup", () => {
                this.isMoving = false;
            });
        },
    },
    mounted() {
        this.initPosition();
        this.initDrag();
    },
    watch: {
        "order"(curr) {
            if (curr) {
                this.initPosition();
            }
        }
    }
}
</script>

<style scoped>
.slider-column {
    cursor: pointer;
}

.v-ui {
    align-items: center;
    display: flex;
    justify-content: flex-start;
    vertical-align: middle;
}

.v-ui .v-ui-area {
    align-items: center;
    display: flex;
    height: 100%;
    justify-content: flex-start;
    line-height: normal;
    user-select: none;
    vertical-align: middle;
    width: 100%;
}

.slider-column .track {
    align-items: center;
    display: flex;
    margin-left: 15.5px;
    position: relative;
    width: 100%;
}

.slider-column .track.track-vertical {
    align-items: flex-end;
    height: 100%;
    width: 2px;
}

.bar-wrap {
    background: #e7e7e7;
    border-radius: 1.5px;
    top: 0;
    bottom: 0;
    left: 0;
    right: 0;
    overflow: hidden;
    position: absolute;
}

.bar {
    background: #FF6699;
    top: 0;
    bottom: 0;
    left: 0;
    right: 0;
    position: absolute;
    transform-origin: 0 100%;
}

.dot-wrap {
    top: auto;
    bottom: 0;
    left: -5px;
    position: relative;
    cursor: pointer;
}

.dot {
    height: 12px;
    width: 12px;
    align-items: center;
    background-color: #FF6699;
    border-radius: 50%;
    display: flex;
    transform: translateZ(0);
    transition: all .2s;
    vertical-align: middle;
}
</style>