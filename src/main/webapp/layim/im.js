var userId=$("#userId").val();
var socket = null;  // 判断当前浏览器是否支持WebSocket

if ('WebSocket' in window) {
    //部署到局域网或者服务器需要修改此处ip
    socket = new WebSocket("ws://114.251.65.250:2084/LLWS/"+userId);
} else {
    alert('该浏览器不支持本系统即时通讯功能，推荐使用谷歌或火狐浏览器！');
}

layui.use(['layim','layer','jquery'], function(layim){

    var data;

    $.ajax({
        type: "post",
        url: "http://114.251.65.250:2084/index/getInitList?userId="+userId,
        cache:false,
        async:false,
        success: function(res){
            data = JSON.parse(res).data;
        }
    })

    layim.config({
        init: data
        ,brief: false
        //查看群员接口
        ,members: {
            url: 'http://114.251.65.250:2084/api/qun/getByGroupId'
        }
        ,uploadImage: {
            url: 'http://114.251.65.250:2084/sns/uploadFile?userId='+userId
            ,type: '' //默认post
        }
        ,uploadFile: {
            url: 'http://114.251.65.250:2084/sns/uploadFile?userId='+userId
            ,type: '' //默认post
        }
        ,min:false
        ,find:''
        ,title: 'KingIM'        //主面板最小化后显示的名称
        ,chatLog: 'http://114.251.65.250:2084/api/friend/getHistoryMessagePage'  //聊天记录地址
        ,copyright: true          //是否授权
        ,right: '10px'
        ,notice:true      //开启桌面消息提醒
        ,msgbox:'http://114.251.65.250:2084/api/friend/msgBoxPage?userId='+userId //消息盒子页面地址，若不开启，剔除该项即可
        ,isAudio:true
        ,isVideo:true
    });

    // 连接发生错误的回调方法
    socket.onerror = function() {
        console.log("llws连接失败!");
    };
    // 连接成功建立的回调方法
    socket.onopen = function() {
        console.log("llws连接成功!");
    }

    // 接收到消息的回调方法
    socket.onmessage = function(res) {
        console.log("llws收到消息啦:" +res.data);
        res = eval("("+res.data+")");
        if(res.type == 'friend' || res.type == 'group'){
            layim.getMessage(res);
        }else{
            layim.setFriendStatus(res.id,res.content);
        }

    }

    // 连接关闭的回调方法
    socket.onclose = function() {
        console.log("llws关闭连接!");
    }
    // 监听窗口关闭事件，当窗口关闭时，主动去关闭websocket连接，防止连接还没断开就关闭窗口，server端会抛异常。
    window.onbeforeunload = function() {
        socket.close();
    }

    // 监听发送消息
    layim.on('sendMessage', function(data){
        var uuid=$("#uuid").val();
        var obj={
            "mine":{
                "avatar":data.mine.avatar,
                "content":data.mine.content,
                "cid":uuid,   //标记消息，用于撤回等操作
                "id":data.mine.id,
                "mine":true,
                "username":data.mine.username
            },
            "to":{
                "avatar":data.to.avatar,
                "id":data.to.id,
                "name":data.to.groupname,
                "sign":data.to.sign,
                "type":data.to.type,
                "username":data.to.username
            }
        }
        var msg=JSON.stringify(obj);
        if(data.to.type=="friend"){
            socket.send(msg);    	//发送消息到websocket服务
            $.post("http://114.251.65.250:2084/api/friend/saveMessage",{"fromUserId":userId,"toUserId":data.to.id,"content":data.mine.content},function(res){
                console.log(res);
            })
        }else if(data.to.type=="group"){
            $.post("http://114.251.65.250:2084/api/qun/getSimpleMemberByGroupId?id="+data.to.id,function(res){
                console.log(res)
                if(res!=null){
                    var obj1={
                        "mine":{
                            "avatar":data.mine.avatar,
                            "content":data.mine.content,
                            "cid":uuid,
                            "id":data.mine.id,
                            "mine":true,
                            "username":data.mine.username
                        },
                        "to":{
                            "avatar":data.to.avatar,
                            "id":data.to.id,
                            "name":data.to.groupname,
                            "sign":data.to.sign,
                            "type":data.to.type,
                            "username":data.to.username,
                            "memberList":res
                        }
                    }
                    socket.send(JSON.stringify(obj1));  	//发送消息倒webSocket服务
                }
            })
            $.post("http://114.251.65.250:2084/api/qun/saveMessage",{"userId":userId,"groupId":data.to.id,"content":data.mine.content},function(res){
                  // console.log(res);
            })
       }
    });

    //监听在线状态的切换事件
    layim.on('online', function(data){
        console.log(data);
        if(data=='hide')
            data='offline';
        var obj={
            "mine":{
                "content":data,
                "id":userId
            },
            "to":{
                "type":"onlineStatus"
            }
        }
        $.post("http://114.251.65.250:2084/api/friend/updateOnLineStatus",{"userId":userId,"status":data},function(res){
             // console.log(res);
        })
        socket.send(JSON.stringify(obj));
    });

    $(function () {
        $.post("http://114.251.65.250:2084/index/getOfflineMsgFromRedis?userId="+userId,function(res){
            //获取离线消息
            console.log(res);
            $.each(res,function(k,v){
                var s = eval('(' + v + ')');
                layim.getMessage(s);
            });
        });
    })

    $.post('http://114.251.65.250:2084/friendApplyController/getByToUserId',{"toUserId" : userId,"status" : 0,"pageNum" : 1,"pageSize" : 10 }, function(result) {
        var count=result.total;
       // console.log(count)
        if(count>0){
            layim.msgbox(count);
        }
        //count即为你通过websocket或者Ajax实时获取到的最新消息数量,它将在主面板的消息盒子icon上不断显隐提示，直到点击后自动消失
    });

    //监听查看群员
    layim.on('members', function(data){
      //  console.log(data);
    });

    //监听聊天窗口的切换
    layim.on('chatChange', function(data){
      //  console.log(data);
    });

    layim.on('sign', function(value){
        //console.log(value.length); //获得新的签名
        if(value.length<200){
            $.post("http://114.251.65.250:2084/index/updateUserInfo",{"id":userId,"sign":value},function(result){
                console.log(result);
            })
        }else{
            layer.msg("签名不能超过200字符！")
        }
    });

});



