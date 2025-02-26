<template>
    <div class="select-controller-wrap">
        <div class="select-controller" ref="SelectController" :class="showOptions ? 'selecting' : ''" @click="handleShow">
            <p class="select-controller-options">{{ category[0].name }} → {{ category[1].name }}</p>
            <i class="iconfont icon-xiajiantou" :class="showOptions ? 'options-icon' : ''"></i>
        </div>
        <div class="list-container-wrap" ref="categoryList" :style="showOptions ? '' : 'display: none;'">
            <div class="list-master">
                <div>
                    <div
                        class="select-master-option"
                        :class="{ 'select-master-option-selected': masterIndex === index }"
                        v-for="(item, index) in categoryList"
                        :key="index"
                        @click="selectMaster(index)"
                    >
                        {{ item.mcName }}
                    </div>
                </div>
            </div>
            <div class="list-slave" v-if="categoryList.length">
                <!-- <div class="list-slave"> -->
                    <div
                        class="select-slave-option"
                        v-for="(item, index) in categoryList[masterIndex].scList"
                        :key="index"
                        @click="selectSlave(index)"
                    >
                        <p
                            class="option-name"
                            :class="{'select-slave-option-selected': category[0].id === item.mcId && category[1].id === item.scId}"
                        >
                            {{ item.scName }}
                        </p>
                        <p v-if="item.descr" class="option-descr">
                            {{ item.descr }}
                        </p>
                    <!-- </div> -->
                </div>
            </div>
        </div>
    </div>
</template>

<script>
export default {
    name: "CategorySelect",
    data() {
        return {
            showOptions: false,
            masterIndex: 0,
            slaveIndex: 0,
        }
    },
    computed: {
        categoryList() {
            return this.$store.state.channels;
        }
    },
    props: {
        category: {
            type: Array,
            default() {
                return [{id: "anime", name: "番剧"}, {id: "finish", name: "完结动画"}];
            }
        }
    },
    methods: {
        handleShow() {
            this.showOptions = !this.showOptions;
        },

        selectMaster(masterIndex) {
            this.masterIndex = masterIndex;
            let lm = document.querySelector('.list-master');
            let smo = document.querySelector('.select-master-option');
            lm.scrollTo({
                behavior: "smooth",
                top: smo.clientHeight * masterIndex,
            })
        },

        selectSlave(slaveIndex) {
            this.slaveIndex = slaveIndex;
            const master = {id: this.categoryList[this.masterIndex].mcId, name: this.categoryList[this.masterIndex].mcName};
            const slave = {id: this.categoryList[this.masterIndex].scList[this.slaveIndex].scId, name: this.categoryList[this.masterIndex].scList[this.slaveIndex].scName};
            this.$emit("update:Category", [master, slave]);
            this.showOptions = false;
        },

        handleShut(event) {
            const selectController = this.$refs.SelectController;
            const categoryList = this.$refs.categoryList;
            this.showOptions = selectController.contains(event.target) || categoryList.contains(event.target);
        },
    },
    mounted() {
        window.addEventListener("click", this.handleShut);
    },
    beforeUnmount() {
        window.removeEventListener("click", this.handleShut);
    }
}
</script>

<style scoped>
.select-controller-wrap {
    z-index: 2000;
    position: relative;
}

.select-controller {
    padding: 7px 12px;
    border: 2px solid #cdd0d5;
    border-radius: 4px;
    min-width: 200px;
    cursor: pointer;
    display: inline-flex;
    justify-content: space-between;
    align-items: center;
    color: #99a2aa;
    background: #ffffff;
    transition: border .3s ease;
}

.select-controller:hover {
    border-color: #FF6699;
}

.icon-xiajiantou {
    transition: transform 0.3s;
}

.select-controller-options {
    line-height: 22px;
    height: 22px;
    font-size: 14px;
    display: inline-block;
    color: #1e1e1e;
    white-space: nowrap;
}

.list-container-wrap {
    border: 2px solid #cdd0d5;
    box-shadow: 0 2px 4px 0 #1f1f1f;
    border-radius: 4px;
    top: 46px;
    position: absolute;
    left: 0;
    z-index: 10;
    display: flex;
    background: #ffffff;
}

.list-master {
    padding: 12px 0;
    width: 128px;
    height: 264px;
    overflow-x: hidden;
    overflow-y: auto;
}

.select-master-option {
    padding: 12px 24px;
    width: 128px;
    justify-content: space-between;
    align-items: center;
    cursor: pointer;
    display: flex;
}

.list-slave {
    padding-top: 12px;
    min-width: 240px;
    height: 256px;
    overflow-x: hidden;
    overflow-y: auto;
}

.select-slave-option {
    padding: 12px 24px;
    min-width: 128px;
    align-items: center;
    cursor: pointer;
    display: flex;
}

.option-name {
    font-size: 14px;
    margin-right: 18px;
    color: #1e1e1e;
    white-space: nowrap;
}

.option-descr {
    font-size: 12px;
    color: #8c8b8b;
    white-space: nowrap;
}

.select-master-option:hover, .select-slave-option:hover {
    background-color: #f4f5f7;
}
</style>