$(document).ready(function(){
    init($(".data").val());
});

$(".main-header-content").on("click",".addSubscribe",function(){
    $.ajax({
        url: '/api/post/addSubscribeModule',
        dataType: 'json',
        type: 'post',
        data: 'moduleName='+$(".moduleName").attr("moduleName"),
        success: function(data){
            if(data.status != 0){
                $(".reminderMsg").html(data.msg);
                $("#reminderModal").modal("show");
                return;
            }
            $(".subscribeBtn").removeClass("addSubscribe");
            $(".subscribeBtn").addClass("delSubscribe");
            $(".subscribeBtn").html("取消订阅");
        }
    });
});

$(".main-header-content").on("click",".delSubscribe",function(){
    $.ajax({
        url: '/api/post/delSubscribe',
        dataType: 'json',
        type: 'post',
        data: 'objectId='+$(".data").val(),
        success: function(data){
            if(data.status != 0){
                $(".reminderMsg").html(data.msg);
                $("#reminderModal").modal("show");
                return;
            }
            $(".subscribeBtn").removeClass("delSubscribe");
            $(".subscribeBtn").addClass("addSubscribe");
            $(".subscribeBtn").html("订阅");
        }
    });
});

$(".singleBaSignIn").click(function(){
    $.ajax({
        url: '/api/post/singleModuleSignIn',
        dataType: 'json',
        type: 'post',
        data: 'moduleId='+$(".data").val(),
        success: function(data){
            verificationSignIn();
            if(data.status!=0){
                $(".reminderMsg").html(data.msg);
                $("#reminderModal").modal("show");
                return;
            }
            $(".reminder>span").html(data.msg);
            $(".reminder").css("display","block");
            setTimeout(function(){
                $(".reminder").css("display","none");
             },6000);

        }
    });
});

$("#reminderModalOkBtn").click(function(){
    $("#reminderModal").modal("hide");
})

$(".pageBox").on("click","button",function(){
    $(".postBox").empty();
    $(".pageBox").empty();
    postList($(this).attr("myPageNumber"));
});

$(".refresh").click(function(){
    window.location.href=window.location.href;
});

$(".sendPostBox").click(function(){
    anchor($(this).attr("anchorId"));
});

$(".top").click(function(){
    anchor($(this).attr("anchorId"));
});

$("#commitPost").click(function(){
    alert($(".data").val());
    var t = setInterval(function(){
        if(getUpdateResult()){
            clearInterval(t);
            var uploadId = getUploadId();
            $.ajax({
                url: '/api/post/commitPost',
                dataType: 'json',
                type: 'post',
                data: "moduleId="+$(".data").val()+"&postName="+$(".postName").val()+"&content="+$(".content").val()+"&uploadIds="+uploadId,
                success: function(data){
                    updateInit();
                    if(data.status!=0){
                        $(".reminderMsg").html(data.msg);
                        $("#reminderModal").modal("show");
                        return;
                    }
                    $(".postBox").empty();
                    $(".pageBox").empty();
                    postList(1);
                    $(".reminder>span").html(data.msg);
                    $(".reminder").css("display","block");
                    setTimeout(function(){
                        $(".reminder").css("display","none");
                    },6000);
                }
            });
        }
    },300); 
});

$(".searchPostBtn").click(function(){
    $(".pageBox").empty();
    $(".postBox").empty();
    if($(".searchPostName").val() == ""){
        postList(1);
    }else{
        postList(1,$(".searchPostName").val());
    }
    
})

function anchor(id){
    document.querySelector(id).scrollIntoView(true);
}


function init (id){
    $.ajax({
        url: '/api/post/moduleInfo',
        dataType: 'json',
        type: 'get',
        data: "id="+id,
        success: function(data){
            if(data.status != 0){
                $(".reminderMsg").html(data.msg);
                $("#reminderModal").modal("show");
                return;
            }
            var moduleInfo = data.data;
            $(".moduleName").html(moduleInfo.moduleName);
            $(".moduleName").attr("moduleName",moduleInfo.moduleName);
            $(".typeName").html("目录：<a style='color:white;text-decoration:underline;' href='/api/post/moduleList/"+moduleInfo.typeId+"/1'>"+moduleInfo.typeName+"</a>");
            $(".introduce").html(moduleInfo.introduce);
            $(".level").html("等级："+moduleInfo.level);
            $(".countSubscribe").html("订阅数量："+moduleInfo.countSubscribe);
            $(".countPost").html("帖子数量："+moduleInfo.countPost);
            $(".exp").empty();
            $(".exp").append(
                "<div class='progress-moduler bg-success have' style='color:black;width:"+moduleInfo.exp / 10+"%'>"+moduleInfo.exp+"</div>"+
                "<div class='progress-moduler bg-info need' style='color:black;width:"+(moduleInfo.levelExp-moduleInfo.exp)/10+"%'>"+(moduleInfo.levelExp-moduleInfo.exp)+"</div>"
            );
            $(".headPicture").attr("src",moduleInfo.headPictureSrc);
            $("body").css("background-image","url("+ moduleInfo.moduleBackgroundPictureSrc +")");
            $("body").css("background-size","100% 100%");
            $("body").css("background-repeat","no-repeat")
            $(".enrollDate").html("创建时间：" + timestampToTime(moduleInfo.enrollDate));
        }
    });

    $.ajax({
        url: '/api/post/getSubscribeModule',
        dataType: 'json',
        type: 'get',
        success: function(data){
            if(data.status != 0){
                return;
            }
            var subscribeInfo = data.data;
            for(var i = 0; i < subscribeInfo.length; i++){
                if(subscribeInfo[i].moduleId == id){
                    $(".subscribeBtn").removeClass("addSubscribe");
                    $(".subscribeBtn").addClass("delSubscribe");
                    $(".subscribeBtn").html("取消订阅");
                    break;
                }
                
            }
        }
    });

    $.ajax({
        url: '/api/user/userInfo',
        dataType: 'json',
        type: 'get',
        success: function(data){
            if(data.status == 0){
                $(".Name").html(data.data.userName);
                $(".userLevel").html(data.data.level+"级");
                $(".userHave").html(data.data.exp);
                $(".userHave").css("width",data.data.exp + "%");
                $(".userNeed").html(100 - data.data.exp);
                $(".userNeed").css("width",100 - data.data.exp + "%");
                $(".userExp").removeClass("fade");
                var src =  data.data.headPictureSrc;
                var name = src.substr(src.lastIndexOf("/"));
                $(".headPortrait").attr("src","/file/img"+name);

            }
        }
    });


    verificationSignIn();
    postList(1);

}

function verificationSignIn(){
    $.ajax({
        url: '/api/post/verificationSignIn',
        dataType: 'json',
        type: 'get',
        data: 'moduleId='+$(".data").val(),
        success: function(data){
            if(data.status==2){
                $('.singleBaSignIn').prop('disabled', true); 
                $(".singleBaSignIn").html(data.msg);
            }
            
        }
    });
}

function postList(pageNumber,postName){
    $.ajax({
        url: '/api/post/postList',
        dataType: 'json',
        type: 'get',
        data: {"moduleId":$(".data").val(),"pageNumber":pageNumber,"postName":postName},
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
                "                             <div class=\"author-thumb\"><img style=\"width:35px; height:35px\" src=\""+ postInfo.list[i].accountVo.headPictureSrc +"\"/></div> <a href=\"/api/user/dispatcher/userInfo/"+ postInfo.list[i].accountVo.userId +"\" style=\"color:gray;\">"+ postInfo.list[i].accountVo.userName +"</a>    <a style=\"padding-left:50px;\" href='/api/post/postInfo/dispatcher/"+postInfo.list[i].postVo.postId+"'>"+postInfo.list[i].postVo.postName+"</a>"+
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
                                            "<ul class='info postInfo'>";
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