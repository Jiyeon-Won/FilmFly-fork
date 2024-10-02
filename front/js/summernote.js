$('#summernote').summernote({
  height: 500,
  placeholder: '내용',
  lang: 'ko-KR',
  toolbar: [
    ['fontsize', ['fontsize']],
    ['style', ['bold', 'italic', 'underline', 'strikethrough', 'clear']],
    ['color', ['color']],
    ['para', ['ul', 'ol', 'paragraph']],
    ['insert', ['picture', 'link']]
  ],
  fontNames: ['Arial', 'Arial Black', 'Comic Sans MS', 'Courier New', '맑은 고딕', '궁서', '굴림체', '굴림', '돋음체', '바탕체'],
  fontSizes: ['8', '9', '10', '11', '12', '14', '16', '18', '20', '22', '24', '28', '30', '36', '50', '72', '96'],
  focus: true,
  callbacks: {
    onImageUpload: function(files) {
      for (var i = 0; i < files.length; i++) {
        imageUploader(files[i], this);
      }
    },
    onMediaDelete: function($target) {
      var deletedImageUrl = $target.attr('src');
      imageDelete(deletedImageUrl);
    },
  }
});

function imageUploader(file, el) {
  let formData = new FormData();
  formData.append('file', file);

  apiModule.POST(`/image/upload`, formData, function(data) {
    let imageUrl = `${serverUrl}` + data;
    $(el).summernote('insertImage', imageUrl, function($image) {
      $image.css('width', "25%");
    });
  }, function(error) {
  }, {
    contentType: false,
    processData: false,
    enctype: 'multipart/form-data'
  });
}

function imageDelete(imageName) {

  apiModule.DELETE(`/image/delete?imageName=${imageName}`,null, function(response) {
    console.log("Image deleted: ", response);
  }, function(error) {
    console.error("Image delete failed: ", error);
  });
}