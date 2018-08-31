$(document).ready(function () {
   $("#img").on("change",function (event) {
       var value = $(this).val();
       if(value != null && value.length > 4 && value.length < 255){
           changeProfile(new FormData($("#user-image-form")[0]));
       }
   });

   $("#delete-show").on("click",function (event) {
       event.preventDefault();
       var clazz = $("#delete-div").attr("class");
       if(clazz == "show_"){
           $("#delete-div").show();
           $("#delete-div").attr("class","hide_")
       }else {
           $("#delete-div").hide();
           $("#delete-div").attr("class","show_")
       }
   });

   $("#no").on("click",function () {
       $("#delete-div").hide();
       $("#delete-div").attr("class","show_")
   })
});

function changeProfile(FormData) {
    $.ajax({
        type: "POST",
        url: $("#user-image-form").attr("action"),
        data: FormData,
        processData: false,
        contentType: false,
        success: function (url) {
            $("#img-blog").children().remove();
            $("#img-blog").append(" <img width=\"200px\" height=\"200px\"  class=\"img-fluid img-rounded agent-thumb\" src='/users/" + url + "' " +
                " alt=\"\">");
        },
        error: function () {

        }
    })
}