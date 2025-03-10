package com.bilimili.buaa13.im.handler;

import com.alibaba.fastjson2.JSON;
import com.bilimili.buaa13.im.IMServer;
import com.bilimili.buaa13.entity.Command;
import com.bilimili.buaa13.entity.IMResponse;
import com.bilimili.buaa13.entity.User;
import com.bilimili.buaa13.tools.JsonWebTokenTool;
import com.bilimili.buaa13.tools.RedisTool;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.util.AttributeKey;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

//  修改于2024.08,09
@Slf4j
@Component
public class TokenValidationHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {


    private final Boolean commandLegal = true;
    private static JsonWebTokenTool jsonWebTokenTool;
    private static RedisTool redisTool;
    private UUserService userService = new UUserService();
    private CChannelService channelService = new CChannelService();
    @Autowired
    public void setDependencies(JsonWebTokenTool jsonWebTokenToolEntity, RedisTool redisToolEntity) {
        TokenValidationHandler.redisTool = redisToolEntity;
        TokenValidationHandler.jsonWebTokenTool = jsonWebTokenToolEntity;
    }

    public TokenValidationHandler() {

    }



    //-------------------------------------------------------
    //修改于2024.08.09；方法重构

    public TokenValidationHandler(UUserService userService, CChannelService channelService, RedisTool redisTool) {
        this.userService = userService;
        this.channelService = channelService;
        TokenValidationHandler.redisTool = redisTool;
    }

    protected void channelRead1(ChannelHandlerContext ctx, TextWebSocketFrame tx) {
        Command command = JSON.parseObject(tx.text(), Command.class);
        String token = command.getContent();

        Optional<Integer> optionalUid = userService.validateToken(token);

        if (optionalUid.isPresent()) {
            Integer uid = optionalUid.get();
            channelService.bindUserToChannel(uid, ctx.channel());
            redisTool.addSetMember("login_member", uid);
            ctx.pipeline().remove(this);
            tx.retain();
            ctx.fireChannelRead(tx);
        } else {
            sendLoginExpiredResponse(ctx);
        }
    }

    private void sendLoginExpiredResponse(ChannelHandlerContext ctx) {
        ctx.channel().writeAndFlush(IMResponse.error("登录已过期")).addListener(ChannelFutureListener.CLOSE);
    }

    //----------------------------------------------------------
    //在结尾添加了UUserService和CChannelService两个类



    //2024 /08/09  结束
    
    @Override
    public void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame tx) {
        Command command = JSON.parseObject(tx.text(), Command.class);
        String token = command.getContent();

        Integer uid = isValidToken(token);
        if (uid != null) {
            // 将uid绑到ctx上
            ctx.channel().attr(AttributeKey.valueOf("userId")).set(uid);
            // 将channel存起来
            if (IMServer.userChannel.get(uid) == null) {
                Set<Channel> set = new HashSet<>();
                set.add(ctx.channel());
                IMServer.userChannel.put(uid, set);
            } else {
                IMServer.userChannel.get(uid).add(ctx.channel());
            }
            redisTool.addSetMember("login_member", uid);   // 将用户添加到在线用户集合
//            System.out.println("该用户的全部连接状态：" + IMServer.userChannel.get(uid));
//            System.out.println("当前在线人数：" + IMServer.userChannel.size());
            // 移除token验证处理器，以便以后使用无需判断
            ctx.pipeline().remove(TokenValidationHandler.class);
            // 保持消息的引用计数，以确保消息不会被释放
            tx.retain();
            // 将消息传递给下一个处理器
            ctx.fireChannelRead(tx);
        } else {
            System.out.println("抵达channelRead0的else处");
            ctx.channel().writeAndFlush(IMResponse.error("登录已过期"));
            ctx.close();
        }
    }

    /**
     * 进行JWT验证
     * @param token Bearer JWT
     * @return  返回用户ID 验证不通过则返回null
     */
    private Integer isValidToken(String token) {
        if (!StringUtils.hasText(token) || !token.startsWith("Bearer ")) {
            return null;
        }

        token = token.substring(7);

        // 解析token
        boolean verifyToken = jsonWebTokenTool.verifyToken(token);
        if (!verifyToken) {
            log.error("当前token已过期");
            return null;
        }
        String userId = JsonWebTokenTool.getSubjectFromToken(token);
        String role = JsonWebTokenTool.getClaimFromToken(token, "role");
        User user = redisTool.getObject("security:" + role + ":" + userId, User.class);

        if (user == null) {
            log.error("用户未登录");
            return null;
        }

//        log.info("成功通过验证！");
        return user.getUid();
    }

}


class UUserService {

    Optional<Integer> validateToken(String token) {
        // 假设这里有验证token的逻辑
        Integer uid = isValidToken(token);
        return Optional.ofNullable(uid);
    }

    private Integer isValidToken(String token) {
        // 令牌验证逻辑
        return null; // 示例：返回null表示无效令牌，返回用户ID表示有效令牌
    }
}

class CChannelService {

    public void bindUserToChannel(Integer uid, Channel channel) {
        AttributeKey<Integer> userIdKey = AttributeKey.valueOf("userId");
        channel.attr(userIdKey).set(uid);

        Set<Channel> userChannels = IMServer.userChannel.computeIfAbsent(uid, k -> new HashSet<>());
        userChannels.add(channel);
    }
}