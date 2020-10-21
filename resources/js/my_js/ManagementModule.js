$(document).ready(function(){
    getManagementModule();
})
var moduleId
$(".myManagementModule").on("click","button",function(){
    moduleId = $(this).attr("myValue");
    getBlackUserList($(this).attr("myValue"),1);
    getPostList(moduleId, 1);
    moduleInit(moduleInfo[$(this).attr("myIndex")]);
})

$(".blackUserList").on("click",".delBlackUser",function(){
    delBlackUser(moduleId,$(this).attr("myValue"));
    getBlackUserList(moduleId,$(".pageBox .active>button").attr("myPageNumber"));
})
var deletePostInfo;
$(".postList").on("click", ".deletePostBtn", function(){
    deletePostInfo = $(this);
    $("#deletePostModule").removeClass("hidden");
    $("#deletePostModule").modal('show');
})
$("#deleteReasonBtn").click(function(){
    $.ajax({
        url: '/api/post/moduleAdmin/deletePost',
        data: {"moduleId": deletePostInfo.attr("myValue"), "postId": deletePostInfo.attr("myValue"),"userId": deletePostInfo.attr("myUserId"),"deleteReason": $(".deleteReason").val(), "postName": deletePostInfo.attr("myName") },
        dataType: 'json',
        type: 'post',
        success: function(data){
            getPostList(moduleId,1, $(".searchPostName").val());
        }
    });
    $("#deletePostModule").modal("hide");
});
$(".pageBox").on("click","button",function(){
    $(".pageBox").empty();
    getBlackUserList(moduleId,$(this).attr("myPageNumber"));
})
$(".postPageBox").on("click","button",function(){
    $(".postPageBox").empty();
    getPostList(moduleInfo.moduleId,$(this).attr("myPageNumber"));
})
$(".addBlackUser").click(function(){
    if($(".userName").val() == "" || $(".describe").val() == ""){
        $("#reminderModal h4").html("请补全信息");
        $("#reminderModal").removeClass("hidden");
        $("#reminderModal").modal('show');
        return;
    }
    $.ajax({
        url: '/api/post/moduleAdmin/addModuleBlackUser',
        data: {"moduleId": moduleId, "describe": $(".describe").val(), "userName":$(".userName").val() },
        dataType: 'json',
        type: 'post',
        success: function(data){
            $("#reminderModal h4").html(data.msg);
            $("#reminderModal").removeClass("hidden");
            $("#reminderModal").modal('show');
            getBlackUserList(moduleId,1);
            $(".userName").val("");
            $(".describe").val("");
        }
    });
})
$(".searchUserBtn").click(function(){
    if($(".searchUserName").val() == ""){
        getBlackUserList(moduleId, 1);
        return;
    }
    getBlackUserList(moduleId, 1, $(".searchUserName").val());
})
$(".searchPostBtn").click(function(){
    if($(".searchPostName").val() == ""){
        getPostList(moduleId, 1);
        return;
    }
    getPostList(moduleId, 1, $(".searchPostName").val());
});
$(".preserve").click(function(){
    if($(".moduleName").val() =="" || $(".moduleName").val().length < 3){
        $("#reminderModal h4").html("请输入模块名称，且长度不能小于3个字符");
        $("#reminderModal").removeClass("hidden");
        $("#reminderModal").modal('show');
        return;
    }else if($(".introduce").val() == "" || $(".introduce").val().length >= 100){
        $("#reminderModal h4").html("请输入模块介绍，且长度不能大于100个字符");
        $("#reminderModal").removeClass("hidden");
        $("#reminderModal").modal('show');
        return;
    }
    $.ajax({
        url: '/api/post/moduleAdmin/updateModuleInfo',
        data: {"moduleId": moduleId, "moduleName": $(".moduleName").val(),"introduce":$(".introduce").val(), "headPictureId":$(".headPortraits").attr("myValue"),"backgroundPictureId": $(".backPortraits").attr("myValue")},
        dataType: 'json',
        type: 'post',
        success: function(data){
            getManagementModule();
            $("#reminderModal h4").html(data.msg);
            $("#reminderModal").removeClass("hidden");
            $("#reminderModal").modal('show');
        }
    });
})

var moduleInfo
$(".uploadHeadPicture").change(function(){
    uploadImg($(this),$(".headPortraits"));
})
$(".uploadBackagePicture").change(function(){
    uploadImg($(this),$(".backPortraits"));
})

function getManagementModule(){
    $.ajax({
        url: '/api/post/getMyManagementModule',
        dataType: 'json',
        type: 'get',
        success: function(data){
            if(data.status != 0){
                return;
            }
            var managementModule = data.data;
            moduleInfo = managementModule;
            $(".myManagementModule").empty();
            for(var i = 0; i < managementModule.length; i++){
                $(".myManagementModule").append("<li><button myIndex=\""+ i +"\" class=\"btn btn-secondary  btn-sm full-width\" myValue=\""+managementModule[i]. moduleId +"\">"+ managementModule[i].moduleName +"</button></li>");    
            }
            if(moduleId == null){
                $(".myManagementModule button:eq(0)").trigger("click");
            }
        }
    });
}

function getBlackUserList(moduleId,pageNumber,serchUserName){
    $.ajax({
        url: '/api/post/moduleAdmin/queryModuleBlackUserList',
        data: {"moduleId": moduleId, "pageNumber": pageNumber,"userName":serchUserName},
        dataType: 'json',
        type: 'post',
        success: function(data){
            $(".blackUserList").empty();
            $(".pageBox").empty();
            var html = "            <li class=\"list-group-item\" style=\"border-bottom: white;\">"+
            "							<div class=\"row\">"+
            "								<div class=\"col col-lg-2 col-md-2 col-sm-2 col-2\">"+
            "									用户名"+
            "								</div>"+
            "								<div class=\"col col-lg-3 col-md-3 col-sm-3 col-3\">"+
            "									时间"+
            "								</div>"+
            "								<div class=\"col col-lg-4 col-md-4 col-sm-4 col-4\">"+
            "									加入黑名单原因"+
            "								</div>"+
            "								<div class=\"col col-lg-3 col-md-3 col-sm-3 col-3\">"+
            "									操作"+
            "								</div>"+
            "							</div>"+
            "						</li>";
            
            if(data.status != 0){
                html += "<li class=\"list-group-item text-center\"><span>"+ data.msg +"</span><li>"
                $(".blackUserList").append(html);
                return;
            }
            var blackUserList = data.data.list;
            
            for(var i = 0; i < blackUserList.length; i++){
                html +="                <li class=\"list-group-item\">"+
                "							<div class=\"row\">"+
                "								<div class=\"col col-lg-2 col-md-2 col-sm-2 col-2 my-auto\">"+
                "									<a href=\"/api/user/dispatcher/userInfo/"+ blackUserList[i].account.userId +"\">"+ blackUserList[i].account.userName +"</a>"+
                "								</div>"+
                "								<div class=\"col col-lg-3 col-md-3 col-sm-3 col-3 my-auto\">"+
                "									"+ timestampToTime(blackUserList[i].moduleBlackUser.enrollDate)+
                "								</div>"+
                "								<div class=\"col col-lg-4 col-md-4 col-sm-4 col-4 my-auto\">"+
                "									"+blackUserList[i].moduleBlackUser.describe+
                "								</div>"+
                "								<div class=\"col col-lg-3 col-md-3 col-sm-3 col-3 my-auto\">"+
                "									<button class=\"btn btn-primary btn-sm full-width delBlackUser\" myValue=\""+ blackUserList[i].moduleBlackUser.userId +"\" style=\"width: 70px;\">删除</button>"+
                "								</div>"+
                "							</div>"+
                "						</li>";
            }
            $(".blackUserList").append(html);
            var pageInfo = data.data;
            var pageHtml = "";
            pageHtml += "<li class=\"page-item\"><button class=\"page-link\" myPageNumber='"+pageInfo.prePage+"'>上一页</button></li>";
            for(var i = 0; i < pageInfo.navigatepageNums.length; i++){
                if(pageInfo.navigatepageNums[i] == pageInfo.pageNum){
                    pageHtml  +=  "<li class=\"page-item active\"><button class=\"page-link\" myPageNumber='"+pageInfo.navigatepageNums[i]+"'>"+pageInfo.navigatepageNums[i]+"</button></li>";
                    continue;
                }
                pageHtml += "<li class=\"page-item\"><button class=\"page-link\" myPageNumber='"+pageInfo.navigatepageNums[i]+"'>"+pageInfo.navigatepageNums[i]+"</button></li>";
            }
            pageHtml += "<li class=\"page-item\"><button class=\"page-link\" myPageNumber='"+pageInfo.nextPage+"'>下一页</button></li>"; 

            $(".pageBox").append(pageHtml);

        },error: function(e){
            alert(JSON.stringify(e));
        }
    });
}

function getPostList(moduleId,pageNumber,postName){
    $.ajax({
        url: '/api/post/postList',
        data: {"moduleId": moduleId, "pageNumber": pageNumber,"postName":postName},
        dataType: 'json',
        type: 'get',
        success: function(data){
            $(".postList").empty();
            $(".postPageBox").empty();
            var html = "          <li class=\"list-group-item\" style=\"border-bottom: white;\">      <div class=\"row text-center\">"+
            "								<div class=\"col col-lg-2 col-md-2 col-sm-2 col-2\">"+
            "									发帖人"+
            "								</div>"+
            "								<div class=\"col col-lg-3 col-md-3 col-sm-3 col-3\">"+
            "									发帖时间"+
            "								</div>"+
            "								<div class=\"col col-lg-4 col-md-4 col-sm-4 col-4\">"+
            "									帖子标题"+
            "								</div>"+
            "								<div class=\"col col-lg-3 col-md-3 col-sm-3 col-3\">"+
            "									操作"+
            "								</div>"+
            "							</div></li>";
            
            if(data.status != 0){
                html += "<li class=\"list-group-item text-center\"><span>"+ data.msg +"</span><li>"
                $(".postList").append(html);
                return;
            }

            var postList = data.data.list;
            
            for(var i = 0; i < postList.length; i++){
                html +="                <li class=\"list-group-item\">"+
                "							<div class=\"row\">"+
                "								<div class=\"col col-lg-2 col-md-2 col-sm-2 col-2 my-auto\">"+
                "									<a href=\"/api/user/dispatcher/userInfo/"+ postList[i].postVo.userId +"\">"+ postList[i].accountVo.userName +"</a>"+
                "								</div>"+
                "								<div class=\"col col-lg-3 col-md-3 col-sm-3 col-3 my-auto\">"+
                "									"+ timestampToTime(postList[i].postVo.enrollDate)+
                "								</div>"+
                "								<div class=\"col col-lg-4 col-md-4 col-sm-4 col-4 my-auto\">"+
                "									<a href=\"/api/post/postInfo/dispatcher/"+postList[i].postVo.postId+"\">"+ postList[i].postVo.postName +"</a>"+
                "								</div>"+
                "								<div class=\"col col-lg-3 col-md-3 col-sm-3 col-3 my-auto\">"+
                "									<button class=\"btn btn-primary btn-sm full-width deletePostBtn\" myUserId = \""+ postList[i].postVo.userId +"\" myValue=\""+ postList[i].postVo.postId +"\" myName=\""+ postList[i].postVo.postName +"\" style=\"width: 70px;\">删除</button>"+
                "								</div>"+
                "							</div>"+
                "						</li>";
            }
            $(".postList").append(html);
            var pageInfo = data.data;
            var pageHtml = "";
            pageHtml += "<li class=\"page-item\"><button class=\"page-link\" myPageNumber='"+pageInfo.prePage+"'>上一页</button></li>";
            for(var i = 0; i < pageInfo.navigatepageNums.length; i++){
                if(pageInfo.navigatepageNums[i] == pageInfo.pageNum){
                    pageHtml  +=  "<li class=\"page-item active\"><button class=\"page-link\" myPageNumber='"+pageInfo.navigatepageNums[i]+"'>"+pageInfo.navigatepageNums[i]+"</button></li>";
                    continue;
                }
                pageHtml += "<li class=\"page-item\"><button class=\"page-link\" myPageNumber='"+pageInfo.navigatepageNums[i]+"'>"+pageInfo.navigatepageNums[i]+"</button></li>";
            }
            pageHtml += "<li class=\"page-item\"><button class=\"page-link\" myPageNumber='"+pageInfo.nextPage+"'>下一页</button></li>"; 

            $(".postPageBox").append(pageHtml);

        }
    });
}

function delBlackUser(moduleId,userId){
    $.ajax({
        url: '/api/post/moduleAdmin/deleteModuleBlackUser',
        data: {"moduleId": moduleId,"userId": userId},
        dataType: 'json',
        type: 'post'
    });
}

function moduleInit(moduleInfo){
    console.log(moduleInfo);
    $(".headPortraits").attr("src",moduleInfo.headPictureSrc);
    $(".headPortraits").attr("myValue",moduleInfo.headPictureId);
    if(moduleInfo.moduleBackgroundPictureSrc != null){
        $(".backPortraits").attr("src",moduleInfo.moduleBackgroundPictureSrc);
        $(".backPortraits").attr("myValue",moduleInfo.moduleBackgroundPictureId);
    }else{
        $(".backPortraits").attr("src","/resources/myImg/wu.jpg");
    }
    $(".moduleName").val(moduleInfo.moduleName);
    $(".introduce").html(moduleInfo.introduce);
}

function uploadImg(file,img){
    let formData = new FormData();
    formData.append('crowd_file',file[0].files[0]);
    $.ajax({
        type: "post",
        url: "/api/post/uploadImg",
        dataType:'json',
        data: formData,
        processData : false, // 使数据不做处理
        contentType : false, // 不要设置Content-Type请求头
        success: function(data) {
            if(data.status != 0){
                $("#reminderModal h4").html(data.msg);
                $("#reminderModal").removeClass("hidden");
                $("#reminderModal").modal('show');
                return;
            }
            $(img).attr("myValue",data.data.id);
            $(img).attr("src",data.data.src);
        }
    });
}

function timestampToTime(timestamp) {
    var date = new Date(timestamp);//时间戳为10位需*1000，时间戳为13位的话不需乘1000
    Y = date.getFullYear() + '-';
    M = (date.getMonth()+1 < 10 ? '0'+(date.getMonth()+1) : date.getMonth()+1) + '-';
    D = date.getDate() + ' ';
    h = date.getHours();
    m = ":"+(date.getMinutes() < 10 ? '0'+(date.getMinutes()) : date.getMinutes());
    s = ":"+(date.getSeconds() < 10 ? '0'+(date.getSeconds()) : date.getSeconds());
    return Y+M+D;
}