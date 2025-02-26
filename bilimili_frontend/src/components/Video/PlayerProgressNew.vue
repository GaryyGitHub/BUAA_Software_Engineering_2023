<template>
    <div class="player-progress-wrap" @mouseenter="this.isActive = true" @mouseleave="this.isDragging ? '' : this.isActive = false" ref="progress-control">
        <div class="player-progress" :class="isActive ? 'activated' : ''">
            <div class="player-progress-duration">
                <div class="player-progress-current" :style="`transform: scaleX(${currentPercentage});`"></div>
            </div>
        </div>
    </div>
</template>

<script>
    export default {
        data(){
            return {
                isActive: false,
                isMoving: false,
            }
        },
        props: {
            currentPercentage: {
                type: Number,
                default: () => 0.0,
            }
        },
        methods: {
            init() {
                const control = this.$refs["progress-control"];
                let progress, schedule;

                control.addEventListener("mousedown", (event) => {
                    this.isActive = true;
                    this.isMoving = true;
                    schedule = control.getBoundingClientRect();

                    progress = (event.clientX - schedule.left) / schedule.width;

                    this.$emit("changeCurrent", progress);
                });

                document.addEventListener("mousemove", (event) => {
                    if(this.isMoving) {
                        schedule = control.getBoundingClientRect();
                        progress = (event.clientX - schedule.left) / schedule.width;
                        this.$emit("changeCurrent", progress);
                    } else {
                        //console.log("wrong place!");
                    }
                });

                document.addEventListener("mouseup", () => {
                   this.isMoving = false;
                   this.isActive = false;
                });
            }
        },
        mounted() {
            this.init();
        }
    }
</script>

<style scoped>
.player-progress-wrap {
    align-items: flex-end;
    cursor: pointer;
    display: flex;
    height: 0.5vh;
    padding-bottom: 0.1vh;
}

.player-progress {
    align-items: center;
    display: flex;
    height: 0.2vh;
    position: relative;
    width: 100%;
}

.activated {
    height: 0.4vh;
}

.player-progress-duration {
    background-color: hsl(0, 0%, 100%, 0.2);
    border-radius: 1.5px;
    bottom: 0;
    left: 0;
    right: 0;
    top: 0;
    overflow: hidden;
    position: absolute;
}

.player-progress-current {
    background-color: #FF6699;
    bottom: 0;
    left: 0;
    right: 0;
    top: 0;
    position: absolute;
    transform-origin: 0 0;
}
</style>