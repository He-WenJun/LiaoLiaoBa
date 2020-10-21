$(document).ready(function(){
    getMyData();
});


$(".preserve").click(function(){
    if($(".userName").val() == "" || $(".describe").val() == ""){
        $(".reminderMsg").html("请补全资料");
        $("#reminderModal").modal("show");
        return;
    }

    if($(".backgroundPicture").attr("myValue") == undefined){
        $.ajax({
            url: "/api/user/updateUserInfo",
            type: "post",
            dataType:"json",
            data:{"userName": $(".userName").val(), "introduce":$(".describe").val(), "headPictureId": $(".headPortraits").attr("myValue")} ,
            success:function(data){
                console.log(JSON.stringify(data));
                $(".modal-body h4").html(data.msg);
                $("#reminderModal").removeClass("hidden");
                $("#reminderModal").modal("show");
            }
        });
    }else{
        $.ajax({
            url: "/api/user/updateUserInfo",
            type: "post",
            dataType:"json",
            data:  {"userName": $(".userName").val(),"headPictureId": $(".headPortraits").attr("myValue"), "backgroundPictureId": $(".backgroundPicture").attr("myValue"), "introduce":$(".describe").val()} ,
            success:function(data){
                console.log(JSON.stringify(data));
                $(".modal-body h4").html(data.msg);
                $("#reminderModal").removeClass("hidden");
                $("#reminderModal").modal("show");
            }
        });
    }
   
})
$(".uploadHeadPicture").change(function(){
    uploadImg($(this),$(".headPortraits"));
})
$(".uploadBackagePicture").change(function(){
    uploadImg($(this),$(".backgroundPicture"));
})



function getMyData(){
    $.ajax({
        url: "/api/user/userInfo",
        type: "get",
        dataType:"json",
        success:function(data){
            if(data.status != 0){
                $(".reminderMsg").html(data.msg);
                $("#reminderModal").modal("show");
                return;
            }

            var myData = data.data;
            $(".userName").val(myData.userName);
            $(".preserve").attr("userId",myData.userId);
            $(".headPortraits").attr("src", myData.headPictureSrc);
            $(".headPortraits").attr("myValue", myData.accountInfo.headPictureId);
            if(myData.backgroundPicture != null){
                $(".backgroundPicture").attr("src", myData.backgroundPicture);
                $(".backgroundPicture").attr("myValue", myData.accountInfo.backgroundPictureId);
            }
            $(".describe").val(myData.accountInfo.introduce);

        }
    });
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