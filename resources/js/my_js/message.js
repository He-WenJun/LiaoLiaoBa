$(document).ready(function(){
    getMessage();
})

$(".messageList").on("click",".messageBtn", function(){
    $.ajax({
        url: '/api/post/delMessage',
        dataType: 'json',
        type: 'post',
        data: {"messageIndex": $(this).attr("myIndex")},
    });
    if($(this).attr("myTypeId") == 1){
        window.location.replace("/api/post/postInfo/dispatcher/"+$(this).attr("postId"));
    }else if($(this).attr("myTypeId") == 2){
        getMessage();
     }else if($(this).attr("myTypeId") == 3){
        window.location.replace("/api/post/postInfo/dispatcher/"+$(this).attr("postId"));
    }
})


function getMessage(){
    $.ajax({
        url: '/api/post/getMessage',
        dataType: 'json',
        type: 'get',
        success: function(data){
            if(data.status != 0){
                $(".messageList").html("<li class=\"text-center\"><span>"+ data.msg +"</span></li>");
                return;
            }
            var messageList = data.data;
            if(messageList.length > 0){
                $(".messageCount").html(messageList.length);
                $(".messageCount").removeClass("hidden");
            }
            var html = "";
            for(var i=0; i < messageList.length; i++){
                html += "                <li class=\"message-unread\">"+
                "								<div class=\"notification-event\">"+
                "									<span class=\"h6 notification-friend\">"+ messageList[i].message +"</span>";
                
                if(messageList[i].typeId == 1){
                    html +="							<span class=\"chat-message-item\">"+ messageList[i].data.content +"</span>"+
                    "									<span class=\"notification-date\"><time class=\"entry-date updated\">"+ timestampToTime(messageList[i].date)+"</time></span>"+
                    "								</div>"+
                    "								<span class=\"notification-icon\">"+
                    "									<button class=\"btn btn-primary messageBtn\" style=\"width: 45px; height: 20px; padding: 0px;\" myIndex=\""+ i +"\" myId=\""+  messageList[i].id +"\" myTypeId="+ messageList[i].typeId +" pageNumber=\""+ messageList[i].data.pageNumber +"\" postId=\""+ messageList[i].data.postId +"\">去看看</button>"+
                    "								</span>"+
                    "							</li>"; 


                }else if(messageList[i].typeId == 2){
                    html +="							<span class=\"chat-message-item\">"+ messageList[i].data.postName +"</span>"+
                    "									<span class=\"notification-date\"><time class=\"entry-date updated\">"+timestampToTime(messageList[i].date) +"</time></span>"+
                    "								</div>"+
                    "								<span class=\"notification-icon\">"+
                    "									<button class=\"btn btn-primary messageBtn\" style=\"width: 45px; height: 20px; padding: 0px;\" myIndex=\""+ i +"\" myId=\""+  messageList[i].id +"\" myTypeId="+ messageList[i].typeId +">确定</button>"+
                    "								</span>"+
                    "							</li>"; 



                }else if(messageList[i].typeId == 3){
                    html +="							<span class=\"chat-message-item\">"+ messageList[i].data.content +"</span>"+
                    "									<span class=\"notification-date\"><time class=\"entry-date updated\">"+ timestampToTime(messageList[i].date) +"</time></span>"+
                    "								</div>"+
                    "								<span class=\"notification-icon\">"+
                    "									<button class=\"btn btn-primary messageBtn\" style=\"width: 45px; height: 20px; padding: 0px;\" myIndex=\""+ i +"\" myId=\""+  messageList[i].id +"\" myTypeId="+ messageList[i].typeId +" postId=\""+ messageList[i].data.postId +"\">去看看</button>"+
                    "								</span>"+
                    "							</li>"; 
                }
            }

            $(".messageList").html(html);

        }
    });
}

function timestampToTime(timestamp) {
    var date = new Date(timestamp);//时间戳为10位需*1000，时间戳为13位的话不需乘1000
    Y = date.getFullYear() + '-';
    M = (date.getMonth()+1 < 10 ? '0'+(date.getMonth()+1) : date.getMonth()+1) + '-';
    D = date.getDate() + ' ';
    h = date.getHours() + ':';
    m = (date.getMinutes() < 10 ? '0'+(date.getMinutes()) : date.getMinutes());
    s = ":"+(date.getSeconds() < 10 ? '0'+(date.getSeconds()) : date.getSeconds());
    return M+D+h+m;
}