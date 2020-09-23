$(document).ready(function(){
    if($("#serverResponse").val() != undefined){
        postList(JSON.parse($("#serverResponse").val()));
    }
});

function postList(data){
    if(data.status != 0){
        $(".postBox").html("<li style='margin: 3px;' class=\"list-group-item postInfoBox text-center\">"+ data.msg +"</li>");
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




$(".searchBtn").click(function(){
    if($(".searchValue").val() != ""){
        window.location.replace("/api/post/search/"+$(".searchValue").val()+"/1");
    }else if(($(".searchValue2").val() != "")){
        window.location.replace("/api/post/search/"+$(".searchValue2").val()+"/1");
    }
});