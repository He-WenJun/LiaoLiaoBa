$(document).ready(function(){
    init($(".data").val());
});

$(".commentBox").on("click",".replyBtn",function(){
    $(".replyBox[reply='"+$(this).attr("objectReply")+"']").slideToggle("slow",function(){
        if($(this).css("display") == "none"){
            $(".replyBtn[objectReply="+$(this).attr("reply")+"]").html("回复");
        }else{
            $(".replyBtn[objectReply="+$(this).attr("reply")+"]").html("收起回复");
        }
    });
});
$(".commentBox").on("click",".replyCommit",function(){
    var commentMsg = $("input[reply='"+$(this).attr("objectReply")+"']").val();
    if(commentMsg != undefined && commentMsg !=""){
        $.ajax({
            url: '/api/post/commitReply',
            dataType: 'json',
            type: 'post',
            data: {"commentId":$("input[reply="+$(this).attr("objectReply")+"]").attr("commentId"),"content":$("input[reply="+$(this).attr("objectReply")+"]").val(),"postId":$(".data").val(),"pageNumber":$(".thisPage").attr("myPageNumber"),"targetUserId":$(this).attr("targetUserId"), "moduleId": $("#commitPost").attr("moduleId")},
            success: function(data){
                console.log(data);
                if(data.status!=0){
                    $(".reminderMsg").html(data.msg);
                    $("#reminderModal").modal("show");
                    return;
                }
                refreshComment($(".data").val(),$(".thisPage").attr("myPageNumber"));
            }
        });
    }
});

var targetUserId;

$("#commitPost").click(function(){
    var t = setInterval(function(){
        if(getUpdateResult()){
            clearInterval(t);
            var uploadId = getUploadId();
            $.ajax({
                url: '/api/post/commitComment',
                traditional: true, 
                dataType: 'json',
                type: 'post',
                data: {"postId": $(".data").val(), "targetUserId": targetUserId,"content": $(".commentContent").val(),"uploadIds":uploadId, "moduleId": $("#commitPost").attr("moduleId")},
                success: function(data){
                    updateInit();
                    if(data.status!=0){
                        $(".reminderMsg").html(data.msg);
                        $("#reminderModal").modal("show");
                        return;
                    }
                    $(".commentBox").empty();   
                    $(".pageBox").empty();
                    refreshComment($(".data").val(),1);
                   
                }
            });
        }
    },300); 
});

$(".pageBox").on("click","button",function(){
    $(".postBox").empty();
    $(".pageBox").empty();
    $(".commentBox").empty();
    refreshComment($(".data").val(),$(this).attr("myPageNumber"));
});

var floor = 1;
var pageSize = 10;

function init(postId){
    refreshPost(postId);
    refreshComment(postId,"1");
}

function refreshPost(postId){
    $.ajax({
        url: "/api/post/postInfo",
        dataType: "json",
        type: "get",
        data: "postId=" + postId,
        success: function(data){
            if(data.status != 0){
                $(".reminderMsg").html(data.msg);
                $("#reminderModal").modal("show");
                return;
            }
            var postInfo = data.data;
            $(".post-title").html(postInfo.postVo.postName);
            targetUserId = postInfo.accountVo.userId;
            $(".post__author-name").html("&nbsp;"+"<a href=\"/api/user/dispatcher/userInfo/"+ postInfo.accountVo.userId +"\">"+postInfo.accountVo.userName+"</a>");
            $(".published").html("&nbsp;"+timestampToTime(postInfo.postVo.enrollDate));
            $(".authorPortrait").attr("src",postInfo.accountVo.headPictureSrc);
            $(".postContent").append(postInfo.postVo.content);
            $(".backModuleBtn").attr("href","/api/post/moduleInfo/dispatcher/"+postInfo.postVo.moduleId);
            $("#commitPost").attr("moduleId", postInfo.postVo.moduleId);
        }
    });
}

function refreshComment(postId,pageNumber){
    $(".commentBox").empty();
    $(".pageBox").empty();
    $.ajax({
        url: "/api/post/commentInfo",
        dataType: "json",
        type: "get",
        data: "postId=" + postId +"&pageNumber=" + pageNumber,
        success: function(data){
            if(data.status != 0){
                $(".commentBox").append("<div class='col-lg-12 col-md-12' style='height: 300px;'><h4 class='text-center my-auto'>"+data.msg+"</h4></div>");
                return;
            }
            var commentInfo = data.data;
            var commentHtml = ""
            for(var i = 0; i < commentInfo.list.length; i++){
                commentHtml += "<li class=\"comment-item\">"+
                "						<div class=\"post__author author vcard inline-items\">"+
                "							<img alt=\"author\" src='"+commentInfo.list[i].accountVO.headPictureSrc+"'>"+
                "							<div class=\"author-date\">"+
                "								<a class=\"h6 post__author-name fn\" href=\"/api/user/dispatcher/userInfo/"+ commentInfo.list[i].accountVO.userId +"\">&nbsp;"+ commentInfo.list[i].accountVO.userName +"</a>"+
                "							</div>"+

                "						</div>"+
                "						<p>"+ commentInfo.list[i].comment.content +"</p>";
                if(commentInfo.list[i].fileList != undefined){
                    commentHtml += "<ul class=\"contentImgBox\">";
                    for(var j = 0; j < commentInfo.list[i].fileList.length; j++){
                        commentHtml += "<li><img src=\""+ commentInfo.list[i].fileList[j].src +"\" class=\"contentImg\" /></li>";
                    }
                    commentHtml += " </ul>";
                }
                commentHtml += " <ul class=\"msgInfoBox text-right\">"+
                                    "<li>评论日期："+ timestampToTime(commentInfo.list[i].comment.enrollDate) +"</li>";
                if(commentInfo.list[i].replyVOList != undefined){
                    commentHtml += "<li><button objectReply='reply-"+ floor +"' class='replyBtn btn btn-sm btn-primary'>收起回复</button></li>";
                    commentHtml += "<div class='replyBox' reply=\"reply-"+ floor +"\"><div class=\"input-group mb-1\">"+
                                        "<input commentId='"+ commentInfo.list[i].comment.commentId +"' reply=\"reply-"+ floor +"\" type=\"text\" class=\"form-control userName\" placeholder=\"2~5个字符\">"+
                                        "<div class=\"input-group-append\">"+
                                            "<button class=\"btn btn-sm btn-primary replyCommit \" objectReply=\"reply-"+ floor +"\" type=\"button\" targetUserId='"+ commentInfo.list[i].comment.userId +"' style=\"height:100%\" >发送回复</button>  "+
                                        "</div>"+
                                    "</div></div>";
                }else{
                    commentHtml += "<li><button objectReply='reply-"+ floor +"' class='replyBtn btn btn-sm btn-primary'>回复</button></li>";
                    commentHtml += "<div class='replyBox' style='display:none;' reply=\"reply-"+ floor +"\"><div class=\"input-group mb-1\">"+
                                        "<input commentId='"+ commentInfo.list[i].comment.commentId +"' reply=\"reply-"+ floor +"\" type=\"text\" class=\"form-control userName\" placeholder=\"2~5个字符\">"+
                                        "<div class=\"input-group-append\">"+
                                            "<button class=\"btn btn-lg btn-primary replyCommit\" objectReply=\"reply-"+ floor +"\" type=\"button\" targetUserId='"+ commentInfo.list[i].comment.userId +"' style=\"height:100%\">发送回复</button>  "+
                                        "</div>"+
                                    "</div></div>";
                }
                commentHtml += "</ul>";
                if(commentInfo.list[i].replyVOList != undefined){
                    commentHtml +=  "<div class=\"container\" reply=\"reply-"+ floor +"\">";
                }else{
                    commentHtml +=   "<div class=\"container display-none\" reply=\"reply-"+ floor +"\">";
                }
                if(commentInfo.list[i].replyVOList != undefined){
                    for(var j = 0; j < commentInfo.list[i].replyVOList.length; j++){
                        commentHtml += "<ul class=\"children replyBox\" reply=\"reply-"+ floor +"\">"+
                "							<li class=\"comment-item\">"+
                "								<div class=\"post__author author vcard inline-items\">"+
                "									<img alt=\"author\" src=\""+ commentInfo.list[i].replyVOList[j].accountVO.headPictureSrc +"\">"+
                "									<div class=\"author-date\">"+
                "										<a class=\"h6 post__author-name fn\" href=\"/api/user/dispatcher/userInfo/"+ commentInfo.list[i].replyVOList[j].accountVO.userId +"\">&nbsp;"+ commentInfo.list[i].replyVOList[j].accountVO.userName +"</a>"+
                "										<div class=\"post__date\">"+
                "											<time class=\"published\"\">&nbsp;"+ timestampToTime(commentInfo.list[i].replyVOList[j].reply.enrollDate) +"</time>"+
                "										</div>"+
                "									</div>"+
                "								</div>"+
                "								<p>"+ commentInfo.list[i].replyVOList[j].reply.content +"</p>"+
                "							</li>"+
                "						</ul>";
                    }
                }
                commentHtml += "</li>";
                    floor++;
            }
            $(".commentBox").append(commentHtml);
            var pageHtml = "<li class=\"page-item\"><button class=\"page-link\" myPageNumber='"+commentInfo.prePage+"'>上一页</button></li>";
            for(var i = 0; i < commentInfo.navigatepageNums.length; i++){
                if(commentInfo.navigatepageNums[i] == commentInfo.pageNum){
                    pageHtml = pageHtml+"<li class=\"page-item active\"><button class=\"page-link thisPage\" myPageNumber='"+commentInfo.navigatepageNums[i]+"'>"+commentInfo.navigatepageNums[i]+"</button></li>";
                    continue;
                }
                pageHtml = pageHtml+"<li class=\"page-item\"><button class=\"page-link\" myPageNumber='"+commentInfo.navigatepageNums[i]+"'>"+commentInfo.navigatepageNums[i]+"</button></li>";
            }
            pageHtml = pageHtml+"<li class=\"page-item\"><button class=\"page-link\" myPageNumber='"+commentInfo.nextPage+"'>下一页</button></li>"; 
            
            $(".pageBox").append(pageHtml);
            hidePost(pageNumber);
        }
    });
}

function hidePost (pageNumber){
    if(pageNumber != 1){
        $(".post").hide();
    }else{
        $(".post").show();
    }
}

function timestampToTime(timestamp) {
    var date = new Date(timestamp);//时间戳为10位需*1000，时间戳为13位的话不需乘1000
    var Y = date.getFullYear() + '-';
    var M = (date.getMonth() + 1 < 10 ? '0' + (date.getMonth() + 1) : date.getMonth() + 1) + '-';
    var D = date.getDate() + ' ';
    //var h = date.getHours() + ':';
    //var m = date.getMinutes() + ':';
    //var s = date.getSeconds();
    return Y + M + D;
}