$(document).ready(function () {

    $("#img").on("change",function (event) {
        event.preventDefault();
        var value = $(this).val();
        if(value != null && value.length >= 4 && value.length < 255){
            uploadImg(new FormData($("#listing-img-form")[0]))
        }
    });

    $(document).on("click",".delete-img",function () {
        var id = $(this).attr("id").split("-")[0];
        deleteImg(id);
    })

    $(document).on("click",".main-img",function () {
        var url = $(this).parent().children(".img-fluid").attr("id");
        $.ajax({
            type: "POST",
            url: "/listing/change/main-img/" + $("#listingId").val(),
            data: {url: url},
            success: function (isChanged) {
                if(isChanged){
                    alert("main image successfully changed")
                }
            },
            error: function () {

            }
        })
    })

});

function uploadImg(FormData) {
    $.ajax({
        type: "POST",
        url: "/listing/upload",
        data: FormData,
        processData: false,
        contentType: false,
        success: function (img) {
            if(img.id > 0){
                var imgDiv = "<div id='" + img.id +"-div1' class='swiper-slide'>" +
                    "<figure itemprop='associatedMedia' itemscope" +
                    " itemtype='http://schema.org/ImageObject'><a" +
                    "href='/listings/" + img.url + "' itemprop='contentUrl'" +
                    "data-size='2000x1414'>" +
                    "<img src='/listings/" + img.url + "' " +
                    "class='img-fluid swiper-lazy'" +
                    " alt='Drawing Room'> </a>" +
                    "</figure>" +
                    "</div>";
                $("#img-blog1").append(imgDiv);
                var imgDiv2 = "<div id='" + img.id +"-div2' class=\"swiper-slide\">\n" +
                    "<img style=\"cursor: pointer\" id='"+ img.id + "-d' class=\"delete-img\" src=\"/icons/delete.png\">" +
                    "<img style=\"cursor: pointer\" id='" + img.id + "-m' class=\"main-img\" src=\"/icons/home.png\">"+
                "<img src=\"/listings/" + img.url + "\" id='" + img.url + "' class=\"img-fluid\" alt=\"\">" +
                    "</div>";
                $("#img-blog2").append(imgDiv2);
                $("#img-js").remove();
                $("#img-js1").remove();
                $("#img-js2").remove();
                $("#body").append("<script id='img-js' src='/js/img.js'></script>")
                $("#body").append("<script id='img-js1' src='/lib/photoswipe/photoswipe.min.js'></script>")
                $("#body").append("<script id='img-js2' src='/lib/photoswipe/photoswipe-ui-default.min.js'></script>")
            }
        },
        error: function () {
        }
    })
};

function deleteImg(id) {
    $.ajax({
        type: "POST",
        url: "/listing/image/delete/" + id,
        success: function (isDeleted) {
            if(isDeleted){
                $("#" + id + "-div1").remove();
                $("#" + id + "-div2").remove();
                $("#img-js").remove();
                $("#img-js1").remove();
                $("#img-js2").remove();
                $("#body").append("<script id='img-js' src='/js/img.js'></script>")
                $("#body").append("<script id='img-js1' src='/lib/photoswipe/photoswipe.min.js'></script>")
                $("#body").append("<script id='img-js2' src='/lib/photoswipe/photoswipe-ui-default.min.js'></script>")

            }
        },
        error: function () {

        }
    })
}