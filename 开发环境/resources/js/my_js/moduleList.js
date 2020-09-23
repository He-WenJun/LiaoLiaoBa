$(document).ready(function(){
    init(JSON.parse($(".data").val()));
});
function init(data){
    re = new RegExp("/[0-9]*$","g");
    var Newstr =window.location.pathname.replace(re, "");
    $.ajax({
        url: Newstr,
        dataType: 'json',
        type: 'get',
        success: function(data){
            $(".title").html(data.data.moduleParentType.typeName);
            for(var i = 0; i < data.data.moduleSonTypeList.length; i++){
                $(".baTypeBox").append("<a type='button ' href='/api/post/moduleList/"+data.data.moduleSonTypeList[i].id+"/1' class='btn btn-secondary btn-sm typeButton' >"+data.data.moduleSonTypeList[i].typeName+"</a>&nbsp;");
            }
        }
    });

    if(data.status != 0){
        $(".pagination").append("<h4>"+data.msg+"</h4>");
        return;
    }
    var moduelList = data.data.list;
    for(var i = 0; i < moduelList.length; i++){
        if(moduelList[i].countSubscribe == undefined){
            moduelList[i].countSubscribe = 0;
        }
        if(moduelList[i].countPost == undefined){
            moduelList[i].countPost = 0;
        }
        if(moduelList[i].introduce == undefined){
            moduelList[i].introduce ="";
        }
        var html = "<div class=\'subscribeBaBox\'>"+
                    "      <img class=\'img-rounded img-thumbnail\' width='70px' height='70px' src='"+moduelList[i].headPictureSrc+"' />"+
                    "       <ul class=\"infoUl\">"+
                    "             <li><span class=\'fudong\'>"+moduelList[i].moduleName+"</span></li>"+
                    "             <li><span class=\'fudong\'>订阅数量："+moduelList[i].countSubscribe+"</span></li>"+
                    "             <li><span class=\'fudong\'>帖子数量："+moduelList[i].countPost+"</span></li>"+
                    "              <li><a href='/api/post/moduleInfo/dispatcher/"+moduelList[i].moduleId+"'>进入贴吧</a></li>"+
                    "        </ul>"+
                    "        <hr/>"+
                    "        <p class='introduce'>"+moduelList[i].introduce+"</p>"+
                    "</div>";
        $(".subscribe").append(html);
    }
    $(".count").html("共有"+data.data.total+"个模块");
    
    var pages = 0;
    if(data.data.pageNum+5 < data.data.pages){
        pages = data.data.pageNum+5; 
    }else{
        pages = data.data.pages - data.data.pageNum;
    }

    var pageHtml = "";
    for(var i = 0; i < data.data.navigatepageNums.length; i++){
        if(data.data.navigatepageNums[i] == data.data.pageNum){
            pageHtml  +=  "<li class=\"page-item active\"><a class=\"page-link\" href="+Newstr+"/"+data.data.navigatepageNums[i]+">"+data.data.navigatepageNums[i]+"</a></li>";
            continue;
        }
        pageHtml += "<li class=\"page-item\"><a class=\"page-link\" href="+Newstr+"/"+data.data.navigatepageNums[i]+">"+data.data.navigatepageNums[i]+"</a></li>";
    }
    pageHtml += "<li class=\"page-item\"><a class=\"page-link\" href="+Newstr+"/"+data.data.prePage+">上一页</a></li>";
    pageHtml += "<li class=\"page-item\"><a class=\"page-link\" href="+Newstr+"/"+data.data.nextPage+">下一页</a></li>"; 
  
    $(".pagination").append(pageHtml);
}