var toId=$("#toId").val();
var userId=$("#userId").val();
var type=$("#type").val();
var htm="";
var pageNum=2;
var pageSize=10;
var two="";
$(document).ready(function(){
  var jsonStr=$("#message").val();
  var dataObj=eval("("+jsonStr+")");
  var first=10;
  var data = dataObj[0].list;
  if(data.length-first<0){
	  first=0;
  }else{
	  first=data.length-first;
  }
  getDetail(data,first);
  $("#moreLog").click(function(){
	  two=pageNum*10;
	  if(data.length-two>=0){
	  	  two=data.length-two;
		  getDetail(data,two);
		  pageNum++;
	  }else{
	  	var a=0;
	  	getDetail(data,a);
	  	alert("没有更多聊天记录")
	  }
  });
});

function getDetail(data,first){
	$("#messageList").html("");
	htm="";
	console.log(first);
	if(type=="friend"){
	   	for(var i=data.length-1;i>=first;i--){
	   		if(data[i].fromUserId == userId){
		     htm+='<li class="layim-chat-mine">'
				 +'  <div class="layim-chat-user">'
				 +'	  <img src="'+data[i].fromAvatar+'">'
				 +'	  <cite><i>'+data[i].sendTime.substring(0,16)+'</i>我</cite>'
				 +'  </div>'
				 +'  <div class="layim-chat-text">'+ changesome(data[i].content)+'</div>'
				 +'</li>'
			 }else{
			     htm+='<li>'
					 +'  <div class="layim-chat-user">'
					 +'	  <img src="'+data[i].fromAvatar+'">'
					 +'	  <cite>'+data[i].fromName+'<i>'+data[i].sendTime.substring(0,16)+'</i></cite>'
					 +'  </div>'
					 +'  <div class="layim-chat-text">'+changesome(data[i].content)+'</div>'
					 +'</li>'
			 }
		}
	}else if(type=="group"){
        for(var i=data.length-1;i>=0;i--){
	   		if(data[i].userId == userId){
		     htm+='<li class="layim-chat-mine">'
				 +'  <div class="layim-chat-user">'
				 +'	  <img src="'+data[i].avatar+'">'
				 +'	  <cite><i>'+data[i].sendTime.substring(0,16)+'</i>我</cite>'
				 +'  </div>'
				 +'  <div class="layim-chat-text">'+ changesome(data[i].content)+'</div>'
				 +'</li>'
			 }else{
			     htm+='<li>'
					 +'  <div class="layim-chat-user">'
					 +'	  <img src="'+data[i].avatar+'">'
					 +'	  <cite>'+data[i].nickName+'<i>'+data[i].sendTime.substring(0,16)+'</i></cite>'
					 +'  </div>'
					 +'  <div class="layim-chat-text">'+changesome(data[i].content)+'</div>'
					 +'</li>'
			 }
		}
	}

    $("#messageList").prepend(htm);
}

//转换内容
 function changesome(content){
	//支持的html标签
	var html = function(end){
		return new RegExp('\\n*\\['+ (end||'') +'(code|pre|div|span|p|table|thead|th|tbody|tr|td|ul|li|ol|li|dl|dt|dd|h2|h3|h4|h5)([\\s\\S]*?)\\]\\n*', 'g');
	};
	content = (content||'').replace(/&(?!#?[a-zA-Z0-9]+;)/g, '&amp;')
		.replace(/</g, '&lt;').replace(/>/g, '&gt;').replace(/'/g, '&#39;').replace(/"/g, '&quot;') //XSS
		.replace(/@(\S+)(\s+?|$)/g, '@<a href="javascript:;">$1</a>$2') //转义@

		.replace(/face\[([^\s\[\]]+?)\]/g, function(face){  //转义表情
			var alt = face.replace(/^face/g, '');
			return '<img alt="'+ alt +'" title="'+ alt +'" src="' + faces[alt] + '">';
		})
		.replace(/img\[([^\s]+?)\]/g, function(img){  //转义图片
			return '<img class="layui-layim-photos" src="' + img.replace(/(^img\[)|(\]$)/g, '') + '">';
		})
		.replace(/file\([\s\S]+?\)\[[\s\S]*?\]/g, function(str){ //转义文件
			var href = (str.match(/file\(([\s\S]+?)\)\[/)||[])[1];
			var text = (str.match(/\)\[([\s\S]*?)\]/)||[])[1];
			if(!href) return str;
			return '<a class="layui-layim-file" href="'+ href +'" download target="_blank"><i class="layui-icon">&#xe61e;</i><cite>'+ (text||href) +'</cite></a>';
		})
		.replace(/audio\[([^\s]+?)\]/g, function(audio){  //转义音频
			return '<div class="layui-unselect layui-layim-audio" layim-event="playAudio" data-src="' + audio.replace(/(^audio\[)|(\]$)/g, '') + '"><i class="layui-icon">&#xe652;</i><p>音频消息</p></div>';
		})
		.replace(/video\[([^\s]+?)\]/g, function(video){  //转义音频
			return '<div class="layui-unselect layui-layim-video" layim-event="playVideo" data-src="' + video.replace(/(^video\[)|(\]$)/g, '') + '"><i class="layui-icon">&#xe652;</i></div>';
		})

		.replace(/a\([\s\S]+?\)\[[\s\S]*?\]/g, function(str){ //转义链接
			var href = (str.match(/a\(([\s\S]+?)\)\[/)||[])[1];
			var text = (str.match(/\)\[([\s\S]*?)\]/)||[])[1];
			if(!href) return str;
			return '<a href="'+ href +'" target="_blank">'+ (text||href) +'</a>';
		}).replace(html(), '\<$1 $2\>').replace(html('/'), '\</$1\>') //转移HTML代码
		.replace(/\n/g, '<br>') //转义换行
	return content;
};