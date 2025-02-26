/**
 * 对文本进行关键词高亮
 * @param {String} keyword 要高亮的关键词
 * @param {String} inputString 要转换的文本
 */
export function highlightKeyword(keyword, inputString) {
    // 使用正则表达式创建一个匹配关键词的模式
    const regex = new RegExp(`(${keyword.split('').join('|')})`, 'gi');
    // 使用字符串替换函数进行替换，添加高亮标签
    return inputString.replace(regex, '<em style="color: #f25d8e; font-style: normal;">$1</em>');
}

/**
 * 处理播放时长，将秒数转换为分钟和秒的格式
 * @param {Number} time 传入的总时长，以秒为单位
 * @returns {String} 处理后的时间字符串，格式为 'mm:ss'
 */
export function handleTime(time) {
    time = parseInt(time);
    time = Math.floor(time);
    let m = Math.floor(time / 60);
    let s = Math.floor(time % 60);
    m = m < 10 ? '0' + m : m;
    s = s < 10 ? '0' + s : s;
    return m + ':' + s;
}

/**
 * 处理大于1万的数字
 * @param {Number} num 如：198765
 * @returns {String} 转换后：'19.9万'
 */
export function handleNum(num) {
    if (num > 10000) {
        num = (num / 10000).toFixed(1);
        return num + '万';
    } else {
        return num;
    }
}

/**
 * 处理日期，截取(年)月日或者24小时内或者60分钟内
 * @param {Number|String|Date} dateTime 传入的日期时间，可以是数字、字符串或日期对象
 * @returns {String} 处理后的日期字符串 2023-11-11 / 3-24 / 8小时前 / 30分钟前
 */
export function handleDate(dateTime) {
    const currentDate = new Date();
    const inputDate = new Date(dateTime);
    // 计算时间差（以毫秒为单位）
    const timeDifference = currentDate - inputDate;
    
    if (timeDifference < 60 * 60 * 1000) {
        const minutes = Math.floor(timeDifference / 1000 / 60);
        return `${minutes}分钟前`;
    } else if (timeDifference < 24 * 60 * 60 * 1000) {
        const hours = Math.floor(timeDifference / 1000 / 60 / 60);
        return `${hours}小时前`;
    } else {
        const currentYear = currentDate.getFullYear();
        const inputYear = inputDate.getFullYear();
        // 判断是否今年
        if (inputYear < currentYear) {
            const month = inputDate.getMonth() + 1; // 月份是从0开始的
            const day = inputDate.getDate();
            return `${inputYear}-${month}-${day}`;
        } else {
            const month = inputDate.getMonth() + 1; // 月份是从0开始的
            const day = inputDate.getDate();
            return `${month}-${day}`;
        }
    }
}

/**
 * 处理日期，格式化为 年月日时分/今天时分/昨天时分
 * @param {Number|String|Date} dateTime 传入的日期时间，可以是数字、字符串或日期对象
 * @returns {String} 处理后的日期字符串 2023年12月18日 04:46 / 今天 15:00 / 昨天 00:00
 */
export function handleDateTime(dateTime) {
    // 将传入的日期时间转换为 Date 对象
    const inputDate = new Date(dateTime);
    // 如果转换后的日期无效，返回 '未知时间'
    if (isNaN(inputDate.getTime())) {
        return '未知时间';
    }
    // 获取当前日期时间，计算输入日期与当前日期之间的天数差异
    const now = new Date();
    // 判断日期是否是今天
    if (
        inputDate.getFullYear() === now.getFullYear() &&
        inputDate.getMonth() === now.getMonth() &&
        inputDate.getDate() === now.getDate()
    ) {
        // 获取小时、分钟，并在前面补零
        const hours = String(inputDate.getHours()).padStart(2, '0');
        const minutes = String(inputDate.getMinutes()).padStart(2, '0');
        return `今天 ${hours}:${minutes}`;
    } else {
        // 判断日期是否是昨天
        now.setDate(now.getDate() - 1); // 减去一天
        if (
            inputDate.getFullYear() === now.getFullYear() &&
            inputDate.getMonth() === now.getMonth() &&
            inputDate.getDate() === now.getDate()
        ) {
            // 获取小时、分钟，并在前面补零
            const hours = String(inputDate.getHours()).padStart(2, '0');
            const minutes = String(inputDate.getMinutes()).padStart(2, '0');
            return `昨天 ${hours}:${minutes}`;
        } else {
            // 如果日期不是今天或昨天，格式化为年月日时分的字符串
            const year = inputDate.getFullYear();
            const month = String(inputDate.getMonth() + 1).padStart(2, '0');
            const day = String(inputDate.getDate()).padStart(2, '0');
            const hours = String(inputDate.getHours()).padStart(2, '0');
            const minutes = String(inputDate.getMinutes()).padStart(2, '0');
            return `${year}年${month}月${day}日 ${hours}:${minutes}`;
        }
    }
}


/**
 * 处理日期，格式化为 月日时分
 * @param {Number|String|Date} dateTime 传入的日期时间，可以是数字、字符串或日期对象
 * @returns {String} 处理后的日期字符串 12-26 02:53
 */
export function handleDateTime2(dateTime) {
    // 将传入的日期时间转换为 Date 对象
    const inputDate = new Date(dateTime);
    // 如果转换后的日期无效，返回 '未知时间'
    if (isNaN(inputDate.getTime())) {
        return '未知时间';
    }
    const month = String(inputDate.getMonth() + 1).padStart(2, '0');
    const day = String(inputDate.getDate()).padStart(2, '0');
    const hours = String(inputDate.getHours()).padStart(2, '0');
    const minutes = String(inputDate.getMinutes()).padStart(2, '0');
    return `${month}-${day} ${hours}:${minutes}`;
}

/**
 * 处理时间，格式化为 年-月-日 时:分 或者 n小时前 或者 n分钟前
 * @param {Number|String|Date} dateTime 传入的日期时间，可以是数字、字符串或日期对象
 * @returns {String} YYYY-MM-DD HH:mm / n小时前 / n分钟前 / 刚刚
 */
export function handleDateTime3(dateTime) {
    const inputDate = new Date(dateTime);
    if (isNaN(inputDate.getTime())) {
        return "未知时间";
    }

    var currentTime = new Date();
    var timeDiff = currentTime.getTime() - inputDate.getTime();


    if (timeDiff < 30 * 1000) { // 30s 前
        return "刚刚";
    } else if (timeDiff < 60 * 1000) { // 60s 前
        return "1分钟前";
    } else if (timeDiff < 60 * 60 * 1000) { // 一小时内，n分钟前
        const minutes = Math.floor(timeDiff / 1000 / 60);
        return `${minutes}分钟前`;
    } else if (timeDiff < 24 * 60 * 60 * 1000) { // 一天内，n小时前
        const hours = Math.floor(timeDiff / 1000 / 60 / 60);
        return `${hours}小时前`;
    }

    const year = String(inputDate.getFullYear());
    const month = String(inputDate.getMonth() + 1).padStart(2, '0');
    const day = String(inputDate.getDate()).padStart(2, '0');
    const hours = String(inputDate.getHours()).padStart(2, '0');
    const minutes = String(inputDate.getMinutes()).padStart(2, '0');

    return `${year}-${month}-${day} ${hours}:${minutes}`;
}