$(document).ready(function(){
    getBaType(1);
});

$(".preserve").click(function(){
    if($(".moduleName").val() == "" || $(".headPortraits").attr("myValue") == undefined || $(".introduce").val() == "" || $(".nowModuleType").attr("mySonModuleId") == undefined){
        $("#reminderModal h4").html("请补全信息");
        $("#reminderModal").removeClass("hidden");
        $("#reminderModal").modal('show');
        return;
    }
    
    $.ajax({
        url: '/api/post/mkdirModule',
        dataType: 'json',
        data:{"moduleName": $(".moduleName").val(), "headPictureId": $(".headPortraits").attr("myValue"), "introduce": $(".introduce").val(), "typeId" : $(".nowModuleType").attr("mySonModuleId")},
        type: 'post',
        success: function(data){
            $("#reminderModal h4").html(data.msg);
            $("#reminderModal").removeClass("hidden");
            $("#reminderModal").modal('show');
        }
    });
});

$("#accordion").on("click", ".moduleSonBtn", function(){
    $(".moduleSonBtn").addClass("btn-secondary");
    $(".moduleSonBtn").removeClass("btn-primary");
    $(this).removeClass("btn-secondary");
    $(this).addClass("btn-primary");
    $(".nowModuleType").html("当前模块类型："+$(this).html());
    $(".nowModuleType").attr("mySonModuleId",$(this).attr("moduleSonId"));
    $(".nowModuleType").attr("myParentModuleId",$(this).attr("myParentModuleId"));
})

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
                        html += "<button myParentModuleId=\""+ data.data.list[i].moduleParentType.id +"\" moduleSonId=\""+ data.data.list[i]. moduleSonTypeList[j].id +"\" class='moduleSonBtn btn btn-secondary btn-sm'><font style='vertical-align: inherit;'><font style='ertical-align: inherit;'>"+data.data.list[i]. moduleSonTypeList[j].typeName+"</font></font></button>&nbsp;"
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

$(".gengduo").click(function(){
    getBaType(Number($(this).attr("value")));
});

$(".uploadHeadPicture").change(function(){
    uploadImg($(this),$(".headPortraits"));
})

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