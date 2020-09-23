$(document).ready(function(){
    if(/*[${serverResponse.status}]*/ 0 == 0){
        window.location.replace("http://liaoliaoba.com/resources/index.html");
    }else{
        $(".content").show();
    }
});

$(".submit").click(function(){
    if($(".headPortrait").parent().attr("imageId") == ""){
        let url = $(".headPortrait").attr("src");
        var xhr = new XMLHttpRequest();
        xhr.open('GET', url);
        xhr.responseType = 'blob';
        xhr.onload = function() {
            var content = xhr.response;
            var blob = new Blob([content]);//blob.type=''
            var file2 = new File([blob], '支付宝用户头像2.png', {type: 'image/png'});
            let formData = new FormData();
            formData.append('crowd_file',file2 );
            $.ajax({
                url:'http://liaoliaoba.com/api/post/uploadImg',
                dataType:'json',
                type:'POST',
                async: false,
                data: formData,
                contentType : false, // 不要设置Content-Type请求头
                processData : false, // 使数据不做处理
                success: function(data){
                    enroll(data.data.id);
                }
            });          
        };
        xhr.send();
    }else{
        enroll($(".headPortrait").parent().attr("imageId"));
    }
    
});
function enrollVerification (uname){   
    var t = setInterval(function(){
       $.ajax({
          url: "http://liaoliaoba.com/api/user/enrollVerification",
          type: "get",
          data: "userName="+uname,
          dataType:"json",
          success:function(data){
             if(data.status == 0){
                $(".modal-body p").html(data.msg);
                $("#myModal").modal('show');
                setTimeout(function(){
                   window.location.replace("http://liaoliaoba.com/resources/index.html");
                },3000);
             }else if(data.status == 1){
                $(".modal-body p").html(data.msg);
                $("#myModal").modal('show');
             }else if(data.status == 2){
                $(".modal-body p").html(data.msg);
                $("#myModal").modal('show');
                clearInterval(t);
             }
          }
       });
    },1000);
 };

$(".crowd_file").change(function(){
    upload();        
});
function upload(){
    var crowd_file = $('.crowd_file')[0].files[0];
    var formData = new FormData();
    formData.append("crowd_file",$('.crowd_file')[0].files[0]);
    $.ajax({
        url:'/api/post/uploadImg',
        dataType:'json',
        type:'POST',
        async: false,
        data: formData,
        contentType : false, // 不要设置Content-Type请求头
        processData : false, // 使数据不做处理
        success: function(data){
            $(".headPortrait").attr("src",data.data.src);
            $(".headPortrait").parent().attr("imageId",data.data.id);
        }
    });
}
function enroll(imgId){
    //提交注册请求
    $.ajax({
        url: "http://liaoliaoba.com/api/user/zhiFuBaoEnrollHold",
        type: "post",
        data: "a.userId="+$(".userId").val()+"&a.userName="+$(".userName").val()+"&a.password="+$(".password").val()+"&a.email="+$(".mail").val()+"&a_f.headPictureId="+imgId,
        dataType:"json",
        success: function(data){
            if(data.status == 0){
                $(".modal-body p").html(data.msg);
                $("#okbtn").addClass("enrollVerification");
                $("#okbtn").attr("uname",data.data);
                $("#myModal").modal('show');
                setTimeout(function(){
                enrollVerification(data.data);
                },3000);
            }else if(data.status == 1){
                $(".modal-body p").html(data.msg);
                $("#myModal").modal('show');
            }
        }
    });
}