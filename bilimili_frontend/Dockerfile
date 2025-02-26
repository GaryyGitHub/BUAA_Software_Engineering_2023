# 使用官方 Node.js 镜像作为构建环境
FROM node:16 AS build-stage

# 设置工作目录
WORKDIR /bilimili/frontend

# 复制 package.json 和 package-lock.json
COPY package*.json ./

# 安装依赖
RUN npm install

# 复制项目文件
COPY . .

# 构建 Vue 应用
RUN npm run build

# 使用 Nginx 镜像作为生产环境
FROM nginx:alpine

# 复制构建好的应用到 Nginx 的默认目录
COPY --from=build-stage /bilimili/frontend/dist /usr/share/nginx/html

# 复制自定义的 Nginx 配置文件
COPY nginx.conf /etc/nginx/nginx.conf

# 暴露自定义端口 8787
EXPOSE 8787

# 启动 Nginx
CMD ["nginx", "-g", "daemon off;"]
