$(document).ready(function(){
    getSubscribeBa();
    getWeather();
    moduleRanking();
    getBaType($(".gengduo").attr("value"));
    getNewestPost(1);
});

$(".gengduo").click(function(){
    getBaType(Number($(this).attr("value")));
});

$(".management").on("click",".baOption",function(){
    $.ajax({
        url: '/api/post/delSubscribe',
        dataType: 'json',
        type: 'post',
        data: 'objectId='+$(this).attr("myBaId"),
        success: function(data){
            $(".managementMsg").html(data.msg);
            $(".management").empty();
            $(".subscribe").empty();
            getSubscribeBa();
        }
    });
});

$(".managementBaBtn").click(function(){
    $(".managementMsg").html("");
    $("#managementBa").modal("show");
});

$(".addSubscribeBa").click(function(){
    var moduleName = $(".baName").val();
    $(".baName").val("")
    $.ajax({
        url: '/api/post/addSubscribeModule',
        dataType: 'json',
        type: 'post',
        data: 'moduleName='+moduleName,
        success: function(data){
            if(data.status == 0){
                $(".management").empty();
                $(".subscribe").empty();
                getSubscribeBa();
                $(".managementMsg").css("color","black");
            }else{
                $(".managementMsg").css("color","orange");
            }
            $(".managementMsg").html(data.msg);
        }
    });
});

$(".signIn").click(function(){
    $.ajax({
        url: '/api/post/moduleSignIn',
        dataType: 'json',
        type: 'get',
        success: function(data){
            getuserInfo();
            $(".reminder>span").html(data.msg);
            $(".reminder").css("display","block");
            setTimeout(function(){
                $(".reminder").css("display","none");
             },6000);
        }
    });
});



$("#load-more-button").click(function(){

    
    getNewestPost($(this).attr("pageNumber"));
 
});


function getSubscribeBa(){
    $.ajax({
        url: '/api/post/getSubscribeModule',
        dataType: 'json',
        type: 'get',
        success: function(data){
            if(data.status == 0){
                for(var i = 0; i < data.data.length; i++){
                    var html = "<li class='list-group-item'><div class='container'><img class='img-responsive img-thumbnail display-inline-block' width='60px' heigth='60px' src=\""+ data.data[i].headPictureSrc +"\"/><div class='display-inline-block float-right'><span class='float-right'>"+data.data[i].moduleName+"</span><br/><span class='fudong float-right'>"+data.data[i].level+"级</span><br/><span class='fudong float-right'><a href='/api/post/moduleInfo/dispatcher/"+data.data[i].moduleId+"'>进入模块</a></span></div></div></li>";
                    $(".subscribe").append(html);
                    $(".management").append("<button type='button' class='btn btn-secondary btn-sm my-auto baOption' myBaId='"+data.data[i].moduleId+"'><span style='float: left;'>"+data.data[i].moduleName+"</span>&nbsp;&nbsp;<span>&times;</span></button>&nbsp;");
                }
                $(".subscribeUl").parent().removeClass("hidden");
                $(".login").addClass("hidden");
            }else if(data.status == 2){
                var html = "<li class='list-group-item'><span>"+data.msg+"</span></li>";
                $(".subscribe").append(html);
                $(".management").append("<p>"+data.msg+"</p>");
                $(".signIn").hide();
                $(".subscribeUl").parent().removeClass("hidden");
                $(".login").addClass("hidden");
            }
        }, error: function(e) {
            console.log(123)
        }
    });
}

function getBaType(pageNumber){
    $.ajax({
        url: '/api/post/moduleType',
        dataType: 'json',
        data:"pageNumber="+pageNumber,
        type: 'get',
        success: function(data){
            if(data.status == 0){
                for(var i = 0;i < data.data.list.length; i++){
                    var html = "<div class=\"card\"> "+
                    "                <div class=\"card-header\"> "+
                    "                     <a class=\"collapsed card-link\" data-toggle=\"collapse\" href=\"#type"+data.data.list[i].moduleParentType.id+"\">"+
                                                data.data.list[i].moduleParentType.typeName+
                                          "</a> "+
                                    " </div>"+
                                    "<div id=\"type"+data.data.list[i].moduleParentType.id+"\" class=\"collapse\" data-parent=\"#accordion\">"+
                                        "<div class=\"card-body\">";
                    for(var j = 0;j < data.data.list[i]. moduleSonTypeList.length; j++){
                        html += "<a href='/api/post/moduleList/"+data.data.list[i]. moduleSonTypeList[j].id+"/1' class='btn btn-secondary btn-sm'><font style='vertical-align: inherit;'><font style='ertical-align: inherit;'>"+data.data.list[i]. moduleSonTypeList[j].typeName+"</font></font></a>&nbsp;"
                    }
                    html += "           </div>"+
                    "               </div>"+
                    "           </div>";

                    $("#accordion").append(html);
                }
                if(!data.data.isLastPage){
                    $(".gengduo").attr("value", Number(data.data.pageNum)+1);
                }else{
                    $(".gengduo").hide();
                }
            }
        }
    });
}

function getWeather(){
    $.ajax({
        url: '/api/mail/weather',
        dataType: 'json',
        type: 'get',
        success: function(data){
            var weather = data.data;
            $(".temperature").html(weather.temperature+"°");
            $(".weather").html(weather.weather);
            $(".winddirection").html(weather.winddirection);
            $(".windpower").html(weather.windpower);
            $(".humidity").html(weather.humidity);
            for(var i = 0; i < weather.forecastWeathers.length; i++){
                var html = "<li><div class='day'>"+ weather.forecastWeathers[i].week +"</div><div class='day'>"+ weather.forecastWeathers[i].dayWeather +"</div><div class='day'>"+ weather.forecastWeathers[i].nightWeather +"</div><div class='day'>"+ weather.forecastWeathers[i].dayTemp +"°</div><div class='day'>"+ weather.forecastWeathers[i].nightTemp +"°</div></li>"
                $(".weekly-forecast").append(html);
            }
            $(".date").html(weather.city);
            $(".place").html("最后更新时间："+timestampToTime(weather.reporttime));
        }
    });
}



function moduleRanking(){
    $.ajax({
        url: '/api/post/moduleRanking',
        dataType: 'json',
        type: 'get',
        success: function(data){
            var moduleList = data.data.list;
            $(".firstModuleHeadPictureSrc").attr("src",moduleList[0].headPictureSrc);
            $(".firstModuleName").attr("href","/api/post/moduleInfo/dispatcher/"+moduleList[0].moduleId);
            $(".firstModuleName").html(moduleList[0].moduleName);
            $(".firstModuleIntroduce").html(moduleList[0].introduce);
            $(".firstModuleCountSubscribe").html("订阅数量："+moduleList[0].countSubscribe);
            $(".firstModuleCountPost").html("帖子数量："+moduleList[0].countPost);
            
            $(".secondModuleHeadPictureSrc").attr("src",moduleList[1].headPictureSrc);
            $(".secondModuleName").attr("href","/api/post/moduleInfo/dispatcher/"+moduleList[1].moduleId);
            $(".secondModuleName").html(moduleList[1].moduleName);
            $(".secondModuleIntroduce").html(moduleList[1].introduce);
            $(".secondModuleCountSubscribe").html("订阅数量："+moduleList[1].countSubscribe);
            $(".secondModuleCountPost").html("帖子数量："+moduleList[1].countPost);

            var sing = 1;
            var html ="";
            for(var i = 2; i < moduleList.length; i++){
                if(sing  == 6){
                    sing = 1;
                    html += "</div>";
                    $(".rankingList").append(html);
                    html = "";
                    i--;
                    continue;
                }else if(sing == 1){
                    if(i == 2){
                        html += "<div class=\"carousel-item active\">";
                    }else{
                        html += "<div class=\"carousel-item\">";
                    }
                    sing++;
                    i--;
                    continue;
                }else{
                    html += "<div class=\"display-inline-block border-radius-shadow\" style=\"width: 115px; margin-left:5px;\">"+
                    "			<div class=\"display-inline-block\">"+
                    "				<img class=\"img-responsive img-rounded display-inline-block\" width=\"40px\" height=\"40px\" src=\""+ moduleList[i].headPictureSrc +"\">"+
                    "			</div>"+
                    "			<div class=\"display-inline-block\">"+
                    "				<a href=\""+ "/api/post/moduleInfo/dispatcher/"+moduleList[i].moduleId +"\" style=\"padding-bottom: 10px;\">"+ moduleList[i].moduleName +"</a>"+
                    "				<br/>"+
                    "			    <a href=\"/api/post/moduleList/"+ moduleList[i].typeId +"/1\">"+ moduleList[i].typeName +"</a>"+
                    "			</div>"+
                    "		 </div>";
                    sing++;
                }
            }
            setInterval(function(){
                $(".right").trigger("click");
            },5000);
        }
    });
}

function getNewestPost(pageNumber){
    $.ajax({
        url: '/api/post/getNewestPost',
        dataType: 'json',
        data: {"pageNumber" : pageNumber},
        type: 'get',
        success: function(data){
            if(data.status != 0){
                return;
            }
            if(data.status != 0){
                $(".postBox").html("<li style='margin: 3px;' class=\"list-group-item postInfoBox text-center\">"+ data.msg +"</li>");
                return;
            }
            var postInfo = data.data;
            $(".postBox #view").remove();
            for(var i = 0; i < postInfo.list.length; i++){
                var html = "<a id=\"view\"></a><li style='margin: 3px;' class=\"list-group-item postInfoBox\">"+
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
                                            "<ul class='info postInfo list-style-none' style='float:right'>";
                if(postInfo.list[i].postVo.latestCommentDate != undefined){
                    html += "<li class='m-auto'><span>"+postInfo.list[i].postVo.latestCommentDate+"回复</span></li>";
                }
                    html += "<li class=\"m-auto\"><span>浏览次数："+postInfo.list[i].postVo.readCount+"</span></li>"+
                            "<li class=\"m-auto\"><span>发帖日期："+timestampToTime(postInfo.list[i].postVo.enrollDate)+"</span></li>"+
                        "</ul>";
                $(".postBox").append(html);
            }
            if(postInfo.isLastPage){
                $("#load-more-button").addClass("hidden");
            }else{
                $("#load-more-button").attr("pageNumber", postInfo.nextPage);
            }
            document.getElementById("view").scrollIntoView(false);
        }
    });
}














function timestampToTime(timestamp) {
    var date = new Date(timestamp);//时间戳为10位需*1000，时间戳为13位的话不需乘1000
    Y = date.getFullYear() + '-';
    M = (date.getMonth()+1 < 10 ? '0'+(date.getMonth()+1) : date.getMonth()+1) + '-';
    D = date.getDate() + ' ';
    h = date.getHours() + ':';
    m = (date.getMinutes() < 10 ? '0'+(date.getMinutes()) : date.getMinutes()) + ':';
    s = (date.getSeconds() < 10 ? '0'+(date.getSeconds()) : date.getSeconds());
    return Y+M+D+h+m+s;
    }
