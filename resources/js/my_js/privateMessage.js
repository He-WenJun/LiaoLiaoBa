$(document).ready(function(){
    $("#sidebar-right-responsive").addClass("hidden");
    getPrivateMessageList();
});


$(".privateMessageBox").on("click",".bigPrivateMessage",function(){
    $(".privateMessageBox ").addClass("open");
})
$(".privateMessageBox").on("click",".minPrivateMessage",function(){
    $(".privateMessageBox ").removeClass("open");
})

$("#messageBox").on("click",".oldMessage",function(){
    getOldMessage();
})

$(".privateMessageBox").on("click",".delChar",function(){
    $.ajax({
        url: "/api/post/delPrivateMessage",
        data: {"targetUserId": $(this).attr("myvalue")},
        type: "post",
        success: function(data) {
            getPrivateMessageList();
        }
    });
})
$(".minPrivateMessageBox").on("click",".delChar",function(){
    $.ajax({
        url: "/api/post/delPrivateMessage",
        data: {"targetUserId": $(this).attr("myvalue")},
        type: "post",
        success: function(data) {
            getPrivateMessageList();
        }
    });
})

$(".fixed-sidebar").on("click",".lastPrivateMessage",function(){
    $.ajax({
        url: "/api/post/lastMessage",
        type: "get",
        dataType:'json',
        success: function(data) {
            if(data.status == 0){
                $(".sendMessage").attr("myVaule", data.data);
                $.ajax({
                    url: "/api/user/getUserInfoList",
                    data:JSON.stringify([data.data]),
                    type: "post",
                    dataType:'json',
                    contentType: "application/json;charset=utf-8",
                    success: function(data) {
                        if(data.status != 0){
                            return;
                        }
                        $("#messageBox").empty();
                        startIndex = -1;
                        chatTargetUser = data.data[0];
                        $(".chatName").html(chatTargetUser.userName);
                        $(".chatBox").addClass("open-chat");
                        firstOldMessage = 0;
                        getOldMessage();
                    }
                });
            }
        
        }
    });

})

var chatTargetUser;
var startIndex = -1;


$(".privateMessageBox").on("click",".privateMessageUser",function(){
    $(".sendMessage").attr("myVaule", $(this).attr("myValue"));
    $.ajax({
        url: "/api/user/getUserInfoList",
        data:JSON.stringify([$(this).attr("myValue")]),
        type: "post",
        dataType:'json',
        contentType: "application/json;charset=utf-8",
        success: function(data) {
            if(data.status != 0){
                return;
            }
            $("#messageBox").empty();
            startIndex = -1;
            chatTargetUser = data.data[0];
            $(".chatName").html(chatTargetUser.userName);
            $(".chatBox").addClass("open-chat");
            firstOldMessage = 0;
            getOldMessage();
        }
    });
})

$(".minPrivateMessageBox").on("click",".privateMessageUser",function(){
    $(".sendMessage").attr("myVaule", $(this).attr("myValue"));
    $.ajax({
        url: "/api/user/getUserInfoList",
        data:JSON.stringify([$(this).attr("myValue")]),
        type: "post",
        dataType:'json',
        contentType: "application/json;charset=utf-8",
        success: function(data) {
            if(data.status != 0){
                return;
            }
            $("#messageBox").empty();
            startIndex = -1;
            chatTargetUser = data.data[0];
            $(".chatName").html(chatTargetUser.userName);
            $(".chatBox").addClass("open-chat");
            firstOldMessage = 0;
            getOldMessage();
        }
    });
})

$(".chatBox").on("click",".closeChatBtn",function(){
    $(".chatBox").removeClass("open-chat");
})




function getPrivateMessageList(){
    $(".privateMessageBox").empty();
    $.ajax({
        url: "/api/post/getPrivateMessageList",
        type: "get",
        dataType:"json",
        success: function(data){
            if(data.status != 0){
                return;
            }
            var privateMessage = data.data;
            
            var minNav = "<div class=\"fixed-sidebar-right sidebar--small\" id=\"sidebar-right\">"+
            "		<div class=\"mCustomScrollbar\" data-mcs-theme=\"dark\">"+
            "			<ul class=\"chat-users\">";
            for(var i=0; i < privateMessage.length; i++){
                minNav += "<li class=\"inline-items js-chat-open\ myValue=\""+ privateMessage[i].userId +"\">"+
                        "	<div class=\"author-thumb\">"+
                                "<img alt=\"author\" style=\"width:35px; height:35px\" src=\""+ privateMessage[i].headPictureSrc +"\" class=\"avatar privateMessageUser\" myValue=\""+ privateMessage[i].userId +"\">"+
                            "</div>"+
                        "</li>";
            }

            minNav  +=  "</ul>"+
            "		</div>"+
            "		<div class=\"search-friend inline-items\">"+
            "			<a href=\"#\" class=\"js-sidebar-open bigPrivateMessage\">"+
            "				<svg class=\"olymp-menu-icon\"><use xlink:href=\"/resources/svg-icons/sprites/icons.svg#olymp-menu-icon\"></use></svg>"+
            "			</a>"+
            "		</div>"+
            "		<a href=\"#\" class=\"olympus-chat inline-items js-chat-open lastPrivateMessage\">"+
            "			<svg class=\"olymp-chat---messages-icon\"><use xlink:href=\"/resources/svg-icons/sprites/icons.svg#olymp-chat---messages-icon\"></use></svg>"+
            "		</a>"+
            "	</div>";
            $(".privateMessageBox").append(minNav);

            var bigNav = "<div class=\"fixed-sidebar-right sidebar--large\" id=\"sidebar-right-1\">"+
            "		<div class=\"mCustomScrollbar\" data-mcs-theme=\"dark\">"+
            "			<div class=\"ui-block-title ui-block-title-small\">"+
            "				<a href=\"#\" class=\"title\">私信列表</a>"+
            "			</div>"+
            "			<ul class=\"chat-users\">";
            

            for(var i=0; i < privateMessage.length; i++){
                bigNav += "		<li class=\"inline-items js-chat-open\ myValue=\""+ privateMessage[i].userId +"\">"+
                "					<div class=\"author-thumb\">"+
                "						<img alt=\"author\" style=\"width:35px; height:35px\" src=\""+ privateMessage[i].headPictureSrc +"\" class=\"avatar\">"+
                "						 "+
                "					</div>"+
                "					<div class=\"author-status\">"+
                "						<a class=\"h6 privateMessageUser author-name\" myValue=\""+ privateMessage[i].userId +"\">"+ privateMessage[i].userName +"</a>"+
                "						<span class=\"status\">"+ privateMessage[i].accountInfo.introduce +"</span>"+
                "					</div>"+
                "					<div class=\"more\"><svg class=\"olymp-three-dots-icon\"><use xlink:href=\"/resources/svg-icons/sprites/icons.svg#olymp-three-dots-icon\"></use></svg>"+
                "						<ul class=\"more-icons\">"+
                "							<li>"+
                "								<svg data-toggle=\"tooltip\" data-placement=\"top\" myValue=\""+ privateMessage[i].userId +"\" data-original-title=\"删除私信\" class=\"olymp-block-from-chat-icon delChar\"><use xlink:href=\"/resources/svg-icons/sprites/icons.svg#olymp-block-from-chat-icon\"></use></svg>"+
                "							</li>"+
                "						</ul>"+
                "					</div>"+
                "				</li>";
            }


            bigNav +="  </ul>"+
            "		</div>"+
            "		<div class=\"search-friend inline-items\" style=\"border-color: white;\">"+

            "		</div>"+
            "		<a href=\"#\" class=\"olympus-chat inline-items js-chat-open lastPrivateMessage\">"+
            "			<h6 class=\"olympus-chat-title\">打开上一次私信</h6>"+
            "			<svg class=\"olymp-chat---messages-icon\"><use xlink:href=\"/resources/svg-icons/sprites/icons.svg#olymp-chat---messages-icon\"></use></svg>"+
            "		</a>"+
            "	</div>";

            $(".privateMessageBox").append(bigNav);
            $(".minPrivateMessageBox").append(bigNav);
            $("#sidebar-right-responsive").removeClass("hidden");
            
            webSocket_init();
        }
    });
}

function getOldMessage(){
    $.ajax({
        url: "/api/post/getOldMessage",
        type: "post",
        dataType:"json",
        data: {"userId": chatTargetUser.userId, "startIndex": startIndex},
        success: function(data){
            if(data.status != 0){
                return;
            }
            var messageList = data.data;
            var myHeadImg = $(".myHeadImg").attr("src");
            startIndex = messageList.index;
            $(".oldMessage").remove();
            var html ="";
            if(!messageList.lastMessage){
                html += "<li class=\"text-center\"><button type=\"button\" class='btn btn-secondary btn-sm oldMessage'>查看历史记录</button></li>";
            }
             for(var i=0; i<messageList.privateMessageList.length; i++){
                if(messageList.privateMessageList[i].userId == chatTargetUser.userId){
                    html += "           <li class=\"row\" style=\"padding-top: 20px\">"+
                    "						<div class=\"col-3 col-lg-3\" style=\"padding-right: 0px; margin-right: 0px\">"+
                    "						    <img width='45px' height='45px' src=\""+ chatTargetUser.headPictureSrc +"\"  style=\"margin-top: 5px; border-radius:50%;\" alt=\"author\" class=\"img-thumbnail\">"+
                    "						</div>"+
                    "						<div class=\"col col-md-7 text-center\">"+
                    "							<span class=\"chat-message-item\" style=\"word-break:normal; width:auto; display:block; white-space:pre-wrap;word-wrap : break-word ;overflow: hidden ; background-color: #f0f4f9; float:left; width: 150px;\" class=\"text-center\">"+ messageList.privateMessageList[i].message +"</span>"+
                    "						</div>"+
                    "					</li>"+
                                        "<li class=\"row\"><div class=\"mx-auto\">"+ MDHM(messageList.privateMessageList[i].date) +"<div></li>";


                   }else{

                    html += "           <li class=\"row\" style=\"padding-top: 20px\">"+
                    "						<div class=\"col col-md-7 col-lg-7 offset-md-2 offset-lg-2 text-center\">"+
                    "							<span class=\"chat-message-item\" style=\"word-break:normal; width:auto; display:block; white-space:pre-wrap;word-wrap : break-word ;overflow: hidden ; background-color: #f0f4f9; float:right; width: 150px;\" class=\"text-center\">"+ messageList.privateMessageList[i].message +"</span>"+
                    "						</div>"+
                    "						<div class=\"col-3 col-lg-3\" style=\"padding-left: 0px; margin-left: 0px\">"+
                    "						    <img width='45px' height='45px' src=\""+ myHeadImg +"\"  style=\"margin-top: 5px; border-radius:50%;\" alt=\"author\" class=\"img-thumbnail\">"+
                    "						</div>"+
                    "					</li>"+
                                        "<li class=\"row\"><div class=\"mx-auto\">"+ MDHM(messageList.privateMessageList[i].date) +"<div></li>";
                 }
            }
            $("#messageBox").prepend(html);
            if(firstOldMessage == 0){
                goBottom();
            }
            firstOldMessage = 1;
        }
    });
}

function goBottom()
{
var div = document.getElementById('pre-scrollable');
div.scrollTop = div.scrollHeight;
}

var sessionId;
var webSocket;
var firstOldMessage = 0;

$(".chatBox").on("click",".sendMessage",function(){
    var html = "           <li class=\"row\" style=\"padding-top: 20px\">"+
        "						<div class=\"col col-md-7 col-lg-7 offset-md-2 offset-lg-2 text-center\">"+
        "							<span class=\"chat-message-item\" style=\"word-break:normal; width:auto; display:block; white-space:pre-wrap;word-wrap : break-word ;overflow: hidden ; background-color: #f0f4f9; float:right; width: 150px;\" class=\"text-center\">"+$(".message").val()+"</span>"+
        "						</div>"+
        "						<div class=\"col-3 col-lg-3\" style=\"padding-left: 0px; margin-left: 0px\">"+
        "						    <img width='45px' height='45px' src=\""+ $(".myHeadImg").attr("src") +"\"  style=\"margin-top: 5px; border-radius:50%;\" alt=\"author\" class=\"img-thumbnail\">"+
        "						</div>"+
        "					</li>"+
                            "<li class=\"row\"><div class=\"mx-auto\">"+ MDHM(Date.parse(new Date())) +"<div></li>";
        $("#messageBox").append(html);
    webSocket.send(JSON.stringify(createMessage($(this).attr("myVaule"), sessionId, $(".message").val())));
    goBottom();
    $(".message").val("");
})

function webSocket_init(){
    $.ajax({
        url: "/api/user/getSessionId",
        type: "get",
        dataType:"json",
        success: function(data){
            if(data.status != 0){
                return;
            }
            sessionId = data.data;
            webSocket = new WebSocket("ws://49.234.121.88:8213/privateMessage/"+data.data);
            webSocket.onmessage=function(result){
                var respJson = JSON.parse(result.data);
                if(respJson.status != 0){
                    $("#messageBox").append("<p style=\"padding-left:30%;\">"+respJson.msg+"</p>");
                    goBottom();
                    return;
                }
                var  html = "           <li class=\"row\" style=\"padding-top: 20px\">"+
                    "						<div class=\"col-3 col-lg-3\" style=\"padding-right: 0px; margin-right: 0px\">"+
                    "						    <img width='45px' height='45px' src=\""+ chatTargetUser.headPictureSrc +"\"  style=\"margin-top: 5px; border-radius:50%;\" alt=\"author\" class=\"img-thumbnail\">"+
                    "						</div>"+
                    "						<div class=\"col col-md-7 text-center\">"+
                    "							<span class=\"chat-message-item\" style=\"word-break:normal; width:auto; display:block; white-space:pre-wrap;word-wrap : break-word ;overflow: hidden ; background-color: #f0f4f9; float:left; width: 150px;\" class=\"text-center\">"+ respJson.data.message +"</span>"+
                    "						</div>"+
                    "					</li>"+
                                        "<li class=\"row\"><div class=\"mx-auto\">"+ MDHM(respJson.data.date) +"<div></li>";
                $("#messageBox").append(html);
                goBottom();
            };

            webSocket.onerror = function(){
                alert("私信列表WebSocket连接失败");
            }
        }
    });
}


function createMessage (targetUserId,sessionId,message){
    var obj = new Object;
    obj.targetUserId =targetUserId;
    obj.sessionId =sessionId;
    obj.message =message;
    return obj;
}

function MDHM(timestamp) {
    var date = new Date(timestamp);//时间戳为10位需*1000，时间戳为13位的话不需乘1000
    Y = date.getFullYear() + '-';
    M = (date.getMonth()+1 < 10 ? '0'+(date.getMonth()+1) : date.getMonth()+1) + '-';
    D = date.getDate() + ' ';
    h = date.getHours() + ':';
    m = (date.getMinutes() < 10 ? '0'+(date.getMinutes()) : date.getMinutes());
    s = ":"+(date.getSeconds() < 10 ? '0'+(date.getSeconds()) : date.getSeconds());
    return M+D+h+m;
}
    



