$(document).ready(function(){
    getUserInfo($(".data").val());
    getHimConcern($(".data").val(),1);
    getConcernHim($(".data").val(),1);
    postList($(".data").val(),1);
    getSubscribeModule($(".data").val());
    getOthersUserSetting($(".data").val());
})


$(".himConcernPageBox").on("click","button",function(){
    $(".himConcernPageBox").empty();
    $(".himConcern").empty();
    getHimConcern($(".data").val() ,$(this).attr("myPageNumber"));
});
$(".concernHimPageBox").on("click","button",function(){
    $(".concernHimPageBox").empty();
    $(".concernHim").empty();
    getConcernHim($(".data").val() ,$(this).attr("myPageNumber"));
});
$(".pageBox").on("click","button",function(){
    $(".postBox").empty();
    $(".pageBox").empty();
    postList($(".data").val() ,$(this).attr("myPageNumber"));
});

$(".addSubscribe").click(function(){
    addSubscribeUser($(".data").val());
});

$(".togglebutton input").click(function(){
    if($(this).attr("myValue") == "false"){
        $(this).attr("myValue","true");
    }else{
        $(this).attr("myValue","false");
    }
});

$(".privateMessageBtn").click(function(){
    $.ajax({
        type: "post",
        url: "/api/post/addUserGoToPrivateMessageList",
        data:{"userId": $(".data").val()},
        dataType:'json',
        success: function(data) {
            getPrivateMessageList();
        }
    });
});

$(".commitSetting").click(function(){
    $.ajax({
        type: "post",
        url: "/api/user/addOthersUserSetting",
        data:{"commentPost": $(".commentPost").attr("myValue"), "sendMessage": $(".sendMessage").attr("myValue"), "userId": $(".data").val()},
        type: "post",
        dataType:'json',
        success: function(data) {
            $(".reminderMsg").html(data.msg);
            $("#reminderModal").modal("show");
        }
    });
});

function getUserInfo(userId){
    $.ajax({
        type: "get",
        url: "/api/user/getUserInfoList",
        data:JSON.stringify([userId]),
        type: "post",
        dataType:'json',
        contentType: "application/json;charset=utf-8",
        success: function(data) {
            if(data.status != 0){
                $(".reminderMsg").html(data.msg);
                $("#reminderModal").modal("show");
                return;
            }
            var userInfo = data.data[0];
            $(".name").html(userInfo.userName);
            $(".addSubscribe").attr("userId",userInfo.userId);
            $(".myHead").attr("src",userInfo.headPictureSrc);
            
            if(userInfo.backgroundPicture != undefined){
                $("body").css("background-image","url("+ userInfo.backgroundPicture +")");
                $("body").css("background-attachment","fixed");
                $("body").css("background-repeat","no-repeat");
                $("body").css("background-size","100% 100%");
            }
            $(".country").html(userInfo.accountInfo.introduce);
            $(".level").html(userInfo.level+"级");
            $(".exp").append(
                "<div class='progress-moduler bg-success have' style='color:black;width:"+userInfo.exp +"%'>"+userInfo.exp+"</div>"+
                "<div class='progress-moduler bg-info need' style='color:black;width:"+(userInfo.levelExp - userInfo.exp)+"%'>"+(userInfo.levelExp - userInfo.exp)+"</div>"
            );
        }
    });
}

function getHimConcern(userId,pageNumber){
    $.ajax({
        type: "get",
        url: "/api/post/himConcernList",
        data: {"userId":userId, "pageNumber":pageNumber},
        type: "get",
        dataType: "json",
        success: function(data){
            if(data.status != 0){
                $(".himConcern").append("<p class='text-center'>"+data.msg+"</p>");
                return;
            }
            var postInfo = data.data;
            $(".himConcernTitle").html("Ta关注的人数（"+ postInfo.total +"）");
            for(var i=0; i < postInfo.list.length; i++){
                var html = "<li><a href=\"/api/user/dispatcher/userInfo/"+ postInfo.list[i].userId +"\"><img style=\"display: block; width: 100%; height: 100%;\" src=\""+ postInfo.list[i].headPictureSrc +"\" alt=\"user\"></a></li>";
                $(".himConcern").append(html);
            }
            
            if(postInfo.navigatepageNums.length > 1){
                var pageHtml = "<li class=\"page-item\"><button class=\"page-link minButton text-center\" myPageNumber='"+postInfo.prePage+"'><span><</span></button></li>";
                for(var i = 0; i < postInfo.navigatepageNums.length; i++){
                    if(postInfo.navigatepageNums[i] == postInfo.pageNum){
                        pageHtml = pageHtml+"<li class=\"page-item active\"><button class=\"page-link minButton text-center\" myPageNumber='"+postInfo.navigatepageNums[i]+"'>"+postInfo.navigatepageNums[i]+"</button></li>";
                        continue;
                    }
                    pageHtml = pageHtml+"<li class=\"page-item\"><button class=\"page-link minButton text-center\" myPageNumber='"+postInfo.navigatepageNums[i]+"'>"+postInfo.navigatepageNums[i]+"</button></li>";
                }
                pageHtml = pageHtml+"<li class=\"page-item\"><button class=\"page-link minButton text-center\" myPageNumber='"+postInfo.nextPage+"'>></button></li>"; 
                
                $(".himConcernPageBox").append(pageHtml);
            }
        }
    });
}

function getConcernHim(userId,pageNumber){
    $.ajax({
        type: "get",
        url: "/api/post/concernHimList",
        data: {"userId":userId, "pageNumber":pageNumber},
        type: "get",
        dataType: "json",
        success: function(data){
            if(data.status != 0){
                $(".concernHim").append("<p class='text-center'>"+data.msg+"</p>");
                return;
            }
            var postInfo = data.data;
            $(".concernHimTitle").html("关注Ta的人数（"+ postInfo.total +"）");
            for(var i=0; i < postInfo.list.length; i++){
                var html = "<li><a href=\"/api/user/dispatcher/userInfo/"+ postInfo.list[i].userId +"\"><img style=\"display: block; width: 100%; height: 100%;\" src=\""+ postInfo.list[i].headPictureSrc +"\" alt=\"user\"></a></li>";
                $(".concernHim").append(html);
            }
            
            if(postInfo.navigatepageNums.length > 1){
                var pageHtml = "<li class=\"page-item\"><button class=\"page-link minButton text-center\" myPageNumber='"+postInfo.prePage+"'><span><</span></button></li>";
                for(var i = 0; i < postInfo.navigatepageNums.length; i++){
                    if(postInfo.navigatepageNums[i] == postInfo.pageNum){
                        pageHtml = pageHtml+"<li class=\"page-item active\"><button class=\"page-link minButton text-center\" myPageNumber='"+postInfo.navigatepageNums[i]+"'>"+postInfo.navigatepageNums[i]+"</button></li>";
                        continue;
                    }
                    pageHtml = pageHtml+"<li class=\"page-item\"><button class=\"page-link minButton text-center\" myPageNumber='"+postInfo.navigatepageNums[i]+"'>"+postInfo.navigatepageNums[i]+"</button></li>";
                }
                pageHtml = pageHtml+"<li class=\"page-item\"><button class=\"page-link minButton text-center\" myPageNumber='"+postInfo.nextPage+"'>></button></li>"; 
                
                $(".concernHimPageBox").append(pageHtml);
            }
        }
    });
}

function addSubscribeUser(objectId){
    $.ajax({
        type: "post",
        url: "/api/post/addSubscribeUser",
        data: {"objectId":objectId},
        dataType: "json",
        success: function(data){
            $(".reminderMsg").html(data.msg);
            $("#reminderModal").modal("show");
        }
    });
}

function postList(userId, pageNumber){
    $.ajax({
        url: "/api/post/getPostListByUserId",
        dataType: 'json',
        type: 'get',
        data: {"userId":userId, "pageNumber":pageNumber},
        success: function(data){
            if(data.status != 0){
                $(".postBox").append("<h4 class='text-center'>"+data.msg+"</h4>");
                return;
            }
            var postInfo = data.data;
            for(var i = 0; i < postInfo.list.length; i++){
                var html = "<li style='margin: 3px;' class=\"list-group-item postInfoBox\">"+
                "                <div class=\"container\">"+
                "                      <div class=\"row\">"+
                "                            <div class=\"col-lg-10 col-md-10 text-left\">"+
                "                                  <a href='/api/post/postInfo/dispatcher/"+postInfo.list[i].postVo.postId+"'>"+postInfo.list[i].postVo.postName+"</a>"+
                "                             </div>";
                if(postInfo.list[i].postVo.commentCount != undefined){
                        html += "           <div class=\"col-lg-2 col-md-2 text-right\">"+
                        "                         <span>"+postInfo.list[i].postVo.commentCount+"回复</span>"+
                        "                   </div>";
                }
                html += "             </div>";

                if(postInfo.list[i].fileList != undefined){
                    html += "            <hr/>"+
                        "                <div class=\"row\">"+
                        "                     <div class=\"col-lg-12 col-md-12\">";
                    for(var j = 0; j < postInfo.list[i].fileList.length; j++){
                        html += "<img width='200px' heigth='200px' src='"+postInfo.list[i].fileList[j].src+"' class=\"msgImg img-thumbnail\" onclick='imgDisplay(this)'/>";
                    }
                    html += "                 </div>"+
                    "                    </div>";
                }
                html +="                 <hr/>"+
                "                       <div class=\"row\">"+
                                            "<div class='col-lg-12 postInfoBox'>"+
                                            "<ul class='info postInfo list-style-none'>";
                if(postInfo.list[i].postVo.latestCommentDate != undefined){
                    html += "<li class='m-auto'><span>"+postInfo.list[i].postVo.latestCommentDate+"回复</span></li>";
                }
                    html += "<li class=\"m-auto\"><span>浏览次数："+postInfo.list[i].postVo.readCount+"</span></li>"+
                            "<li class=\"m-auto\"><span>发帖日期："+timestampToTime(postInfo.list[i].postVo.enrollDate)+"</span></li>"+
                        "</ul>";
                $(".postBox").append(html);
            }
            var pageHtml = "<li class=\"page-item\"><button class=\"page-link\" myPageNumber='"+postInfo.prePage+"'>上一页</button></li>";
            for(var i = 0; i < postInfo.navigatepageNums.length; i++){
                if(postInfo.navigatepageNums[i] == postInfo.pageNum){
                    pageHtml = pageHtml+"<li class=\"page-item active\"><button class=\"page-link\" myPageNumber='"+postInfo.navigatepageNums[i]+"'>"+postInfo.navigatepageNums[i]+"</button></li>";
                    continue;
                }
                pageHtml = pageHtml+"<li class=\"page-item\"><button class=\"page-link\" myPageNumber='"+postInfo.navigatepageNums[i]+"'>"+postInfo.navigatepageNums[i]+"</button></li>";
            }
            pageHtml = pageHtml+"<li class=\"page-item\"><button class=\"page-link\" myPageNumber='"+postInfo.nextPage+"'>下一页</button></li>"; 
            
            $(".pageBox").append(pageHtml);
        }
    });
}

function getSubscribeModule(userId){
    $.ajax({
        url: '/api/post/getSubscribeModuleByUserId',
        dataType: 'json',
        data: {"userId": userId},
        type: 'get',
        success: function(data){
            if(data.status == 0){
                for(var i = 0; i < data.data.length; i++){
                    var imgName = data.data[i].headPictureSrc.substr(data.data[i].headPictureSrc.lastIndexOf("/"));
                    var html = "<li class='list-group-item'><div class='container'><img class='img-responsive img-thumbnail display-inline-block' width='60px' heigth='60px' src='/file/img/"+imgName+"'/><div class='display-inline-block float-right'><span class='float-right'>"+data.data[i].moduleName+"</span><br/><span class='fudong float-right'>"+data.data[i].level+"级</span><br/><span class='fudong float-right'><a href='/api/post/moduleInfo/dispatcher/"+data.data[i].moduleId+"'>进入模块</a></span></div></div></li>";
                    $(".subscribe").append(html);
                    $(".management").append("<button type='button' class='btn btn-secondary btn-sm my-auto baOption' myBaId='"+data.data[i].moduleId+"'><span style='float: left;'>"+data.data[i].moduleName+"</span>&nbsp;&nbsp;<span>&times;</span></button>&nbsp;");
                }

            }else if(data.status == 2){
                var html = "<li class='list-group-item'><span>"+data.msg+"</span></li>";
                $(".subscribe").append(html);
                $(".management").append("<p>"+data.msg+"</p>");
                $(".signIn").hide();

            }
        }
    });
}

function getOthersUserSetting(userId){
    $.ajax({
        url: '/api/user/getOthersUserSetting',
        dataType: 'json',
        data: {"userId": userId},
        type: 'post',
        success: function(data){
            if(data.status != 0){
                return;
            }
            var setting = data.data;
            if(setting.commentPost){
                $(".commentPost").attr("myValue",setting.commentPost);
                $(".commentPost").attr("checked","checked");
            }
            if(setting.sendMessage){
                $(".sendMessage").attr("myValue",setting.sendMessage);
                $(".sendMessage").attr("checked","checked");
            }
        }
    });
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