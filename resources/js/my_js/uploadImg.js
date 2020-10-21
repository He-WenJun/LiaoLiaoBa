var imgSrc = []; //图片路径
var imgName = []; //图片名字
var framDateArray = [];//文件流
var uploadId = [];
var updateResult = false;
imgUpload({
	inputId:'file', //input框id
	imgBox:'imgBox', //图片容器id
	buttonId:'commitPost', //提交按钮id
	upUrl:'http://liaoliaoba.com/api/post/uploadImg',  //提交地址
	//data:'file1' //参数名
})

//选择图片
function imgUpload(obj) {
	var oInput = '#' + obj.inputId;
	var imgBox = '#' + obj.imgBox;
	var btn = '#' + obj.buttonId;
	$(oInput).on("change", function() {
		var fileImg = $(oInput)[0];
		var fileList = fileImg.files;
		for(var i = 0; i < fileList.length; i++) {
			var imgSrcI = getObjectURL(fileList[i]);
			imgName.push(fileList[i].name);
			var framDate = new FormData();
			framDate.append("crowd_file",fileList[i]);
			imgSrc.push(imgSrcI);
			framDateArray.push(framDate);
		}
		addNewContent(imgBox);
	})
	$(btn).on('click', function() {
		if(framDateArray.length>0){
			submitPicture(obj.upUrl, framDateArray);
			var t = setInterval(function(){
				if(uploadId.length == framDateArray.length){
					clearInterval(t);
					updateResult = true;
				}
			},300); 
		}else{
			updateResult = true;
		}
	})
}
//图片展示
function addNewContent(obj) {
	$(imgBox).html("");
	for(var a = 0; a < imgSrc.length; a++) {
		var oldBox = $(obj).html();
		$(obj).html(oldBox + '<div class="imgContainer"><img title=' + imgName[a] + ' alt=' + imgName[a] + ' src=' + imgSrc[a] + ' onclick="imgDisplay(this)"><p onclick="removeImg(this,' + a + ')" class="imgDelete">删除</p></div>');
	}
}
//删除
function removeImg(obj, index) {
	imgSrc.splice(index, 1);
	imgName.splice(index, 1);
	framDateArray.splice(index,1);
	var boxId = "#" + $(obj).parent('.imgContainer').parent().attr("id");
	addNewContent(boxId);
}
//上传(将文件流数组传到后台)
function submitPicture(url,data) {
	if(url&&data){
		for(var i = 0; i < data.length; i++){
			$.ajax({
				type: "post",
				url: url,
				dataType:'json',
				data: data[i],
				contentType : false, // 不要设置Content-Type请求头
				processData : false, // 使数据不做处理
				success: function(data) {
					if(data.status != 0){
						return;
					}
					uploadId.push(data.data.id);
				}
			});
		};
		
	}
}
//图片灯箱
function imgDisplay(obj) {
	var src = $(obj).attr("src");
	var imgHtml = '<div style="width: 100%;height: 100vh;overflow: auto;background: rgba(0,0,0,0.5);text-align: center;position: fixed;top: 0;left: 0;z-index: 1000;"><img src=' + src + ' style="margin-top: 100px;width: 70%;margin-bottom: 100px;"/><p style="font-size: 50px;position: fixed;top: 30px;right: 30px;color: white;cursor: pointer;" onclick="closePicture(this)">×</p></div>'
	$('body').append(imgHtml);
}
//关闭
function closePicture(obj) {
	$(obj).parent("div").remove();
}

//图片预览路径
function getObjectURL(file) {
	var url = null;
	if(window.createObjectURL != undefined) { // basic
		url = window.createObjectURL(file);
	} else if(window.URL != undefined) { // mozilla(firefox)
		url = window.URL.createObjectURL(file);
	} else if(window.webkitURL != undefined) { // webkit or chrome
		url = window.webkitURL.createObjectURL(file);
	}
	return url;
}

function getUpdateResult(){
	return updateResult;
}

function getUploadId(){
	return uploadId;
}

function updateInit(){
	imgSrc.length = 0;
	imgName.length = 0;
	framDateArray.length = 0;
	uploadId.length = 0;
	updateResult = false;
	$("#imgBox").empty();
}