$(document).ready(function(){
    getuserInfo();
    getMuen();
});

function navInit(data){
    var strVar="";
    strVar += "<div class=\"fixed-sidebar-left sidebar--small\" id=\"sidebar-left\">";
    strVar += "		<a class=\"logo\">";
    strVar += "			<div class=\"img-wrap\">";
    strVar += "				<img src=\"/resources/img/logo.png\" alt=\"Olympus\">";
    strVar += "			<\/div>";
    strVar += "		<\/a>";
    strVar += "		<!--缩入的左菜单-->";
    strVar += "		<div class=\"mCustomScrollbar data-mcs-theme=\"dark\">";
    strVar += "			<ul class=\"left-menu\">";
    strVar += "				<li>";
    strVar += "					<a href=\"#\" class=\"js-sidebar-open nav-min\">";
    strVar += "						<svg class=\"olymp-menu-icon left-menu-icon\"  data-toggle=\"tooltip\" data-placement=\"right\"   data-original-title=\"打开菜单\"><use xlink:href=\"/resources/svg-icons\/sprites\/icons.svg#olymp-menu-icon\"><\/use><\/svg>";
    strVar += "					<\/a>";
    strVar += "				<\/li>";  
    for(var i = 0; i < data.length; i++){
        strVar += "				<li>";
        if(data[i].menuSonsList == ""){
            strVar += "					<a href=\""+ data[i].menuParent.resource +"\">";
            strVar += "						<svg class=\"olymp-menu-icon left-menu-icon\"  data-toggle=\"tooltip\" data-placement=\"right\"   data-original-title=\""+ data[i].menuParent.name +"\"><use xlink:href=\"/resources/svg-icons\/sprites\/icons.svg#"+ data[i].menuParent.icon +"\"><\/use><\/svg>";
            strVar += "					<\/a><p><br/></p>";
        }else{
            strVar += "               <button style='padding: 14px 0px 14px 25px' type=\"button\" class=\"btn popover-options\"><a style='padding: 0px 0px 25px 0px' title=\""+ data[i].menuParent.name +"\" data-container=\"body\" data-toggle=\"popover\" data-placement=\"right\" data-content=\"";
            strVar += "<ul>";
            for(var j = 0; j < data[i].menuSonsList.length; j++){
                strVar += "<li><a href='"+data[i].menuSonsList[j].resource+"'>"+ data[i].menuSonsList[j].name +"</a></li>";
            }
            strVar +="<ul>\"><svg class=\"olymp-newsfeed-icon left-menu-icon\" data-toggle=\"tooltip\" data-placement=\"right\"   data-original-title=\""+ data[i].menuParent.name +"\"><use xlink:href=\"/resources/svg-icons\/sprites\/icons.svg#"+ data[i].menuParent.icon +"\"><\/use><\/svg></a></button>";
        }
        strVar += "				<\/li>";   
    }
    strVar += "			<\/ul><div class=\"ps__scrollbar-x-rail\" style=\"width: 70px; left: 0px; bottom: 0px;\"><div class=\"ps__scrollbar-x\" tabindex=\"0\" style=\"left: 0px; width: 64px;\"></div></div><div class=\"ps__scrollbar-y-rail\" style=\"top: 0px; right: 0px;\"><div class=\"ps__scrollbar-y\" tabindex=\"0\" style=\"top: 0px; height: 0px;\"></div></div>";
    strVar += "		<\/div>";
    strVar += "	<\/div>";

    strVar += "	<div class=\"fixed-sidebar-left sidebar--large\" id=\"sidebar-left-1\">";
    strVar += "		<a class=\"logo\">";
    strVar += "			<div class=\"img-wrap\">";
    strVar += "				<img src=\"/resources/img/logo.png\" alt=\"Olympus\">";
    strVar += "			<\/div>";
    strVar += "			<div class=\"title-block\">";
    strVar += "			<\/div>";
    strVar += "		<\/a>";
    strVar += "		<!--弹出菜单-->";
    strVar += "		<div class=\"mCustomScrollbar\" data-mcs-theme=\"dark\">";
    strVar += "			<ul class=\"left-menu\">";
    strVar += "				<li>";
    strVar += "					<a style='padding:5px;padding-left:25px;' href=\"#\" class=\"js-sidebar-open nav-big\">";
    strVar += "						<svg class=\"olymp-close-icon left-menu-icon\"><use xlink:href=\"/resources/svg-icons\/sprites\/icons.svg#olymp-close-icon\"><\/use><\/svg>";
    strVar += "						<span class=\"left-menu-title\">折叠菜单栏<\/span>";
    strVar += "					<\/a>";
    strVar += "				<\/li>";
    
    for(var i = 0; i < data.length; i++){
        strVar += "				<li>";
        if(data[i].menuSonsList == ""){
            strVar += "					<a style='padding:15px;padding-left:25px;' href=\""+ data[i].menuParent.resource +"\">";
            strVar += "						<svg class=\"olymp-menu-icon left-menu-icon\"  data-toggle=\"tooltip\" data-placement=\"right\"   data-original-title=\""+ data[i].menuParent.name +"\"><use xlink:href=\"/resources/svg-icons\/sprites\/icons.svg#"+ data[i].menuParent.icon +"\"><\/use><\/svg>";
            strVar += "                     <span>"+ data[i].menuParent.name +"</span>";
            strVar += "					<\/a>";
        }else{
            strVar += "					<a style='padding:5px;padding-left:25px;'>";
            strVar += "						<svg class=\"olymp-menu-icon left-menu-icon\"  data-toggle=\"tooltip\" data-placement=\"right\"   data-original-title=\""+ data[i].menuParent.name +"\"><use xlink:href=\"/resources/svg-icons\/sprites\/icons.svg#"+ data[i].menuParent.icon +"\"><\/use><\/svg>";
            strVar += "                     <span>"+ data[i].menuParent.name +"</span>";
            strVar += "					<\/a>";
            strVar += "<ul class='text-center'>";
            for(var j = 0; j < data[i].menuSonsList.length; j++){
                strVar += "<li>" + "<a href='"+data[i].menuSonsList[j].resource+"'>"+ data[i].menuSonsList[j].name +"</a>" + "</li>";
            }
            strVar += "</ul>";
        }
        strVar += "				<\/li>";   
    }
    strVar += "			<\/ul>";
    strVar += "		<\/div>";
    strVar += "	<\/div>";


    var phoneMenu="";
    for(var i = 0; i < data.length; i++){
        if(data[i].menuSonsList == ""){
            phoneMenu += "<li><a href='"+ data[i].menuParent.resource +"'>" + data[i].menuParent.name + "</a></li>";
        }else{
            for(var j = 0; j < data[i].menuSonsList.length; j++){
                phoneMenu += "<li><a href='"+data[i].menuSonsList[j].resource+"'>"+ data[i].menuSonsList[j].name +"</a></li>";
            }
        }
        
    }
    $(".nav-left").append(strVar);
    $(".phoneMenu").append(phoneMenu);
    
    $(".popover-options a").popover({html : true });
}


$(".nav-left").on("click",".nav-min",function(){
    $(".nav-left").addClass("open");
})
$(".nav-left").on("click",".nav-big",function(){
    $(".nav-left").removeClass("open");
})
$('body').on('click', function (e) {
    $('[data-toggle="popover"]').each(function () {
        //the 'is' for buttons that trigger popups
        //the 'has' for icons within a button that triggers a popup
        if (!$(this).is(e.target) && $(this).has(e.target).length === 0 && $('.popover').has(e.target).length === 0) {
            $(this).popover('hide');
        }
    });
});

function getMuen(){
    $.ajax({
        url: "/api/user/menu",
        type: "get",
        dataType:"json",
        success:function(data){
            var strVar="";
            strVar += "<div class=\"fixed-sidebar-left sidebar--small\" id=\"sidebar-left\">";
            strVar += "		<a class=\"logo\">";
            strVar += "			<div class=\"img-wrap\">";
            strVar += "				<img href=\"/resources/index.html\" src=\"/resources/img/logo.png\" alt=\"Olympus\">";
            strVar += "			<\/div>";
            strVar += "		<\/a>";
            strVar += "";
            strVar += "		<div class=\"mCustomScrollbar\" data-mcs-theme=\"dark\">";
            strVar += "			<ul class=\"left-menu\">";
            strVar += "			  <li><a class=\"nav-link dropdown btn\" href="+"/resources/login/index.html"+">"+"登录<br/>注册"+"</a></li>	";
            strVar += "			<\/ul>";
            strVar += "		<\/div>";
            strVar += "	<\/div>";
            strVar += "";
            strVar += "	<div class=\"fixed-sidebar-left sidebar--large\" id=\"sidebar-left-1\">";
            strVar += "		<a class=\"logo\">";
            strVar += "			<div class=\"img-wrap\">";
            strVar += "				<img src=\"img\/logo.png\" alt=\"Olympus\">";
            strVar += "			<\/div>";
            strVar += "			<div class=\"title-block\">";
            strVar += "				<h6 class=\"logo-title\">olympus<\/h6>";
            strVar += "			<\/div>";
            strVar += "		<\/a>";
            strVar += "";
            strVar += "		<div class=\"mCustomScrollbar\" data-mcs-theme=\"dark\">";
            strVar += "			<ul class=\"left-menu\">";
            strVar += "			  <li><a class=\"nav-link dropdown btn\" href="+"/resources/login/index.html"+">"+"登录<br/>注册"+"</a></li>	";
            strVar += "			<\/ul>";
            strVar += "		<\/div>";
            strVar += "	<\/div>";
           if(data.status == 1){
                $(".nav-left").append(strVar);
                $(".phoneMenu").append("<li><a class=\"nav-link dropdown btn\" href="+"/resources/login/index.html"+">"+"登录<br/>注册"+"</a></li>");
           }else if(data.status == 0){
                navInit(data.data);
           }else if(data.status == 3){
                $(".phoneMenu").append("<li><a class=\"nav-link dropdown btn\" href="+"/resources/login/index.html"+">"+"登录<br/>注册"+"</a></li>");
                 $("#nav").append(strVar);
                $(".modal-body p").html(data.msg);
                $("#myModal").modal('show');
           }
        }
     })
}

function getuserInfo(){
    $.ajax({
        url: '/api/user/userInfo',
        dataType: 'json',
        type: 'get',
        success: function(data){
            if(data.status == 0){
                $(".myHeadImg").attr("src",data.data.headPictureSrc);
                $(".author-title").html(data.data.userName);
                $(".my_info").removeClass("hidden");
            }
        }
    });
}

 $("header").on("click","#okbtn",function(){
    $("#myModal").modal('hide');
    window.location.replace("/resources/login/index.html");
})
$("#reminderModalOkBtn").click(function(){
    $(".modal").modal('hide');
})