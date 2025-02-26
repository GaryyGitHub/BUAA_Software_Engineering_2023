<template>
    <div class="loading-icon">
        <div class="loading">
                <div class="obj"></div>
                <div class="obj"></div>
                <div class="obj"></div>
                <div class="obj"></div>
                <div class="obj"></div>
                <div class="obj"></div>
                <div class="obj"></div>
                <div class="obj"></div>
            <div class="obj"></div>
            <div class="obj"></div>
      </div>
      <div class="slogan">你感兴趣的视频都在BiliMili</div>
    </div>
</template>

<script>
export default {
    name: "PlayerLoadingNew",
    mounted() {
        const objects = document.querySelectorAll('.obj');

        let delay = -1.4;

        objects.forEach((obj, index) => {
            obj.style.animationDelay = `${delay + 0.2 * index}s`; // 每个对象的动画延迟递增0.2秒
            obj.style.animationDuration = '2s'; // 动画持续时间为1.6秒
        });
    },
}
</script>

<style scoped>
.loading-icon {
    height: 100%;
    display: flex;
    align-items: center;
    justify-content: center;
    flex-direction: column;
}

.loading {
    height: 10vh;
    display: flex;
    align-items: center;
}

.obj {
    width: 0.4vw;
    height: 4vh;
    background: white;
    opacity: 0.8;
    margin: 0 0.4vw;
    border-radius: 10px;
    animation: process 2s infinite;
}

@keyframes process {
    0% {
        transform: scaleY(1.5);
    }
    50% {
        transform: scaleY(3);
    }
    100% {
        transform: scaleY(1.5);
    }
}

.slogan {
    margin-top: 2vh;
    color: white;
    opacity: 0.8;
    font-size: 14px;
    line-height: 2vh;
}
</style>