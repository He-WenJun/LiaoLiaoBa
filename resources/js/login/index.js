$(function() {

   $(".input input").focus(function() {
      $(this).parent(".input").each(function() {
         $("label", this).css({
            "line-height": "18px",
            "font-size": "18px",
            "font-weight": "100",
            "top": "0px"
         })
         $(".spin", this).css({
            "width": "100%"
         })
      });
   }).blur(function() {
      $(".spin").css({
         "width": "0px"
      })
      if ($(this).val() == "") {
         $(this).parent(".input").each(function() {
            $("label", this).css({
               "line-height": "60px",
               "font-size": "24px",
               "font-weight": "300",
               "top": "10px"
            })
         });

      }
   });

   /*$(".button").click(function(e) {
      var pX = e.pageX,
         pY = e.pageY,
         oX = parseInt($(this).offset().left),
         oY = parseInt($(this).offset().top);

      $(this).append('<span class="click-efect x-' + oX + ' y-' + oY + '" style="margin-left:' + (pX - oX) + 'px;margin-top:' + (pY - oY) + 'px;"></span>')
      $('.x-' + oX + '.y-' + oY + '').animate({
         "width": "500px",
         "height": "500px",
         "top": "-250px",
         "left": "-250px",

      }, 600);
      $("button", this).addClass('active');
   })*/

   $(".login").click(function(e) {
      if($("#name").val() == "" || $("#pass").val() == "" || $("#verificationCode").val() == ""){
         $(".modal-body p").html("请补全信息");
         $("#myModal").modal('show');
         return;
      }
      
      $.ajax({
         url: "/api/user/login",
         type: "post",
         data: "accountNumber="+$("#name").val()+"&password="+$("#pass").val()+"&loinVerifyCode="+$("#verificationCode").val(),
         dataType:"json",
         success:function(data){
            if(data.status == 0){
               window.location.replace("/resources/index.html");
            }else if(data.status == 1){
               $("#verificationCodeImg").attr('src','/api/user/verificationCode?'+Math.random());
               $(".modal-body p").html(data.msg);
               $("#myModal").modal('show');
            }
         }
      })
   })

   
   $(".enroll").click(function(e) {
      //验证邮箱正则
      var re = /^(([^()[\]\\.,;:\s@\"]+(\.[^()[\]\\.,;:\s@\"]+)*)|(\".+\"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/;
      if($("#regname").val() == "" || $("#regname").val().length < 3){
         $(".modal-body p").html("用户名不能为空，且长度不能小于3个字符");
         $("#myModal").modal('show');
         return;
      }else if($("#regpass").val() == "" || $("#regpass").val().length < 6){
         $(".modal-body p").html("密码不能为空，且长度不能小于6个字符");
         $("#myModal").modal('show');
         return;
      }else if(!re.test($("#regmail").val())){
         $(".modal-body p").html("您输入的邮箱有误");
         $("#myModal").modal('show');
         return;
      }else if($("#regpass").val() != $("#reregpass").val()){
         $(".modal-body p").html("两次密码输入不一致");
         $("#myModal").modal('show');
         return;
      }

      $.ajax({
         url: "/api/user/enrollHold",
         type: "post",
         data: "userName="+$("#regname").val()+"&password="+$("#regpass").val()+"&email="+$("#regmail").val(),
         dataType:"json",
         success:function(data){
            alert(JSON.stringify(data));
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
      })
   })

   function enrollVerification (uname){   
      var t = setInterval(function(){
         $.ajax({
            url: "/api/user/enrollVerification",
            type: "get",
            data: "userName="+uname,
            dataType:"json",
            success:function(data){
               if(data.status == 0){
                  $(".modal-body p").html(data.msg);
                  $("#myModal").modal('show');
                  setTimeout(function(){
                     window.location.replace("/resources/index.html");
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
   
   function successfully (e) {
      var pX = e.pageX,
         pY = e.pageY,
         oX = parseInt($(this).offset().left),
         oY = parseInt($(this).offset().top);

      $(this).append('<span class="click-efect x-' + oX + ' y-' + oY + '" style="margin-left:' + (pX - oX) + 'px;margin-top:' + (pY - oY) + 'px;"></span>')
      $('.x-' + oX + '.y-' + oY + '').animate({
         "width": "500px",
         "height": "500px",
         "top": "-250px",
         "left": "-250px",

      }, 600);
      $("button", this).addClass('active');
   }
   //关闭模态窗
   $("#okbtn").click(function(){
      $("#myModal").modal('hide');
   });
   //更换验证码
   $("#verificationCodeImg").click(function(){
      alert("更换验证码");
      $(this).attr('src','/api/user/verificationCode?'+Math.random());
   });

   $(".alt-2").click(function() {
      if (!$(this).hasClass('material-button')) {
         $(".shape").css({
            "width": "100%",
            "height": "100%",
            "transform": "rotate(0deg)"
         })

         setTimeout(function() {
            $(".overbox").css({
               "overflow": "initial"
            })
         }, 600)

         $(this).animate({
            "width": "140px",
            "height": "140px"
         }, 500, function() {
            $(".box").removeClass("back");

            $(this).removeClass('active')
         });

         $(".overbox .title").fadeOut(300);
         $(".overbox .input").fadeOut(300);
         $(".overbox .button").fadeOut(300);

         $(".alt-2").addClass('material-buton');
      }

   })

   $(".material-button").click(function() {

      if ($(this).hasClass('material-button')) {
         setTimeout(function() {
            $(".overbox").css({
               "overflow": "hidden"
            })
            $(".box").addClass("back");
         }, 200)
         $(this).addClass('active').animate({
            "width": "700px",
            "height": "700px"
         });

         setTimeout(function() {
            $(".shape").css({
               "width": "50%",
               "height": "50%",
               "transform": "rotate(45deg)"
            })

            $(".overbox .title").fadeIn(300);
            $(".overbox .input").fadeIn(300);
            $(".overbox .button").fadeIn(300);
         }, 700)

         $(this).removeClass('material-button');

      }

      if ($(".alt-2").hasClass('material-buton')) {
         $(".alt-2").removeClass('material-buton');
         $(".alt-2").addClass('material-button');
      }

   });

});