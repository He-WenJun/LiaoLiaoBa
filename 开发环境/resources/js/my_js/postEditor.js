$(document).ready(function(){
    tinymce.init({
        selector: '#mytextarea',
        plugins: 'print autolink directionality visualblocks visualchars fullscreen image link table charmap hr pagebreak nonbreaking insertdatetime advlist lists',
        toolbar: 'code undo redo restoredraft | cut copy paste pastetext | forecolor backcolor bold italic underline strikethrough link table | alignleft aligncenter alignright alignjustify outdent indent | \
        styleselect formatselect fontsizeselect | bullist numlist | blockquote subscript superscript removeformat | image',
        min_height: 500,
        max_height: 600,
        language: "zh_CN",
        convert_urls: false,
        images_upload_handler: function (blobInfo, succFun, failFun) {
            let formData = new FormData();
            formData.append('crowd_file',blobInfo.blob());
            $.ajax({
                type: "post",
                url: "/api/post/uploadImg",
                dataType:'json',
                data: formData,
                processData : false, // 使数据不做处理
                contentType : false, // 不要设置Content-Type请求头
                success: function(data) {
                    alert(JSON.stringify(data));
                    if(data.status != 0){
                        failFun('Error Status: ' + data.status);
                        return;
                    }
                    if (!data || typeof data.data.src != 'string') {
                        failFun('Invalid JSON: ' + data.data.src);
                        return;
                    }
                    succFun(data.data.src);
                }
            });
        }
    });
    $("[myName='发表帖子']").trigger('click');
})

$("#commitPost").click(function(){
    if($(this).attr("myValue") == 1){
        if($(".postOrModule option:selected").attr("moduleId") != undefined){
            $.ajax({
                url: '/api/post/commitPost',
                dataType: 'json',
                type: 'post',
                data: {"moduleId": $(".postOrModule option:selected").attr("moduleId"),"postName": $(".postName").val(),"postContent":tinyMCE.activeEditor.getContent()},
                success: function(data){
                    if(data.status!=0){
                        $(".modal-body h4").html(data.msg);
                        $(".modal").removeClass("hidden");
                        $(".modal").modal('show');
                        return;
                    }
                    $(".reminder>span").html(data.msg);
                    $(".reminder").css("display","block");
                    setTimeout(function(){
                        $(".reminder").css("display","none");
                    },6000);
                }
            });
        }
    }else if($(this).attr("myValue") == 2){
        if($(".postOrModule option:selected").attr("postId") != undefined){
            $.ajax({
                url: '/api/post/updatePost',
                dataType: 'json',
                type: 'post',
                data: {"postId": $(".postOrModule option:selected").attr("postId"), "postName": $(".postName").val(), "postContent": tinyMCE.activeEditor.getContent()},
                success: function(data){     
                    $(".modal-body h4").html(data.msg);
                    $(".modal").removeClass("hidden");
                    $(".modal").modal('show');
                }
            });
        }
    }
});
$("input[type='radio']").click(function(){
    $(".title").html($(this).attr("myName"));
    tinyMCE.activeEditor.setContent("");
    $(".postName").val("");
    if($(this).attr("myValue") == 1){
        $("#commitPost").html("发表帖子");
        $("#commitPost").attr("myValue","1");
        $(".postOrModule").empty();
        $(".postOrModule").append("<option>请选择发送到的模块</option>");
        $.ajax({
            url: '/api/post/getSubscribeModule',
            dataType: 'json',
            type: 'get',
            success: function(data){
                if(data.status != 0){
                    $(".modal-body h4").html(data.msg);
                    $(".modal").removeClass("hidden");
                    $(".modal").modal('show');
                    return;
                }
                var modules = data.data;
                for(var i= 0; i < modules.length; i++){
                    $(".postOrModule").append("<option moduleId='"+ modules[i].moduleId +"'>"+ modules[i].moduleName +"</option>");
                }

            }
        });
    }else if($(this).attr("myValue") == 2){
        $("#commitPost").html("保存修改");
        $("#commitPost").attr("myValue","2");
        $(".postOrModule").empty();
        $(".postOrModule").append("<option>请选择要修改的帖子</option>");
        $.ajax({
            type: "get",
            url: "/api/post/getMyPostName",
            dataType:'json',
            success: function(data) {
                if(data.status != 0){
                    $(".modal-body h4").html(data.msg);
                    $(".modal").removeClass("hidden");
                    $(".modal").modal('show');
                    return;
                }
                var postNames = data.data;
                for(var i= 0; i < postNames.length; i++){
                    $(".postOrModule").append("<option postId='"+ postNames[i].postId +"'>"+ postNames[i].postName +"</option>");
                }
            }
        });
    }
})

$(".postOrModule").on("change",function(){
    if($("#commitPost").attr("myValue") == 1){
        $(".postOrModule option:selected").attr("moduleId");
    }else if($("#commitPost").attr("myValue") == 2){
        if($(".postOrModule option:selected").attr("postId") != undefined){
            $.ajax({
                url: "/api/post/postInfo",
                dataType: "json",
                type: "get",
                data: "postId=" + $(".postOrModule option:selected").attr("postId"),
                success: function(data){
                    alert(JSON.stringify(data));
                    if(data.status != 0){
                        $(".modal-body h4").html(data.msg);
                        $(".modal").removeClass("hidden");
                        $(".modal").modal('show');
                        return;
                    }
                    var postInfo = data.data;
                    tinyMCE.activeEditor.setContent(postInfo.postVo.content)
                    $(".postName").val(postInfo.postVo.postName);
                }
            });
        }


    }
});



$("#commitPost1").click(function(){
    alert($(".data").val());
    var t = setInterval(function(){
        var uploadId = getUploadId();
         $.ajax({
            url: '/api/post/commitPost',
            dataType: 'json',
            type: 'post',
            data: "baId="+$(".data").val()+"&postName="+$(".postName").val()+"&content="+$(".content").val()+"&uploadIds="+uploadId,
            success: function(data){
                updateInit();
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
    },300); 
});