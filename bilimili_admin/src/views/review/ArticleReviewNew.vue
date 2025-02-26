<template>
    <div class="video-review-wrap">
        <div class="page-container">
            <div class="video-item">
                <div class="video-table-wrap">
                    <div class="v-table" v-loading="loading">
                        <div class="table-title">
                            <div class="titleBar">
                                <div class="title-item waiting" :class="articleStatus === 0 ? 'active' : ''" @click="handleChange(0)">待审核</div>
                                <div class="title-item passed" :class="articleStatus === 1 ? 'active' : ''" @click="handleChange(1)">已过审</div>
                                <div class="title-item banned" :class="articleStatus === 2 ? 'active' : ''" @click="handleChange(2)">未过审</div>
                            </div>
                            <div class="table-data">
                                <div class="total-num"> {{ total }}</div>
                                <div class="total">条记录</div>
                            </div>
                        </div>
                        <div class="v-table__wrapper">
                            <table>
                                <thead>
                                <tr>
                                    <th>AID</th>
                                    <th>封面</th>
                                    <th>标题</th>
                                    <th>UP主</th>
                                    <th>UID</th>
                                    <th>详情</th>
                                </tr>
                                </thead>
                                <tbody v-if="articles.length != 0">
                                <tr v-for="(item, index) in articles" :key="index">
                                    <td>{{ item.article.aid }}</td>
                                    <td><img :src="item.article.coverUrl" class="videoCover" alt=""></td>
                                    <td>{{ item.article.title }}</td>
                                    <td>{{ item.user.nickname }}</td>
                                    <td>{{item.user.uid}}</td>
                                    <td>
                                            <span
                                                class="detail-btn"
                                                @click="openNewPage({
                                                    name: 'articleDetail',
                                                    params: {aid: item.article.aid}
                                                })"
                                            >→</span>
                                    </td>
                                </tr>
                                </tbody>
                            </table>
                            <div class="blank" v-if="!loading && articles.length == 0">
                                <span>尚无任何数据</span>
                            </div>
                        </div>
                        <div class="v-table-page">
                            <el-pagination
                                background
                                layout="prev, pager, next"
                                :total="total"
                                :page-size="10"
                                :pager-count="pagerCount"
                                :current-page="page"
                                @current-change="changePage"
                            ></el-pagination>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</template>

<script>
export default {
    name: "ArticleReview",
    data() {
        return {
            articleStatus: 0,
            articles: [],
            page: 1,
            total: 50,
            pagerCount: 3,
            loading: true,
        }
    },
    methods: {
        async getAll() {
            const res = await this.$get('/review/article/total', {
                params: {
                    astatus: this.articleStatus,
                },
                headers: {
                    Authorization: "Bearer " + localStorage.getItem("bm_token"),
                },
            });
            if (res.data.data) {
                this.total = res.data.data;
            } else {
                this.total = 0;
                this.articles = [];
            }
        },

        async getPage() {
            const res = await this.$get('/review/article/getpage', {
                params: {
                    astatus: this.articleStatus,
                    page: this.page,
                    quantity: 10,
                },
                headers: {
                    Authorization: "Bearer " + localStorage.getItem("bm_token"),
                },
            });
            if (res.data.data) {
                this.articles = res.data.data;
            } else {
                this.articles = [];
            }
        },

        handleChange(status) {
            this.articleStatus = status;
            this.page === 1 ? this.reload() : this.page = 1;
        },

        async changePage(pageNum) {
            this.page = pageNum;
            await this.reload();
        },

        async reload() {
            this.loading = true;
            await this.getAll();
            if (this.total) {
                await this.getPage();
            }
            this.loading = false;
        },

        openNewPage(route) {
            window.open(this.$router.resolve(route).href, '_blank');
        },
    },
    async created() {
        await this.getAll();
        if (this.total) {
            await this.getPage();
        }
        this.loading = false;
    },
}
</script>

<style scoped>
.video-review-wrap {
    flex: 1 1 auto;
}

.page-container {
    width: 100%;
    padding: 16px;
    margin-right: auto;
    margin-left: auto;
}

@media (min-width: 960px) {
    .page-container {
        max-width: 900px;
    }
}

@media (min-width: 1120px) {
    .page-container {
        max-width: 1050px;
    }
}

@media (min-width: 1280px) {
    .page-container {
        max-width: 1200px;
    }
}

.video-item {
    border-radius: 15px;
    background-color: #fff;
    display: block;
    overflow: hidden;
    padding: 0;
    z-index: 0;
}

.video-table-wrap {
    height: calc(100vh - 96px);
    position: relative;
    overflow: hidden ;
    overflow-anchor: none;
}

.table-title {
    display: flex;
    height: 64px;
    border-bottom: 2px solid #e7e7e7;
    justify-content: space-between;
    align-items: center;
}

.titleBar, .table-data {
    display: flex;
    flex: 0 0 auto;
}
.table-data {
    margin-left: 60px;
}

li, ol, ul, button, table, tbody, tfoot, thead, tr, th, td {
    margin: 0;
    padding: 0;
    border: 0;
    font-family: inherit;
    font-size: 100%;
    font-weight: 400;
    font-style: normal;
    line-height: 1.25;
}

.title-item {
    flex: 0 0 auto;
    width: 100px;
    height: 64px;
    padding-bottom: 18px;
    padding-top: 20px;
    margin-left: 35px;
    font-size: 16px;
    font-weight: 650;
    color: #313131;
    cursor: pointer;
}

.active {
    color: #2136AC;
    font-weight: 600;
    border-bottom: 3px solid #2136AC;
}

.table-data>div {
    flex: 0 0 auto;
    line-height: 54px;
    padding-top: 10px;
}

.v-table {
    position: relative;
    line-height: 1.5;
    height: 100%;
    background-color: #fff;
    color: #61666D;
}

.v-table__wrapper {
    overflow: auto;
    height: calc(100% - 140px);
    padding: 0 4px 8px;
}

.v-table .v-table__wrapper table {
    width: 95%;
    border-spacing: 0;
    margin-left: 20px;
}

.v-table .v-table__wrapper table thead {
    z-index: 500;
}

.v-table .v-table__wrapper table thead tr th {
    background: #fff;
    z-index: 1;
    border-bottom: 0;
    position: sticky;
    top: 0;
    height: 56px;
    font-weight: 700;
    text-align: start;
    padding: 0 16px;
    color: rgba(0,0,0,0.6);
    white-space: nowrap;
}

.v-table table tbody tr {
    font-size: 13px ;
    border: 5px solid transparent;
    transition: box-shadow .3s,transform .3s, border-color .3s;
    border-radius: 15px;
}

.v-table table tbody tr:not(.v-data-table__selected):hover {
    box-shadow: 0 2px 20px -2px rgba(35, 49, 126, 0.4);
    border-color: #ffd024;
    transform: translateY(-1px);
}

.v-table .v-table__wrapper table tbody tr td {
    height: 100px;
}

.v-table .v-table__wrapper table tbody tr td, .v-table .v-table__wrapper table tbody tr th, .v-table .v-table__wrapper table thead tr td, .v-table .v-table__wrapper table thead tr th, .v-table .v-table__wrapper table tfoot tr td, .v-table .v-table__wrapper table tfoot tr th {
    padding: 0 16px;
}

.v-table-page {
    padding-top: 45px;
    height: 40px;
    width: 100%;
    display: flex;
    justify-content: center;
}

.total {
    margin-bottom: 6px;
    font-size: 15px;
    margin-right: 30px;
}

.total-num {
    font-size: 18px;
    color: #2136AC;
    margin-right: 15px;
}

.blank {
    height: 150px;
    width: 100%;
    display: flex;
    align-items: center;
    justify-content: center;
}

.blank span {
    color: #404041;
    line-height: 40px;
    font-size: 18px;
    font-weight: 450;
}

.detail-btn {
    font-size: 20px;
    font-weight: lighter;
    cursor: pointer;
    color:  #008AC5;
}

.detail-btn:hover {
    color: #2136AC;
    text-decoration: underline;
}

.category {
    color: #fff;
    line-height: 18px;
    padding: 2px 8px;
    border-radius: 10px;
    border: 1px solid;
}

.videoCover {
    height: 90px;
    width: 160px;
    object-fit: cover;
    box-shadow: 2px 2px 8px #0000001f;
}
</style>